package net.zutha.model.datatypes

import javax.naming.directory.SchemaViolationException

object ZPermissionLevel extends DataType{
  def apply(value: String): ZPermissionLevel = value match {
    case "inherit" => Inherit
    case value => {
      try {
        val intVal = value.toInt
        apply(intVal)
      } catch {
        case _ => throw new SchemaViolationException("Invalid value: "+value+" for ZPermissionLevel property.")
      }
    }
  }
  def apply(value: Int): ZPermissionLevel = {
      if (value < 0) throw new SchemaViolationException("ZPermissionLevel properties cannot be negative.")
      else Specified(value)
  }
  def unapply(value: String): Option[ZPermissionLevel] = try {
      Some(apply(value))
    } catch {
      case _ => None
    }
  def unapply(propValue: PropertyValue): Option[ZPermissionLevel] = propValue match {
    case Inherit => Some(Inherit)
    case s@Specified(_) => Some(s)
    case _ => None
  }

  def default = ZPermissionLevel("")

  object Inherit extends ZPermissionLevel {
    def asString = "inherit"
  }
  case class Specified(value: Int) extends ZPermissionLevel {
    def asString = value.toString
  }
}

trait ZPermissionLevel extends PropertyValue

