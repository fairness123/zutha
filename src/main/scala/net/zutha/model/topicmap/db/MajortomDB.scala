package net.zutha.model.topicmap.db

import de.topicmapslab.majortom.model.core.ITopicMapSystem
import org.tmapi.core.TopicMapSystemFactory
import de.topicmapslab.majortom.store.TopicMapStoreProperty
import de.topicmapslab.majortom.util.FeatureStrings
import de.topicmapslab.majortom.queued.store.QueuedTopicMapStore
import de.topicmapslab.majortom.model.store.ITopicMapStore


trait MajortomDB {
  val rdbms = "de.topicmapslab.majortom.database.store.JdbcTopicMapStore"
  val queued = "de.topicmapslab.majortom.queued.store.QueuedTopicMapStore"
  val inMemory = "de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore"

  val useStore = inMemory
  
  def makeTopicMapSystem: ITopicMapSystem = {
    val factory = TopicMapSystemFactory.newInstance();
    factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,useStore)
    factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost")
    factory.setProperty("de.topicmapslab.majortom.jdbc.database", "majortom")
    factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL99")
    factory.setFeature(FeatureStrings.AUTOMATIC_MERGING,true)
    factory.setFeature(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME,true)
    
    //factory.setFeature(FeatureStrings.SUPPORT_HISTORY, true);

    factory.newTopicMapSystem().asInstanceOf[ITopicMapSystem]
  }

  def makeTopicMapStore(sys: ITopicMapSystem): ITopicMapStore = {
    new QueuedTopicMapStore(sys)
  }
}
