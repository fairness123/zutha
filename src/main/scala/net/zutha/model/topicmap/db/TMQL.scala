package net.zutha.model.topicmap.db

import scala.collection.JavaConversions._
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.path.query.TMQLQuery
import de.topicmapslab.tmql4j.components.processor.results.model.{IResultSet}
import de.topicmapslab.tmql4j.components.processor.results.xml.XMLValue
import xml._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.util.Helpers._
import de.topicmapslab.tmql4j.query.IQuery
import de.topicmapslab.tmql4j.components.processor.results.tmdm.SimpleResult
import org.tmapi.core.{Topic, Association}
import net.zutha.model.exceptions.TypeConversionException
import net.zutha.model.constructs._
import net.zutha.model.constants.SchemaIdentifier._
import de.topicmapslab.tmql4j.components.processor.prepared.IPreparedStatement
import de.topicmapslab.majortom.model.core.ITopicMap

trait TMQL {
  def tm: ITopicMap;

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
      runtime.preparedStatement(new TMQLQuery(tm, qstr)))
    (qstr: String) => get(qstr)
  } 
/*  def prepareStatement(qstr: String) = {
    val query = new TMQLQuery(tm, qstr)
    runtime.preparedStatement(query)
  }*/

  private def runQuery(qstr : String): IResultSet[_] = {
    val query = runtime.run(tm,qstr)
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

  private def runItemQuery(q: IQuery): Seq[Item] = {
    runTopicQuery(q).filterNot(_.isAnonymous).map(_.toItem)
  }

  private def runItemTypeQuery(q: IQuery): Seq[ItemType] = {
    try runItemQuery(q).map(_.toItemType)
    catch {
      case e: TypeConversionException =>
        throw new IllegalArgumentException("query does not return ItemType results")
    }
  }

  private def runTypeQuery(q: IQuery): Seq[ZType] = {
    try runItemQuery(q).map(_.toZType)
    catch {
      case e: TypeConversionException =>
        throw new IllegalArgumentException("query does not return ItemType results")
    }
  }
  //---------------- Parameter Queries -----------------
  val TRANSITIVE = "%pragma taxonometry tm:transitive "

    /** check if ?item is an instance of ?zdmType
   *  @params item, zdmType
   *  @return non-empty result-set if this item is an instance of itemType
   */
  def itemIsA(item: Item, zdmType: ZType): Boolean = {
    val statement = prepareStatement(TRANSITIVE + "?item >> types == ?zdmType")
    statement.setTopic("?item",item)
    statement.setTopic("?zdmType",zdmType)
    statement.run()
    runBooleanQuery(statement)
  }

  /** check if this Topic is an Anonymous Topic which doesn't exist in the ZDM
   *  @params topic
   *  @return true if this topic is an AnonymousTopic
   */
  def topicIsAnonymous(topic: Topic): Boolean = {
    val statement = prepareStatement("?topic >> types == " + ZSI+ANONYMOUS_TOPIC)
    statement.setTopic("?topic",topic)
    statement.run()
    runBooleanQuery(statement)
  }

  /** check if this Association is has a player which is an Anonymous Topic
   *  @params association
   *  @return true if this association is anonymous
   */
  def associationIsAnonymous(association: Association): Boolean = {
    val statement = prepareStatement("?association >> roles >> players " + ZSI+ANONYMOUS_TOPIC)
    statement.setConstruct("?association",association)
    statement.run()
    runBooleanQuery(statement)
  }

  /** get all types of an Item (transitive)
   *  @params item
   */
  def allTypesOfItem(item: Item): Set[ZType] = {
    val statement = prepareStatement(TRANSITIVE + "?item >> types")
    statement.setTopic("?item",item)
    statement.run()
    runTypeQuery(statement).toSet
  }

  /** get direct types of an Item
   *  @params item
   */
  def directTypesOfItem(item: Item): Set[ZType] = {
    val statement = prepareStatement("?item >> types")
    statement.setTopic("?item",item)
    statement.run()
    runTypeQuery(statement).toSet
  }

  /** get all supertypes of a ?zdmType (transitive)
   *  @params zdmType
   */
  def allSupertypesOfItem(zdmType: ZType): Set[ZType] = {
    val statement = prepareStatement(TRANSITIVE + "?zdmType >> supertypes")
    statement.setTopic("?zdmType",zdmType)
    statement.run()
    runTypeQuery(statement).toSet
  }

  /** get all players of ?otherRole of type ?playerType in associations of type ?assocType where ?item plays the role ?role
   *  @params item, role, assocType, otherRole, playerType
   */
  def traverseAssociation(item: Item, role: ZRole, assocType: AssociationType, otherRole: ZRole, playerType: ZType): Set[Item] = {
    val statement = prepareStatement(TRANSITIVE + "?item <- ?role << roles ?assocType >> roles ?otherRole -> ?playerType")
    statement.setTopic("?item",item)
    statement.setTopic("?role",role)
    statement.setTopic("?assocType",assocType)
    statement.setTopic("?otherRole",otherRole)
    statement.setTopic("?playerType",playerType)
    statement.run()
    runItemQuery(statement).toSet
  }
}
