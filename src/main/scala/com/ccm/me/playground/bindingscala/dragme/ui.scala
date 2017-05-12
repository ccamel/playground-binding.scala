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
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, MouseEvent}
import org.scalajs.dom.{Node, document}

class ui extends ShowCase {
  val model: DraggableRect = DraggableRect(Var(250), Var(170), Var(300), Var(150), Var(false), Var(None))

  @dom override def css: Binding[BindingSeq[Node]] =
      <style>
        {s"""
        .draggable {
          background-color: #81C6DE;
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

        .draggable .handle {
          width: 10px;
          height: 10px;
          background: #945540;
          position: absolute;
        }

        .draggable .n {
           top: 0px;
           left: ${model.w.bind / 2}px;
           cursor: n-resize;
        }

        .draggable .s {
           bottom: 0px;
           left: ${model.w.bind / 2}px;
           cursor: s-resize;
        }

        .draggable .e {
           top: ${model.h.bind / 2}px;
           right: 0px;
           cursor: e-resize;
        }

        .draggable .w {
           top: ${model.h.bind / 2}px;
           left: 0px;
           cursor: w-resize;
        }

        .draggable .se {
          right: 0px;
          bottom: 0px;
          cursor: se-resize;
        }

        .draggable .sw {
          left: 0px;
          bottom: 0px;
          cursor: sw-resize;
        }

        .draggable .ne {
          top: 0px;
          right: 0px;
          cursor: ne-resize;
        }

        .draggable .nw {
          top: 0px;
          left: 0px;
          cursor: nw-resize;
        }

      """}
      </style>
      <!-- -->

  @dom override def render: Binding[Node] = {
    <div class="container">
      <p>Select, move or resize the following rectangle:</p>
      <div class="draggable"
           onmousedown={onMouseDown(true, Some(Move)) _}
           onmouseup={onMouseUp _}>
        {renderLabel.bind}{renderHandles.bind}
      </div>
    </div>
  }

  @dom def renderLabel =
    <p onmousedown={_: Event => false}>
      {if (!model.selected.bind) "Select me!" else "Move/resize me!"}
    </p>

  @dom def renderHandles = {
    if (model.selected.bind) {
      Constants(Seq((N, "n"), (S, "s"), (E, "e"), (W, "w"), (NE, "ne"), (NW, "nw"), (SE, "se"), (SW, "sw")): _*).map { d =>
          <div class={s"handle ${d._2}"}
               onmousedown={onMouseDown(true, Some(Resize(d._1))) _}
               onmouseup={onMouseUp _}/>
      }
    } else {
      <!-- empty -->
      <!-- empty -->
    }
  }

  private def onMouseDown(selected: Boolean, action: Option[EditMode])(e: MouseEvent) = {
    model.selected.value = selected

    if (model.selected.value) {
      val p = Position(e.clientX.toInt, e.clientY.toInt)

      model.drag.value = action.flatMap(a ⇒ Some(Edition(p, a)))
    }
    e.stopPropagation()
  }

  private def onMouseMove(e: MouseEvent) = {
    model.drag.value match {
      case Some(drag@Edition(Position(lastx, lasty), action)) ⇒
        val (x, y) = (e.clientX.toInt, e.clientY.toInt)
        val (dx, dy) = (x - lastx, y - lasty)
        action match {
          case Move =>
            model.x.value += dx
            model.y.value += dy
          case Resize(N) =>
            model.y.value += dy
            model.h.value -= dy
          case Resize(S) =>
            model.h.value += dy
          case Resize(E) =>
            model.w.value += dx
          case Resize(W) =>
            model.x.value += dx
            model.w.value -= dx
          case Resize(NW) =>
            model.x.value += dx
            model.y.value += dy
            model.w.value += -dx
            model.h.value += -dy
          case Resize(NE) =>
            model.y.value += dy
            model.w.value += +dx
            model.h.value += -dy
          case Resize(SW) =>
            model.x.value += dx
            model.w.value += -dx
            model.h.value += dy
          case Resize(SE) =>
            model.w.value += dx
            model.h.value += dy
        }
        model.drag.value = Some(drag.copy(p = Position(x, y)))
      case None ⇒
    }
    e.stopPropagation()
  }

  private def onMouseUp(e: MouseEvent) = {
    model.drag.value = None
  }

  override def install() {
    document.onmousemove = onMouseMove _
    document.onmousedown = onMouseDown(false, None) _
  }

  override def name: String = "playground-binding.scala/drag-me"
  @dom override def description: Binding[Node] = <div>HTML Div element resizable using mouse</div>
  override def link: String = s"#playground-binding.scala/drag-me"
  override def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/obKiF28/8")

}

