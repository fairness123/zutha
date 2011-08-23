package net.zutha
package snippet

import _root_.scala.xml.{NodeSeq,Text}
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.db.DB
import model.{ZID, Item}
import net.liftweb.common._
import Box._
import net.liftweb.http.{SHtml, SnippetExecutionException, S}

class CurrentItem {
  def pageTitle(content: NodeSeq): NodeSeq = {
    val name = S.param("name") openOr "<no name>"
    val viewStr = S.attr("view") match {
      case Full(view) => " - " + view
      case _ => ""
    }
    Text("Zutha.net - "+ name +viewStr)
  }



  def summary: NodeSeq => NodeSeq = {
    def display(item: Item) = {
      //ZID
      ".zid *" #> item.zid &
      //Item Name
      "@name_selection" #> {ns =>
        val name_ns = ("#main_item_name ^^" #> "")(ns)
        (".name *" #> item.name) (name_ns)
      } &
      //Types
      ".types" #> makeItemLinkList(item.getDirectTypes)
    }

    display(getItem)
  }

  def props: NodeSeq => NodeSeq = {
    val item = getItem
    
    ".property_set *" #> {ns => ns}
  }

  private def getItem: Item = (for {
      zid <- S.param("zid") ?~ "no zid param was given"
      repairedZID <- ZID.repair(zid) ?~ "an invalid zid was provided"
      item <- DB.get.getItem(ZID(repairedZID)) ?~ "no item with the specifed zid can be found"
    } yield (item)) match{
      case Full(item) => item
      case Failure(msg, _, _) => throw new SnippetExecutionException(msg)
      case _ => throw new SnippetExecutionException("something went wrong looking up item by zid")
    }

  private def makeItemLinkList(items: Seq[Item]): NodeSeq => NodeSeq = {
    ".intermediate *" #> items.dropRight(1).map{
      t => ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))} &
    ".last *" #> {val t = items.last
      ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))}
  }
} //end of class
