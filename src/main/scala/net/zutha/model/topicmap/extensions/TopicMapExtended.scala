package net.zutha.model.topicmap.extensions

import net.zutha.model.constructs.Zid
import net.zutha.model.constants.ZuthaConstants._
import net.zutha.model.constants.{TopicMapConstants => TM}
import org.tmapi.core.{Topic, TopicMap}

case class TopicMapExtended(val tm: TopicMap) {
  def lookupTopicByZSI(zsi: String): Option[Topic] = lookupTopicBySI(ZSI_PREFIX + zsi)

  def lookupTopicByZID(zid: Zid): Option[Topic] = lookupTopicBySI(ZID_PREFIX + zid)

  def lookupTopicBySI(siStr: String): Option[Topic] = {
    val si = tm.createLocator(siStr)
    val topic = tm.getTopicBySubjectIdentifier(si)
    if(topic==null) None
    else Some(topic)
  }

  def getOrCreateTopicBySI(siStr: String): Topic = lookupTopicBySI(siStr) match {
    case Some(t) => t
    case _ => {
      val loc = tm.createLocator(siStr)
      tm.createTopicBySubjectIdentifier(loc)
    }
  }

  def getOrCreateOccurrenceTypeBySI(siStr: String): Topic = lookupTopicBySI(siStr) match {
    case Some(ot) => ot
    case _ =>
      val occTypeTopic = getOrCreateTopicBySI(TM.OCCURRENCE_TYPE_SI)
      val si = tm.createLocator(siStr)
      val ot = tm.createTopicBySubjectIdentifier(si)
      ot.addType(occTypeTopic)
      ot
  }

}
