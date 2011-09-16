package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic

import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constants.{SchemaIdentifier => SI}
import net.zutha.model.db.DB.db
import net.zutha.model.topicmap.db.TopicMapDB
import TopicMapDB.{getSchemaItem => ZSI}
import net.zutha.model.constructs.{ZType}
import net.zutha.util.Helpers._

object TMType{
  val getItem = makeCache[Topic,String,TMType](_.getId, topic => new TMType(topic))
  def apply(topic: Topic):TMType = getItem(topic)
}
class TMType protected (topic: Topic) extends TMItem(topic) with ZType {
  def isAbstract: Boolean = {
    val abstractConstAssoc = topic.getRolesPlayed(ZSI(SI.TYPE),ZSI(SI.ABSTRACT_CONSTRAINT))
    !abstractConstAssoc.isEmpty
  }

  def hasSuperType(superType: ZType): Boolean = getAllSuperTypes.contains(superType)

  def getAllSuperTypes: Set[ZType] = {
    val supertypes = TopicMapDB.allSupertypesOfItem(this) + this
    supertypes
  }

  // --------------- defined fields ---------------
  def definesFields: Boolean = {
//    val fields = runItemTypeQuery(Q.fieldsDeclaredByItemType)
//    fields.size > 0
    val propertyDefRoles = topic.getRolesPlayed(db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION)
    val assocDefRoles = topic.getRolesPlayed(db.siASSOCIATION_FIELD_DECLARER,db.siASSOCIATION_FIELD_DECLARATION)
    propertyDefRoles.size > 0 || assocDefRoles.size > 0
  }

  def getDefinedProperties = {
    val propDefRoles = topic.getRolesPlayed(db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION).toSet
    propDefRoles.map{_.getParent.getRoles(db.siPROPERTY_TYPE).head.getPlayer.toPropertyType}
  }

  /** @return a Seq of (role:Item, assocType:ItemType) representing the
   *  association fields defined by this ItemType
   */
  def getDefinedAssociationFields = {
    val declarerRoles = topic.getRolesPlayed(db.siASSOCIATION_FIELD_DECLARER,db.siASSOCIATION_FIELD_DECLARATION)
    declarerRoles.map{itemTypeRole =>
      val declAssoc = itemTypeRole.getParent
      val role = declAssoc.getRoles(db.siROLE).head.getPlayer.toRole
      val assocType = declAssoc.getRoles(db.siASSOCIATION_TYPE).head.getPlayer.toAssociationType
      TMAssociationFieldType(role,assocType)
    }.toSet
  }
}
