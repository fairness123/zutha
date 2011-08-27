package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs.{ItemType}
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constants.{SchemaIdentifier, TMQLQueries => Q}
import SchemaIdentifier._

object TMItemType {
  def apply(item: TMItem): TMItemType = new TMItemType(item)
}

class TMItemType(item: TMItem) extends TMItem(item.topic) with ItemType{

  // --------------- Type Information ---------------
  //TODO lookup isAbstract constraint
  def isAbstract: Boolean = {
    runBooleanQuery(Q.ItemTypeIsAbstract)
  }

  def hasSuperType(superType: ItemType): Boolean = getAllSuperTypes.contains(superType)

  def getAllSuperTypes: Seq[ItemType] = {
    val items = runItemTypeQuery(Q.AllSupertypesOfItemType)
    items.sortBy(_.zid)
  }
  //uses MaJorToM's getSupertypes which has bug and gets subtypes
  def _getAllSuperTypes: Seq[ItemType] = {
    topic.getSupertypes.toSeq.map(_.toItemType).sortBy(_.zid)
  }


  // --------------- defined fields ---------------
  def definesFields: Boolean = {
    val rt = TopicMapDB.getSchemaItem(CONSTRAINED_ITEM_TYPE)
    val propDefAssocType = TopicMapDB.getSchemaItem(ITEM_PROPERTY_CONSTRAINT)
    val assocDefAssocType = TopicMapDB.getSchemaItem(ITEM_ROLE_CONSTRAINT)
    val propDefRoles = topic.getRolesPlayed(rt,propDefAssocType)
    val assocDefRoles = topic.getRolesPlayed(rt,assocDefAssocType)
    propDefRoles.size > 0 || assocDefRoles.size > 0
  }

  def getDefinedFields: Seq[ItemType] = getDefinedProperties ++ getDefinedAssociations
  
  def getDefinedProperties: Seq[ItemType] = {
    val itemTypeRole = TopicMapDB.getSchemaItem(CONSTRAINED_ITEM_TYPE)
    val propTypeRole = TopicMapDB.getSchemaItem(CONSTRAINED_PROPERTY_TYPE)
    val propDefAssocType = TopicMapDB.getSchemaItem(ITEM_PROPERTY_CONSTRAINT)
    val propDefRoles = topic.getRolesPlayed(itemTypeRole,propDefAssocType)
    propDefRoles.map{_.getParent.getRoles(propTypeRole).head.getPlayer.toItemType}.toSeq
  }

  def getDefinedAssociations: Seq[ItemType] = {
    val itemTypeRole = TopicMapDB.getSchemaItem(CONSTRAINED_ITEM_TYPE)
    val roleTypeRole = TopicMapDB.getSchemaItem(CONSTRAINED_ROLE_TYPE)
    val assocDefAssocType = TopicMapDB.getSchemaItem(ITEM_ROLE_CONSTRAINT)
    val assocDefRoles = topic.getRolesPlayed(itemTypeRole,assocDefAssocType)
    assocDefRoles.map{_.getParent.getRoles(roleTypeRole).head.getPlayer.toItemType}.toSeq
  }

}
