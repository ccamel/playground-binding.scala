/*
The MIT License (MIT)

Copyright (c) 2017 Chris Camel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.ccm.me.playground.bindingscala.treeview

import scala.collection.Seq
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.timers
import scala.util.Random

// minimal business model implementation
object FileStore {
  // data for the store (compact representation)
  val data = List("1", "0", "ontologies", 'd,"2", "1", "agency", 'd,"3", "2", "agency-ontology.ttl", 'f,"4", "2", "agency-ontology.html", 'f,"6", "2", "agency.png", 'f,"7", "2", "agency.graffle", 'f,"8", "1", "examples", 'd,"9", "8", "xml example.xml", 'f,"10", "8", "gw-mp-record.ttl", 'f,"11", "1", "assets", 'd,"12", "11", "css.css", 'f,"13", "1", "place", 'd,"14", "13", "place-ontology.html", 'f,"15", "13", "place.graffle", 'f,"17", "13", "place.png", 'f,"18", "13", "place-ontology.ttl", 'f,"19", "1", "concept", 'd,"20", "19", "concept-ontology.ttl", 'f,"21", "19", "concept.graffle", 'f,"22", "19", "concept-ontology.html", 'f,"23", "19", "concept.png", 'f,"24", "1", "house-membership", 'd,"25", "24", "house-membership-ontology.ttl", 'f,"27", "24", "house-membership.graffle", 'f,"28", "24", "house-membership-ontology.html", 'f,"29", "24", "house-membership.png", 'f,"30", "1", "time-period", 'd,"31", "30", "time-period-ontology.html", 'f,"32", "30", "time-period.graffle", 'f,"33", "30", "time-period-ontology.ttl", 'f,"34", "30", "time-period.png", 'f,"35", "1", "election", 'd,"37", "35", "election.graffle", 'f,"38", "35", "election.png", 'f,"39", "35", "election-ontology.ttl", 'f,"40", "35", "election-ontology.html", 'f,"41", "1", "README.md", 'f,"42", "1", "list-of-lists.csv", 'f,"44", "1", "specialised-agency", 'd,"45", "44", "specialised-agency.png", 'f,"46", "44", "specialised-agency.graffle", 'f,"48", "1", "petition", 'd,"49", "48", "petition-ontology.html", 'f,"50", "48", "petition.graffle", 'f,"51", "48", "petition.png", 'f,"52", "48", "petition-ontology.ttl", 'f,"53", "1", "core", 'd,"54", "53", "core-ontology.html", 'f,"55", "53", "core.png", 'f,"56", "53", "core-ontology.ttl", 'f,"57", "53", "core.graffle", 'f,"58", "1", "contact-point", 'd,"59", "58", "contact-point.png", 'f,"60", "58", "contact-point-ontology.html", 'f,"61", "58", "contact-point.graffle", 'f,"63", "58", "contact-point-ontology.ttl", 'f,"64", "1", "urls.csv", 'f,"65", "1", "index.html", 'f)

  abstract class FSElement(id: String, label: String)

  final case class FSFile(id: String, label: String) extends FSElement(id, label)

  final case class FSFolder(id: String, label: String) extends FSElement(id, label)

  private val rnd = new Random()

  def childrenOf(id: String): Future[Seq[FSElement]] = {
    // not optimal but it does the job.
    val children = data.grouped(4)
                       .filter( e ⇒ e(1) == id )
                       .map {
                         case e@List( id, pid, name, 'd ) ⇒ FSFolder(id.toString, name.toString)
                         case e@List( id, pid, name, 'f ) ⇒ FSFile(id.toString, name.toString)
                       }
                      .toList

    // simulate asynchronous load...
    val p = Promise[Seq[FSElement]]()
    timers.setTimeout(rnd.nextInt(2000) + 150) {
      p.success(children)
    }
    p.future
  }
}