package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs.Property
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Helpers._
import org.tmapi.core.{Topic, Name, Occurrence}

abstract class TMProperty extends Property {
  def toProperty: Property = this
}

object TMOccurrenceProperty{
  val getItem = makeCache[Occurrence,String,TMOccurrenceProperty](_.getId, occ => new TMOccurrenceProperty(occ))
  def apply(occ: Occurrence):TMOccurrenceProperty = getItem(occ)
}
class TMOccurrenceProperty(occ: Occurrence) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = occ.getType.toItemType

  def value = occ.getValue

  def datatype = occ.getDatatype.toExternalForm
}

object TMNameProperty{
  val getItem = makeCache[Name,String,TMNameProperty](_.getId, name => new TMNameProperty(name))
  def apply(name: Name):TMNameProperty = getItem(name)
}
class TMNameProperty protected (name: Name) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = name.getType.toItemType

  def value = name.getValue

  def datatype = "http://www.w3.org/2001/XMLSchema#string"
}
