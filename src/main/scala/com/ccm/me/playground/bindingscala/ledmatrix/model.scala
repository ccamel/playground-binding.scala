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

import scala.collection.mutable

case class Screen(w: Int, h: Int) extends PartialFunction[(Int, Int), Screen] with Iterable[Var[Int]] {
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

  def buffer(): Screen = {
    val b = Screen(w, h)
    b.apply(this)
    b
  }

  def --(qt: Int): Unit = foreach { c =>
    val (r, g, b) = Screen.int2rgb(c.value)
    c.value = Screen.rgb2int((r - qt).max(0), (g - qt).max(0), (b - qt).max(0))
  }

  override def iterator: Iterator[Var[Int]] = cells.flatten.iterator

  override protected[this] def newBuilder: mutable.Builder[Var[Int], Seq[Var[Int]]] = new mutable.ListBuffer

  override def isDefinedAt(x: (Int, Int)): Boolean = (cells runWith (_ isDefinedAt x._2)) (x._1)
}


object Screen {
  def apply(): Screen = {
    Screen(60, 34)
  }

  @inline def int2rgb(c: Int): (Int, Int, Int) = ((c & 0xff0000) >> 16, (c & 0xff00) >> 8, c & 0xff)

  @inline def rgb2int(r: Int, g: Int, b: Int): Int = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff)

  @inline def hsl2rgb(hsl: (Float, Float, Float)): (Int, Int, Int) = {
    import math.round

    val (h, s, l) = hsl

    def normalize(q: Float, p: Float, c: Float): Int = round(c match {
      case c if c < 1.0f => p + (q - p) * c
      case c if c < 3.0f => q
      case c if c < 4.0f => p + (q - p) * (4.0f - c)
      case _ => p
    })

    if (s > 0.0f) {
      val h2 = if (h < 1.0f) h * 6.0f else 0.0f

      val q = l + s * (if (l > 0.5f) 1.0f - l else l)
      val p = 2.0f * l - q

      (normalize(q, p, if (h2 < 4.0f) h2 + 2.0f else h2 - 4.0f),
        normalize(q, p, h2),
        normalize(q, p, if (h2 < 2.0f) h2 + 4.0f else h2 - 2.0f))
    } else {
      val v = round(l)
      (v, v, v)
    }
  }


}
