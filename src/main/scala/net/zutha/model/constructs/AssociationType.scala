package net.zutha.model.constructs

import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

trait AssociationType extends Interface{
  def getAllSuperAssociationTypes: Set[AssociationType]
  def directDefinedRoles: Set[ZRole]
  def allDefinedRoles: Set[ZRole]
  def getRoleCardMin(role: ZRole): ZNonNegativeInteger
  def getRoleCardMax(role: ZRole): ZUnboundedNNI
}
