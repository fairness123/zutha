package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constants.{SchemaIdentifier, TMQLQueries => Q}
import SchemaIdentifier._
import net.zutha.model.constructs.{Item, ItemType}

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
    val items = runItemTypeQuery(Q.AllSupertypesOfItemType).filterNot(_.isAnonymous)
    items.sortBy(_.zid)
  }
  //uses MaJorToM's getSupertypes which has bug and gets subtypes
  def _getAllSuperTypes: Seq[ItemType] = {
    topic.getSupertypes.filterNot(_.isAnonymous).toSeq.map(_.toTMItemType).sortBy(_.zid)
  }


  // --------------- defined fields ---------------
  def definesFields: Boolean = {
//    val fields = runItemTypeQuery(Q.fieldsDeclaredByItemType)
//    fields.size > 0

    val propertyDeclarer = TopicMapDB.getSchemaItem(PROPERTY_DECLARER)
    val propertyDeclaration = TopicMapDB.getSchemaItem(PROPERTY_DECLARATION)
    val propertyDefRoles = topic.getRolesPlayed(propertyDeclarer,propertyDeclaration)

    val assocFieldDeclarer = TopicMapDB.getSchemaItem(ASSOCIATION_FIELD_DECLARER)
    val assocFieldDeclaration = TopicMapDB.getSchemaItem(ASSOCIATION_FIELD_DECLARATION)
    val assocDefRoles = topic.getRolesPlayed(assocFieldDeclarer,assocFieldDeclaration)
    propertyDefRoles.size > 0 || assocDefRoles.size > 0
  }

  def getDefinedProperties: Seq[ItemType] = {
    val propertyDeclarer = TopicMapDB.getSchemaItem(PROPERTY_DECLARER)
    val propDeclaration = TopicMapDB.getSchemaItem(PROPERTY_DECLARATION)
    val propType = TopicMapDB.getSchemaItem(PROPERTY_TYPE)
    val propDefRoles = topic.getRolesPlayed(propertyDeclarer,propDeclaration)
    propDefRoles.map{_.getParent.getRoles(propType).head.getPlayer.toTMItemType}.toSeq
  }

  /** @return a Seq of (role:Item, assocType:ItemType) representing the
   *  association fields defined by this ItemType
   */
  def getDefinedAssociationFields: Seq[(Item,ItemType)] = {
    val assocFieldDeclarer = TopicMapDB.getSchemaItem(ASSOCIATION_FIELD_DECLARER)
    val assocFieldDecl = TopicMapDB.getSchemaItem(ASSOCIATION_FIELD_DECLARATION)
    val assocTypeItem = TopicMapDB.getSchemaItem(ASSOCIATION_TYPE)
    val roleItem = TopicMapDB.getSchemaItem(ROLE)
    val itemTypeRoles = topic.getRolesPlayed(assocFieldDeclarer,assocFieldDecl)
    itemTypeRoles.map{itemTypeRole =>
      val declAssoc = itemTypeRole.getParent
      val role = declAssoc.getRoles(roleItem).head.getPlayer.toTMItem
      val assocType = declAssoc.getRoles(assocTypeItem).head.getPlayer.toTMItemType
      (role,assocType)
    }.toSeq
  }

}
