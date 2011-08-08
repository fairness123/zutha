package net.zutha.model.topicmap

import scala.collection.JavaConversions._
import net.zutha.model.{ZID, Item}
import net.zutha.model.constants.ZuthaConstants._
import org.tmapi.core.{Occurrence, Topic}
import net.zutha.model.constants.{TopicMapConstants => TM}

case class TopicMapItem(topic: Topic) extends Item{
  val tm = topic.getTopicMap

  def toItem = this
  
  def zid: ZID = {
    val zidSIs = topic.getSubjectIdentifiers.map(_.toExternalForm)
      .filter{_.startsWith(ZID_PREFIX)}
      .toSeq.sorted
    //every item must have at least one ZID
    if (zidSIs.size == 0) throw new Exception("item has no ZIDs")
    
    val zidStr = zidSIs.get(0).replace(ZID_PREFIX,"")

    try{
      ZID(zidStr)
    } catch {
      case e: IllegalArgumentException => throw new Exception("item has an invalid ZID")
    }
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
