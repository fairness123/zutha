package net.zutha.util

import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Printer._
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constructs.{ZProperty, ZItem}

object JsonExport extends App {
  val tm = TopicMapDB.tmm

  val items = TopicMapDB.allItems
  val assocs = TopicMapDB.allAssociations


  def itemToJson(item: ZItem): JValue = {
    val props = item.getAllProperties
    val json =
      ("zid" -> item.zids) ~
      ("type" -> item.itemType.zid) ~
      ("properties" -> props.map(propToJson))

    json
  }

  def propToJson(prop: ZProperty): JValue = {
    "" -> ""
  }
}
