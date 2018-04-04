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

import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, Route, dom}
import org.scalajs.dom.{Node, document}

import scala.scalajs.js
import scala.scalajs.js.JSApp

trait Name {
  def name: String
}

trait Render {
  def css: Binding[BindingSeq[Node]]
  def render: Binding[Node]
}

trait ShowCase extends Render with Name {
  def description: Binding[Node]
  def link: String
  def scalaFiddle: Option[String]

  def install(): Unit = {}
}

object App {
  protected def getInstance(): this.type = this

  val $ = js.Dynamic.global.$
  val homeShowCase = new home.ui()
  val showCases = Seq(
    homeShowCase,
    new calc.ui(),
    new ledmatrix.ui(),
    new loancalculator.ui(),
    new treeview.ui(),
    new dragme.ui(),
    new virtuallist.ui(),
    new svgeditor.ui()
  )
  val sourceURL = "https://github.com/ccamel/playground-binding.scala"

  val route: Route.Hash[ShowCase] = Route.Hash[ShowCase](homeShowCase)(
    new Route.Format[ShowCase] {
      override def unapply(hashText: String): Option[ShowCase] = Some(showCases.find(_.name == hashText.drop(1)).getOrElse(homeShowCase))
      override def apply(state: ShowCase): String = state.name
    }
  )
  val showCase: Var[ShowCase] = route.state

  route.watch()

  def main(args: Array[String]): Unit = {
    dom.render(document.head, bootCss)
    dom.render(document.getElementById("application"), bootView)

    installMaterialize.watch()
    onShowcaseChange.watch()
  }

  def bootCss = {
    // See: https://stackoverflow.com/questions/43675301/how-to-combine-binding-fragments-without-wrapping-them-in-an-xml-literal
    Binding(Constants(
      header,
      showCase.bind.css
    ).flatMap(_.bind))
  }

  @dom def bootView = {
    <div id="showcase" data:name={showCase.bind.name}>
      { bodyHeader.bind }
      <main>
      { showCase.bind.render.bind }
      </main>
      { bodyFooter.bind }
    </div>
  }

  @dom def header = {
    <meta charset="UTF-8"/>
    <title>{showCase.bind.name}</title>

    <link href="http://fonts.googleapis.com/icon?family=Material+Icons|VT323|Cutive+Mono" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
    <script src="https://use.fontawesome.com/e20ea8441d.js"></script>
    <style>
      {"""
       body {
        display: flex;
        min-height: 100vh;
        flex-direction: column;
      }
      #showcase {
        display: flex;
        min-height: 100vh;
        flex-direction: column;
      }
      main {
        flex: 1 0 auto;
      }
      footer {
        height: 80px;
        width:100%;
      }
      .footer-icons a {
        color: white;
        margin-right: 10px;
      }
      """}
    </style>
  }

  @dom def bodyHeader: Binding[BindingSeq[Node]] = {
    <header>
      <a href="https://github.com/ccamel/playground-binding.scala">
        <img style="position: absolute; top: 0; right: 0; border: 0;"
             src="https://camo.githubusercontent.com/38ef81f8aca64bb9a64448d0d70f1308ef5341ab/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6461726b626c75655f3132313632312e706e67"
             alt="Fork me on GitHub"
             data:data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png"/>
      </a>
      <nav class="top-nav light-blue darken-2">
        <div class="container">
          <div class="nav-wrapper">
            <a class="page-title">{showCase.bind.name}</a>
            { val sc = showCase.bind
              if (sc != homeShowCase)
              <ul class="right hide-on-med-and-down">
                <li>
                  <a href={s"#${homeShowCase.name}"}>Home</a>
                </li>
                {sc.scalaFiddle match {
                   case Some(l) =>
                     <li>
                       <a href={l}>ScalaFiddle</a>
                     </li>
                   case None =>
                     <!-- -->
                 }
                }
               </ul>
               else
                <!-- -->
            }
          </div>
        </div>
      </nav>
    </header>
    <!-- -->
  }

  @dom def bodyFooter = {
    <footer class="page-footer light-blue darken-2">
      <div class="container">
        <div class="row">
          © 2017 Chris Camel - MIT License (v. {AppInfo.version})
          <div class="right footer-icons">
            <a href="https://github.com/ccamel"><i class="fa fa-github fa-2x"></i></a>
            <a href="https://www.linkedin.com/in/christophe-camel"><i class="fa fa-linkedin-square fa-2x"></i></a>
          </div>
        </div>
      </div>
    </footer>
  }

  @dom def installMaterialize = {
    val h = showCase.bind
    $("select").material_select();
  }

  def onShowcaseChange = Binding {
    // call the install method on change
    showCase.bind.install()
  }
}
