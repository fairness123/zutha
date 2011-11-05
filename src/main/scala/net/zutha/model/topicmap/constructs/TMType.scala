package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic

import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.db.DB.db
import net.zutha.util.Helpers._
import net.zutha.model.constructs.{ZAssociationFieldType, ZType}

object TMType{
  val getItem = makeCache[Topic,String,TMType](_.getId, topic => new TMType(topic))
  def apply(topic: Topic):TMType = getItem(topic)
}
class TMType protected (topic: Topic) extends TMItem(topic) with ZType {
  def isAbstract: Boolean = {
    val abstractConstAssoc = topic.getRolesPlayed(db.siTYPE,db.siABSTRACT_CONSTRAINT)
    !abstractConstAssoc.isEmpty
  }

  def hasSuperType(superType: ZType): Boolean = getAllSuperTypes.contains(superType)

  lazy val getAllSuperTypes: Set[ZType] = { //TODO allow an item's supertypes to be modified
    val supertypes = db.allSupertypesOfItem(this) + this
    supertypes
  }

  // --------------- defined fields ---------------
  def definesFields: Boolean = {
    val propertyDefRoles = topic.getRolesPlayed(db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION)
    val assocDefRoles = topic.getRolesPlayed(db.siASSOCIATION_FIELD_DECLARER,db.siASSOCIATION_FIELD_DECLARATION)
    propertyDefRoles.size > 0 || assocDefRoles.size > 0
  }

  def getDefinedPropertyTypes = { //TODO get inherited property types
    val definedPropTypes = db.traverseAssociation(topic,db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION,
      db.siPROPERTY_TYPE.toRole).map(_.toPropertyType)
    definedPropTypes
    
//    val propDefRoles = topic.getRolesPlayed(db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION).toSet
//    propDefRoles.map{_.getParent.getRoles(db.siPROPERTY_TYPE).head.getPlayer.toPropertyType}
  }

  /** @return a Seq[ZAssociationFieldType] representing the
   *  association fields defined by this Type
   */
  def getDefinedAssociationFieldTypes = {
    val declAssociations = db.findAssociations(db.siASSOCIATION_FIELD_DECLARATION,false,
      db.siASSOCIATION_FIELD_DECLARER -> this)
    declAssociations.map{declAssoc =>
      val role = declAssoc.getPlayers(db.siROLE.toRole).head.toRole
      val assocType = declAssoc.getPlayers(db.siASSOCIATION_TYPE.toRole).head.toAssociationType
      ZAssociationFieldType(role,assocType)
    }.toSet
  }
  
}
