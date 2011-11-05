package net.zutha.model.constructs

object ZScope{
  def apply(scopeItems:ZItem*) = {
    new ZScope(scopeItems.toSet)
  }
}
case class ZScope(val scopeItems: Set[ZItem])
