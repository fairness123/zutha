package net.zutha.model.topicmap.db

import org.tmapi.core.Topic
import scala.collection.mutable.Map
import net.zutha.model.topicmap.constructs.{TMItemType, TMItem}

object ConstructCache {

  val getItem = {
    val items: Map[String,TMItem] = Map()
    (topic: Topic) => {
      if (items.contains(topic.getId)) items(topic.getId)
      else {
        val item = new TMItem(topic)
        items.put(topic.getId, item)
        item
      }
    }
  }

  val getItemType = {
    val itemTypes: Map[String,TMItemType] = Map()
    (item: TMItem) => {
      val id = item.toTopic.getId
      if (itemTypes.contains(id)) itemTypes(id)
      else {
        val itemType = new TMItemType(item)
        itemTypes.put(id, itemType)
        itemType
      }
    }
  }
}
