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
package com.ccm.me.playground.bindingscala.home

import com.ccm.me.playground.bindingscala.{App, ShowCase}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node

class ui extends ShowCase {
  @dom override def css: Binding[BindingSeq[Node]] =
    <style>
      {"""
        .card {
          box-shadow: 0 5px 10px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
        }

        .card:hover {
          box-shadow: 0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
        }

        .message h1 {
          font-size: 2.8em;
        }
        """}
    </style>
    <!-- -->

  @dom override def render: Binding[Node] =
  <div class="container">
      <div class="section">
        <div class="row">
          <div class="message">
            <h1>playground/Binding.scala</h1>
            <p>My playground I use for playing with fancy and exciting technologies. This one's for <a href="https://www.scala-lang.org/">scala</a>,
              <a href="https://www.scala-js.org/">scalajs</a> and <a href=" https://github.com/ThoughtWorksInc/Binding.scala">binding.scala</a>.</p>
          </div>
        </div>
        <div class="row">
          <h5>Showcases</h5>
          <div class="divider"/>
        </div>
        <div class="row">
        <div class="col s12">
         {Constants(App.showCases.filterNot(_ == App.homeShowCase): _*).map { s =>
           <div class="col s4">
            <div class="card blue-grey darken-1">
              <div class="card-content white-text">
                <span class="card-title">{s.name.split("/").last}</span>
                <p>{s.description.bind}</p>
              </div>
              <div class="card-action">
                <a href={s.link.toString}>
                  &gt;
                  Play</a>
                <a href={App.sourceURL}>
                  &gt;
                  Source</a>{s.scalaFiddle match {
                case Some(l) =>
                  <a href={l.toString}>
                    &gt;
                    scalafiddle</a>
                case None => <!-- -->
                }}
                </div>
              </div>
            </div>
          }}
         </div>
      </div>
    </div>
  </div>

  override def name: String = "playground-binding.scala/home"
  @dom override def description: Binding[Node] = <div>Home</div>
  override def link: String = s"#playground-binding.scala/home"
  override def scalaFiddle: Option[String] = None

}

