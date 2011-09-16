package net.zutha.model.topicmap.extensions

import org.tmapi.core.{Occurrence, Topic}

import net.zutha.model.constants.TopicMapConstants
import net.zutha.util.Helpers._
import net.zutha.model.topicmap.db.{TopicMapDB}

object TopicExtended{
  val get = makeCache[Topic,String,TopicExtended](_.getId, topic => new TopicExtended(topic))
  def apply(topic: Topic):TopicExtended = get(topic)
}
class TopicExtended(topic: Topic) {
  private val tm = topic.getTopicMap

  def createIntOccurrence(occType: Topic, value: String): Occurrence = {
    val integerType = tm.createLocator(TopicMapConstants.INTEGER_SI)
    topic.createOccurrence(occType, value, integerType)
  }

  lazy val isAnonymous: Boolean = TopicMapDB.topicIsAnonymous(topic)
}
