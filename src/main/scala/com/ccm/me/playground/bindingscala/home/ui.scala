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
  @dom override def css: Binding[BindingSeq[Node]] = <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
      <style>
        {"""
           cards-container {
             column-break-inside: avoid;
           }
           .cards-container .card {
             display: inline-block;
             overflow: visible;
           }

           @media only screen and (max-width: 600px) {
             .cards-container {
               -webkit-column-count: 1;
               -moz-column-count: 1;
               column-count: 1;
             }
           }
           @media only screen and (min-width: 601px) {
             .cards-container {
               -webkit-column-count: 2;
               -moz-column-count: 2;
               column-count: 2;
             }
           }
           @media only screen and (min-width: 993px) {
             .cards-container {
               -webkit-column-count: 3;
               -moz-column-count: 3;
               column-count: 3;
             }
           }
        """}
      </style>

  @dom override def render: Binding[Node] =
  <div class="container">
      <div class="section">
        <div class="row">
          <h5>Available showcases</h5>
          <div class="divider"/>
        </div>
        <div class="row">
        <div class="col s12 cards-container">
         {Constants(App.showCases.filterNot(_ == App.homeShowCase): _*).map { s =>
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

