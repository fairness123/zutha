package net.zutha.model.topicmap.extensions

import org.tmapi.core.{Association}

import net.zutha.util.Cache._
import net.zutha.model.topicmap.db.TopicMapDB

object AssociationExtended{
  val get = makeCache[Association,String,AssociationExtended](_.getId, association => new AssociationExtended(association))
  def apply(association: Association):AssociationExtended = get(association)
}
class AssociationExtended(association: Association) {
  lazy val isAnonymous: Boolean = TopicMapDB.associationIsAnonymous(association)
}
