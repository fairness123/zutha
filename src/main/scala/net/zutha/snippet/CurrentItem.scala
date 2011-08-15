package net.zutha
package snippet

import _root_.scala.xml.{NodeSeq,Text}
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.db.DB
import model.{ZID, Item}
import net.liftweb.common._
import Box._
import net.liftweb.http.{SnippetExecutionException, S}

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
      ".zid *" #> item.zid &
      //"@name_selection" #> ("#main_item_name ^^" #> (".name *" #> item.name))
      "@name_selection" #> {ns =>
        val name_ns = ("#main_item_name ^^" #> "")(ns)
        (".name *" #> item.name) (name_ns)
      }

    }


    (for {
      zid <- S.param("zid") ?~ "no zid param was given"
      repairedZID <- ZID.repair(zid) ?~ "an invalid zid was provided"
      item <- DB.get.getItem(ZID(repairedZID)) ?~ "no item with the specifed zid can be found"
    } yield (item)) match{
      case Full(item) => display(item)
      case Failure(msg, _, _) => throw new SnippetExecutionException(msg)
      case _ => throw new SnippetExecutionException("something went wrong looking up item by zid")
    }
  }

  def props(content: NodeSeq): NodeSeq = {
    content
  }

} //end of class
