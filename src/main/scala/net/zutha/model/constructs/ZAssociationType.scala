package net.zutha.model.constructs

import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

object ZAssociationType{
  def apply(item: ZItem): ZAssociationType = item.toAssociationType
  def unapply(item: ZItem): Option[ZAssociationType] =
    if(item.isAssociationType) Some(item.toAssociationType) else None
}
trait ZAssociationType extends ZInterface{
  def getAllSuperAssociationTypes: Set[ZAssociationType]
  def getDirectAssocRoleConstraints: Set[ZAssociation]
  def getAssocRoleConstraints: Set[ZAssociation]
  def getDirectDefinedRoles: Set[ZRole]
  def getAllDefinedRoles: Set[ZRole]
  def getRoleCardMin(role: ZRole): ZNonNegativeInteger
  def getRoleCardMax(role: ZRole): ZUnboundedNNI
}
