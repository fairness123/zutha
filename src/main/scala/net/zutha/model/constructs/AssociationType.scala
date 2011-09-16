package net.zutha.model.constructs


trait AssociationType extends Interface{
  def getDirectDefinedRoles: Set[ZRole]
  def getDefinedRoles: Set[ZRole]
  def getRoleMinCardinality(role: ZRole): Int
  def getRoleMaxCardinality(role: ZRole): Int
}
