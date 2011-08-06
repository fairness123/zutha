package net.zutha
package snippet

import net.zutha.model.{Item}
import net.liftweb.common.{Full}
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S}
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.db.DB

class ShowItem {
  def render(content: NodeSeq): NodeSeq = {
    def display(item:Item) = {
      bind("item", content,
           "zid" -> Text(item.zid.toString),
           "name" -> Text(item.name))
    }
		
    S.attr("zid") match {
      case Full(zid) => DB.getItem(zid) match {
          case Some(item) => display(item)
          case _ => Text("item cannot be found")
      }
      case _ => Text("no item zid was specified") //TODO:return 404 Not Found response
    }

  }

} //end of class
