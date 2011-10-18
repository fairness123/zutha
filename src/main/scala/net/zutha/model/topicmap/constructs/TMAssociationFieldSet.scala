package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMAssociationFieldSet(parentItem: ZItem, associationFieldType: ZAssociationFieldType) extends ZAssociationFieldSet{

  def definingType: ZType = associationFieldType.definingType
  def role: ZRole = associationFieldType.role
  def associationType: ZAssociationType = associationFieldType.associationType
  def associationFields = parentItem.getAssociationFields(associationFieldType)

  def cardMin = associationFieldType.cardMin
  def cardMax = associationFieldType.cardMax
}
