package net.zutha.model.db

import net.zutha.model.ProposedItem
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constructs._

trait DB extends SchemaItems{
  def getNextZID: Zid;

  def getItemByZid(zid: Zid): Option[ZItem];

  def createItem(item: ProposedItem);

  //Queries
  def findAssociations(assocType: ZAssociationType, strict: Boolean, rolePlayers:(ZRole,ZItem)*): Set[ZAssociation]
  def itemIsA(item: ZItem, zType: ZType): Boolean
  def allTypesOfItem(item: ZItem): Set[ZType]
  def directTypesOfItem(item: ZItem): Set[ZType]
  def allSupertypesOfItem(zdmType: ZType): Set[ZType]
  def traverseAssociation(item: ZItem, role: ZRole, assocType: ZAssociationType, otherRole: ZRole): Set[ZItem]
}

object DB {
  def db: DB = TopicMapDB
}
