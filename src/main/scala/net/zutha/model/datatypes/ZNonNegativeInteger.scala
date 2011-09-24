package net.zutha.model.datatypes

import javax.naming.directory.SchemaViolationException

object ZNonNegativeInteger extends DataType{
  def apply(value: String): Option[ZNonNegativeInteger] = try {
      val intVal = value.toInt
      if (intVal < 0) throw new SchemaViolationException("ZNonNegativeInteger properties cannot be negative.")
      Some(ZNonNegativeInteger(intVal))
    } catch {
      case _ => throw new SchemaViolationException("Invalid value: "+value+" for ZNonNegativeInteger property.")
    }
}
case class ZNonNegativeInteger(value: Int) extends PropertyValue
