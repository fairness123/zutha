package net.zutha.model.builder

import net.zutha.model.constructs._


class AssociationFieldSetBuilder(val parent: ItemBuilder, val assocFieldSetType:ZAssociationFieldSetType) {
  val assocFieldType = assocFieldSetType.associationFieldType
  private var _associationFields: Set[AssociationFieldBuilder] = Set()

  /**
   * @return Some(new assocField) if a new one is allowed
   *  or None if this AssocFieldSet is not allowed to have more members
   */
  def addAssociationField: Option[AssociationFieldBuilder] = {
    if(_associationFields.size < assocFieldSetType.cardMax){
      val newBuilder = new AssociationFieldBuilder(parent,assocFieldType)
      _associationFields += newBuilder
      Some(newBuilder)
    }
    else None
  }
  /**
   * Removes the given AssocField from this AssocFieldSet
   * @returns true if it was removed successfully or if it was not found
   *  and false if this AssocFieldSet is not allowed to have less members
   */
  def removeAssociationField(toRemove:AssociationFieldBuilder):Boolean = {
    if(_associationFields.size > assocFieldSetType.cardMin){
      _associationFields -= toRemove
      true
    }
    else false
  }
}
