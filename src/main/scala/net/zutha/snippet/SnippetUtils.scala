package net.zutha.snippet

import net.liftweb.util.Helpers._
import xml.NodeSeq
import net.zutha.model.constructs.ZItem
import net.zutha.lib.uri.ItemLoc


object SnippetUtils {
  private[snippet] def makeElemList(elems: Seq[NodeSeq]): NodeSeq => NodeSeq = {
    val intermediate = if(elems.isEmpty) elems else elems.dropRight(1)
    ".intermediate *" #> intermediate.map{elem =>
      ".listval" #> elem} &
    ".last *" #> elems.lastOption.map{elem =>
      ".listval" #> elem}
  }
  private[snippet] def makeItemLinkList(items: Seq[ZItem]) = {
    val elems = items.map(makeItemLink)
    makeElemList(elems)
  }
  private[snippet] def makeItemLink(item: ZItem) = <a href={ItemLoc.makeUri(item)}>{item.name}</a>

  def itemSummary(item: ZItem, mainItem: Boolean = true): NodeSeq => NodeSeq = {
    ".zid *" #> item.zid &
    //Item Name
    ".name_selection" #> {ns =>
      val name_ns = if(mainItem) ("#main_item_name ^^" #> "")(ns)
        else (".subitem_name ^^" #> "")(ns)
      ("a *" #> item.name &
        "a [href]" #> ItemLoc.makeUri(item)
      )(name_ns)
    } &
    //Types
    ".item_type" #> makeItemLink(item.getType) &
    ".traits" #> makeItemLinkList(Seq(item.getType)) //TODO: get Item Traits
  }

}
