package net.zutha.model.uri {

import net.zutha.model.constructs.{Zid,Item}
import net.zutha.model.db.DB

object ZIDLookup {
  /**
   * extractor for a ZID
   * @return (wasRepaired,item)
   */
  def unapply(maybeZid: String): Option[(Boolean,Item)] = maybeZid match {
    case Zid(repairedZID) => DB.db.getItem(Zid(repairedZID)) match {
        case Some(item) => Some( (maybeZid!=repairedZID), item)
        case _ => None
    }
    case _ => None
  }
}
}
