package net.zutha.lib

import net.liftweb._
import util._
import Helpers._
import net.liftweb.http.SHtml

class DynamicSubjectIndicatorProps extends DynamicFormElementSet[SubjectIndicatorProp]{
  def newProp = new SubjectIndicatorProp()
}

  //container class to store Subject Indicators
  class SubjectIndicatorProp() extends RemovableFormElement {
    var uri = ""

    override def render_fields = {
      "@uri" #> SHtml.ajaxText(uri,s => uri = s)
    }
  }
