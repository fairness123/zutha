package net.zutha.model.user

import org.openid4java.discovery.Identifier
import net.liftweb.openid.{OpenIDUser, OpenIDConsumer, OpenIDVendor}
import net.liftweb.http.S
import org.openid4java.consumer.VerificationResult
import net.liftweb.common.{Full, Box}
import xml.Text._
import xml.{Text, NodeSeq}


object ZuthaOpenIdVendor extends ZuthaOpenIdVendor

trait ZuthaOpenIdVendor extends OpenIDVendor {
  type UserType = Identifier
  type ConsumerType = OpenIDConsumer[UserType]

  def currentUser = OpenIDUser.is

  /**
   * Generate a welcome message for the OpenID identifier
   */
  protected def generateWelcomeMessage(id: Identifier): String =
    (S ? "Welcome")+ ": "+ id

  /**
   * If verification failed, generate a polite message to that
   * effect.
   */
  protected def generateAuthenticationFailure(res: VerificationResult): String =
    S ? "Failed to authenticate"

  def postLogin(id: Box[Identifier],res: VerificationResult): Unit = {
    id match {
      case Full(id) => S.notice(generateWelcomeMessage(id))

      case _ => S.error(generateAuthenticationFailure(res))
    }

    OpenIDUser(id)
  }

  def logoutUrl = "/"+PathRoot+"/"+LogOutPath
  
  def logUserOut() {
    OpenIDUser.remove
  }

  /**
   * Generate a welcome message.
   */
  def displayUser(in: UserType): NodeSeq = Text("Welcome "+in)

  def createAConsumer = new ZuthaOpenIdConsumer[UserType]
}
