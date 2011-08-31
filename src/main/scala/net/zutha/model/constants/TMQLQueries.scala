package net.zutha.model.constants
import ZuthaConstants._
import SchemaIdentifier._

object TMQLQueries {
  val TRANSITIVE = "%pragma taxonometry tm:transitive "


  /** check if this Item is an ItemType
   *  @params item
   *  @return non-empty result-set if this item is an ItemType
   */
  val ItemIsAnItemType = TRANSITIVE + "?item >> types == " + ITEM_TYPE

  /** check if this Topic is an Anonymous Topic which doesn't exist in the ZDM
   *  @params topic
   *  @return non-empty result-set if this topic is an AnonymousTopic
   */
  val TopicIsAnonymous = "?topic >> types == " + ANONYMOUS_TOPIC

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
  val ItemTypeIsAbstract = "?itemType <- " + ITEM_TYPE +
    " << roles " + ABSTRACT_CONSTRAINT

  /** get all field-types declared by an ItemType
   *  @params itemType
   */
  val fieldsDeclaredByItemType = TRANSITIVE + "?itemType <- " + FIELD_DECLARER +
    " << roles " + FIELD_DECLARATION + " >> roles " + FIELD_TYPE + " -> " + FIELD_TYPE

  /** get all property-types declared by an ItemType
   *  @params itemType
   */
  val propertiesDeclaredByItemType = TRANSITIVE + "?itemType <- " + PROPERTY_DECLARER +
    " << roles "+ PROPERTY_DECLARATION +" >> roles " + PROPERTY_TYPE + " -> " + PROPERTY_TYPE
}
