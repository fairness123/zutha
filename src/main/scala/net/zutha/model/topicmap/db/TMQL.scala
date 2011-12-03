package net.zutha.model.topicmap.db

import scala.collection.JavaConversions._
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.path.query.TMQLQuery
import de.topicmapslab.tmql4j.components.processor.results.model.{IResultSet}
import de.topicmapslab.tmql4j.components.processor.results.xml.XMLValue
import xml._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Cache._
import de.topicmapslab.tmql4j.query.IQuery
import de.topicmapslab.tmql4j.components.processor.results.tmdm.SimpleResult
import org.tmapi.core.{Topic, Association}
import net.zutha.model.exceptions.TypeConversionException
import net.zutha.model.constructs._
import net.zutha.model.constants.ZuthaConstants._
import de.topicmapslab.tmql4j.components.processor.prepared.IPreparedStatement
import de.topicmapslab.majortom.model.core.ITopicMap
import net.liftweb.common.Logger

trait TMQL extends Logger{
  def tmm: ITopicMap;

  private lazy val runtime = TMQLRuntimeFactory.newFactory().newRuntime("tmql-2007")

  //register Zutha prefixes
  private val prefixHandler = runtime.getLanguageContext().getPrefixHandler()
  prefixHandler.registerPrefix("zid", "http://zutha.net/item/")
  prefixHandler.registerPrefix("zsi", "http://psi.zutha.net/")

  def getPrefixes = runtime.getLanguageContext().getPrefixHandler().getPrefixMap

  /** makes a PreparedStatement or returns an already created one
   *  @param qstr the query string to make a preparedStatement from
   *  @return a PreparedStatement for given query string
   */
  val prepareStatement = {
    val get = makeCache[String,String,IPreparedStatement](identity, qstr =>
      runtime.preparedStatement(new TMQLQuery(tmm, qstr)))
    (qstr: String) => get(qstr)
  } 
/*  def prepareStatement(qstr: String) = {
    val query = new TMQLQuery(tmm, qstr)
    runtime.preparedStatement(query)
  }*/

  private def runQuery(qstr : String): IResultSet[_] = {
    val query = runtime.run(tmm,qstr)
    val res = query.getResults
    res
  }

  private def xmlQueryResultsToNodeSeq(rs : IResultSet[_]): NodeSeq = {
    rs.getResultType match{
      case "XML" =>
        val allxml = rs.getResults.foldRight(NodeSeq.Empty){(r, acc) =>
          val flatXml = r.asInstanceOf[XMLValue].first().toString
          val ns = XML.loadString(flatXml)
          val newacc = ns ++: acc
          newacc
        }
        allxml
      case _ => throw new IllegalArgumentException
    }
  }

  // ------------------ Generic Query Processors ------------------

  /**
   * @param qstr a TMQL query that will return XML results
   * @return the results as a NodeSeq
   */
  def runXmlQuery(qstr: String): NodeSeq = {
    val rs = runQuery(qstr)
    xmlQueryResultsToNodeSeq(rs)
  }

  def runStringQuery(qstr: String): String = {
    val rs = runQuery(qstr)
    rs.getResultType match{
      case "XML" =>
        val pp = new PrettyPrinter(80,3)
        try{
          val allxml = xmlQueryResultsToNodeSeq(rs)
          val prettyXml = pp.formatNodes(allxml)
          prettyXml
        } catch {
          case _ => rs.toString
        }

      case _ => rs.toString
    }
  }

  // ------------------ Internal Query Processors ----------------------
  private def runBooleanQuery(q: IQuery): Boolean = {
    val rs = q.getResults
    rs.size() > 0
  }

  private def runTopicQuery(q: IQuery): Seq[Topic] = {
    val rs = q.getResults
    rs.map{_ match {
      case res: SimpleResult => res.first match {
        case tt: Topic => tt
        case _ => throw new IllegalArgumentException("query results were not Topics as expected")
      }
      case _ => throw new IllegalArgumentException("query did not return TMAPI constructs as expected")
    }
    }.toSeq
  }

  private def runItemQuery(q: IQuery): Seq[ZItem] = {
    runTopicQuery(q).filterNot(_.isAnonymous).map(_.toItem)
  }

  private def runTypeQuery(q: IQuery): Seq[ZType] = {
    try runItemQuery(q).map(_.toType)
    catch {
      case e: TypeConversionException =>
        throw new IllegalArgumentException("query does not return ZType results")
    }
  }

  private def runItemTypeQuery(q: IQuery): Seq[ZItemType] = {
    try runItemQuery(q).map(_.toItemType)
    catch {
      case e: TypeConversionException =>
        throw new IllegalArgumentException("query does not return ZItemType results")
    }
  }

  private def runAssociationQuery(q: IQuery): Seq[Association] = {
    val rs = q.getResults
    val results = rs.map{_ match {
      case res: SimpleResult => res.first match {
        case assoc: Association => assoc
        case _ => throw new IllegalArgumentException("query results were not Associations as expected")
      }
      case _ => throw new IllegalArgumentException("query did not return TMAPI constructs as expected")
    }
    }.toSeq
    val numResults = results.length
    results
  }
  private def runZAssociationQuery(q: IQuery): Seq[ZAssociation] = {
    runAssociationQuery(q).filterNot(_.isAnonymous).map(_.toZAssociation)
  }

