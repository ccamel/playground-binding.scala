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

import com.thoughtworks.binding.Binding.Var

case class Position(x: Int, y: Int)

sealed trait Direction

case object N extends Direction

case object S extends Direction

case object E extends Direction

case object W extends Direction

case object NW extends Direction

case object NE extends Direction

case object SW extends Direction

case object SE extends Direction

sealed trait EditMode

case object Move extends EditMode

case class Resize(direction: Direction) extends EditMode

case class Edition( // last cursor position (during edition)
                    p: Position,
                    action: EditMode)

trait Editable {
  val drag: Var[Option[Edition]]
}

case class DraggableRect(
                          x: Var[Int],
                          y: Var[Int],
                          w: Var[Int],
                          h: Var[Int],
                          selected: Var[Boolean],
                          override val drag: Var[Option[Edition]]) extends Editable

object DraggableRect {
  def apply(): DraggableRect = DraggableRect(Var(0), Var(0), Var(10), Var(10), Var(false), Var(None))
}