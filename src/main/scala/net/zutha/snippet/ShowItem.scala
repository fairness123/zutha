package net.zutha
package snippet

import net.liftweb.common.{Full}
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S}
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.db.DB
import model.{ZID, Item}

class ShowItem {
  def render(content: NodeSeq): NodeSeq = {
    def display(item: Item) = {
      bind("item", content,
           "zid" -> Text(item.zid.toString),
           "name" -> Text(item.name),
           "param" -> Text(S.param("param").getOrElse("no value")))
    }
		
    S.attr("zid") match {
      case Full(zid) => zid match {
        case ZID(repairedZID) => DB.get.getItem(ZID(repairedZID)) match {
          case Some(item) => display(item)
          case _ => Text("item cannot be found")
        }
        case _ => Text("invalid item zid")
      }
      case _ => Text("no item zid was specified") //TODO:return 404 Not Found response
    }

  }

} //end of class
