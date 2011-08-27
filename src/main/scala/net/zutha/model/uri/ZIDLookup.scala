package net.zutha.model.uri {

import net.zutha.model.constructs.{ZID,Item}
import net.zutha.model.db.DB

object ZIDLookup {
  /**
   * extractor for a ZID
   * @return (wasRepaired,item)
   */
  def unapply(maybeZid: String): Option[(Boolean,Item)] = maybeZid match {
    case ZID(repairedZID) => DB.get.getItem(ZID(repairedZID)) match {
        case Some(item) => Some( (maybeZid!=repairedZID), item)
        case _ => None
    }
    case _ => None
  }
}
}
