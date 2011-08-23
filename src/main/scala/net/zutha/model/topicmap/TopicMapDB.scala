package net.zutha.model.topicmap

import scala.collection.JavaConversions._
import net.zutha.model.constants._
import ZuthaConstants._
import ApplicationConstants._
import org.tmapi.core._
import de.topicmapslab.majortom.model.core.{ITopicMap, ITopicMapSystem}
import de.topicmapslab.majortom.model.transaction.ITransaction
import net.zutha.model.db.{ZIDTicker, TMQL, MajortomDB, DB}
import net.zutha.model.{ProposedItem, ZID}
import org.tmapix.io.CTMTopicMapReader
import tools.nsc.io.{File}

object TopicMapDB extends DB with MajortomDB with TMQL with TMConstructExtensions{
  val ENABLE_TRANSACTIONS = true

  val sys: ITopicMapSystem = makeTopicSystem

  // Zutha Topic Map
  def tm = {
    Option(sys.getTopicMap(ZUTHA_TOPIC_MAP_URI)) match {
      case Some(ztm) => ztm
      case None => sys.createTopicMap(ZUTHA_TOPIC_MAP_URI)
    }
  }

  // ZID Ticker
  var zidTicker = new ZIDTicker(tm)
  def getNextZID: ZID = zidTicker.getNext

  def getItem(zid: ZID) = tm.lookupTopicByZID(zid).map{_.toItem}

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
    topic.addZID(zidTicker.getNext)

    //commit proposed item
    if(ENABLE_TRANSACTIONS) txn.asInstanceOf[ITransaction].commit
  }

  def printTMLocators = {
    for(val loc <- sys.getLocators) {
      println (loc.getReference)
    }
  }

  def resetDBtoSchema() = {
    //delete current topic map (it will be recreated when next requested)
    val oldTm = tm
    oldTm.remove(true)
    zidTicker = new ZIDTicker(tm)

    //open current schema.ctm file
    val schema_file = File(TM_DATA_PATH + "schema.ctm")

    //open ctm data files: schema_gen.ctm, ctm_templates.ctm
    val schema_gen_file = File(TM_DATA_PATH + "schema_gen.ctm")
    val ctm_templates_file = File(TM_DATA_PATH + "ctm_templates.ctm")

    //if schema_gen.ctm or ctm_templates.ctm are newer than schema.ctm,
    //then egenerate schema.ctm

    val schemaMod = schema_file.lastModified
    if (schemaMod < schema_gen_file.lastModified ||
        schemaMod < ctm_templates_file.lastModified){

      //read schema_gen.ctm, ctm_templates.ctm to string
      val schema_gen = schema_gen_file.slurp
      val ctm_templates = ctm_templates_file.slurp

      //insert ctm_templates
      val schema_with_templates = schema_gen.replace("%include ctm_templates.ctm",ctm_templates)

      //replace %zid% markers with unique ZIDs
      val schema = "%zid%".r.replaceAllIn(schema_with_templates,(m => getNextZID.toString))

      //save transformed schema_gen as schema.ctm
      schema_file.writeAll(schema)
    }

    //make a topic map from the ctm schema
    val source: java.io.File = schema_file.jfile
    val reader: CTMTopicMapReader = new CTMTopicMapReader(tm, source)
    reader.read
  }

}
