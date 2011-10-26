package net.zutha.snippet

import xml.NodeSeq
import net.liftweb._
import common.{Empty, Full}
import http._
import openid.OpenIDUser
import util._
import Helpers._
import net.zutha.model.user.ZuthaOpenIdVendor

class Login {

  def loginLink: NodeSeq => NodeSeq = {
    OpenIDUser.is match {
      case Full(id) => ".currentUser *" #> id.getIdentifier &
          ".currentUser [href]" #> "" & //TODO get URL of current User's Item
          ".logout [href]" #> ZuthaOpenIdVendor.logoutUrl
      case _ => "#loginStatus *" #> <span onclick="$('#loginCurtain').show();$('#loginLayer').show()" style="cursor: pointer;">Login</span>
    }
  }

  def loginCloseButton: NodeSeq => NodeSeq = {
    ".loginClose [onclick]" #> "$('#loginCurtain').hide();$('#loginLayer').hide()"
  }
}
