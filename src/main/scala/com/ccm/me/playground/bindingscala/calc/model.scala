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
package com.ccm.me.playground.bindingscala.calc

import scala.annotation.tailrec
import scala.collection.+:
import scala.util.{Failure, Success, Try}

trait Priority {
  val priority: Int
}

sealed trait Token

final case class NoOp() extends Token

final case class Clear() extends Token

trait Decimal extends Token

final case class Digit(v: Int) extends Decimal

final case class Dot() extends Decimal

trait Op extends Token with Priority

final case class Plus() extends Op {
  val priority = 1
}

final case class Minus() extends Op {
  val priority = 1
}

final case class Multiply() extends Op {
  val priority = 2
}

final case class Divide() extends Op {
  val priority = 2
}

final case class Result() extends Op {
  val priority = 0
}

trait Memory extends Token

final case class MR() extends Memory

final case class MC() extends Memory

final case class MS() extends Memory

case class CalcModel(outputs: Seq[Double],
                     operators: Seq[Op],
                     state: Symbol,
                     accumulator: String,
                     memory: Option[Double]) extends Immutable with PartialFunction[Token, CalcModel] {

  def result: Try[Double] = state match {
    case 'error => Failure(new IllegalStateException("error"))
    case _ => accumulator match {
      case "" => Success(0d)
      case x => Success(x.toDouble)
    }
  }

  def apply(t: Token): CalcModel = {
    state match {
      case 'accum => t match {
        case NoOp() => doNothing()
        case Clear() => doClear()

        case d@Digit(_) => doAccumulate(d)
        case d@Dot() => doAccumulate(d).go('dot)

        case op: Op => doOperator(op).go('operator)

        case MS() => doMS().go('operator)
        case MR() => doMR().go('operator)
        case MC() => doMC().go('operator)
      }

      case 'operator => t match {
        case NoOp() => doNothing()
        case Clear() => doClear()

        case d@Digit(_) => doResetAccu().doAccumulate(d).go('accum)
        case d@Dot() => doResetAccu().doAccumulate(d).go('dot)

        case op: Op => doOperator(op)

        case MS() => doMS()
        case MR() => doMR()
        case MC() => doMC()
      }

      case 'dot => t match {
        case NoOp() => doNothing()
        case Clear() => doClear()

        case d@Digit(_) => doAccumulate(d)
        case Dot() => doNothing()

        case op: Op => doOperator(op).go('operator)

        case MS() => doMS().go('operator)
        case MR() => doMR().go('operator)
        case MC() => doMC().go('operator)
      }
      case 'error => t match {
        case Clear() => doClear()
        case _ => doNothing()
      }
    }
  }

  private def doAccumulate(d: Decimal): CalcModel = copy(accumulator = d match {
    case Digit(0) if accumulator.isEmpty => ""
    case Digit(n) => accumulator + n.toString
    case Dot() => accumulator + '.'
  })

  private def doOperator(op: Op): CalcModel = {
    @tailrec
    def reduce(c: CalcModel, op: Op): CalcModel = {
      c.operators match {
        case head +: tail if op.priority <= head.priority =>
          val args = c.outputs.take(2)
          val result = head match {
            case Plus() => args(1) + args(0)
            case Minus() => args(1) - args(0)
            case Multiply() => args(1) * args(0)
            case Divide() => args(1) / args(0)
          }
          reduce(c.copy(operators = tail, outputs = result +: c.outputs.drop(2), accumulator = result.toString), op)
        case _ => c
      }
    }

    val c2 = copy(outputs = result.getOrElse(0d) +: outputs)

    val c = reduce(c2, op)
    op match {
      case Result() => c
      case _ => c.copy(operators = op +: c.operators)
    }
  }

  private def doResetAccu() = copy(accumulator = "")

  private def doClear() = CalcModel()

  private def doMS() = copy(memory = Some(result.getOrElse(0d)))

  private def doMC() = copy(memory = None)

  private def doMR() = memory.map(it => copy(accumulator = it.toString())).getOrElse(doNothing())

  private def doNothing() = this

  private def go(s: Symbol) = copy(state = s)

  override def isDefinedAt(t: Token): Boolean = if (state == 'error) t == Clear() else
    t match {
      case NoOp() => true
      case Clear() => true

      case d@Digit(_) => true
      case d@Dot() => state != 'dot

      case op: Op => true

      case MS() => true
      case MR() => memory.isDefined
      case MC() => memory.isDefined
    }

  override def toString(): String = s"s: $state - accum: $accumulator - outputs: $outputs - operators: $operators - memory: $memory"
}


object CalcModel {
  def apply(): CalcModel = {
    CalcModel(Seq.empty, Seq.empty, 'accum, "", None)
  }
}

