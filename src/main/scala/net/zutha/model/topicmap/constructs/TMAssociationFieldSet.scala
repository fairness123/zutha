package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMAssociationFieldSet(definingType: ZType, parentItem: Item,
                            role: ZRole, associationType: AssociationType) extends AssociationFieldSet{

  def getAssociationFields = parentItem.getAssociationFields(role,associationType)
  def associationFieldType: AssociationFieldType = TMAssociationFieldType(role,associationType)
}
