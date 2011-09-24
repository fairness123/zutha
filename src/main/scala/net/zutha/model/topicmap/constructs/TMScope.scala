package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.constructs.{Item, ZScope}
import net.zutha.model.topicmap.db.TopicMapDB
import de.topicmapslab.majortom.model.core.{ITopicMap, IScope}

object TMScope{
  def apply(scopeItems: Set[Item]) = new TMScope(scopeItems)
  def apply(scopeItems: Item*) = new TMScope(scopeItems.toSet)
}
class TMScope(val scopeItems: Set[Item]) extends ZScope{

  def toIScope: IScope = {
    val topics = scopeItems.map(_.asInstanceOf[TMItem].toTopic)
    TopicMapDB.tmm.asInstanceOf[ITopicMap].createScope(topics.toSeq)
  }
}
