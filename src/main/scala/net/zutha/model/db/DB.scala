package net.zutha.model.db

import net.zutha.model.{ProposedItem, Item, ZID}
import net.zutha.model.topicmap.TopicMapDB

trait DB {
  def getNextZID: ZID;

  def getItem(zid: ZID): Option[Item];

  def createItem(item: ProposedItem);


}

object DB {
  def get: DB = TopicMapDB
}
