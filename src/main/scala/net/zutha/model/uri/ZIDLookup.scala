package net.zutha.model.uri {

import net.zutha.model.{ZID,Item}
import net.zutha.model.db.DB

object ZIDLookup {
  /**
   * extractor for a ZID
   * @return (wasFixed,item)
   */
  def unapply(zid: String): Option[(Boolean,Item)] = zid match {
    case ZID(repairedZID,_,_) => DB.getItem(repairedZID) match {
        case Some(item) => Some( (zid!=repairedZID), item)
        case _ => None
    }
    case _ => None
  }
}
}
