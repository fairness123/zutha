package net.zutha.snippet

import net.liftweb.util.Helpers._
import net.zutha.model.constructs.ZItem
import xml.{Text, NodeSeq}
import net.zutha.lib.uri.{ItemLoc, RoleInfo, ItemInfo}

class RolePage(roleInfo: RoleInfo) {

  private val item = roleInfo.item
  private val assocType = roleInfo.assoc
  private val role = roleInfo.role
  private val otherRole = roleInfo.otherRole

  def title(content: NodeSeq): NodeSeq = {
    val viewStr = roleInfo.view match {
      case "" => ""
      case view => " :: " + view
    }
    Text("Zutha.net - " + item.name + " :: " + assocType.nameF(role)
      + " :: " + otherRole.name + viewStr)
  }

  def summary = SnippetUtils.itemSummary(item)

  def roleListName = {
    ".role *" #> role.name &
    ".role [href]" #> ItemLoc.makeUri(role) &
    ".assoc_type *" #> assocType.nameF(role) &
    ".assoc_type [href]" #> ItemLoc.makeUri(assocType) &
    ".other_role *" #> otherRole.name &
    ".other_role [href]" #> ItemLoc.makeUri(otherRole)
  }




  private def getRolePlayers: Seq[ZItem] = {
    val players = item.getAssociationFields(role,assocType).flatMap{_.getPlayers(otherRole)}
      .toSeq.sortBy(_.name) //TODO sort by worth
    players
  }
  private def renderRolePlayer(player: ZItem) = SnippetUtils.itemSummary(player,false)

  def rolePlayers =
    ".role_player *" #> getRolePlayers.map(renderRolePlayer(_))
}
