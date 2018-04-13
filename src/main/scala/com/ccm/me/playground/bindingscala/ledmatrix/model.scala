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

import com.thoughtworks.binding.Binding.Var

case class Screen(w: Int, h: Int) {
  val cells: Array[Array[Var[Int]]] = Array.tabulate(w, h) { (_, _) => Var(0xFFFFFF) }

  @inline def apply(x: Int, y: Int, c: Int): Unit = cells(x)(y).value = c

  @inline def apply(x: Int, y: Int, r: Int, g: Int, b: Int): Unit = this (x, y, Screen.rgb2int(r, g, b))

  def apply(from: Screen): Unit = {
    for (x <- 0 until w;
         y <- 0 until h) {
      this (x, y, from.cells(x)(y).value)
    }
  }

  @inline def clear(c: Int): Unit = foreach {
    _.value = c
  }

  def foreach[U](f: Var[Int] => U): Unit = cells.flatten.foreach(f)

  def buffer(): Screen = {
    val b = Screen(w, h)
    b.apply(this)
    b
  }

  def --(qt: Int): Unit = foreach { c =>
    val (r, g, b) = Screen.int2rgb(c.value)
    c.value = Screen.rgb2int((r - qt).max(0), (g - qt).max(0), (b - qt).max(0))
  }
}


object Screen {
  def apply(): Screen = {
    Screen(60, 34)
  }

  @inline def int2rgb(c: Int): (Int, Int, Int) = ((c & 0xff0000) >> 16, (c & 0xff00) >> 8, c & 0xff)

  @inline def rgb2int(r: Int, g: Int, b: Int): Int = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff)
}
