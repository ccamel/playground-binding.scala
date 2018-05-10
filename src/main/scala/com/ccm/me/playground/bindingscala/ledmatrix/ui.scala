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

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node
import org.scalajs.dom.raw._

import scala.language.implicitConversions
import scala.scalajs.js.timers
import scala.scalajs.js.timers.SetTimeoutHandle

class ui extends ShowCase {

  // TODO: needed declarations for SVG - see if it can be improved
  implicit final class WidthHeightOps @inline()(node: SVGSVGElement) {
    @inline def width_=(value: String) = node.width.baseVal.valueAsString = value

    @inline def height_=(value: String) = node.height.baseVal.valueAsString = value
  }

  implicit final class RectWidthHeightOps @inline()(node: SVGRectElement) {
    @inline def width_=(value: String) = node.width.baseVal.valueAsString = value

    @inline def width_=(value: Int) = node.width.baseVal.value = value

    @inline def height_=(value: String) = node.height.baseVal.valueAsString = value

    @inline def height_=(value: Int) = node.height.baseVal.value = value

    @inline def x_=(value: String) = node.x.baseVal.valueAsString = value

    @inline def x_=(value: Int) = node.x.baseVal.value = value

    @inline def y_=(value: String) = node.y.baseVal.valueAsString = value

    @inline def y_=(value: Int) = node.y.baseVal.value = value
  }

  val screen: Screen = Screen()
  val surface: Var[String] = Var("Normal")
  val timerHandle: Var[Option[SetTimeoutHandle]] = Var(None)
  val timerInterval: Var[Int] = Var(50)
  val demos = Seq(ConstantColorDemo(), RandomDemo(), PlasmaDemo(), LissajousDemo(), FireDemo())
  val selectedDemo: Var[Option[Demo]] = Var(None)
  val dotSize: Var[Int] = Var(6)
  val dotSpace: Var[Int] = Var(0)
  val dotRadius: Var[Int] = Var(1)

  // runs
  val isPlaying = Binding {
    timerHandle.bind.isDefined
  } // alias
  val renderTime = Var(0d)
  val pulse = Var(false)

  screen.clear(ui.screenBackgroundColor)

  onPlaying.watch()
  onPulse.watch()

  @dom def css: Binding[BindingSeq[Node]] =
    <style>
      {val space = dotSpace.bind
    val size = dotSize.bind
    val radius = dotRadius.bind

    s"""
          .cell-row {
          margin-bottom: ${space}px;
          padding: 0px;
          height: ${size}px;
          }

          .cell {
          display: inline-block;
          width: ${size}px;
          height: ${size}px;
          -moz-border-radius: ${radius}px;
          border-radius: ${radius}px;
          margin-right: ${space}px;
          margin-bottom: ${space}px;
          padding: 0px;
          }
        """}
    </style>

  <!-- -->

  @dom def render: Binding[Node] = {
    <div class="container">
      {renderContent.bind}
    </div>
  }

  @dom def renderContent = {
    <div>
      <div class="container">
        <div class="row">
          <form class="col s6">
            {renderControl.bind}
          </form>
        </div>
      </div>
      <div class="container">
        {renderScreen.bind}
      </div>
      <div class="container">
        <p class="caption">Metrics:</p>
        <div class="row">
          <div class="col s4">
            <a>
              <span class="badge">
                {f"${renderTime.bind}%.2f"}
                ms</span>
              Rendering time</a>
          </div>
        </div>
      </div>
    </div>
  }

