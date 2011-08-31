package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs.Property
import org.tmapi.core.{Name, Occurrence}
import net.zutha.model.topicmap.TMConversions._

abstract class TMProperty extends Property {
  def toProperty: Property = this
}

class TMOccurrenceProperty(occ: Occurrence) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = occ.getType.toTMItemType

  def value = occ.getValue

  def datatype = occ.getDatatype.toExternalForm
}

class TMNameProperty(name: Name) extends TMProperty {
  override def toProperty: Property = this

  def propertyType = name.getType.toTMItemType

  def value = name.getValue

  def datatype = "http://www.w3.org/2001/XMLSchema#string"
}
