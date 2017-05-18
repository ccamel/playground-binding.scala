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
package com.ccm.me.playground.bindingscala.svgeditor

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, MouseEvent, SVGElement, SVGTextElement}
import org.scalajs.dom.{Node, document}

import scala.language.implicitConversions

class ui extends ShowCase {
  implicit def makeIntellijHappy[T<:org.scalajs.dom.raw.Node](x: scala.xml.Node): Binding[T] = throw new AssertionError("This should never execute.")
  implicit def intToString(i: Int ) = i.toString
  implicit val events: Var[Option[Event]] = Var(None)

  val model: RectShape = RectShape(Var(250), Var(170), Var(300), Var(150), Var("Hello !"))
  val selected: Var[Option[RectShape]] = Var(None)

  @dom override def css: Binding[BindingSeq[Node]] =
      <style>
      </style>
      <!-- -->

  @dom override def render: Binding[Node] = {
    import View._
    implicit def toSvgTags(a: dom.Runtime.TagsAndTags2.type) = scalatags.JsDom.svgTags

    <div class="container" style="height:100%">
      <p>Select, move or resize the following rectangle:</p>

      <svg data:width="100%"
           data:height="600">
        <g data:id="layers">
          <g data:id="layer-canvas">
            {
            model.draw().bind
            }
          </g>
        </g>
      </svg>
    </div>
  }

  override def install() {
    def emit(e: Event) = {
      events.value = Some(e)
      events.value = None
    }

    document.onmousemove = emit _
    document.onmousedown = emit _
    document.onmousedown = emit _
  }

  override def name: String = "playground-binding.scala/svg-editor"
  @dom override def description: Binding[Node] = <div>SVG shapes resizable using mouse</div>
  override def link: String = s"#playground-binding.scala/svg-editor"
  override def scalaFiddle: Option[String] = None

}