  @dom def renderControl = {
    <div class="row">
      <div class="col s12">
        <label>Selected demo</label>
        <select onchange={e: Event => selectDemo(Some(demos(e.target.asInstanceOf[HTMLSelectElement].value.toInt)))}>
          <option value="" disabled={true} selected={true}>Chose a demo</option>{for {
          i <- Constants(0 until demos.size: _*)
        } yield {
          <option value={i.toString}>
            {demos(i).name}
          </option>
        }}
        </select>
      </div>
    </div>
      <div class="row">
        <div class="col s12">
          <label>Rendering surface</label>
          <select onchange={e: Event => surface.value = e.target.asInstanceOf[HTMLSelectElement].value}>
            <option value="Span" selected={true}>Grid of div/span elements</option>
            <option value="Svg">SVG elements</option>
          </select>
        </div>
      </div>
      <div>
        {selectedDemo.bind match {
        case Some(demo) => <div class="row teal lighten-5">
          <div>
            <span style="font-size: 12px; padding-left: 5px;">Demo options</span>
            <div class="divider"/>{demo.renderForm.bind}
          </div>
        </div>
        case _ => <div/>
      }}
      </div>
      <div class="row">
        <div class="col s4">
          <label for="dot-size">Dot size (
            {dotSize.bind.toString}
            pixels)</label>
          <p class="range-field">
            <input type="range"
                   id="dot-size"
                   min="0"
                   max="10"
                   value={dotSize.bind.toString}
                   oninput={e: Event => dotSize.value = e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
          </p>
        </div>
        <div class="col s4">
          <label for="dot-space">Dot space (
            {dotSpace.bind.toString}
            pixels)</label>
          <p class="range-field">
            <input type="range"
                   id="dot-space"
                   min="0"
                   max="5"
                   value={dotSpace.bind.toString}
                   oninput={e: Event => dotSpace.value = e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
          </p>
        </div>
        <div class="col s4">
          <label for="dot-radius">Dot radius (
            {dotRadius.bind.toString}
            pixels)</label>
          <p class="range-field">
            <input type="range"
                   id="dot-radius"
                   min="0"
                   max="5"
                   value={dotRadius.bind.toString}
                   oninput={e: Event => dotRadius.value = e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col s4">
          <label for="control">Play</label>
          <div class="switch">
            <label>
              Off
              <input type="checkbox"
                     disabled={selectedDemo.bind.isEmpty}
                     onclick={e: Event => if (e.target.asInstanceOf[HTMLInputElement].checked) play else pause}/>
              <span class="lever"></span>
              On
            </label>
          </div>
        </div>
        <div class="col s8">
          <label for="interval">Interval update (
            {timerInterval.bind.toString}
            ms)</label>
          <p class="range-field">
            <input type="range"
                   id="interval"
                   min="0"
                   max="1000"
                   value={timerInterval.bind.toString}
                   oninput={e: Event => timerInterval.value = e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
          </p>
        </div>
      </div>
  }

  def play: Unit = {
    timerHandle.value = Some(timers.setTimeout(timerInterval.value) {
      pulse.value = !pulse.value
      play
    })
  }

  def pause: Unit = {
    timerHandle.value.foreach(timers.clearTimeout)
    timerHandle.value = None
  }

  @dom def renderScreen = {
    surface.bind match {
      case "Span" => renderScreenSpan.bind
      case "Svg" => renderScreenSvg.bind
      case _ => renderScreenSpan.bind
    }
  }

  @dom def renderScreenSpan = {
    <div data:type={surface.bind}>
      {for (j <- Constants(0 until screen.h: _*)) yield {
      <div class="row cell-row">
        {for (i <- Constants(0 until screen.w: _*)) yield {
        renderCellSpan(i, j).bind
      }}
      </div>
    }}
    </div>
  }

  @dom def renderScreenSvg = {
    implicit def toSvgTags(a: dom.Runtime.TagsAndTags2.type) = scalatags.JsDom.svgTags

    <svg width="800" height="800">
      {for (i <- Constants(0 until screen.w: _*);
            j <- Constants(0 until screen.h: _*)) yield {
      renderCellSvg(i, j).bind
    }}
    </svg>
  }

  @dom def renderCellSpan(i: Int, j: Int) = {
    <span class="cell" style={s"background-color: #${color(i, j).bind}"}></span>
  }

  @dom def renderCellSvg(i: Int, j: Int) = {
    implicit def toSvgTags(a: dom.Runtime.TagsAndTags2.type) = scalatags.JsDom.svgTags

      <rect x={i * (dotSize.bind + dotSpace.bind)}
            y={j * (dotSize.bind + dotSpace.bind)}
            width={dotSize.bind}
            height={dotSize.bind}
            data:style={"fill: #" + color(i, j).bind}/>
  }

  @dom def color(i: Int, j: Int) = {
    val cell = screen.cells(i)(j)
    "000000" + cell.bind.toHexString takeRight 6
  }


  def selectDemo(demo: Option[Demo]): Unit = {
    val oldValue: Boolean = selectedDemo.value.exists(d => d.started.value)
    selectedDemo.value.foreach {
      _.started.value = false
    }
    selectedDemo.value = demo
  }

  def onPlaying = Binding {
    if (!isPlaying.bind) {
      screen.clear(ui.screenBackgroundColor)
    }

    selectedDemo.bind match {
      case Some(d) => d.started.value = isPlaying.bind
      case None =>
    }
  }

  def onPulse = Binding {
    pulse.bind

    selectedDemo.value
      .map(d => (s: Screen) => {
        val t0 = System.nanoTime()
        d(s)
        renderTime.value = (System.nanoTime() - t0) / (1000 * 1000d)
      })
      .foreach(_ (screen))
  }

  override def name: String = "playground-binding.scala/led-matrix"

  @dom override def description: Binding[Node] = <div>A led-matrix with some nice demo effects</div>

  override def link: String = s"#playground-binding.scala/led-matrix"

  override def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/nXYqFFS/3")

}

object ui {
  val screenBackgroundColor = 0x777777
}