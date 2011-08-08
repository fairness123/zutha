/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.zutha.model

trait Item {
  def zid: ZID

  def name: String

  def addZID(zid: ZID)
}


