package net.zutha.model.topicmap

import scala.collection.JavaConversions._
import net.zutha.model.{ZID, Item}
import net.zutha.model.constants.ZuthaConstants._
import org.tmapi.core.{Occurrence, Topic}
import net.zutha.model.constants.{TopicMapConstants => TM}

case class TopicMapItem(topic: Topic) extends Item{
  val tm = topic.getTopicMap

  def toItem = this
  
  def zid = {
    val zidStr = topic.getSubjectIdentifiers.map(_.toExternalForm)
      .filter{_.startsWith(ZID_PREFIX)}
      .toSeq.sorted.get(0)

    ZID(zidStr)
  }

  def name = topic.getNames.toSeq.get(0).getValue

  def addZID(zid: ZID) = {
    val zidLoc = topic.getTopicMap.createLocator(ZID_PREFIX + zid)
    topic.addSubjectIdentifier(zidLoc)
  }

  def createIntOccurrence(occType: Topic, value: String): Occurrence = {
    val integerType = tm.createLocator(TM.INTEGER_SI)
    topic.createOccurrence(occType, value, integerType)
  }
}
