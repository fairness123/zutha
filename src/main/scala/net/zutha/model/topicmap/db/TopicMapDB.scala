package net.zutha.model.topicmap.db

import scala.collection.JavaConversions._
import org.tmapi.core._
import org.tmapix.io.CTMTopicMapReader
import tools.nsc.io.{File}
import de.topicmapslab.majortom.model.core.{ITopicMapSystem}
import de.topicmapslab.majortom.model.transaction.ITransaction

import net.zutha.model.constants._
import ZuthaConstants._
import SchemaIdentifier._
import ApplicationConstants._
import net.zutha.model.{ProposedItem}
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs._
import net.zutha.model.db.DB
import net.zutha.model.exceptions.SchemaItemMissingException
import net.liftweb.common.Loggable

object TopicMapDB extends DB with MajortomDB with TMQL with Loggable{
  val ENABLE_TRANSACTIONS = true

  val sys: ITopicMapSystem = makeTopicMapSystem

  //check if schema needs to be created
  tm.lookupTopicByZSI("item") match {
    case Some(_)  => //nothing needs doing
    case None     => generateSchema()
  }

  // Zutha Topic Map
  def tm = {
    Option(sys.getTopicMap(ZUTHA_TOPIC_MAP_URI)) match {
      case Some(ztm) => ztm
      case None => sys.createTopicMap(ZUTHA_TOPIC_MAP_URI)
    }
  }

  // ZID Ticker
  private lazy val zidTicker = new ZIDTicker(tm)
  def getNextZID: Zid = zidTicker.getNext

  /**
   * @param identifier the SchemaIdentifier of the schema item to retrieve
   * @return the schema item with the given identifier
   * @throws SchemaItemMissingException if the requested topic does not exist
   */
  def getSchemaItem(identifier: SchemaIdentifier): Item = tm.lookupTopicBySI(ZSI_PREFIX + identifier.toString) match {
    case Some(topic) => topic.toItem
    case None => throw new SchemaItemMissingException
  }

  def getItem(zid: Zid) = tm.lookupTopicByZID(zid).map{_.toItem}

  def createItem(item: ProposedItem) {
    //start transaction
    val txn = if(ENABLE_TRANSACTIONS) tm.createTransaction() else tm

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
    if(ENABLE_TRANSACTIONS) txn.asInstanceOf[ITransaction].commit
  }

  def printTMLocators = {
    for(loc <- sys.getLocators) {
      println (loc.getReference)
    }
  }

  def generateSchema() = {
    //open current schema.ctm file
    val schema_file = File(TM_DATA_PATH + "schema.ctm")

    //open ctm data files: schema_gen.ctm, schema_templates.ctm
    val schema_gen_file = File(TM_DATA_PATH + "schema_gen.ctm")
    val ctm_templates_file = File(TM_DATA_PATH + "schema_templates.ctm")

    //if schema_gen.ctm or schema_templates.ctm are newer than schema.ctm,
    //then regenerate schema.ctm
    val schemaMod = schema_file.lastModified
    if (schemaMod < schema_gen_file.lastModified ||
        schemaMod < ctm_templates_file.lastModified){

      //read schema_gen.ctm, schema_templates.ctm to string
      val schema_gen = schema_gen_file.slurp
      val ctm_templates = ctm_templates_file.slurp

      //insert ctm_templates
      val schema_with_templates = schema_gen.replace("%include schema_templates.ctm",ctm_templates)

      //replace %zid% markers with unique ZIDs
      val schema = "%zid%".r.replaceAllIn(schema_with_templates,(m => getNextZID.toString))

      //save transformed schema_gen as schema.ctm
      schema_file.writeAll(schema)
    }

    //make a topic map from the ctm schema
    val source: java.io.File = schema_file.jfile
    val reader: CTMTopicMapReader = new CTMTopicMapReader(tm, source,ZUTHA_TOPIC_MAP_URI)
    reader.read

    logger.info("database has been reset back to schema")
  }

}
