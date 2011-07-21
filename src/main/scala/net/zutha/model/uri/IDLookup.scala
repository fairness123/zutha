package net.zutha.model.uri {

import net.zutha.model.{ID,DB,Item}

object IDLookup {
  /**
   * extractor for an ID
   * @return (wasFixed,item)
   */
  def unapply(id: String): Option[(Boolean,Item)] = id match {
    case ID(repairedID) => DB.getItem(repairedID) match {
        case Some(item) => Some( (id!=repairedID), item)
        case _ => None
    }
    case _ => None
  }
}
}