package net.zutha.model.topicmap.extensions

import collection.JavaConversions._

import net.zutha.util.Cache._
import net.zutha.model.topicmap.db.TopicMapDB
import org.tmapi.core.{Role, Topic, Association}

object AssociationExtended{
  val get = makeCache[Association,String,AssociationExtended](_.getId, association => new AssociationExtended(association))
  def apply(association: Association):AssociationExtended = get(association)
}
class AssociationExtended(association: Association) {
  lazy val isAnonymous: Boolean = TopicMapDB.associationIsAnonymous(association)

  /**
   * @return Set[(roleType:Topic,player:Topic]
   */
  def getRolePlayersT: Set[(Topic,Topic)] = association.getRoles.toSet.map((r: Role) => (r.getType,r.getPlayer))

  def getPlayersT: Set[Topic] = association.getRoles.toSet.map((r:Role) => r.getPlayer)

  def getPlayersOfRoleT(role: Topic): Set[Topic] = association.getRoles(role).toSet.map((r:Role) => r.getPlayer)
}
