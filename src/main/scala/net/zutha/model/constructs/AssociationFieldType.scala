package net.zutha.model.constructs

import net.zutha.model.exceptions.SchemaViolationException
import net.zutha.model.datatypes.{ZNonNegativeInteger, Infinity, Finite, ZUnboundedNNI}

trait AssociationFieldType{
  def definingType: ZType
  def role: ZRole
  def associationType: AssociationType

  /**
   * @return a Set containing the other Roles that can be played in this field's Association.
   * If this field's Role can be played more than once, that Role will be included in the result Set
   */
  def otherRoles: Set[ZRole] = {
    val allRoles = associationType.getAllDefinedRoles
    associationType.getRoleCardMax(role) match {
      case Finite(0) => throw new SchemaViolationException("Association Field uses Role that is not allowed by Association Type")
      case Finite(1) => allRoles - role
      case Finite(_) => allRoles
      case Infinity => allRoles
    }
  }

  def companionAssociationFieldTypes: Set[AssociationFieldType]

  def declarationAssociation: ZAssociation
  def cardMin: ZNonNegativeInteger
  def cardMax: ZUnboundedNNI
}
