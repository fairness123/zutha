package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs.PropertyType
import org.tmapi.core.Topic
import net.zutha.util.Helpers._

object TMPropertyType{
  val getItem = makeCache[Topic,String,TMPropertyType](_.getId, topic => new TMPropertyType(topic))
  def apply(topic: Topic):TMPropertyType = getItem(topic)
}
class TMPropertyType protected (topic: Topic) extends TMInterface(topic) with PropertyType{

}
