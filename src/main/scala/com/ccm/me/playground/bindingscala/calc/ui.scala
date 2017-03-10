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
package com.ccm.me.playground.bindingscala.calc

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.html.Anchor
import org.scalajs.dom.raw.{Event, HTMLElement}

object ui {
  @dom def render(calc: CalcModel): Binding[HTMLElement] = {
    val model = Var(calc)

    val btns = List(List("7", "8", "9", "+", "C"),
      List("4", "5", "6", "-", "MS"),
      List("1", "2", "3", "x", "MR"),
      List(".", "0", "=", "/", "MC"))

    <section>
      <h2>binding.scala/calc</h2>
      <div class="container" style="width: 400px;">
        <div class="row">
          <div class="col s10" style="font-family: 'VT323', monospace;">
            <input style="text-align:right; font-size: 21px;" type="Text" readOnly={true} value={getDisplay(model).bind}></input>
          </div>{renderMemoryTag(model).bind}{renderOperatorTag(model).bind}
        </div>{Constants(btns: _*).map { l =>
        <div class="row">
          {Constants(l: _*).map { c =>
          <div class="col s2">{b(model, c).bind}</div>
        }}
        </div>
      }}
      </div>

    </section>
  }

  @dom def renderMemoryTag(model: Var[CalcModel]) = <div style="font-size: 10px;">
    {model.bind.memory.map(it => "M").getOrElse(" ")}
  </div>

  @dom def renderOperatorTag(model: Var[CalcModel]) = <div style="font-size: 10px;">
    {model.bind.operators.headOption.map {
      case Plus() => "+"
      case Minus() => "-"
      case Multiply() => "*"
      case Divide() => "/"
      case _ => " "
    }.getOrElse(" ")}
  </div>

  @dom def getDisplay(model: Var[CalcModel]) = Option(model.bind.accumulator).filterNot(_.isEmpty).getOrElse("0")

  @dom def b(model: Var[CalcModel], label: String): Binding[Anchor] = {
    val op: (String, Token) = label match {
      case "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "0" => ("grey darken-1", Digit(label.toInt))
      case "." => ("grey darken-1", Dot())
      case "+" => ("blue lighten-5 blue-text", Plus())
      case "-" => ("blue lighten-5 blue-text", Minus())
      case "x" => ("blue lighten-5 blue-text", Multiply())
      case "/" => ("blue lighten-5 blue-text", Divide())
      case "=" => ("lime lighten-5 blue-text", Result())
      case "C" => ("red lighten-5 blue-text", Clear())
      case "MR" => ("green lighten-5 green-text", MR())
      case "MC" => ("green lighten-5 green-text", MC())
      case "MS" => ("green lighten-5 green-text", MS())
      case _ => ("", NoOp())
    }
    val calc = model.bind
    val disabled = if (calc.isDefinedAt(op._2)) "" else "disabled"
    <a class={s"btn ${op._1} ${disabled} waves-effect waves-light"} style="width: 60px; padding: 0px" onclick={_: Event => model := calc(op._2)}>
      {label}
    </a>
  }

}
