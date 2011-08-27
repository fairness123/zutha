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
  try{
    getZIDs
  } catch {
    case e: Exception => throw new IllegalArgumentException("Topic is not an Item")
  }

  private[topicmap] val tm = topic.getTopicMap

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

  private[topicmap] def runItemQuery(qstr: String): Seq[Item] = {
    val rs = runQuery(qstr)
    rs.map{_ match {
      case res: SimpleResult => res.first match {
        case tt: Topic => tt.toItem
        case _ => throw new IllegalArgumentException("query results were not Topics as expected")
      }
      case _ => throw new IllegalArgumentException("query did not return TMAPI constructs as expected")
    }
    }.toSeq
  }

  //TODO remove non-ItemType constructs from results
  private[topicmap] def runItemTypeQuery(qstr: String): Seq[ItemType] = {
    try runItemQuery(qstr).map(_.toItemType)
    catch {
      case e: IllegalArgumentException =>
        throw new IllegalArgumentException("query does not return ItemType results")
    }
  }

  // -------------- Common method overrides --------------
  override def hashCode() = ("http://zutha.net/majortomTopic/" + topic.getId).hashCode()

  override def equals(obj:Any) = obj match {
    case item: Item => item.hashCode == hashCode
    case topic: Topic => topic.toItem.hashCode == hashCode
    case _ => false
  }

  // -------------- Conversion --------------
  def toTopic = topic
  def toItem = this

  lazy val isItemType = {
    runBooleanQuery(Q.ItemIsAnItemType)
  }
  /**
   * converts this Item to an ItemType
   * @throws IllegalArgumentException if this Item is not an ItemType
   */
  def toItemType: ItemType = ConstructCache.getItemType(this)

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

  def getDirectTypes: Set[ItemType] = {
    topic.getTypes.map(_.toItemType).toSet
  }

  def getAllTypes: Set[ItemType] = {
    val items = runItemTypeQuery(Q.AllTypesOfItem).toSet
    items
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
    val abstractNamePropType = TopicMapDB.getSchemaItem(SchemaIdentifier.ABSTRACT_NAME).toItemType
    if(propType.hasSuperType(abstractNamePropType)){
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
      definingType.getDefinedAssociations
        .filterNot(_.isAbstract) //abstract propTypes do not have associated propSets
        .map(assocType => new TMAssociationSet())
    }
  }

}
