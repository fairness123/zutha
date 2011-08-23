package net.zutha.model.topicmap

import org.tmapi.core.{Topic, TopicMap}
import de.topicmapslab.majortom.model.core.ITopicMap

trait TMConversions {
  //convert tmapi Topic Map to MajorTM Topic Map
  implicit def topicMapToITopicMap(tm: TopicMap) = tm.asInstanceOf[ITopicMap]

  //convert tmapi Topic Map to Zutha Topic Map
  implicit def topicMapToZuthaTopicMap(tm: TopicMap) = new ZuthaTopicMap(tm)

  //convert tmapi Topic to Zutha Topic Map Item
  implicit def topicToTopicMapItem(topic: Topic) = new TopicMapItem(topic)

  //convert tmapi Topic to Zutha Topic
  implicit def topicToZuthaTopic(topic: Topic) = new ZuthaTopic(topic)
}
