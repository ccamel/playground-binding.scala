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
import org.scalajs.dom.raw.{KeyboardEvent, MouseEvent, WheelEvent}

class ui extends ShowCase {
  val paneHeight = 259 // height of the scroll bar in px - TODO: can the value be automagically computed ?

  val list: ListModel[Item] = ListModel[Item]()

  loadData.watch()

  @dom override def css: Binding[BindingSeq[Node]] =
    <style>
      {
        s"""
         .pane {
            height: ${paneHeight}px;
         }
         div.mainpane {
             float: left;
             border-color: 1px solid #e0e0e0;
         }
         div.scrolltrack {
             float: right;
             background: #e8e8e8;
             border: 1px solid rgb(163, 163, 163);
             z-index: 10;
             margin-top: .5rem;
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
    <!-- -->

  @dom override def render: Binding[Node] = {
      <div class="container list">
        <p>Virtual list displaying <em>1,000,000</em> elements. You can explore the data with the following means:</p>
        <ul class="browser-default">
          <li>Move the scrollbar by dragging the mouse</li>
          <li>Use the mouse wheel (over the listbox)</li>
          <li>Use the keyboard keys, up to go up, down to go down</li>
        </ul>
        <div class="pane" style="width: 320px;"
             data:autofocus=""
             onmousewheel={onMouseWheel _}>
          <div class="mainpane" style="width: 304px;">
            <ul class="collection">
            {for (item <- list.data) yield {
            <li class="collection-item">
              <div>{item.label}
                <span class="secondary-content">#{item.id.toString}</span>
              </div>
            </li>
            }}
            </ul>
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
        val offset = list.offset.value + (dy * list.total.value / paneHeight)

        if( offset != list.offset.value ) {
          updateOffset(offset)
          list.lastDragPosition.value = Some(Position(x, y))
        }
      case None ⇒
    }
  }

  def onMouseUp(e: MouseEvent) = {
    list.lastDragPosition.value = None
  }

  def onMouseWheel(e: WheelEvent) = {
    updateOffset(list.offset.value + e.deltaY.toInt)

    e.stopImmediatePropagation()
  }

  def onKeyDown(e: KeyboardEvent) = {
    // TODO: is there any symbolic constants around ?
    e.keyCode match {
      case 33 ⇒ updateOffset(list.offset.value - 5)
      case 34 ⇒ updateOffset(list.offset.value + 5)
      case 38 ⇒ updateOffset(list.offset.value - 1)
      case 40 ⇒ updateOffset(list.offset.value + 1)
      case _ ⇒
    }
  }

  private def updateOffset( offset: Long) = {
    list.offset.value = (0l max offset) min list.total.value
  }

  private def scrollbarHeight: Binding[Int] = Binding {
    if(list.total.bind == 0)
      0
    else 12 /* minimal pixel size*/ max (paneHeight * list.size.bind / list.total.bind).toInt
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

  override def install(): Unit = {
    document.onmouseup = onMouseUp _
    document.onmousemove = onMouseMove _
    document.onkeydown = onKeyDown _
  }

  override def name: String = "playground-binding.scala/virtual-list"
  @dom override def description: Binding[Node] = <div>A virtual list rendering <em>1,000,000</em> items</div>
  override def link: String = s"#playground-binding.scala/virtual-list"
  override def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/JuZsaoP/0")
}

