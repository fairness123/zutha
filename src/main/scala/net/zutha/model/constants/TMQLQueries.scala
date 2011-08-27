package net.zutha.model.constants
import ZuthaConstants._
import SchemaIdentifier._

object TMQLQueries {
  val PREFIXES = "%prefix zsi " + ZSI_PREFIX + " "
  val TRANSITIVE = "%pragma taxonometry tm:transitive "


  /** check if this Item is an ItemType
   *  @params item
   *  @return non-empty result-set if this item is an ItemType
   */
  val ItemIsAnItemType = PREFIXES + TRANSITIVE + "?item >> types == zsi:" + ITEM_TYPE

  /** get all types of an Item (transitive)
   *  @params item
   */
  val AllTypesOfItem = TRANSITIVE + "?item >> types"

   /** get all supertypes of an ItemType (transitive)
   *  @params itemType
   */
  val AllSupertypesOfItemType = TRANSITIVE + "?itemType >> supertypes"

  /** @param itemType
   *  @return a non-empty result set if this itemType is Abstract
   */
  val ItemTypeIsAbstract = PREFIXES + "?itemType <- zsi:" + CONSTRAINED_ITEM_TYPE +
    " << roles zsi:" + ABSTRACT_CONSTRAINT

  /** get all field-types declared by an ItemType
   *  @params itemType
   */
  val fieldsDeclaredByItemType = PREFIXES + TRANSITIVE + "?itemType <- zsi:" + CONSTRAINED_ITEM_TYPE +
    " << roles zsi:" + ITEM_FIELD_CONSTRAINT + " >> roles zsi:" + CONSTRAINED_FIELD_TYPE + " ->"

  /** get all property-types declared by an ItemType
   *  @params itemType
   */
  val propertiesDeclaredByItemType = PREFIXES + "?itemType <- zsi:" + CONSTRAINED_ITEM_TYPE +
    " << roles zsi:"+ ITEM_PROPERTY_CONSTRAINT +" >> roles zsi:" + CONSTRAINED_PROPERTY_TYPE + " ->"
}
