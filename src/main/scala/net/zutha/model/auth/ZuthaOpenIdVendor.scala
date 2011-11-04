package net.zutha.model.auth

import org.openid4java.discovery.Identifier
import net.liftweb.openid.{OpenIDVendor}
import org.openid4java.consumer.VerificationResult
import xml.{Text, NodeSeq}
import net.liftweb.http.{SessionVar, S}
import net.liftweb.common.{Empty, Full, Box}
import net.zutha.model.item.ZuthaIdentity


object ZuthaOpenIdVendor extends ZuthaOpenIdVendor

object CurrentUserVar extends SessionVar[Box[ZuthaIdentity]](Empty)

trait ZuthaOpenIdVendor extends OpenIDVendor {
  type UserType = ZuthaIdentity
  type ConsumerType = ZuthaOpenIdConsumer[UserType]

  def currentUser = CurrentUserVar.is

  /**
   * If verification failed, generate a polite message to that
   * effect.
   */
  protected def generateAuthenticationFailure(res: VerificationResult): String =
    S ? "Failed to authenticate"

  def postLogin(id: Box[Identifier],res: VerificationResult): Unit = {
    id match {
      case Full(id) => {
        val zuthaIdentityItem = Users(id.getIdentifier)
        CurrentUserVar(Full(zuthaIdentityItem))
      }

      case _ => {
        CurrentUserVar(Empty)
        S.error(generateAuthenticationFailure(res))
      }
    }

  }

  def logoutUrl = "/"+PathRoot+"/"+LogOutPath
  
  def logUserOut() {
    CurrentUserVar.remove
  }

  /**
   * Generate a welcome message.
   */
  def displayUser(in: UserType): NodeSeq = Text("Welcome "+in)

  def createAConsumer = new ZuthaOpenIdConsumer[UserType]
}
