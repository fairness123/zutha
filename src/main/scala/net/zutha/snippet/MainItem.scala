package net.zutha
package snippet

import net.liftweb.common.{Box,Empty,Full}
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S}
import _root_.net.liftweb.util.Helpers
import Helpers._

class MainItem {
  def render(content: NodeSeq): NodeSeq = {
    def displayItem(zid:String) = {
        bind("main", content,
             AttrBindParam("zid",zid,"zid")
            )
    }

    //lookup Main Item data
    (S.param("zid"),S.param("name")) match {
      case (Full(zid),Full(name)) => {
        //validate id, name here

        displayItem(zid)
      }
      case _ => Text("invalid URL") //TODO:make better error handler
    }
  }

}
