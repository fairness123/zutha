package net.zutha.model.constants


object SchemaIdentifier extends Enumeration {
  type SchemaIdentifier = Value

  //types of item types
  val ITEM_TYPE = Value("item-type")
  val PROPERTY_TYPE = Value("property-type")
  val ASSOCIATION_TYPE = Value("association-type")
  val ROLE_TYPE = Value("role-type")

  //item types
  val ITEM = Value("item")

  //association types
  val ITEM_FIELD_CONSTRAINT = Value("item-field-constraint")
  val ITEM_PROPERTY_CONSTRAINT = Value("item-property-constraint")
  val ITEM_ROLE_CONSTRAINT = Value("item-role-constraint")
  val ASSOCIATION_ROLE_CONSTRAINT = Value("association-role-constraint")
  val ABSTRACT_CONSTRAINT = Value("abstract-constraint")

  //role types
  val CONSTRAINED_ITEM_TYPE = Value("constrained-item-type")
  val CONSTRAINED_FIELD_TYPE = Value("constrained-field-type")
  val CONSTRAINED_PROPERTY_TYPE = Value("constrained-property-type")
  val CONSTRAINED_ASSOCIATION_TYPE = Value("constrained-association-type")
  val CONSTRAINED_ROLE_TYPE = Value("constrained-role-type")

  //property types
  val ABSTRACT_NAME = Value("abstract-name")
  val NAME = Value("name")
  val ZID = Value("zid")
}
