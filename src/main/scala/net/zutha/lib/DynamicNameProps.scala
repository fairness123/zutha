package net.zutha.lib

import net.liftweb._
import util._
import Helpers._
import net.liftweb.http.SHtml

class DynamicNameProps extends DynamicFormElementSet[NameProp]{
    def newProp = new NameProp()
}

class NameProp() extends RemovableFormElement {
  var nameType = "Name"
  var name = ""
  var scope = ""

  override def render_fields = {
    "@nameType" #> SHtml.ajaxText(nameType,s => nameType = s) &
    "@name" #> SHtml.ajaxText(name,s => name = s) &
    "@scope" #> SHtml.ajaxText(scope,s => scope = s)
  }
}


