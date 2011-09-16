package net.zutha.model.constructs

import net.zutha.model.exceptions.SchemaViolationException

trait AssociationFieldType{
  def role: ZRole
  def associationType: AssociationType

  /**
   * @return a Set containing the other Roles that can be played in this field's Association.
   * If this field's Role can be played more than once, that Role will be included in the result Set
   */
  def otherRoles: Set[ZRole] = {
    val allRoles = associationType.getDefinedRoles
    associationType.getRoleMaxCardinality(role) match {
      case 0 => throw new SchemaViolationException("Association Field uses Role that is not allowed by Association")
      case 1 => allRoles - role
      case _ => allRoles
    }
  }
}