  //=========================== Parameter Queries ===========================
  //=========================================================================

  val TRANSITIVE = "%pragma taxonometry tm:transitive "

  //------------------- General Queries ---------------------

  /** check if ?item is an instance of ?zType
   *  @params item, zType
   *  @return non-empty result-set if this item is an instance of zType
   */
  def itemIsA(item: ZItem, zType: ZType): Boolean = {
    val statement = prepareStatement(TRANSITIVE + "?item >> types == ?zType")
    statement.setTopic("?item",item)
    statement.setTopic("?zType",zType)
    statement.run()
    runBooleanQuery(statement)
  }

  /** get all types of an Item (transitive)
   *  @params item
   */
  def allTypesOfItem(item: ZItem): Set[ZType] = {
    val statement = prepareStatement(TRANSITIVE + "?item >> types")
    statement.setTopic("?item",item)
    statement.run()
    runTypeQuery(statement).toSet
  }

  /** get direct types of an Item
   *  @params item
   */
  def directTypesOfItem(item: ZItem): Set[ZType] = {
    val statement = prepareStatement("?item >> types")
    statement.setTopic("?item",item)
    statement.run()
    val result = runTypeQuery(statement).toSet
    debug("types of " + item.zid + ": " + result)
    result
  }

  /** get all instances of an Item (transitive)
   *  @params item
   */
  def allInstancesOfItem(item: ZItem): Set[ZItem] = {
    val statement = prepareStatement(TRANSITIVE + "?item >> instances")
    statement.setTopic("?item",item)
    statement.run()
    runItemQuery(statement).toSet
  }

  /** get all ancestors of a ?zType
   *  @params zType the type whose ancestors we want to find
   */
  def findAncestorsOfType(zType: ZType): Set[ZType] = {
    val statement = prepareStatement(TRANSITIVE + "?zType >> supertypes")
    statement.setTopic("?zType",zType)
    statement.run()
    runTypeQuery(statement).toSet
  }
  /** get all descendants of a ?zType
   *  @params zType the type whose descendants we want to find
   */
  def findDescendantsOfType(zType: ZType): Set[ZType] = {
    val statement = prepareStatement(TRANSITIVE + "?zType >> subtypes")
    statement.setTopic("?zType",zType)
    statement.run()
    runTypeQuery(statement).toSet
  }
  /** get all players of ?otherRole of type ?playerType in associations of type ?assocType where ?item plays the role ?role
   *  @params item, role, assocType, otherRole, playerType
   */
  //TODO replace with TMAPI implementation: TMQL does not use transitivity on playerType
  def traverseAssociation(item: ZItem, role: ZRole, assocType: ZAssociationType, otherRole: ZRole): Set[ZItem] = {
    val statement = prepareStatement(TRANSITIVE +
      "?item << players ?role << roles ?assocType >> roles ?otherRole >> players")
    statement.setTopic("?item",item)
    statement.setTopic("?role",role)
    statement.setTopic("?assocType",assocType)
    statement.setTopic("?otherRole",otherRole)
    statement.run()
    runItemQuery(statement).toSet
  }

  /** get all associations of type assocType with the given (role,player) pairs
   *  @param assocType matched associations must have this type (transitive)
   *  @param strict If true, matched associations must have exactly the set of rolePlayers given.
   *    If false, matched associations  must have at least the set of rolePlayers given
   *  @param rolePlayers a set of (Role,Player) pairs that matched associations must contain
   **/
  def findAssociationsTMQL(assocType: ZAssociationType, strict: Boolean, rolePlayers:(ZRole,ZItem)*): Set[ZAssociation] = {
    var args = (1 to rolePlayers.length).map(i => "?role"+i+" : ?player"+i)
    if(!strict) args :+= ("...")
    val q = TRANSITIVE + "?assocType" + args.mkString("(",", ",")")
    val statement = prepareStatement(q)
    statement.setTopic("?assocType",assocType)
    (1 to rolePlayers.length).foreach{i =>
      statement.setTopic("?role"+i,rolePlayers(i-1)._1)
      statement.setTopic("?player"+i,rolePlayers(i-1)._2)
    }
    statement.run()
    statement.run()
    val rawQuery = statement.getNonParameterizedQueryString
    runZAssociationQuery(statement).toSet
  }


  //------------------- Specialized Queries ---------------------

  /** check if this Topic is an Anonymous Topic which doesn't exist in the ZDM
   *  @params topic
   *  @return true if this topic is an AnonymousTopic
   */
  def topicIsAnonymous(topic: Topic): Boolean = {
    val statement = prepareStatement("?topic >> types == " + ZSI_PREFIX+"topicmap/anonymous-topic")
    statement.setTopic("?topic",topic)
    statement.run()
    runBooleanQuery(statement)
  }

  /** check if this Association is has a player which is an Anonymous Topic
   *  @params association
   *  @return true if this association is anonymous
   */
  def associationIsAnonymous(association: Association): Boolean = {
    val statement = prepareStatement("?association >> roles >> players " + ZSI_PREFIX+"topicmap/anonymous-topic")
    statement.setConstruct("?association",association)
    statement.run()
    runBooleanQuery(statement)
  }

}
