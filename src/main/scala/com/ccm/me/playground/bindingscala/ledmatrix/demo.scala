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
package com.ccm.me.playground.bindingscala.ledmatrix

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.html.Element
import org.scalajs.dom.raw.{Event, HTMLInputElement, HTMLSelectElement}

import scala.util.Random

trait Name {
  def name: String
}

trait Render extends Function1[Screen, Unit] {

}

trait Form {
  // render a form to control the options of the demo
  @dom def renderForm(): Binding[Element] = <div/>
}

trait Demo extends Render with Form with Name {

}

case class ConstantColorDemo() extends Demo {

  val color = (Var(0xdb), Var(0xe8), Var(0xff))

  def color(i: Int): Var[Int] = color.productElement(i).asInstanceOf[Var[Int]]

  @dom override def renderForm(): Binding[Element] = {
    <div>
      {for (i <- Constants(0 until 3: _*)) yield {
      <div class="col s4">
        <label for="interval">
          {val p = "%1.00f" format (color(i).bind * 100 / 256d)
        val label = i match {
          case 0 => "red"
          case 1 => "green"
          case 2 => "blue"
        }
        s"$label ($p %)"}
        </label>
        <p class="range-field">
          <input type="range" id="interval" min="0" max="255" value={color(i).get.toString} oninput={e: Event => color(i) := e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
        </p>
      </div>
    }}
    </div>
  }

  override def apply(screen: Screen): Unit = {
    for (i <- 0 until screen.w; j <- 0 until screen.h) {
      screen(i, j, color._1.get, color._2.get, color._3.get)
    }
  }

  override def name: String = "Fill with constant Color"
}

case class RandomDemo() extends Demo {
  val r = new Random()

  val monochrome = Var(false)

  @dom override def renderForm(): Binding[Element] = {
    <div class="col s6">
      <label for="control">Black and White</label>
      <div class="switch">
        <label>
          No
          <input type="checkbox" onclick={e: Event => monochrome := e.target.asInstanceOf[HTMLInputElement].checked}/>
          <span class="lever"></span>
          Yes
        </label>
      </div>
    </div>
  }

  override def apply(screen: Screen): Unit = {
    for (i <- 0 until screen.w; j <- 0 until screen.h) {
      screen.cells(i)(j) := (if (monochrome.get) if (r.nextBoolean()) 0xFFFFFF else 0x000000 else r.nextInt(16777215))
    }
  }

  override def name: String = "Random"
}

case class PlasmaDemo() extends Demo {

  import math.{round, sin, sqrt}

  var offset = 0d

  val functions = Seq(
    ("vertical sinusoid", (x: Int, y: Int) => round(128d + 127d * sin(offset + x / 4d)).toInt),
    ("diagonal sinusoid", (x: Int, y: Int) => round(128d + 127d * sin(offset + (x + y) / 4d)).toInt),
    ("double sinusoid", (x: Int, y: Int) => round((128d + 127d * sin(offset + x / 4d) + 128d + 127d * sin(offset + y / 4d)) / 2).toInt),
    ("concentric sinusoid", (x: Int, y: Int) => {
      round((128d + (127d * sin(offset + x / 8.0)) + 128d + (127d * sin(offset + y / 4.0)) + 128d + (127d * sin((offset + x + y) / 8.0)) + 128d + (127d * sin(offset + sqrt(x * x + y * y) / 4d))) / 4d).toInt
    })
  )
  val selectedPlasma = Var(functions.head._1)

  @dom override def renderForm(): Binding[Element] = {
    <div>
      <div class="col s12">
        <label>Choose plasma style</label>
        <select onchange={e: Event => selectedPlasma := e.target.asInstanceOf[HTMLSelectElement].value}>
          {for {
          f <- Constants(functions: _*)
        } yield {
          <option value={f._1}>
            {f._1}
          </option>
        }}
        </select>
      </div>
      <script>
        $('select').material_select()
      </script>
    </div>
  }

  override def apply(screen: Screen): Unit = {
    val d = screen.h / 2
    val step = 2 * math.Pi / screen.w
    val f = functions.find(_._1 == selectedPlasma.get).map(_._2).getOrElse((x: Int, y: Int) => 0)

    for (y <- 0 until screen.h;
         x <- 0 until screen.w
    ) {
      val c = f(x, y)
      screen(x, y, c)
    }

    offset += step
    if (offset > 2 * math.Pi) {
      offset = 0
    }
  }

  override def name: String = "Plasma"
}
