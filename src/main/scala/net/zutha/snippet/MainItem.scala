package net.zutha {
package snippet {

import net.liftweb.common.{Box,Empty,Full}
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S}
import _root_.net.liftweb.util.Helpers
import Helpers._

class MainItem {
  def render(content: NodeSeq): NodeSeq = {
    def displayItem(id:String) = {
        bind("main", content,
             AttrBindParam("id",id,"id")
            )
    }

    //lookup Main Item data
    (S.param("id"),S.param("name")) match {
      case (Full(id),Full(name)) => {
        //validate id, name here

        displayItem(id)
      }
      //if name param not given, find it
      case (Full(id),Empty) => {
          //validate id, lookup name
          //if name is wrong, redirect

          displayItem(id)
      }
      case _ => Text("invalid URL") //TODO:make better error handler
    }
  }


} //end of class

}}
