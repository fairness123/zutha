package net.zutha.model

class ID(id: String) {
  val correctID = id match{
    case ID(newID) => newID
    case _ => throw new IllegalArgumentException
  }
}

object ID {
  private val ValidID = """\A([0-9][0-9A-Z]+)\z""".r
  private val charset = "0123456789ABCDEFGHJKLMNPQRTUVWXY"

  def apply(id: String) = new ID(id)
  def unapply(id: String): Option[String] = id.toUpperCase match {
      case ValidID(upperID) => Some(upperID.replace('O', '0').replace('I','1').replace('S','5').replace('Z','2'))
      case _ => None
  }
  def unapply(id: ID): Option[String] = Some(id.correctID)
}
