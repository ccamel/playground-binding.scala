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

import com.thoughtworks.binding.Binding.{Var, Vars}

case class Position(x: Int, y: Int)

// simple implementation of list (data model for virtual list-view)
case class ListModel[A](
                       // size of the list
                         size: Var[Int],
                       // content
                         data: Vars[A],
                       // total amount of data
                         total: Var[Long],
                         offset: Var[Long],
                         lastDragPosition: Var[Option[Position]]
                       ) {
}

object ListModel {
  def apply[A](): ListModel[A] = ListModel(Var(5), Vars.empty[A], Var(0), Var(0), Var(None))
}
