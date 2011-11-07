package net.zutha.model.constructs

trait ZItemType extends ZType {
  def getAllSuperItemTypes: Set[ZItemType]
  def compatibleTraits: Set[ZTrait]
}
