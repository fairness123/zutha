package net.zutha.model.db

import net.zutha.model.ProposedItem
import net.zutha.model.constructs.{Item, Zid}
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constants.SchemaIdentifier._

trait DB extends SchemaItems{
  def getNextZID: Zid;

  def getItem(zid: Zid): Option[Item];

//  protected def getSchemaItem(identifier: SchemaIdentifier): Item

  def createItem(item: ProposedItem);

}

object DB {
  def db: DB = TopicMapDB
}
