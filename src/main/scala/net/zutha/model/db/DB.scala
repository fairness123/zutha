package net.zutha.model.db

import net.zutha.model.ProposedItem
import net.zutha.model.constructs.{ZItem, Zid}
import net.zutha.model.topicmap.db.TopicMapDB

trait DB extends SchemaItems{
  def getNextZID: Zid;

  def getItem(zid: Zid): Option[ZItem];

  def createItem(item: ProposedItem);

}

object DB {
  def db: DB = TopicMapDB
}
