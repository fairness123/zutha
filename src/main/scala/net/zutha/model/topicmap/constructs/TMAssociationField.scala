package net.zutha.model.topicmap.constructs

import org.tmapi.core.{Role => TMAPIRole}
import net.zutha.model.constructs.{AssociationField}

case class TMAssociationField(tmapiRole: TMAPIRole) extends AssociationField{
  def parent: TMItem = TMItem(tmapiRole.getPlayer)
  def role: TMRole = TMRole(tmapiRole.getType)
  def association: TMAssociation =  TMAssociation(tmapiRole.getParent)
}