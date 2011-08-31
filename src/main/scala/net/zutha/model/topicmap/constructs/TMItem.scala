package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic
import de.topicmapslab.tmql4j.components.processor.results.tmdm.SimpleResult

import net.zutha.model.constants.{ZuthaConstants, SchemaIdentifier, TMQLQueries => Q}
import ZuthaConstants._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs._
import net.zutha.model.topicmap.db.{ConstructCache, TopicMapDB}
class TMItem(val topic: Topic) extends Item{
  //verify that topic is an item
//  try{
//    getZIDs
//  } catch {
//    case e: Exception => throw new IllegalArgumentException("Topic is not an Item")
//  }

  private[topicmap] val tm = topic.getTopicMap

  // -------------- Common method overrides --------------
  override def hashCode() = ("http://zutha.net/majortomTopic/" + topic.getId).hashCode()

  override def equals(obj:Any) = obj match {
    case item: Item => item.hashCode == hashCode
    case topic: Topic => topic.toTMItem.hashCode == hashCode
    case _ => false
  }

  // -------------- Conversion --------------
  lazy val isItemType = {
    runBooleanQuery(Q.ItemIsAnItemType)
  }
  lazy val isAnonymous = {
    runBooleanQuery(Q.TopicIsAnonymous)
  }
  def toTopic = topic
  def toTMItem = this

  def toTMItemType: TMItemType = ConstructCache.getItemType(this)

  def toItemType: ItemType = toTMItemType


  // -------------- ZIDs --------------
  lazy val ZIDs: Seq[String] = {
    val zids = topic.getSubjectIdentifiers.map(_.toExternalForm)
      .filter{_.startsWith(ZID_PREFIX.toString)}.map{_.replace(ZID_PREFIX.toString,"")}
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

  // -------------- Zuthanet Address --------------
  def address: String = {
    "/item/" + zid + "/" + name
  }
  
  // -------------- types --------------
  def hasType(itemType: ItemType): Boolean = getAllTypes.contains(itemType)

  def getType: ItemType = {
    topic.getTypes.filterNot(_.isAnonymous).head.toTMItemType
  }

  def getAllTypes: Set[ItemType] = {
    val items = runItemTypeQuery(Q.AllTypesOfItem).filterNot(_.isAnonymous).toSet
    items.map(_.toItemType)
  }

  def getFieldDefiningTypes: Set[ItemType] = {
    getAllTypes.filter(_.definesFields)
  }

  // -------------- fields --------------
  def getPropertySets: Set[PropertySet] = {
    getFieldDefiningTypes.flatMap{definingType =>
      definingType.getDefinedProperties
        .filterNot(_.isAbstract) //abstract propTypes do not have associated propSets
        .map(propType => new TMPropertySet(this,propType,definingType))
    }
  }

  def getProperties(propType: ItemType): Set[Property] = {
    //check if propType is a name
    val namePropType = TopicMapDB.getSchemaItem(SchemaIdentifier.NAME).toItemType
    if(propType.hasSuperType(namePropType)){ //no need to check if propType is zsi:name itself because zsi:name is abstract
      val names = topic.getNames(propType).map(_.toProperty).toSet
      return names
    }

    //TODO check if propType is ZID


    //propType is an occurrence-implemented property
    val occurrences = topic.getOccurrences(propType).map(_.toProperty).toSet
    occurrences
  }

  def getAssociationSets: Set[AssociationFieldSet] = {
    getFieldDefiningTypes.flatMap{definingType =>
      definingType.getDefinedAssociationFields
        .map(assocType => new TMAssociationFieldSet())
    }
  }


  // -------------- item-centric queries --------------
  private def runQuery(qstr: String) = {
    val statement = TopicMapDB.prepareStatement(qstr)
    statement.setTopic(0,topic)
    statement.run()
    statement.getResults
  }

  def runBooleanQuery(qstr: String): Boolean = {
    val rs = runQuery(qstr)
    rs.size() > 0
  }

  private[topicmap] def runItemQuery(qstr: String): Seq[TMItem] = {
    val rs = runQuery(qstr)
    rs.map{_ match {
      case res: SimpleResult => res.first match {
        case tt: Topic => tt.toTMItem
        case _ => throw new IllegalArgumentException("query results were not Topics as expected")
      }
      case _ => throw new IllegalArgumentException("query did not return TMAPI constructs as expected")
    }
    }.toSeq
  }

  //TODO remove non-ItemType constructs from results
  private[topicmap] def runItemTypeQuery(qstr: String): Seq[TMItemType] = {
    try runItemQuery(qstr).map(_.toTMItemType)
    catch {
      case e: IllegalArgumentException =>
        throw new IllegalArgumentException("query does not return ItemType results")
    }
  }
}
