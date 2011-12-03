package net.zutha.model.topicmap.db

import scala.collection.JavaConversions._
import scala.collection.mutable.Map
import org.tmapi.core._
import org.tmapix.io.CTMTopicMapReader
import tools.nsc.io.{File}
import de.topicmapslab.majortom.model.transaction.ITransaction
import de.topicmapslab.majortom.model.core.{ITopicMapSystem}
import net.liftweb.common.{Loggable}

import net.zutha.model.constants._
import ZuthaConstants._
import ApplicationConstants._
import net.zutha.model.{ProposedItem}
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs._
import net.zutha.model.db.DB
import net.zutha.model.exceptions.SchemaItemMissingException


object TopicMapDB extends DB with MajortomDB with TMQL with Loggable{
  val ENABLE_TRANSACTIONS = true

  val sys: ITopicMapSystem = makeTopicMapSystem

  //check if schema needs to be created
  tmm.lookupTopicByZSI("item") match {
    case Some(_)  => //nothing needs doing
    case None     => generateSchema()
  }

  // Zutha Topic Map
  def tmm = {
    Option(sys.getTopicMap(ZUTHA_TOPIC_MAP_URI)) match {
      case Some(ztm) => ztm
      case None => sys.createTopicMap(ZUTHA_TOPIC_MAP_URI)
    }
  }
  def tm: TopicMap = tmm

  // ZID Ticker
  private lazy val zidTicker = new ZIDTicker(tmm)
  def getNextZID: Zid = zidTicker.getNext

  /**
   * @param identifier the Zutha Identifier of the schema item to retrieve
   * @return the schema item with the given identifier
   * @throws SchemaItemMissingException if the requested topic does not exist
   */
  protected def getSchemaItem(identifier: String): ZItem = tmm.lookupTopicBySI(ZSI_PREFIX + identifier) match {
    case Some(topic) => topic.toItem
    case None => throw new SchemaItemMissingException
  }

  def getItemByZid(zid: Zid) = tmm.lookupTopicByZID(zid).map{_.toItem}

  def createTopic(name: String): Topic = {
    val topic = createTopic()
    val nameObject = topic.createName(MODIFIABLE_NAME,name)
    val nameReifier = createTopic()
    nameReifier.addType(MODIFIABLE_NAME)
    nameObject.setReifier(nameReifier)
    topic
  }
  def createTopic(): Topic = {
    val zid = getNextZID
    val zidUri = ZID_PREFIX + zid
    val loc = tm.createLocator(zidUri)
    val topic = tm.createTopicBySubjectIdentifier(loc)
    topic
  }
  def createAssociation(assocType: ZAssociationType, rolePlayers: (ZRole, ZItem)*) = {
    val assoc = tmm.createAssociation(assocType)
    for((r,p) <- rolePlayers){
      assoc.createRole(r,p)
    }
    assoc
  }
  def createReifiedAssociation(assocType: ZAssociationType, rolePlayers: (ZRole, ZItem)*) = {
    val assoc = createAssociation(assocType, rolePlayers:_*)
    val assocReifier = createTopic()
    assocReifier.addType(assocType)
    assoc.setReifier(assocReifier)
    assoc
  }

  def createItem(item: ProposedItem) {
    //start transaction
    val txn = if(ENABLE_TRANSACTIONS) tmm.createTransaction() else tmm

    val topic = txn.createTopic()

    //add names
    for(nameProp <- item.names.props){
      val name = topic.createName(nameProp.value)
    }

    //add Subject Indicators
    for(siProp <- item.subjectIndicators.props){
      try{
        val loc = txn.createLocator(siProp.uri)
        topic.addSubjectIdentifier(loc)
      } catch{
        case e:MalformedIRIException => //send error message to siProp
      }
    }

    //add Topic Types
    for(typeProp <- item.types.props){
      txn.lookupTopicByZSI(typeProp.typeZSI) match {
        case Some(tt) => topic.addType(tt)
        case _ => //TODO send error: topic type not found
      }
    }

    //create a ZID for this new item
    topic.addZID(getNextZID)

    //commit proposed item
    if(ENABLE_TRANSACTIONS) txn.asInstanceOf[ITransaction].commit()
  }

  def printTMLocators(){
    for(loc <- sys.getLocators) {
      println (loc.getReference)
    }
  }

  def generateSchema(){
    val tmFile = File(TM_DATA_PATH + "tm.ctm")
    val zidTickerFile = File(TM_DATA_PATH + "zid-ticker.dat")

    //regenerate tm.ctm if needed
    val inputFileNames = List("schema_templates", "schema", "core","basic-entities","books","person","test-data")
    val inputFiles = inputFileNames.map{fn => File(TM_DATA_PATH + fn + ".ctm")}

    val changesMade = inputFiles.exists{f => f.lastModified > tmFile.lastModified}
    if( !tmFile.exists || changesMade ) {
      val lines = inputFiles.flatMap(_.chars.getLines)
      val namedZidPlaceholderMap = Map[String,String]()
      val matcher = """(%zid%)([\w]+%)?""".r
      val writer = tmFile.printWriter()

      //Replace %zid% markers with unique ZIDs
      //For markers of the form %zid%<tag>%,
      // replace all markers with the same tag with an identical ZID
      lines.foreach{line =>
        val newLine = matcher.replaceAllIn(line,{m =>
          def newZID = "zid:"+getNextZID.toString
          m.group(2) match {
            case tag:String => namedZidPlaceholderMap.getOrElseUpdate(tag,newZID)
            case _ => newZID //this is an anonymous ZID placeholder
          }
        })
//        newLine foreach (writer write _)
        writer println newLine
      }
      writer.flush()
      writer.close

      //save the final value of the zid-ticker
      val lastUsedZID = zidTicker.getCurrentAsLong.toString
      zidTickerFile.writeAll(lastUsedZID)
    }

    //make a topic map from the ctm schema
    val source: java.io.File = tmFile.jfile
    val reader: CTMTopicMapReader = new CTMTopicMapReader(tmm, source,ZUTHA_TOPIC_MAP_URI)
    reader.read()

    //set ZID Ticker start value
    val tickerValue = zidTickerFile.slurp().toLong
    zidTicker.setTickerValue(tickerValue)

    logger.info("database has been reset back to schema")
  }

  /** get all associations of type assocType with the given (role,player) pairs
   *  @param assocType matched associations must have this type (transitive)
   *  @param strict If true, matched associations must have exactly the set of rolePlayers given.
   *    If false, matched associations  must have at least the set of rolePlayers given
   *  @param rolePlayers a set of (Role,Player) pairs that matched associations must contain
   **/
  def findAssociations(assocType: ZAssociationType, strict: Boolean, rolePlayers:(ZRole,ZItem)*) = {
    val requiredRolePlayers = rolePlayers.toSet
    val allAssoc = tmm.getAssociations[Association](assocType:Topic).toSet.map((a:Association) => a.toZAssociation)
    val results = allAssoc.filter{assoc =>
      val assocRolePlayers = assoc.rolePlayers
      if(strict) assocRolePlayers == requiredRolePlayers
      else requiredRolePlayers.forall(assocRolePlayers contains)
    }
    results
  }
}
