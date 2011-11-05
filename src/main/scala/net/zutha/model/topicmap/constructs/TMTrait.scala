package net.zutha.model.topicmap.constructs

import org.tmapi.core.Topic
import net.zutha.model.constructs.{ZTrait}
import net.zutha.util.Helpers._

object TMTrait{
  val getItem = makeCache[Topic,String,TMTrait](_.getId, topic => new TMTrait(topic))
  def apply(topic: Topic):TMTrait = getItem(topic)
}
class TMTrait protected (topic: Topic) extends TMType(topic) with ZTrait{

}
