package net.zutha.model.topicmap.constructs

import org.tmapi.core.Topic
import net.zutha.model.constructs.{ItemType}
import net.zutha.util.Helpers._

object TMItemType{
  val getItem = makeCache[Topic,String,TMItemType](_.getId, topic => new TMItemType(topic))
  def apply(topic: Topic):TMItemType = getItem(topic)
}
class TMItemType protected (topic: Topic) extends TMType(topic) with ItemType{


}
