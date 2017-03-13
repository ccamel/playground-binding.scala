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

import scala.scalajs.js.timers
import scala.scalajs.js.timers.SetTimeoutHandle

class ui extends ShowCase {
  val screen: Screen = Screen()
  val timerHandle: Var[Option[SetTimeoutHandle]] = Var(None)
  val timerInterval: Var[Int] = Var(200)
  val demos = Seq(ConstantColorDemo(), RandomDemo())
  val selectedDemo: Var[Option[Demo]] = Var(None)
  val dotSize: Var[Int] = Var(5)
  val dotSpace: Var[Int] = Var(1)

  screen.clear(0x777777)

  def name: String = "playground-binding.scala/led-matrix"

  @dom def css: Binding[BindingSeq[Node]] =
      <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
      <style>
        {
        val space = dotSpace.bind
        val size = dotSize.bind

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
        margin-right: ${space}px;
        margin-bottom: ${space}px;
        padding: 0px;
        }
        """}
      </style>

  @dom def render: Binding[BindingSeq[Node]] = {
    <header>
      <nav class="top-nav">
        <div class="container">
          <div class="nav-wrapper"><a class="page-title">{name}</a></div>
        </div>
      </nav>
    </header>

    <div class="container">
      {renderContent.bind}
    </div>
  }

  @dom def renderContent = {
    <div>
      <div class="container">
        <div class="row">
          <form class="col s3">
            {renderControl.bind}
          </form>
        </div>
      </div>
      <div class="container">
        {
          for( j <- Constants(0 until screen.h: _*) ) yield {
            <div class="row cell-row">
              {
                for( i <- Constants(0 until screen.w: _*) ) yield {
                  renderCell(screen.cells(i)(j)).bind
                }
              }
            </div>
          }
        }
      </div>
    </div>
  }

  @dom def renderControl = {
    <div class="row">
      <label>Selected demo</label>
      <select onchange={e: Event => selectedDemo := Some(demos(e.target.asInstanceOf[HTMLSelectElement].value.toInt))}>
        <option value="" disabled={true} selected={true}>Chose a demo</option>{for {
        i <- Constants(0 until demos.size: _*)
      } yield {
        <option value={i.toString}>
          {demos(i).name}
        </option>
      }}
      </select>
    </div>
      <div>
        {selectedDemo.bind match {
        case Some(demo) => <div class="card grey lighten-5">
          <div class="card-content">
            <span class="card-title" style="font-size: 16px;">Demo options</span>{demo.renderForm.bind}
          </div>
        </div>
        case _ => <div/>
      }}
      </div>
      <div class="row">
        <label for="dot-size">Dot size ({dotSize.bind.toString} pixels)</label>
        <p class="range-field">
          <input type="range" id="dot-size" min="0" max="10" value={dotSize.bind.toString} oninput={e: Event => dotSize := e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
        </p>
      </div>
      <div class="row">
        <label for="dot-space">Dot space  ({dotSpace.bind.toString} pixels)</label>
        <p class="range-field">
          <input type="range" id="dot-space" min="0" max="5" value={dotSpace.bind.toString} oninput={e: Event => dotSpace := e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
        </p>
      </div>
      <div class="row">
        <label for="control">Play</label>
        <div class="switch">
          <label>
            Off
            <input type="checkbox" onclick={e: Event => if (e.target.asInstanceOf[HTMLInputElement].checked) play else pause}/>
            <span class="lever"></span>
            On
          </label>
        </div>
      </div>
      <div class="row">
        <label for="interval">Interval update (
          {timerInterval.bind.toString}
          ms)</label>
        <p class="range-field">
          <input type="range" id="interval" min="0" max="1000" value={timerInterval.bind.toString} oninput={e: Event => timerInterval := e.target.asInstanceOf[HTMLInputElement].value.toInt}/>
        </p>
      </div>
  }

  def play: Unit = {
    timerHandle := Some(timers.setTimeout(timerInterval.get) {
      selectedDemo.get.foreach(_ (screen))
      // trigger for next run
      play
    })
  }

  def pause: Unit = {
    println("pause")
    timerHandle.get.foreach(timers.clearTimeout(_))
    timerHandle := None
  }

  @dom def renderCell(cell: Var[Int]) = {
    val color = "000000" + cell.bind.toHexString takeRight 6
    <span class="cell" style={s"background-color: #$color"}></span>
  }
}
