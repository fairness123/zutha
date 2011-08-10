package net.zutha.model.uri

import net.zutha.model.constants.ApplicationConstants._
import net.liftweb.http.{ParsePath, RewriteResponse, RewriteRequest}
import net.zutha.model.Item

object UriRewriter {
  /* rewrite: /item/01H5/Item_Name/details.xhtml?<params>
   *      =>  /item-views/details?zid=01H5&name=Item_Name&<params>
   * rewrite: /item/01H5/Item_Name/Association_Name/assoc-table.xhtml
   *      =>  /assoc-views/assoc_table?zid=01H5&name=Item_Name&association=Association_Name
   * rewrite: /item/01H5/Item_Name/Association_Name/Role_Name/item-list.xhtml
   *      =>  /role-views/item_list?zid=01H5&name=Item_Name&association=Association_Name&role=Role_Name
   * rewrite: /item/0D7P0/some_file.png/details.xhtml
   *      =>  /item-views/details?zid=0D7P0&name=some_file.png
   * rewrite: /file/0D7P0/some_file.png
   *      =>  /file/?zid=0D7P0&name=some_file.png
   */
  def unapply(req: RewriteRequest): Option[RewriteResponse] = req match {
    case RewriteRequest(ParsePath(path,suffix,absolute,endSlash),reqType,httpReq)
      if(!endSlash && absolute) =>
      path match {
      //match: /<stem>/<zid>/<any_name>...
      case stem::ZIDLookup(wasRepaired,item)::name::tail if(!wasRepaired)
         => stem match {
        //match: /item/<zid>/<name>..._.html
        case "item" if(name==item.name && suffix==HTML_EXT) => tail match {
          //redirect: /item/<zid>/<name>/<view>.html => /item-views/<view>?...
          case view::Nil => makeResponse(Some("item-views"),view,makeParams(item))
          //redirect: /item/<zid>/<name>/<assocName>/<view>.html => /assoc-views/<view>?...
          case assocName::view::Nil => makeResponse(Some("assoc-views"),view,makeParams(item,assocName))
          //redirect: /item/<zid>/<name>/<assocName>/<roleName>/<view>.html => /role-views/<view>?...
          case assocName::roleName::view::Nil => makeResponse(Some("role-views"),view,makeParams(item,assocName,roleName))
          case _ => None
        }
        //redirect: /file/<zid>/<name_with_suffix>
        case "file" if{
          val ext = if(suffix=="") "" else "."+suffix
          (name+ext==item.name && tail==Nil)
          } => makeResponse(None,"file",makeParams(item))
        case _ => None
      }
      case _ => None
    }
    case _ => None
  }


  def makeResponse(viewType: Option[String], view: String, params: Map[String,String]): Some[RewriteResponse] = {
    val path = viewType.foldRight(view::Nil){_::_}
    Some(RewriteResponse(path,params))
  }
  def makeParams(item: Item): Map[String,String] = {
      Map("zid" -> item.zid, "name" -> item.name)
  }
  def makeParams(item: Item, assocName: String): Map[String,String] = {
    makeParams(item) + ("association" -> assocName)
  }
  def makeParams(item: Item, assocName: String, roleName: String): Map[String,String] = {
    makeParams(item,assocName) + ("role" -> roleName)
  }

}
