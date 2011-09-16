package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.constructs.{Item, ZScope}
import net.zutha.model.topicmap.db.TopicMapDB
import de.topicmapslab.majortom.model.core.{ITopicMap, IScope}

case class TMScope(scopeItems: Set[Item]) extends ZScope{
  def toIScope: IScope = {
    val topics = scopeItems.map(_.asInstanceOf[TMItem].toTopic)
    TopicMapDB.tm.asInstanceOf[ITopicMap].createScope(topics.toSeq)
  }
}
