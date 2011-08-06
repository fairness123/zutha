
package net.zutha.model.uri

import net.liftweb.common.{Box,Full,Empty}

object ItemUri {
  /*
   * make a URI of the form /item/<zid>/<name> from a zid
   */
  def apply(zid:String):Box[String] = zid match {
    case ZIDLookup(wasFixed,item) => Full("/item/"+item.zid+"/"+item.name)
    case _ => Empty
  }

  /*
   * extractor for a valid item default page uri
   * @return (wasFixed,repairedID,actualName)
   */
  def unapply(uri:List[String]):Option[(Boolean,String,String)] = uri match {
    case ItemUriStem(wasFixed,item,Nil) => Some(wasFixed,item.zid.toString,item.name)
    case ZIDLookup(wasFixed,item)::Nil => Some(true,item.zid.toString,item.name)
    case "item"::ZIDLookup(wasFixed,item)::Nil => Some(true,item.zid.toString,item.name)
    case _ => None
  }
}
