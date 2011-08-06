package net.zutha.model.uri

import net.zutha.model.{Item}

object ItemUriStem {
  /*
   * extractor for the prefix of a uri that identifies the focus item
   * @return (wasFixed,item,remainingUri)
   */
  def unapply(uri:List[String]):Option[(Boolean,Item,List[String])] = uri match {
    case "item"::ZIDLookup(wasFixed,item)::name::tail => {
        Some( (wasFixed || name!=item.name), item, tail)
    }
    case _ => None
  }
}
