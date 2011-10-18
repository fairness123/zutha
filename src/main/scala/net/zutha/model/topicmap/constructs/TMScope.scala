package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.constructs.{ZItem, ZScope}
import net.zutha.model.topicmap.db.TopicMapDB
import de.topicmapslab.majortom.model.core.{ITopicMap, IScope}

object TMScope{
  def apply(scopeItems: Set[ZItem]) = new TMScope(scopeItems)
  def apply(scopeItems: ZItem*) = new TMScope(scopeItems.toSet)
}
class TMScope(val scopeItems: Set[ZItem]) extends ZScope{

  def toIScope: IScope = {
    val topics = scopeItems.map(_.asInstanceOf[TMItem].toTopic)
    TopicMapDB.tmm.asInstanceOf[ITopicMap].createScope(topics.toSeq)
  }
}
