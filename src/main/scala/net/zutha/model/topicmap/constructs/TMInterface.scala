package net.zutha.model.topicmap.constructs

import org.tmapi.core.Topic
import net.zutha.model.constructs.{ZInterface}
import net.zutha.util.Helpers._

object TMInterface{
  val getItem = makeCache[Topic,String,TMInterface](_.getId, topic => new TMInterface(topic))
  def apply(topic: Topic):TMInterface = getItem(topic)
}
class TMInterface protected (topic: Topic) extends TMType(topic) with ZInterface{

}
