package net.zutha.model.db

import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constructs._
import org.tmapi.core.Topic

trait DB extends SchemaItems{
  def getNextZID: Zid;

  def getItemByZid(zid: Zid): Option[ZItem];

  //Queries
  def findAssociations(assocType: ZAssociationType, strict: Boolean, rolePlayers:(ZRole,ZItem)*): Set[ZAssociation]
  def directTypesOfItem(item: ZItem): Set[ZType]
  def ancestorsOfType(zType: ZType): Set[ZType]
  def allTypesOfItem(item: ZItem): Set[ZType]
  def itemIsA(item: ZItem, zType: ZType): Boolean
  def allInstancesOfType(zType: ZType): Set[ZItem]
  def descendantsOfType(zType: ZType): Set[ZType]
  def traverseAssociation(item: ZItem, role: ZRole, assocType: ZAssociationType, otherRole: ZRole): Set[ZItem]
}

object DB {
  def db: DB = TopicMapDB
}
