package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs.Property
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Helpers._
import org.tmapi.core.{Name, Occurrence}
import net.zutha.model.exceptions.SchemaViolationException

abstract class TMProperty extends Property {

  def toProperty: Property = this
  def dataTypeItem = propertyType.dataTypeItem
  def dataType = propertyType.dataType
  def value = dataType(valueString).getOrElse(throw new SchemaViolationException("property: "+this+" has illegal value: "+valueString))
}

object TMOccurrenceProperty{
  val getItem = makeCache[Occurrence,String,TMOccurrenceProperty](_.getId, occ => new TMOccurrenceProperty(occ))
  def apply(occ: Occurrence):TMOccurrenceProperty = getItem(occ)
}
class TMOccurrenceProperty(occ: Occurrence) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = occ.getType.toPropertyType

  def valueString = occ.getValue
}

object TMNameProperty{
  val getItem = makeCache[Name,String,TMNameProperty](_.getId, name => new TMNameProperty(name))
  def apply(name: Name):TMNameProperty = getItem(name)
}
class TMNameProperty protected (name: Name) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = name.getType.toPropertyType

  def valueString = name.getValue
}