package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMAssociationFieldSet(parentItem: Item, associationFieldType: AssociationFieldType) extends AssociationFieldSet{

  def definingType: ZType = associationFieldType.definingType
  def role: ZRole = associationFieldType.role
  def associationType: AssociationType = associationFieldType.associationType
  def associationFields = parentItem.getAssociationFields(associationFieldType)

  def cardMin = associationFieldType.cardMin
  def cardMax = associationFieldType.cardMax
}
