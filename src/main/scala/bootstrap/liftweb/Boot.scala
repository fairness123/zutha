package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._

import net.zutha.model._
import net.zutha.model.uri.ItemUri

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("net.zutha")

    LiftRules.statelessRewrite.append {
      //rewrite: /item/09H5/Item_Name  =>  /details?zid=09H5&name=Item_Name
      case RewriteRequest(
        ParsePath(ItemUri(wasFixed,repairedID,actualName),_,_,_),_,_) if (!wasFixed) => {
          RewriteResponse("details"::Nil, Map("zid" -> repairedID, "name" -> actualName))
        }
    }

    LiftRules.dispatch.prepend {
      //redirect: /0IZ => /item/012/Looked_Up_Name
      //redirect: /item/0IZ => /item/012/Looked_Up_Name
      //redirect: /item/0IZ/wrong_name => /item/012/Corrected_Name
      case r @ Req(ItemUri(wasFixed,repairedID,actualName),_,_) if wasFixed => () => Full(
          PermRedirectResponse(ItemUri(repairedID).open_!,r))
    }
    
    // Build SiteMap
    def siteMap = SiteMap(
      Menu("Home") / "index",
      Menu("details") / "details",
      Menu("Zutha Console") / "zutha_console",
      Menu("Create Item") / "create_item"
    )
    LiftRules.setSiteMap(siteMap)
    
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

