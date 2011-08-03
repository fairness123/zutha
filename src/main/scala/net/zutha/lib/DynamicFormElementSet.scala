package net.zutha.lib

import net.liftweb._
import http._
import util._
import Helpers._
import js.jquery.JqJsCmds
import xml.{NodeSeq}
import collection.mutable.ListBuffer

  abstract class DynamicFormElementSet[T <: RemovableFormElement]{
    val props = new ListBuffer[T]

    val elements_id = nextFuncName

    def newProp: T

    private def appendElement: T = {
      val prop = newProp
      props += prop
      prop.remove = () => removeElement(prop)
      prop
    }

    def removeElement(prop: T) {
      props -= prop
    }

    def renderElements(ns: NodeSeq): NodeSeq = {
      //use the html in the element with name=element as a basis for rendering each property
      val prop_ns: NodeSeq = ("@element ^^" #> "")(ns)
      def sel =
        "@elements [id]" #> elements_id &
        "@element" #> props.map(p => p.render(prop_ns)) &
        //prop_ns will be used for the JsCmd that generates new property blocks
        "@addElement *" #> (add_ns => renderAddButton(prop_ns)(add_ns))
      sel(ns)
    }

    def renderAddButton(prop_ns: NodeSeq)(add_ns: NodeSeq): NodeSeq = {
      SHtml.ajaxButton(add_ns, () => {
        val theProp = appendElement
        JqJsCmds.AppendHtml(elements_id, theProp.render(prop_ns))
      })
    }
  }

  trait RemovableFormElement {
    var remove = () => ()

    def render_fields: CssSel

    def render: NodeSeq => NodeSeq = {
      val guid: String = nextFuncName
      "@element [id]" #> guid &
      "@remove" #> SHtml.ajaxButton("Remove", () => {
        remove()
        JqJsCmds.JqSetHtml(guid,Nil)
      }) &
      render_fields
    }
  }

