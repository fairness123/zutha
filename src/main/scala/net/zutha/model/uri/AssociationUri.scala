
package net.zutha.model.uri

object AssociationUri {

  /*
   * extractor for a valid association page uri
   * @return (wasFixed,repairedID,actualName,repairedAssociation)
   */
  def unapply(uri:List[String]):Option[(Boolean,String,String,String)] = uri match {
    case ItemUriStem(wasFixed,item, association::Nil) => {
        Some(wasFixed, item.zid.toString, item.name, association)
    }
    case _ => None
  }
}
