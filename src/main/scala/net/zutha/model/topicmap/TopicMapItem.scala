package net.zutha.model.topicmap

import scala.collection.JavaConversions._
import net.zutha.model.{ZID, Item}
import net.zutha.model.constants.ZuthaConstants._
import org.tmapi.core.{Topic}
import net.zutha.model.constants.{TopicMapConstants => TM}

class TopicMapItem(topic: Topic) extends Item with TMConversions{
  if(!verifyTopicIsAnItem){
    throw new IllegalArgumentException("Topic is not an Item")
  }

  private def verifyTopicIsAnItem: Boolean = {
    try{
      getZIDs
      true
    } catch {
      case e: Exception => false
    }
  }

  def toItem = this

  // -------------- ZIDs --------------
  lazy val ZIDs: Seq[String] = {
    val zids = topic.getSubjectIdentifiers.map(_.toExternalForm)
      .filter{_.startsWith(ZID_PREFIX)}.map{_.replace(ZID_PREFIX,"")}
      .toSeq.sorted
    //every item must have at least one ZID
    if (zids.size == 0) throw new Exception("item has no ZIDs")

    try{
      zids.map{ZID(_).toString}
    } catch {
      case e: IllegalArgumentException => throw new Exception("item has an invalid ZID")
    }
  }
  def getZIDs = ZIDs

  def zid: String = getZIDs(0)

  def addZID(zid: ZID) = {
    val zidLoc = topic.getTopicMap.createLocator(ZID_PREFIX + zid)
    topic.addSubjectIdentifier(zidLoc)
  }

  // -------------- names --------------
  def name = topic.getNames.toSeq.get(0).getValue


  // -------------- types --------------
  def getDirectTypes: Seq[Item] = {
    topic.getTypes.toSeq.map(_.toItem).sortBy(_.name)
  }

  //generate Zuthanet Address of this item
  def address: String = {
    "/item/" + zid + "/" + name
  }


}
