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
import org.scalajs.dom.raw.{Event, HTMLInputElement}

import scala.math.cos
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
        {
        val p = "%1.00f" format (color(i).bind * 100 / 256d)
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

case class SineWaveDemo() extends Demo {
  var offset = 0d

  val color = (Var(0xdb), Var(0xe8), Var(0xff))

  def color(i: Int): Var[Int] = color.productElement(i).asInstanceOf[Var[Int]]

  @dom override def renderForm(): Binding[Element] = {
    <div>
      {for (i <- Constants(0 until 3: _*)) yield {
      <div class="col s4">
        <label for="interval">
          {
          val p = "%1.00f" format (color(i).bind * 100 / 256d)
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
    val d = screen.h / 2
    val step = 2*math.Pi / screen.w
    screen.clear(0xFFFFFF)
    for (i <- 0 until screen.w) {
      val j = d + ( (d-1) * cos( offset + i * 2d * math.Pi / screen.w)).toInt
      screen(i, j, color._1.get, color._2.get, color._3.get)
    }

    offset += step
    if( offset > 2*math.Pi ) {
      offset = 0
    }

  }

  override def name: String = "Sine wave"
}
