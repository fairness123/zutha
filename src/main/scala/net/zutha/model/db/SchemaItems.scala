package net.zutha.model.db

import net.zutha.model.constants.SchemaIdentifier._
import net.zutha.model.constructs._
import org.tmapi.core.Topic

trait SchemaItems {
  protected def getSchemaItem(identifier: SchemaIdentifier): Item
  
  //kinds of item types
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

  //association types
  lazy val siFIELD_DECLARATION: AssociationType = getSchemaItem(FIELD_DECLARATION).toAssociationType
  lazy val siPROPERTY_DECLARATION: AssociationType = getSchemaItem(PROPERTY_DECLARATION).toAssociationType
  lazy val siASSOCIATION_FIELD_DECLARATION: AssociationType = getSchemaItem(ASSOCIATION_FIELD_DECLARATION).toAssociationType
  lazy val siASSOCIATION_ROLE_CONSTRAINT: AssociationType = getSchemaItem(ASSOCIATION_ROLE_CONSTRAINT).toAssociationType
  lazy val siABSTRACT_CONSTRAINT: AssociationType = getSchemaItem(ABSTRACT_CONSTRAINT).toAssociationType

  //roles
  lazy val siFIELD_DECLARER: ZRole = getSchemaItem(FIELD_DECLARER).toRole
  lazy val siPROPERTY_DECLARER: ZRole = getSchemaItem(PROPERTY_DECLARER).toRole
  lazy val siASSOCIATION_FIELD_DECLARER: ZRole = getSchemaItem(ASSOCIATION_FIELD_DECLARER).toRole

  //property types
  lazy val siROLE_CARD_MIN: PropertyType = getSchemaItem(ROLE_CARD_MIN).toPropertyType
  lazy val siROLE_CARD_MAX: PropertyType = getSchemaItem(ROLE_CARD_MAX).toPropertyType
  
  lazy val siNAME: PropertyType = getSchemaItem(NAME).toPropertyType
  lazy val siMODIFIABLE_NAME: PropertyType = getSchemaItem(MODIFIABLE_NAME).toPropertyType
  lazy val siZID: PropertyType= getSchemaItem(ZID).toPropertyType
}
