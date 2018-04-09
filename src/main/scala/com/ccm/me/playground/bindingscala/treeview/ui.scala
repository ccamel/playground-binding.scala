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

import com.ccm.me.playground.bindingscala.ShowCase
import com.ccm.me.playground.bindingscala.treeview.FileStore.{FSFile, FSFolder}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node
import org.scalajs.dom.raw.Event

import scala.concurrent.ExecutionContext.Implicits.global

class ui extends ShowCase {
  val tree: Tree = Tree.root

  @dom override def css: Binding[BindingSeq[Node]] =
    <style>
      {"""
       div.tree, div.tree ul, div.tree li {
         position: relative;
       }
       div.tree label::before {
         padding-right: 5px;
       }
       div.tree label.fa-folder {
         cursor:pointer;
       }
       div.tree ul {
         list-style: none;
         padding-left: 32px;
       }
       .tree li::before, .tree li::after {
         content: "";
         position: absolute;
         left: -12px;
       }
       .tree li::before {
         border-top: 1px solid #000;
         top: 9px;
         width: 8px;
         height: 0;
       }
       .tree li::after {
         border-left: 1px solid #000;
         height: 100%;
         width: 0px;
         top: 2px;
       }
       .tree ul > li:last-child::after {
         height: 8px;
       }
      """}
    </style>
    <!-- -->

  @dom override def render: Binding[Node] = {
      <div class="container tree">
        <p>Content for the <a href="https://github.com/ukparliament/ontologies">ukparliament/ontologies</a> project:</p>
        <ul>
          {displayTree(tree).bind}
        </ul>
      </div>
  }

  @dom def onTreeNodeClick(tree: TreeNode): Unit = tree.state.value match {
    case UnloadedState =>
      tree.state.value = LoadingState

      FileStore.childrenOf(tree.id).foreach(result => {
        val children = result.map {
          case FSFolder(id, label) => TreeNode(id, Vars.empty, Var(label))
          case FSFile(id, label) => TreeLeaf(id, Var(label))
        }

        tree.nodes.value.clear()
        children.foreach( i => tree.nodes.value += i )

        tree.state.value = LoadedAndExpandedState
      })
    case LoadingState =>
    case LoadedAndExpandedState =>
      tree.state.value = LoadedAndCollapsedState
    case LoadedAndCollapsedState =>
      tree.state.value = LoadedAndExpandedState
  }

  @dom def displayTree(tree: Tree): Binding[Node] = tree match {
    case TreeLeaf(id, label) =>
      <li>
        <label class="fa fa-file">{tree.label.bind}</label>
      </li>
    case node@TreeNode(id, elements, label, state) =>
      <li>
        <i class={s"fa ${treeNodeClass(node).bind}"}/>
        <label for={tree.id} class="fa fa-folder" onclick={_: Event => onTreeNodeClick(node)}>{tree.label.bind}</label>
        <ul class={if(state.bind == LoadedAndCollapsedState) "hide" else ""}>
          {for (e <- elements) yield { displayTree(e).bind}}
        </ul>
      </li>
  }

  @dom def treeNodeClass( node: TreeNode ): Binding[String] = node.state.bind match {
    case UnloadedState => "fa-angle-right fa-fw"
    case LoadingState => "fa-spinner fa-spin fa-fw"
    case LoadedAndExpandedState => "fa-angle-down fa-fw"
    case LoadedAndCollapsedState => "fa-angle-right fa-fw"
  }

  override def name: String = "playground-binding.scala/tree-view"
  @dom override def description: Binding[Node] = <div>tree view with dynamic loading of items</div>
  override def link: String = s"#playground-binding.scala/tree-view"
  override def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/KEznYyM/2")

}

