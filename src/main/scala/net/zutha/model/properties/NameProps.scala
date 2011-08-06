package net.zutha.model.properties

import net.liftweb._
import util._
import Helpers._
import net.liftweb.http.SHtml

class NameProps extends DynamicFormElementSet[NameProp]{
    def newProp = new NameProp()
}

class NameProp() extends RemovableFormElement {
  var typeZSI = "Name"
  var value = ""
//  val scope = new ScopeProps()

  override def render_fields = {
    "@nameType" #> SHtml.ajaxText(typeZSI,s => typeZSI = s) &
    "@name" #> SHtml.ajaxText(value,s => value = s)
  }
}


