package net.zutha.model.topicmap

import org.tmapi.core.TopicMap

trait TMConstructExtensions {
  implicit def topicMapToZuthaTopicMap(tm: TopicMap) = new ZuthaTopicMap(tm)

}
