package net.zutha.model.db

import net.zutha.lib.BaseX
import net.zutha.model.ZID
import net.zutha.model.constants.ApplicationConstants._
import org.tmapi.core.{TopicMap,Occurrence}
import net.zutha.model.topicmap.TMConversions

class ZIDTicker(tm: TopicMap) extends TMConversions{
  val base32converter = BaseX(ZID.charset)
  val tickerProp: Occurrence = getNextZIDOccurrence


  private def increment = {
    val oldTicker = tickerProp.longValue()
    tickerProp.setValue(oldTicker + 1)
    oldTicker
  }

  def getNext: ZID = ZID(THIS_HOST_ID + base32converter.encode(increment))

  def getNextZIDOccurrence: Occurrence = {
    //get ZID_Ticker topic
    val tickerTopic = tm.getOrCreateTopicBySI(ZID_TICKER_SI)

    //get nextZID occurrence type
    val nextZIDOccType = tm.getOrCreateOccurrenceTypeBySI(NEXT_ZID_OCCURRENCE_TYPE_SI)

    //get nextZID occurrence of ZID_Ticker
    val occs = tickerTopic.getOccurrences(nextZIDOccType)
    if (occs.size == 0) { //create nextZID occurrence with initial value of 1
      tickerTopic.createIntOccurrence(nextZIDOccType, "1")
    } else if (occs.size == 1){
      occs.iterator().next()
    } else {
      throw new Exception("ZID_Ticker has too many nextZID occurrences")
    }

  }
}
