package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs.ZProperty
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Cache._
import org.tmapi.core.{Name, Occurrence}
import net.zutha.model.exceptions.SchemaViolationException

abstract class TMProperty extends ZProperty {

  def toProperty: ZProperty = this
  def dataTypeItem = propertyType.dataTypeItem
  val dataType = propertyType.dataType
  def value = valueString match {
    case dataType(propValue) => propValue
    case _ => throw new SchemaViolationException("property: "+this+" has illegal value: "+valueString)
  }
}

object TMOccurrenceProperty{
  val getItem = makeCache[Occurrence,String,TMOccurrenceProperty](_.getId, occ => new TMOccurrenceProperty(occ))
  def apply(occ: Occurrence):TMOccurrenceProperty = getItem(occ)
}
class TMOccurrenceProperty(occ: Occurrence) extends TMProperty {
  override def toProperty: ZProperty = this

  def propertyType = occ.getType.toPropertyType

  def valueString = occ.getValue

  def parent = occ.getParent.toItem
}

object TMNameProperty{
  val getItem = makeCache[Name,String,TMNameProperty](_.getId, name => new TMNameProperty(name))
  def apply(name: Name):TMNameProperty = getItem(name)
}
class TMNameProperty protected (name: Name) extends TMProperty {
  override def toProperty: ZProperty = this

  def propertyType = name.getType.toPropertyType

  def valueString = name.getValue

  def parent = name.getParent.toItem
}

