package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic

import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.db.DB.db
import net.zutha.util.Cache._
import net.zutha.model.constructs.{ZPropertySetType, ZAssociationFieldSetType, ZType}
import net.zutha.model.datatypes.ZUnboundedNNI.Finite

object TMType{
  val getItem = makeCache[Topic,String,TMType](_.getId, topic => new TMType(topic))
  def apply(topic: Topic):TMType = getItem(topic)
}
class TMType protected (topic: Topic) extends TMItem(topic) with ZType {
  def isAbstract: Boolean = {
    val abstractConstAssoc = topic.getRolesPlayed(db.TYPE,db.ABSTRACT_CONSTRAINT)
    !abstractConstAssoc.isEmpty
  }

  def hasAncestor(superType: ZType): Boolean = ancestors.contains(superType)

  lazy val ancestors: Set[ZType] = { //TODO allow an item's supertypes to be modified
    val ancestors = db.ancestorsOfType(this) + this //make sure this item itself is included
    ancestors
  }
  def descendants: Set[ZType] = {
    val descendants = db.descendantsOfType(this) + this
    descendants
  }

  // --------------- defined fields ---------------
  lazy val declaresFields: Boolean = {
    val propertyDefRoles = topic.getRolesPlayed(db.PROPERTY_DECLARER,db.PROPERTY_DECLARATION)
    val assocDefRoles = topic.getRolesPlayed(db.ASSOCIATION_FIELD_DECLARER,db.ASSOCIATION_FIELD_DECLARATION)
    propertyDefRoles.size > 0 || assocDefRoles.size > 0
  }

  lazy val declaredPropertySets = { //TODO get inherited property types
    val definedPropTypes = db.traverseAssociation(topic,db.PROPERTY_DECLARER,db.PROPERTY_DECLARATION,
      db.PROPERTY_TYPE.toRole).map(_.toPropertyType)
    definedPropTypes.map(ZPropertySetType(this,_))
    
//    val propDefRoles = topic.getRolesPlayed(db.siPROPERTY_DECLARER,db.siPROPERTY_DECLARATION).toSet
//    propDefRoles.map{_.getParent.getRoles(db.siPROPERTY_TYPE).head.getPlayer.toPropertyType}
  }

  /** @return a Seq[ZAssociationFieldType] representing the
   *  association fields defined by this Type
   */
  def declaredAssociationFieldSets = {
    val declAssociations = db.findAssociations(db.ASSOCIATION_FIELD_DECLARATION,false,
      db.ASSOCIATION_FIELD_DECLARER -> this)
    declAssociations.map{declAssoc =>
      val role = declAssoc.getPlayers(db.ROLE.toRole).head.toRole
      val assocType = declAssoc.getPlayers(db.ASSOCIATION_TYPE.toRole).head.toAssociationType
      ZAssociationFieldSetType(this,role,assocType)
    }.toSet
  }

  def requiredPropertySets = declaredPropertySets.filter(_.cardMin >= Finite(1))

  def requiredAssociationFieldSets = declaredAssociationFieldSets.filter(_.cardMin >= Finite(1))
}
