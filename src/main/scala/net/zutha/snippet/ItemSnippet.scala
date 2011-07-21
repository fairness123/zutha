package net.zutha {
package snippet {

import net.zutha.model.{DB,Item}
import net.liftweb.common.{Full}
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S}
import _root_.net.liftweb.util.Helpers
import Helpers._

class ItemSnippet {
  def render(content: NodeSeq): NodeSeq = {
    def display(item:Item) = {
      bind("item", content,
           "id" -> Text(item.id),
           "name" -> Text(item.name))
    }
    
    S.attr("id") match {
      case Full(id) => DB.getItem(id) match {
          case Some(item) => display(item)
          case _ => Text("item cannot be found")
      }
      case _ => Text("no item id was specified") //TODO:return 404 Not Found response
    }
    

  }


} //end of class

}}
