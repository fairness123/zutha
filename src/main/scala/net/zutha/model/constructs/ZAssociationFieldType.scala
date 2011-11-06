package net.zutha.model.constructs

import net.zutha.model.exceptions.SchemaViolationException
import net.zutha.model.datatypes.{ZUnboundedNNI}
import ZUnboundedNNI.{Finite,Infinity}
import net.zutha.model.db.DB.db

case class ZAssociationFieldType(role:ZRole, associationType:ZAssociationType){

  def companionAssociationFieldTypes = otherRoles.map(r => ZAssociationFieldType(r,associationType))

  /**
   * @return a Set containing the other Roles that can be played in this field's Association.
   * If this field's Role can be played more than once, that Role will be included in the result Set
   */
  def otherRoles: Set[ZRole] = {
    val allRoles = associationType.definedRoles
    associationType.getRoleCardMax(role) match {
      case Finite(0) => throw new SchemaViolationException("Association Field uses Role that is not allowed by Association Type")
      case Finite(1) => allRoles - role
      case Finite(_) => allRoles
      case Infinity => allRoles
    }
  }

  /**
   * @return the types that declare associationFields of this type
   */
  def declaringTypes: Set[ZType] = {
    val declarations = db.findAssociations(db.ASSOCIATION_FIELD_DECLARATION,true,
      db.ROLE.toRole -> role, db.ASSOCIATION_TYPE.toRole -> associationType)
    declarations.map(_.getPlayers(db.ASSOCIATION_FIELD_DECLARER).head.toType)
  }
}
