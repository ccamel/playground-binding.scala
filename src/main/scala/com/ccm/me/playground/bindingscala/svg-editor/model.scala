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
package com.ccm.me.playground.bindingscala.svgeditor

import com.thoughtworks.binding.Binding.Var

trait Label {
  def label: Var[String]
}

trait Shape extends Label

case class RectShape(x: Var[Int],
                     y: Var[Int],
                     w: Var[Int],
                     h: Var[Int],
                     label: Var[String]) extends Shape

case class CircleShape(x: Var[Int],
                       y: Var[Int],
                       r: Var[Int],
                       label: Var[String]) extends Shape

object RectShape {
  def apply(): RectShape = RectShape(Var(0), Var(0), Var(10), Var(10), Var("hello"))
}
