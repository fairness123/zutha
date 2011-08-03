package net.zutha.lib

import net.liftweb._
import util._
import Helpers._
import net.liftweb.http.SHtml

class DynamicTypeProps extends DynamicFormElementSet[TypeProp]{
    def newProp = new TypeProp()
  }

class TypeProp() extends RemovableFormElement {
  var id = ""
  var name = ""

  override def render_fields = {
    "@name" #> SHtml.ajaxText(name,s => name = s) &
    "@id" #> SHtml.hidden(s => id = s, id)
  }
}
