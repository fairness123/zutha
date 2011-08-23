/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.zutha.model

trait Item {
  def getZIDs: Seq[String];

  def zid: String;

  def name: String;

  def getDirectTypes: Seq[Item];
  
  def addZID(zid: ZID);

  def address: String;
}


