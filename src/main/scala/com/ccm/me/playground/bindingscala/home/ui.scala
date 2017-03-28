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

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.BindingSeq
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node

class ui extends ShowCase {
  val sourceURL = "https://github.com/ccamel/playground-binding.scala"

  @dom override def css: Binding[BindingSeq[Node]] = <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>

  @dom override def render: Binding[BindingSeq[Node]] =
  <header>
    <nav class="top-nav">
      <div class="container">
        <div class="nav-wrapper"><a class="page-title">{name}</a></div>
        <div class="divider"></div>
      </div>
    </nav>
  </header>

  <div class="container">
    <div class="row">
      <div class="section">
        <h5>Available showcases</h5>
        <div class="divider"/>
        <div class="card col s4 blue-grey lighten-5" style="margin: 10px">
          <div class="card-content">
            <span class="card-title">calc</span>
            <p>A very simple and basic calculator</p>
          </div>
          <div class="card-action">
            <a href="#playground-binding.scala/calc">&gt; Play</a>
            <a href={sourceURL}> &gt; Source</a>
            <a href="https://scalafiddle.io/sf/hbwbCOe/0">&gt; scalafiddle</a>
          </div>
        </div>

        <div class="card col s4 blue-grey lighten-5" style="margin: 10px">
          <div class="card-content">
            <span class="card-title">led-matrix</span>
            <p>A led-matrix with some nice demo effects.</p>
          </div>
          <div class="card-action">
            <a href="#playground-binding.scala/led-matrix"> &gt; Play</a>
            <a href={sourceURL}> &gt; Source</a>
            <a href="https://scalafiddle.io/sf/nXYqFFS/3">&gt; scalafiddle</a>
          </div>
        </div>

        <div class="card col s4 blue-grey lighten-5" style="margin: 10px">
          <div class="card-content">
            <span class="card-title">loan-calculator</span>
            <p>A Simple Loan Calculator with amortization table.</p>
          </div>
          <div class="card-action">
            <a href="#playground-binding.scala/loan-calculator"> &gt; Play</a>
            <a href={sourceURL}> &gt; Source</a>
            <a href="https://scalafiddle.io/sf/1RxSQj6/1">&gt; scalafiddle</a>
          </div>
        </div>

      </div>
    </div>
  </div>

  override def name: String = "playground-binding.scala/home"
}

