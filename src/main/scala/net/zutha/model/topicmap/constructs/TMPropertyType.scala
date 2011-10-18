package net.zutha.model.topicmap.constructs

import org.tmapi.core.Topic

import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Helpers._
import net.zutha.model.datatypes.DataType
import net.zutha.model.constructs.{ZItem, ZPropertyType}
import net.zutha.model.db.DB.db
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.exceptions.SchemaViolationException

object TMPropertyType{
  val getItem = makeCache[Topic,String,TMPropertyType](_.getId, topic => new TMPropertyType(topic))
  def apply(topic: Topic):TMPropertyType = getItem(topic)
}
class TMPropertyType protected (topic: Topic) extends TMInterface(topic) with ZPropertyType{

  def dataTypeItem: ZItem = {
    //TODO resolve override rules
    getAllSuperTypes.flatMap(propType => TopicMapDB.traverseAssociation(propType,db.siPROPERTY_TYPE.toRole,
      db.siPROPERTY_DATATYPE_CONSTRAINT,db.siDATATYPE.toRole))
      .headOption.getOrElse(throw new SchemaViolationException("propertyType: "+this.name+" is missing a datatype declaration"))
  }

  def dataType = DataType(dataTypeItem)
}
