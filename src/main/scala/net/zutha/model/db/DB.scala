package net.zutha.model.db

import net.zutha.model.{ProposedItem, Item, ZID}
import net.zutha.model.topicmap.TopicMapDB

trait DB {
  def getNextZID: ZID

  def getItem(zid: ZID): Option[Item];

  def getItem(zid: String): Option[Item] = getItem(ZID(zid))

  def createItem(item: ProposedItem);


}

object DB extends TopicMapDB
