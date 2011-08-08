import net.zutha.model.db.DB
import net.zutha.model.topicmap.TopicMapDB

object TMQLexperiments {
  //q specifies the query that will be run
  val q = q5

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
    TopicMapDB.printTMLocators
    lazyRunMethod
  }

  def lazyRunMethod = {
    val result = TopicMapDB.runQuery(q)
    val prefixes = TopicMapDB.getPrefixes
//    println(prefixes)
    println(result)
  }
}

