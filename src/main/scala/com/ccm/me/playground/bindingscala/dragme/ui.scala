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
package com.ccm.me.playground.bindingscala.dragme

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node
import org.scalajs.dom.raw.MouseEvent

class ui extends ShowCase {
  val model: DraggableRect = DraggableRect(Var(250), Var(170), Var(100), Var(100), Var(None) )

  @dom override def css: Binding[BindingSeq[Node]] =
    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
    <style>
      {s"""
        .dragable {
          background-color: rgb(60, 141, 47);
          cursor: move;
          width: ${model.w.bind}px;
          height: ${model.h.bind}px;
          border-radius: 4px;
          position: absolute;
          left: ${model.x.bind}px;
          top: ${model.y.bind}px;
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      """}
    </style>

  @dom override def render: Binding[BindingSeq[Node]] =
    <header>
      <nav class="top-nav">
        <div class="container">
          <div class="nav-wrapper">
            <a class="page-title">
              {name}
            </a>
          </div>
        </div>
      </nav>
    </header>
    <div class="container tree">
      <p>Moveable div:</p>
      <div class="dragable"
           onmousedown={onMouseDown _}
           onmousemove={onMouseMove _}
           onmouseup={onMouseUp _}
      >Drag Me!</div>
    </div>

  private def onMouseDown(e: MouseEvent) = {
    val p = Position(e.clientX.toInt, e.clientY.toInt)
    model.drag.value = Some(p)
  }

  private def onMouseMove(e: MouseEvent) = {
    model.drag.value match {
      case Some(Position(lastx,lasty)) ⇒
        val (x,y) = (e.clientX.toInt, e.clientY.toInt)

        model.x.value = model.x.value + x - lastx
        model.y.value = model.y.value + y - lasty
        model.drag.value = Some(Position(x,y))
      case None ⇒
    }
  }

  private def onMouseUp(e: MouseEvent) = {
    model.drag.value = None
  }

  override def name: String = "playground-binding.scala/drag-me"
}

