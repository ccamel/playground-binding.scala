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
package com.ccm.me.playground.bindingscala.treeview

import com.thoughtworks.binding.Binding.{Var, Vars}

import scala.collection.Seq
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.timers
import scala.util.Random

// simple implementation of tree structure (data model for tree-view)
sealed trait Tree {
  def id: String

  def label: Var[String]
}

sealed trait State

object UnloadedState extends State

object LoadingState extends State

object LoadedAndExpandedState extends State

object LoadedAndCollapsedState extends State

case class TreeNode(id: String, nodes: Vars[Tree], label: Var[String], state: Var[State] = Var(UnloadedState)) extends Tree

case class TreeLeaf(id: String, label: Var[String]) extends Tree

object Tree {
  def root = TreeNode("0", Vars.empty, Var("/"))
}

// minimal business model
object FileStore {

  abstract class FSElement(id: String, label: String)

  final case class FSFile(id: String, label: String) extends FSElement(id, label)

  final case class FSFolder(id: String, label: String) extends FSElement(id, label)

  def childrenOf(id: String): Future[Seq[FSElement]] = {
    val children = Seq(FSFolder("0", "test"), FSFile("0", "test"))

    // simulate asynchronous load...
    val p = Promise[Seq[FSElement]]()
    timers.setTimeout(new Random().nextInt(2000) + 500) {
      p.success(children)
    }
    p.future
  }
}