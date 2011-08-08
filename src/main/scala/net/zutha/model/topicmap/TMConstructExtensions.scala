package net.zutha.model.topicmap

import org.tmapi.core.{Topic, TopicMap}

trait TMConstructExtensions {
  implicit def topicMapToZuthaTopicMap(tm: TopicMap) = new ZuthaTopicMap(tm)
  implicit def topicToTopicMapItem(topic: Topic) = new TopicMapItem(topic)
}
