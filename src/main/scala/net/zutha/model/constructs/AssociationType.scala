package net.zutha.model.constructs

import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

trait AssociationType extends Interface{
  def getAllSuperAssociationTypes: Set[AssociationType]
  def getDirectAssocRoleConstraints: Set[ZAssociation]
  def getAssocRoleConstraints: Set[ZAssociation]
  def getDirectDefinedRoles: Set[ZRole]
  def getAllDefinedRoles: Set[ZRole]
  def getRoleCardMin(role: ZRole): ZNonNegativeInteger
  def getRoleCardMax(role: ZRole): ZUnboundedNNI
}
