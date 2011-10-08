package net.zutha.model.db

import net.zutha.model.constants.SchemaIdentifier._
import net.zutha.model.constructs._
import net.zutha.model.datatypes.DataType

//TODO use objects for every schema item

trait SchemaItems {
  protected def getSchemaItem(identifier: SchemaIdentifier): Item
  
  //kinds of item type
  lazy val siTYPE: ItemType = getSchemaItem(TYPE).toItemType
  lazy val siITEM_TYPE: ItemType = getSchemaItem(ITEM_TYPE).toItemType
  lazy val siINTERFACE: ItemType = getSchemaItem(INTERFACE).toItemType

  //kinds of interface
  lazy val siFIELD_TYPE: Interface = getSchemaItem(FIELD_TYPE).toInterface
  lazy val siPROPERTY_TYPE: Interface = getSchemaItem(PROPERTY_TYPE).toInterface
  lazy val siASSOCIATION_TYPE: Interface = getSchemaItem(ASSOCIATION_TYPE).toInterface

  //item types
  lazy val siITEM: ItemType = getSchemaItem(ITEM).toItemType
  lazy val siROLE: ItemType = getSchemaItem(ROLE).toItemType
  lazy val siDATATYPE: ItemType = getSchemaItem(DATATYPE).toItemType

  //association types
  lazy val siFIELD_DECLARATION: AssociationType = getSchemaItem(FIELD_DECLARATION).toAssociationType
  lazy val siPROPERTY_DECLARATION: AssociationType = getSchemaItem(PROPERTY_DECLARATION).toAssociationType
  lazy val siASSOCIATION_FIELD_DECLARATION: AssociationType = getSchemaItem(ASSOCIATION_FIELD_DECLARATION).toAssociationType
  lazy val siASSOCIATION_ROLE_CONSTRAINT: AssociationType = getSchemaItem(ASSOCIATION_ROLE_CONSTRAINT).toAssociationType
  lazy val siABSTRACT_CONSTRAINT: AssociationType = getSchemaItem(ABSTRACT_CONSTRAINT).toAssociationType
  lazy val siPROPERTY_DATATYPE_CONSTRAINT: AssociationType = getSchemaItem(PROPERTY_DATATYPE_CONSTRAINT).toAssociationType
  lazy val siOVERRIDES_DECLARATION: AssociationType = getSchemaItem(OVERRIDES_DECLARATION).toAssociationType

  //roles
  lazy val siFIELD_DECLARER: ZRole = getSchemaItem(FIELD_DECLARER).toRole
  lazy val siPROPERTY_DECLARER: ZRole = getSchemaItem(PROPERTY_DECLARER).toRole
  lazy val siASSOCIATION_FIELD_DECLARER: ZRole = getSchemaItem(ASSOCIATION_FIELD_DECLARER).toRole
  lazy val siOVERRIDING_DECLARATION: ZRole = getSchemaItem(OVERRIDING_DECLARATION).toRole
  lazy val siOVERRIDDEN_DECLARATION: ZRole = getSchemaItem(OVERRIDDEN_DECLARATION).toRole

  //property types
  lazy val siROLE_CARD_MIN: PropertyType = getSchemaItem(ROLE_CARD_MIN).toPropertyType
  lazy val siROLE_CARD_MAX: PropertyType = getSchemaItem(ROLE_CARD_MAX).toPropertyType
  lazy val siASSOCIATION_CARD_MIN: PropertyType = getSchemaItem(ASSOCIATION_CARD_MIN).toPropertyType
  lazy val siASSOCIATION_CARD_MAX: PropertyType = getSchemaItem(ASSOCIATION_CARD_MAX).toPropertyType

  lazy val siNAME: PropertyType = getSchemaItem(NAME).toPropertyType
  lazy val siMODIFIABLE_NAME: PropertyType = getSchemaItem(MODIFIABLE_NAME).toPropertyType
  lazy val siZID: PropertyType= getSchemaItem(ZID).toPropertyType

  //datatypes
  lazy val siNonNegativeInteger: Item = getSchemaItem(NON_NEGATIVE_INTEGER)
  lazy val siUnboundedNonNegativeInteger: Item = getSchemaItem(UNBOUNDED_NON_NEGATIVE_INTEGER)
}
