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

  def declarationAssociations: Set[ZAssociation] = {
    db.findAssociations(db.ASSOCIATION_FIELD_DECLARATION,false,
      db.ROLE.toRole -> role, db.ASSOCIATION_TYPE.toRole -> associationType)
  }

  //TODO make a rootDeclaringType method

  /**
   * @return the types that declare associationFields of this type
   */
  def declaringTypes: Set[ZType] = {
    declarationAssociations.map(_.getPlayers(db.ASSOCIATION_FIELD_DECLARER).head.toType)
  }

  def declarersNotOverriddenBy(types: Set[ZType]):Set[ZType] = {
    val nonOverriddenDeclarations = declarationAssociations.filter{decl =>
      val overridingDeclarers = decl.overriddenBy.map(_.getPlayers(db.ASSOCIATION_FIELD_DECLARER).head.toType)
      (overridingDeclarers intersect types) == 0
    }
    nonOverriddenDeclarations.map(_.getPlayers(db.ASSOCIATION_FIELD_DECLARER).head.toType)
  }

  def declarerForType(zType: ZType): Option[ZType]={
    val nonOverriddenDeclarers = declarersNotOverriddenBy(zType.getAllSuperTypes)
    nonOverriddenDeclarers.size match{
      case 0 => None
      case 1 => Some(nonOverriddenDeclarers.head)
      case _ => throw new SchemaViolationException(zType + " has more than one supertype with a non-overridden declaration of "+this)
    }
  }
  def declarerForItem(item: ZItem): Option[ZType]={
    val nonOverriddenDeclarers = declarersNotOverriddenBy(item.getAllTypes)
    nonOverriddenDeclarers.size match{
      case 0 => None
      case 1 => Some(nonOverriddenDeclarers.head)
      case _ => throw new SchemaViolationException(item + " has more than one type with a non-overridden declaration of "+this)
    }
  }

}
