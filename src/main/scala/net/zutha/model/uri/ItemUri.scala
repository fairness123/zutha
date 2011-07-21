
package net.zutha.model.uri

import net.liftweb.common.{Box,Full,Empty}

object ItemUri {
  /*
   * make a URI of the form /item/<id>/<name> from an id
   */
  def apply(id:String):Box[String] = id match {
    case IDLookup(wasFixed,item) => Full("/item/"+item.id+"/"+item.name)
    case _ => Empty
  }

  /*
   * extractor for a valid item default page uri
   * @return (wasFixed,repairedID,actualName)
   */
  def unapply(uri:List[String]):Option[(Boolean,String,String)] = uri match {
    case ItemUriStem(wasFixed,item,Nil) => Some(wasFixed,item.id,item.name)
    case IDLookup(wasFixed,item)::Nil => Some(true,item.id,item.name)
    case "item"::IDLookup(wasFixed,item)::Nil => Some(true,item.id,item.name)
    case _ => None
  }
}
