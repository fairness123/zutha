package net.zutha.model.properties

import net.liftweb._
import util._
import Helpers._
import net.liftweb.http.SHtml

class TypeProps extends DynamicFormElementSet[TypeProp]{
    def newProp = new TypeProp()
  }

class TypeProp() extends RemovableFormElement {
  var typeZSI = ""

  override def render_fields = {
    "@name" #> SHtml.ajaxText(typeZSI,s => typeZSI = s)
  }
}
