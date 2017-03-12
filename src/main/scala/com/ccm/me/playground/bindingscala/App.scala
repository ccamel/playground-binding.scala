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
package com.ccm.me.playground.bindingscala

//import com.ccm.me.playground.bindingscala.calc.{CalcModel, ui}
import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.{Node, document, window}

import scala.scalajs.js
import scala.scalajs.js.JSApp

trait Name {
  def name: String
}

trait Render {
  def css: Binding[BindingSeq[Node]]
  def render: Binding[BindingSeq[Node]]
}

trait ShowCase extends Render with Name

object App extends JSApp {
  val $ = js.Dynamic.global.$
  val hash: Var[String] = Var("")

  val homeShowCase = new home.ui()
  val showCases = Seq(new calc.ui(), new ledmatrix.ui())

  def main(): Unit = {
    println("Starting App")

    dom.render(document.head, bootCss)
    dom.render(document.getElementById("application"), bootView)
    dom.render(document.getElementById("hook"), installMaterialzeSelect)

    document.onreadystatechange = e => {
      $("select").material_select();

      hash := document.location.hash.drop(1)
    }

    window.onhashchange = e => {
      println("On change: "+document.location.hash.drop(1))
      hash := document.location.hash.drop(1)
    }
  }

  @dom def bootCss = {
    val h = hash.bind
    showCases.find(s => s.name.equals(h)).getOrElse(homeShowCase).css.bind
  }

  @dom def bootView = {
    val h = hash.bind
    val x = showCases.find(s => s.name.equals(h)).getOrElse(homeShowCase).render.bind

    x
  }

  @dom def installMaterialzeSelect = {
    val h = hash.bind
    $("select").material_select();

    <div data:installed="true"/>
  }

}
