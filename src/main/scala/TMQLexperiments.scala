
import scala.collection.JavaConversions._
import org.tmapi.core._
import de.topicmapslab.tmql4j.components.processor.runtime.{TMQLRuntimeFactory,ITMQLRuntime}
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet
import de.topicmapslab.tmql4j.components.processor.results.model.IResult
import de.topicmapslab.majortom.store.TopicMapStoreProperty
import de.topicmapslab.majortom.util.FeatureStrings
import net.zutha.model.{QueryEngine}

object TMQLexperiments {
  var sys : TopicMapSystem = null
  var runtime : ITMQLRuntime = null
  var tm : TopicMap = null;
  
  
  val q0 = """DELETE CASCADE ALL"""
  val q1 = """
    	insert '''
      %prefix z http://psi.zutha.net/
      %prefix zid http://zutha.net/item/
    		zid:04 isa [z:Person];
    		- "Raymond E. Feist" .
        zid:05 isa z:Person;
        - "Janny Wurts" .
        zid:06 isa [z:Book];
        - "Daughter of the Empire" .
        [z:Authorship]([z:Author]: zid:04, z:Author : zid:05, [z:Work] : zid:06) 
    	'''
      """
  val q2 = """
    	%prefix z http://psi.zutha.net/
    	select $t / tm:name
    		where $t isa z:Person
    	"""

  val q3 = """
      select $t / tm:name
        where $t isa z:Person
      """
        
  val q4 = """%prefix z http://psi.zutha.net/
      FOR $t IN z:Person >> instances
      RETURN <item>{$t / tm:name}</item>"""
    
  val q5 = """
      %prefix z http://psi.zutha.net/
      FOR $t IN tm:subject >> instances
        RETURN <item><id>{$t >> indicators >> atomify}</id><name>{$t / tm:name}</name></item>
      """
  
    
  def main(args : Array[String]) {
    QueryEngine.printTMLocators
    lazyRunMethod
    //oldRunMethod
  }
  def lazyRunMethod = {
    val result = QueryEngine.runQuery(q2)
    val prefixes = QueryEngine.getPrefixes
    println(prefixes)
    println(result)
  }
  
  def oldRunMethod = {
    setUpTMSystem
    makeTopicMap("http://zutha.net")
    printTMLocators
    setUpTMQL
    runQuery(q2)
  }

  def runQuery(q : String) = {
  	val res = runtime.run(tm, q).getResults
      
  	println (res.getResultType)
    println ( "num_results: " + (res.getResults.count(_ => true)))
    printResults(res)
  }
  
  def printResults(res : IResultSet[_]) = {
	  for(val r <- res.getResults) {
	    for(val obj <- r.asInstanceOf[IResult].getResults){
	      val strRes = getStrVal(obj)
	      println(strRes)
	    }
	  } 
  }
  def getStrVal(obj : Object) : String = {
	  val str = obj match {
	    case n : Name =>
	      n.getValue
	    case _ => obj.toString
	  }
	  return str
  }
  
  def makeTopicMap(uri : String) = {
    sys.createTopicMap(uri)
  }

  def printTMLocators = {
    for(val loc <- sys.getLocators) {
      println (loc.getReference)
    }
  }
  
  def setUpTMSystem = {
    val factory = TopicMapSystemFactory.newInstance();
    factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,"de.topicmapslab.majortom.database.store.JdbcTopicMapStore")
    factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost")
    factory.setProperty("de.topicmapslab.majortom.jdbc.database", "majortom")
    factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL")
    factory.setFeature(FeatureStrings.SUPPORT_HISTORY, true);
    
    sys = factory.newTopicMapSystem()
  }
  def setUpTMQL = {
    tm = sys.getTopicMap("http://zutha.net")
    runtime = TMQLRuntimeFactory.newFactory().newRuntime("tmql-2007");
  }

}

