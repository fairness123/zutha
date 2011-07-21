
package net.zutha.model

object DB {
  def getItem(id:String): Option[Item] = {
    items.get(id)
  }
  val items = Map(
    "00" -> Item("00","First_Item"),
    "01" -> Item("01","Second_Item"),
    "02" -> Item("02","Third_Item"),
    "10G" -> Item("10G","Foreign_Item")
  )
}
