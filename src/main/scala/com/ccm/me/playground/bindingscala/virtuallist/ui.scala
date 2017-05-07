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
package com.ccm.me.playground.bindingscala.virtuallist

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.BindingSeq
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.{Node, document}
import org.scalajs.dom.raw.{HTMLDivElement, MouseEvent}
import math.{min, max}

class ui extends ShowCase {
  val paneHeight = 255

  val list: ListModel[Item] = ListModel[Item]()

  loadData.watch()

  document.onmouseup = onMouseUp _
  document.onmousemove = onMouseMove _

  @dom override def css: Binding[BindingSeq[Node]] = <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
    <style>
      {
        s"""
         .pane {
            height: ${paneHeight}px;
         }
         div.mainpane {
             float: left;
             border-style: solid;
             border-width: thin;
             border-color: gray;
         }
         div.scrolltrack {
             float: right;
             background: #e8e8e8;
             border: 1px solid rgb(163, 163, 163);
             z-index: 10;
             text-align: center;
             height: 100%;
             cursor: pointer;
         }
         div.scrollbar {
             background: #c1c1c1;
             position: relative;
             border-radius: 6px;
             left: 2px;
             width: 10px;
             height: ${scrollbarHeight.bind}px;
             top: ${scrollbarTop.bind}px;
         }
         .rowitem {
           border-bottom: 1px dashed lightgray;
         }
       """}
    </style>

  @dom override def render: Binding[Node] = {
      <div class="container list">
        <p>Virtual list displaying <em>1000000</em> elements</p>
        <div class="pane" style="width: 320px;">
          <div class="mainpane" style="width: 304px;">
            {for (item <- list.data) yield {
            <div class="rowitem">{item.label}</div>
          }}
          </div>
          <div class="scrolltrack" style="width: 16px;"
               onmousemove={e: MouseEvent => onMouseMove(e)}>
            <div class="scrollbar" style=""
                 onmousedown={onMouseDown _}
            >
            </div>
          </div>
        </div>
      </div>
  }

  def onMouseDown(e: MouseEvent) = {
    val p = Position(e.clientX.toInt, e.clientY.toInt)

    list.lastDragPosition.value = Some(p)
  }

  def onMouseMove(e: MouseEvent) = {
    list.lastDragPosition.value match {
      case Some(Position(_,lasty)) ⇒
        val (x, y) = (e.clientX.toInt, e.clientY.toInt)
        val dy = y - lasty
        val offset = 0l max (list.offset.value + (dy * list.total.value / paneHeight)) min list.total.value

        if( offset != list.offset.value ) {
          list.offset.value = (0l max offset) min list.total.value
          list.lastDragPosition.value = Some(Position(x, y))
        }
      case None ⇒
    }
  }

  def onMouseUp(e: MouseEvent) = {
    list.lastDragPosition.value = None
  }

  private def scrollbarHeight: Binding[Int] = Binding {
    if(list.total.bind == 0)
      0
    else Math.max( 12 /* minimal pixel size*/, (paneHeight * list.size.bind / list.total.bind).toInt)
  }

  private def scrollbarTop: Binding[Int] = Binding {
    if(list.total.bind == 0)
      0
    else {
      val height = paneHeight - scrollbarHeight.bind - 1

      (height * list.offset.bind / list.total.bind).toInt
    }
  }

  private def loadData = Binding {
    val page = ItemStore.apply(Paginate(list.offset.bind, list.size.bind))
    list.data.value.clear()
    list.data.value ++= page.content
    list.total.value = page.totalElements
  }

  override def name: String = "playground-binding.scala/virtual-list"
  @dom override def description: Binding[Node] = <div>A virtual list rendering <em>1,000,000</em> items</div>
  override def link: String = s"#playground-binding.scala/virtual-list"
  override def scalaFiddle: Option[String] = None
}

