package net.zutha.model.topicmap.extensions

import org.tmapi.core.{Occurrence, Topic}

import net.zutha.model.constants.TopicMapConstants
import net.zutha.util.Cache._
import net.zutha.model.topicmap.db.{TopicMapDB => tdb}
import net.zutha.model.constructs.ZTrait
import net.zutha.model.db.DB.db
import net.zutha.model.topicmap.TMConversions._

object TopicExtended{
  val get = makeCache[Topic,String,TopicExtended](_.getId, topic => new TopicExtended(topic))
  def apply(topic: Topic):TopicExtended = get(topic)
}
class TopicExtended(topic: Topic) {
  private val tm = topic.getTopicMap

  def createIntOccurrence(occType: Topic, value: String): Occurrence = {
    val integerType = tm.createLocator(TopicMapConstants.INTEGER_SI)
    topic.createOccurrence(occType, value, integerType)
  }

  def setType(tt: Topic){
    topic.addType(tt)
    tdb.createAssociation(db.TYPE_INSTANCE,
      (db.TYPE:Topic) -> tt,
      (db.INSTANCE:Topic) -> topic
    )
  }

  def addTrait(newTrait: ZTrait) {
    //create main item-has-trait association
    val assoc = tdb.createReifiedAssociation(db.ITEM_HAS_TRAIT,
      db.ITEM.toRole -> topic,
      db.TRAIT.toRole -> newTrait
    )
    val assocReifier = assoc.getReifier
    //create topic-map-friendly workaround for item-has-trait link using an anonymous topic
    val anon = tm.createTopic()
    anon.addType(tdb.ANONYMOUS_TOPIC)
    anon.addSupertype(newTrait)
    topic.addType(anon)
    //link anonymous topic to the item-has-trait association
    tdb.createAssociation(tdb.ANONYMOUS_TOPIC_LINK,
      tdb.REIFIED_ZDM_ASSOCIATION -> assocReifier,
      tdb.ANONYMOUS_TOPIC -> anon
    )
  }

  def isAnonymous: Boolean = tdb.topicIsAnonymous(topic)
}
