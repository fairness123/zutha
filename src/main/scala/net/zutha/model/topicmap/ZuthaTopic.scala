package net.zutha.model.topicmap

import org.tmapi.core.{Occurrence, Topic}
import net.zutha.model.constants.TopicMapConstants

class ZuthaTopic(topic: Topic) {
  private val tm = topic.getTopicMap

  def createIntOccurrence(occType: Topic, value: String): Occurrence = {
    val integerType = tm.createLocator(TopicMapConstants.INTEGER_SI)
    topic.createOccurrence(occType, value, integerType)
  }
}
