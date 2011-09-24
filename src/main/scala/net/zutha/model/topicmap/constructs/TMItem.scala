package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Helpers._
import net.zutha.model.constants.ZuthaConstants
import net.zutha.model.topicmap.db.TopicMapDB
import ZuthaConstants._
import net.zutha.model.constructs._
import net.zutha.model.db.DB.db
import de.topicmapslab.majortom.model.core.IScope
import net.zutha.model.exceptions.SchemaViolationException
import org.tmapi.core.{Name, Topic}
import net.zutha.model.datatypes.{PropertyValue, DataType}

object TMItem{
  val getItem = makeCache[Topic,String,TMItem](_.getId, topic => new TMItem(topic))
  def apply(topic: Topic):TMItem = getItem(topic)
}
class TMItem protected (topic: Topic) extends Item{
  val tm = topic.getTopicMap

  // -------------- Common method overrides --------------
  override def hashCode() = ("http://zutha.net/majortomTopic/" + topic.getId).hashCode()

  override def equals(obj:Any) = obj match {
    case item: Item => item.hashCode == hashCode
    case topic: Topic => topic.toItem.hashCode == hashCode
    case _ => false
  }

  // -------------- Conversion --------------
  def toTopic = topic
  def toItem: Item = this

  lazy val isZType = TopicMapDB.itemIsA(this,db.siTYPE)
  def toZType: ZType = TMType(topic)

  lazy val isItemType = TopicMapDB.itemIsA(this,db.siITEM_TYPE)
  def toItemType: ItemType = TMItemType(topic)

  lazy val isInterface = TopicMapDB.itemIsA(this,db.siINTERFACE)
  def toInterface: Interface = TMInterface(topic)

  lazy val isRole = TopicMapDB.itemIsA(this,db.siROLE)
  def toRole: ZRole = TMRole(topic)

  lazy val isAssociationType = TopicMapDB.itemIsA(this,db.siASSOCIATION_TYPE)
  def toAssociationType: AssociationType = TMAssociationType(topic)

  lazy val isPropertyType = TopicMapDB.itemIsA(this,db.siPROPERTY_TYPE)
  def toPropertyType: PropertyType = TMPropertyType(topic)

  // -------------- ZIDs --------------
  lazy val ZIDs: Set[String] = {
    val zids = topic.getSubjectIdentifiers.map(_.toExternalForm)
      .filter{_.startsWith(ZID_PREFIX.toString)}.map{_.replace(ZID_PREFIX.toString,"")}
      .toSet
    //every item must have at least one ZID
    if (zids.size == 0) throw new Exception("item has no ZIDs")

    try{
      zids.map{Zid(_).toString}
    } catch {
      case e: IllegalArgumentException => throw new Exception("item has an invalid ZID")
    }
  }
  def getZIDs = ZIDs

  def zid: String = getZIDs.toSeq.sorted.head

  def addZID(zid: Zid) = {
    val zidLoc = topic.getTopicMap.createLocator(ZID_PREFIX + zid)
    topic.addSubjectIdentifier(zidLoc)
  }

  // -------------- names --------------
  def names(scope: ZScope):Set[String] = topic.getNames(scope:IScope).toSet.map((_:Name).getValue)
  def names(scopeItems: Item*):Set[String] = names(TMScope(scopeItems.toSet))
  def allNames:Set[String] = topic.getNames.toSet.map((_:Name).getValue)
  def unconstrainedNames:Set[String] = names(TMScope())

  def name(scope: ZScope) = names(scope).headOption
  def name(scopeItems: Item*) = names(TMScope(scopeItems.toSet)).headOption
  def name = unconstrainedNames.headOption match {
    case Some(str) => str
    case None => throw new SchemaViolationException("item '" + this + "' has no name")
  }

  // -------------- Zuthanet Address --------------
  def address: String = {
    "/item/" + zid + "/" + name
  }
  
  // -------------- types --------------
  def hasType(zType: ZType): Boolean = getAllTypes.contains(zType)

  def getType = TopicMapDB.directTypesOfItem(this).toSet.head

  def getAllTypes = TopicMapDB.allTypesOfItem(this).toSet

  def getFieldDefiningTypes = {
    val fieldDefiningTypes = getAllTypes.filter(_.definesFields)
    fieldDefiningTypes
  }

  // -------------- fields --------------
  def getPropertySets = {
    getFieldDefiningTypes.flatMap{definingType =>
      definingType.getDefinedPropertyTypes
        .filterNot(_.isAbstract) //abstract propTypes do not have associated propSets
        .map(propType => TMPropertySet(this,propType,definingType))
    }
  }

  def getProperties(propType: PropertyType): Set[Property] = {
    //check if propType is a name
    if(propType.hasSuperType(db.siNAME)){ //no need to check if propType is zsi:name itself because zsi:name is abstract
      val names = topic.getNames(propType).map(_.toProperty).toSet
      return names
    }

    //TODO check if propType is ZID


    //propType is an occurrence-implemented property
    val occurrences = topic.getOccurrences(propType).map(_.toProperty).toSet
    occurrences
  }

  def getPropertyValues(propType: PropertyType): Set[PropertyValue] = {
    getProperties(propType).map(prop => prop.value)
  }

  def getProperty(propType: PropertyType) = getProperties(propType).headOption

  def getPropertyValue(propType: PropertyType): Option[PropertyValue] =
    getPropertyValues(propType).headOption


  def getAssociationFieldSets = {
    getFieldDefiningTypes.flatMap{definingType =>
      definingType.getDefinedAssociationFieldTypes.map{TMAssociationFieldSet(this,_)}
    }
  }

  def getAssociationFields(assocFieldType: AssociationFieldType) = {
    val rolesPlayed = topic.getRolesPlayed(assocFieldType.role, assocFieldType.associationType).toSet
    val visibleRolesPlayed = rolesPlayed.filterNot(_.getParent.isAnonymous)
    visibleRolesPlayed.map{r => TMAssociationField(r)}
  }
}
