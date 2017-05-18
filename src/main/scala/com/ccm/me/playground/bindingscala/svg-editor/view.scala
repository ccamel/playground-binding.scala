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

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, MouseEvent, SVGElement}

import scala.language.implicitConversions
import scalatags.JsDom

trait View {
  def draw(): Binding[SVGElement]
}

object View {
  implicit def makeIntellijHappy[T <: org.scalajs.dom.raw.Node](x: scala.xml.Node): Binding[T] = throw new AssertionError("This should never execute.")

  implicit def toSvgTags(a: dom.Runtime.TagsAndTags2.type): JsDom.svgTags.type = scalatags.JsDom.svgTags

  implicit def intToString(i: Int): String = i.toString

  implicit def rectShapeToDrawable(s: RectShape)(implicit events: Var[Option[Event]]) = new View {
    val selected = Var(false)

    onEvents.watch()


    @dom def onEvents() = {
      events.bind match {
        case Some(e:MouseEvent) if (e.buttons & 0x01) == 1 ⇒
          selected.value = false

        case _ ⇒
      }
    }

    @dom override def draw(): Binding[SVGElement] =
      <g data:transform={s"translate(${s.x.bind}, ${s.y.bind})"} data:shape="rect">
        <g>
          {drawShape.bind}
        </g>
        <g>
          {drawHandles.bind}
        </g>
      </g>

    @dom def drawShape =
      <svg data:width={s.w.bind}
           data:height={s.h.bind}
           onclick={_: Event ⇒ selected.value = true}>
        <rect data:width={s.w.bind}
              data:height={s.h.bind}
              data:style="fill:lightblue;stroke:blue;stroke-width:2;fill-opacity:1;stroke-opacity:1"/>
        <text data:x="50%"
              data:y="50%"
              data:alignment-baseline="middle"
              data:text-anchor="middle">
          {s.label.bind}
        </text>
      </svg>

    @dom def drawHandles = {
      @dom def handles = if (selected.bind) {
        // TODO uggly code
        Constants(Seq("N", "S", "E", "W", "NE", "NW", "SE", "SW"): _*).map { d =>
          val (cursor, dx, dy): (String, Double, Double) = d match {
            case "N" ⇒ ("n", 0.5, 0)
            case "S" ⇒ ("s", 0.5, 1)
            case "E" ⇒ ("e", 1, 0.5)
            case "W" ⇒ ("w", 0, 0.5)
            case "NE" ⇒ ("ne", 1, 0)
            case "NW" ⇒ ("nw", 0, 0)
            case "SE" ⇒ ("se", 1, 1)
            case "SW" ⇒ ("sw", 0, 1)
          }
            <circle data:cx={(s.w.bind * dx).toInt}
                    data:cy={(s.h.bind * dy).toInt}
                    data:r="5"
                    data:style={s"cursor: ${cursor}-resize;fill-opacity:0.5;"}/>
        }
      } else {
        <!-- empty -->
        <!-- empty -->
      }

      <g data:stroke="gray"
         data:stroke-width="1"
         data:fill="lightblue">
        {handles.bind}
      </g>
    }
  }

}
