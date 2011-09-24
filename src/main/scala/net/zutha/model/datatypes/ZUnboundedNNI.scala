package net.zutha.model.datatypes

import javax.naming.directory.SchemaViolationException

object ZUnboundedNNI extends DataType{
  def apply(value: String): Option[ZUnboundedNNI] = value match {
    case "*" => Some(Infinity)
    case value => {
      try {
        val intVal = value.toInt
        if (intVal < 0) throw new SchemaViolationException("ZUnboundedNNI properties cannot be negative.")
        Some(Finite(intVal))
      } catch {
        case _ => throw new SchemaViolationException("Invalid value: "+value+" for ZUnboundedNNI property.")
      }

    }
  }
}
trait ZUnboundedNNI extends PropertyValue

object Infinity extends ZUnboundedNNI
case class Finite(value: Int) extends ZUnboundedNNI
