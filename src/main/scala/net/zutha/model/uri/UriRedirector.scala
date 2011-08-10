package net.zutha.model.uri

import net.zutha.model.constants.ApplicationConstants._
import net.liftweb.http.{PermRedirectResponse, LiftResponse, Req}
import net.zutha.model.Item

object UriRedirector {
  /* redirect: /item/0IZ?<params>
   *        => /item/012/Looked_Up_Name/details.xhtml?<params>
   * redirect: /item/0IZ/wrong_name
   *        => /item/012/Corrected_Name/details.xhtml
   * redirect: /item/0IZ//Association_Name
   *        => /item/012/Looked_Up_Name/Association_Name/assoc_table.xhtml
   * redirect: /item/0867/someimage.png/
   *        => /item/0867/someimage.png/details.xhtml
   * redirect: /item/0867/
   *        => /item/0867/someimage.png/details.xhtml
   * redirect: /file/0867
   *        => /file/0867/someimage.png
   */
  def unapply(req: Req): Option[LiftResponse] = req match {
    case r @ Req(path,suffix,reqType) =>
      def makeResponse(toPath: String) = Some(PermRedirectResponse(toPath,r))
      path match {
      //match: /<stem>/<any_zid>...
      case stem::ZIDLookup(wasRepaired,item)::tail => stem match {
        case "item" => tail match {
          //redirect: /item/<any_zid>  
          //      => /item-views/<defaultView>?zid=<fixedZID>&name=<correctName>
          case Nil => makeResponse(ItemUri(item))
          //match: /item/<fixable_zid>/<wrong_name>...
          //       /item/<valid_zid>/<correct_name>.html
          //       /item/<valid_zid>/<correct_name>/<assoc_name>
          case name::tail2 if (wasRepaired || name!=item.name || tail2==Nil || suffix!=HTML_EXT) => tail2 match {
            //redirect: /item/<any_zid>/<any_name[.html|<other_ext>]>
            //       => /item-views/<defaultView>?zid=<fixedZID>&name=<correctName>
            case Nil => makeResponse(ItemUri(item))
            //redirect: /item/<fixableZID>/<wrongName>/<view>.html
            //       => /item-views/<view>?zid=<fixedZID>&name=<correctName>
            case view::Nil if(suffix==HTML_EXT) => makeResponse(ItemUri(view,item))
            //redirect: /item/<any_zid>/<any_name>/<assocName> 
            //       => /assoc-views/<defaultView>?zid=<fixedZID>&name=<correctName>&association=<assocName>
            case assocName::Nil if(suffix=="") => makeResponse(AssocUri(item,assocName))
            //redirect: /item/<fixableZID>/<wrongName>/<assocName>/<view>.html
            //       => /assoc-views/<view>?zid=<fixedZID>&name=<correctName>&association=<assocName>
            case assocName::view::Nil if(suffix==HTML_EXT) => makeResponse(AssocUri(view,item,assocName))
            //redirect: /item/<any_zid>/<any_name>/<assocName>/<roleName>
            //       => /role-views/<defaultView>?zid=<fixedZID>&name=<correctName>&association=<assocName>&role=<roleName>
            case assocName::roleName::Nil if(suffix=="") => makeResponse(RoleUri(item,assocName,roleName))
            //redirect: /item/<fixableZID>/<wrongName>/<assocName>/<roleName>/<view>.html
            //       => /role-views/<view>?zid=<fixedZID>&name=<correctName>&association=<assocName>&role=<roleName>
            case assocName::roleName::view::Nil if(suffix==HTML_EXT) => makeResponse(RoleUri(view,item,assocName,roleName))
            //too many path segments or invalid suffix
            case _ => None
          }
          //uri is either correct or invalid, but not fixable
          case _ => None
        }
        case "file" => tail match {
          //redirect: /file/<any_zid>
          //       => /file?zid=<fixedZID>&name=<looked_up_name>
          case Nil => makeResponse(FileUri(item))
          //redirect: /file/<fixable_zid>/<wrong_name>
          //       => /file?zid=<fixedZID>&name=<correctName>
          case filename::Nil if{
            val ext = if(suffix=="") "" else "."+suffix
            (wasRepaired || filename+ext!=item.name)} => makeResponse(FileUri(item))
          case _ => None
        }
        //invalid <stem>
        case _ => None
      }
      //doesn't match: /<stem>/<any_zid>...
      case _ => None
    }
    //isn't at Req
    case _ => None
  }
}

//TODO: get default views from user settings

object ItemUri {
  /** returns a URI string of the form /item/<zid>/<item name>/details.html */
  val defaultView = "details"
  def apply(item: Item): String = apply(defaultView, item)
  def apply(view: String, item: Item): String = "/item/"+item.zid+"/"+item.name+"/"+view+"."+HTML_EXT
}
object AssocUri {
  /** returns a URI string of the form /item/<zid>/<itemName>/<assocName>/<defaultView>.html */
  val defaultView = "assoc-table"
  def apply(item: Item, assocName: String): String = apply(defaultView,item,assocName)
  def apply(view: String, item: Item, assocName: String): String =
    "/item/"+item.zid+"/"+item.name+"/"+assocName+"/"+view+"."+HTML_EXT
}
object RoleUri {
  /** returns a URI string of the form /item/<zid>/<itemName>/<assocName>/<roleName>/<defaultView>.html */
  val defaultView = "item-list"
  def apply(item: Item, assocName: String, roleName: String): String = apply(defaultView, item, assocName, roleName)
  def apply(view: String, item: Item, assocName: String, roleName: String): String =
    "/item/"+item.zid+"/"+item.name+"/"+assocName+"/"+roleName+"/"+view+"."+HTML_EXT
}

object FileUri {
  /** returns a URI string of the form /item/<zid>/<item name>/details.html */
  def apply(item: Item): String = "/file/"+item.zid+"/"+item.name
}
