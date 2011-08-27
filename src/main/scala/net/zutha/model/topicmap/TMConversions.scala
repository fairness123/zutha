package net.zutha.model.topicmap

import db.ConstructCache
import de.topicmapslab.majortom.model.core._
import org.tmapi.core._

import net.zutha.model.topicmap.constructs._
import net.zutha.model.constructs.{ItemType, Item}

object TMConversions {
  // --------------------- Extended Topic Map constructs ---------------------
  implicit def topicMapToTopicMapExtended(tm: TopicMap) = new TopicMapExtended(tm)
  implicit def topicToTopicExtended(topic: Topic) = new TopicExtended(topic)

  // --------------------- ZADM constructs ---------------------
  // ZADM Item => tmapi Topic
  implicit def itemToTopic(item: Item) = item.asInstanceOf[TMItem].toTopic
  // ZADM ItemType => tmapi Topic
  implicit def itemTypeToTopic(itemType: ItemType) = itemType.asInstanceOf[TMItemType].toTopic
  
  // --------------------- ZADM Topic Map implementation constructs ---------------------
  //tmapi Topic => TMItem
  implicit def topicToTMItem(topic: Topic) = ConstructCache.getItem(topic)
  // TMItem => tmapi Topic
  implicit def TMItemToTopic(item: TMItem) = item.toTopic

  //tmapi Association => TMAssociation
  implicit def associationToTMAssociation(assoc: Association) = new TMAssociation(assoc)

  //tmapi Occurrence => TMOccurrenceProperty
  implicit def occurrenceToTMOccurrenceProperty(occ: Occurrence) = new TMOccurrenceProperty(occ)

  //tmapi Name => TMNameProperty
  implicit def nameToTMNameProperty(name: Name) = new TMNameProperty(name)

  //tmapi SubjectLocator => TM_URIProperty

  // --------------------- MaJorToM constructs ---------------------
  //tmapi TopicMap => MajorToM ITopicMap
  implicit def topicMapToITopicMap(tm: TopicMap) = tm.asInstanceOf[ITopicMap]

  //tmapi Topic => MajorToM ITopic
  implicit def topicToITopic(t: Topic) = t.asInstanceOf[ITopic]

  //tmapi Association => MajorToM IAssociation
  implicit def associationToIAssociation(assoc: Association) = assoc.asInstanceOf[IAssociation]

  //tmapi Role => MajorToM IAssociationRole
  implicit def roleToIRole(role: Role) = role.asInstanceOf[IAssociationRole]

  //tmapi Occurrence => MajorToM IOccurrence
  implicit def occurrenceToIOccurrence(occ: Occurrence) = occ.asInstanceOf[IOccurrence]

  //tmapi Name => MajorToM IName
  implicit def nameToIName(occ: Name) = occ.asInstanceOf[IName]
}
