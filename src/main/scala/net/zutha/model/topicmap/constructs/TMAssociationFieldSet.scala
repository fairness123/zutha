package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._
import net.zutha.model.db.DB.db
import net.zutha.model.exceptions.SchemaViolationException
import net.zutha.model.datatypes.{ZUnboundedNNI, ZNonNegativeInteger}

case class TMAssociationFieldSet(parentItem: ZItem, definingType: ZType,
                                 associationFieldType: ZAssociationFieldType) extends ZAssociationFieldSet{

  def role: ZRole = associationFieldType.role
  def associationType: ZAssociationType = associationFieldType.associationType
  def associationFields = parentItem.getAssociationFields(associationFieldType)
  def isEmpty = associationFields.isEmpty

  lazy val declarationAssociation = db.findAssociations(db.siASSOCIATION_FIELD_DECLARATION,true,
      db.siASSOCIATION_FIELD_DECLARER -> definingType,
      db.siROLE.toRole -> role,
      db.siASSOCIATION_TYPE.toRole -> associationType
    ).headOption.getOrElse(
      throw new SchemaViolationException("Association Field Type missing association-field-declaration"))

  def cardMin = declarationAssociation.getPropertyValue(db.siASSOCIATION_CARD_MIN).getOrElse(
    throw new SchemaViolationException("association-field-declaration associations must have an association-card-min property")) match {
    case value: ZNonNegativeInteger => value
    case _ => throw new SchemaViolationException("card-min properties must have datatype: ZNonNegativeInteger")
  }

  def cardMax = declarationAssociation.getPropertyValue(db.siASSOCIATION_CARD_MAX).getOrElse(
    throw new SchemaViolationException("association-field-declaration associations must have an association-card-max property")) match {
    case value: ZUnboundedNNI => value
    case _ => throw new SchemaViolationException("card-mx properties must have datatype: ZUnboundedNNI")
  }
}
