package net.zutha.model.topicmap

import net.zutha.model.{ZID, Item}
import org.tmapi.core.{Topic, Locator, TopicMap}

class ZuthaTopicMap(val tm: TopicMap) {
  val ZID_PREFIX = "http://zutha.net/item/"
  val ZSI_PREFIX = "http://psi.zutha.net/"

  def lookupItemByZSI(zsi: String): Option[Item] = {
    lookupTopicByZSI(zsi).map(TopicMapItem(_))
  }

  def lookupItemByZID(zid: ZID): Option[Item] = {
    lookupTopicByZID(zid).map(TopicMapItem(_))
  }

  def lookupItemBySI(si: Locator): Option[Item] = {
    lookupTopicBySI(si).map(TopicMapItem(_))
  }

  def lookupTopicByZSI(zsi: String): Option[Topic] = {
    val si = convertZSItoLocator(zsi)
    lookupTopicBySI(si)
  }

  def lookupTopicByZID(zid: ZID): Option[Topic] = {
    val si = convertIDtoLocator(zid)
    lookupTopicBySI(si)
  }

  def lookupTopicBySI(si: Locator): Option[Topic] = {
    val topic = tm.getTopicBySubjectIdentifier(si)
    if(topic==null) None
    else Some(topic)
  }

  private def convertZSItoLocator(zsi: String) = {
    tm.createLocator(ZSI_PREFIX + zsi)
  }

  private def convertIDtoLocator(zid: ZID) = {
    tm.createLocator(ZID_PREFIX + zid)
  }
}
