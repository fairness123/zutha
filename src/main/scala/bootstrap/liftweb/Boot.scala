package bootstrap.liftweb

import net.liftweb._
import common._
import http._
import sitemap._
import net.zutha.model._
import uri.{UriRedirector, UriRewriter, ItemUri}

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("net.zutha")

    //perform request rewriting
    LiftRules.statelessRewrite.append {
      case UriRewriter(response) => response
    }

    //perform redirects
    LiftRules.dispatch.prepend {
      case UriRedirector(response) => () => Full(response)

    }

    // Use jQuery 1.4
    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}

