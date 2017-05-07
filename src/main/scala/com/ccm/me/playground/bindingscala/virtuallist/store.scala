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

import scala.collection.Seq

// minimal business model implementation
case class Item(id: Long, label: String)

case class Paginate(offset: Long, limit: Int)

case class Page[+T](totalElements: Long, content: Seq[T]) {
  def size(): Int = content.size
}

object ItemStore extends PartialFunction[Paginate, Page[Item]] {
  override def isDefinedAt(p: Paginate): Boolean = p.offset >= 0 && p.offset < count

  def count: Long = 1000000

  override def apply(p: Paginate): Page[Item] = Page(
    count,
    for (n <- (p.offset to p.offset + p.limit).takeWhile( _ < count ))
      yield Item(n, s"This is item $n")
  )
}