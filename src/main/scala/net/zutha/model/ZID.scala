package net.zutha.model

import net.zutha.lib.BaseX

class ZID(zid: String) {
  val correctZID = zid match{
    case ZID(newZID,_,_) => newZID
    case _ => throw new IllegalArgumentException
  }

  def next: ZID = {
    val nextIDstr = correctZID match {
      case ZID(_,hostID,identifier) => hostID + ZID.incrementIdentifier(identifier)
    }
    ZID(nextIDstr)
  }

  override def toString = correctZID
}

object ZID {
  private val ValidCharset = """\A([0-9A-Z][0-9A-Z]+)\z""".r
  private val charset = "0123456789ABCDEFGHJKLMNPQRTUVWXY"

  def apply(zid: String) = new ZID(zid)

  /*  */

  /**
   * corrects the syntax of a ZID and extracts hostID and identifier if it is valid
   * @param maybeZID string form of a ZID to try to resolve to a valid ZID
   * @return (correctedID, hostID, identifier)
   */
  def unapply(maybeZID: String): Option[(String, String, String)] = correctCharset(maybeZID) match {
    case ValidCharset(zid) => {
      val hostIdLen = charset.indexOf(zid(0))
      if(zid.length < hostIdLen + 2)
        None
      else {
        val hostID = zid.substring(0,hostIdLen)
        val identifier = zid.substring(hostIdLen+1)
        Some(zid,hostID,identifier)
      }
    }
    case _ => None
  }

  /**
   * extracts the string representation of a ZID object
   * @return (ZID_string)
   */
  def unapply(zid: ZID): Option[String] = Some(zid.correctZID)

  private def correctCharset(zid: String) = {
    zid.toUpperCase.replace('O', '0').replace('I','1').replace('S','5').replace('Z','2')
  }

  def incrementIdentifier(zidStr: String): String = {
    val converter = BaseX(charset,32)
    val idVal = converter.decode(zidStr)
    val newIdStr = converter.encode(idVal+1)
    newIdStr
  }


}
