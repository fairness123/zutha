package net.zutha.model.db

import net.zutha.model.constants.SchemaIdentifier._
import net.zutha.model.constructs._
import net.zutha.model.datatypes.DataType

//TODO use objects for every schema item

trait SchemaItems {
  protected def getSchemaItem(identifier: SchemaIdentifier): ZItem
  
  //kinds of item type
  lazy val siTYPE: ZItemType = getSchemaItem(TYPE).toItemType
  lazy val siITEM_TYPE: ZItemType = getSchemaItem(ITEM_TYPE).toItemType
  lazy val siINTERFACE: ZItemType = getSchemaItem(INTERFACE).toItemType

  //kinds of interface
  lazy val siFIELD_TYPE: ZInterface = getSchemaItem(FIELD_TYPE).toInterface
  lazy val siPROPERTY_TYPE: ZInterface = getSchemaItem(PROPERTY_TYPE).toInterface
  lazy val siASSOCIATION_TYPE: ZInterface = getSchemaItem(ASSOCIATION_TYPE).toInterface

  //item types
  lazy val siITEM: ZItemType = getSchemaItem(ITEM).toItemType
  lazy val siROLE: ZItemType = getSchemaItem(ROLE).toItemType
  lazy val siDATATYPE: ZItemType = getSchemaItem(DATATYPE).toItemType

  //association types
  lazy val siFIELD_DECLARATION: ZAssociationType = getSchemaItem(FIELD_DECLARATION).toAssociationType
  lazy val siPROPERTY_DECLARATION: ZAssociationType = getSchemaItem(PROPERTY_DECLARATION).toAssociationType
  lazy val siASSOCIATION_FIELD_DECLARATION: ZAssociationType = getSchemaItem(ASSOCIATION_FIELD_DECLARATION).toAssociationType
  lazy val siASSOCIATION_ROLE_CONSTRAINT: ZAssociationType = getSchemaItem(ASSOCIATION_ROLE_CONSTRAINT).toAssociationType
  lazy val siABSTRACT_CONSTRAINT: ZAssociationType = getSchemaItem(ABSTRACT_CONSTRAINT).toAssociationType
  lazy val siPROPERTY_DATATYPE_CONSTRAINT: ZAssociationType = getSchemaItem(PROPERTY_DATATYPE_CONSTRAINT).toAssociationType
  lazy val siOVERRIDES_DECLARATION: ZAssociationType = getSchemaItem(OVERRIDES_DECLARATION).toAssociationType

  //roles
  lazy val siFIELD_DECLARER: ZRole = getSchemaItem(FIELD_DECLARER).toRole
  lazy val siPROPERTY_DECLARER: ZRole = getSchemaItem(PROPERTY_DECLARER).toRole
  lazy val siASSOCIATION_FIELD_DECLARER: ZRole = getSchemaItem(ASSOCIATION_FIELD_DECLARER).toRole
  lazy val siOVERRIDING_DECLARATION: ZRole = getSchemaItem(OVERRIDING_DECLARATION).toRole
  lazy val siOVERRIDDEN_DECLARATION: ZRole = getSchemaItem(OVERRIDDEN_DECLARATION).toRole

  //property types
  lazy val siROLE_CARD_MIN: ZPropertyType = getSchemaItem(ROLE_CARD_MIN).toPropertyType
  lazy val siROLE_CARD_MAX: ZPropertyType = getSchemaItem(ROLE_CARD_MAX).toPropertyType
  lazy val siASSOCIATION_CARD_MIN: ZPropertyType = getSchemaItem(ASSOCIATION_CARD_MIN).toPropertyType
  lazy val siASSOCIATION_CARD_MAX: ZPropertyType = getSchemaItem(ASSOCIATION_CARD_MAX).toPropertyType

  lazy val siNAME: ZPropertyType = getSchemaItem(NAME).toPropertyType
  lazy val siMODIFIABLE_NAME: ZPropertyType = getSchemaItem(MODIFIABLE_NAME).toPropertyType
  lazy val siZID: ZPropertyType= getSchemaItem(ZID).toPropertyType

  //datatypes
  lazy val siNonNegativeInteger: ZItem = getSchemaItem(NON_NEGATIVE_INTEGER)
  lazy val siUnboundedNonNegativeInteger: ZItem = getSchemaItem(UNBOUNDED_NON_NEGATIVE_INTEGER)
}
