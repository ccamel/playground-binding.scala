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
package com.ccm.me.playground.bindingscala.loancalculator

import com.ccm.me.playground.bindingscala.ShowCase
import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.Node
import org.scalajs.dom.raw.{Event, HTMLInputElement}

import scala.reflect.{ClassTag, classTag}

class ui extends ShowCase {
  val loan = Loan( Var(10000d), Var(0.5), Var(1) )

  @dom override def css: Binding[BindingSeq[Node]] = <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.0/css/materialize.min.css"/>
      <style>
        {"""
        h5 {
          font-size: 1.10rem;
        }

        input.centered {
          text-align: center;
        }

        span.underlined {
          text-decoration: underline;
        }
        """}
      </style>

  @dom def render: Binding[Node] =
    <div class="container">
      {renderContent.bind}
    </div>

  @dom def renderContent = {
    def handleChange[T : ClassTag](e: Event, to: Var[T] ) = {
      val i = e.target.asInstanceOf[HTMLInputElement]

      try{
        to match {
          case aDouble: Var[Double @unchecked] if classTag[T] == classTag[Double] ⇒ aDouble.value = i.value.toDouble
          case aInt: Var[Int @unchecked] if classTag[T] == classTag[Int] ⇒ aInt.value = i.value.toInt
        }
        i.setAttribute("class", i.getAttribute("class").replace(" invalid","") )
      } catch {
        case e: Exception =>
          i.setAttribute("class", i.getAttribute("class").replace(" invalid","") + " invalid" )
      }
    }
    <div>
      <div class="container">
        <h5>Loan Calculator</h5>
        <hr/>
        <p>Enter the loan amount, interest rate and duration (in years) in the fields below</p>
        <div class="row">
          <div class="input-field col s3">
            <input placeholder="2000" id="amount-borrowed" type="text" class="centered" value={loan.loan.bind.toString}
                   onchange={e: Event => handleChange(e, loan.loan)}/>
            <label for="amount-borrowed" data:data-error="Value must be a number">Loan amount ($)</label>
          </div>
          <div class="input-field col s3">
            <input placeholder="0.5" id="interrest-rate" type="text" class="centered" value={loan.interrestRate.bind.toString}
                   onchange={e: Event => handleChange(e, loan.interrestRate)}/>
            <label for="interrest-rate" data:data-error="Value must be a decimal">Interest rate (%)</label>
          </div>
          <div class="input-field col s3">
            <input placeholder="0.5" id="amortization" type="text" class="centered" value={loan.amortization.bind.toString}
                   onchange={e: Event => handleChange(e, loan.amortization)}/>
            <label for="amortization" data:data-error="Value must be a decimal">Amortization (years)</label>
          </div>
        </div>
      </div>
      <div class="container">
        <h5>Loan Summary</h5>
        <hr/>
        <p>For a <span class="underlined">${loan.loan.bind.toString}</span> loan at <span class="underlined">{loan.interrestRate.bind.toString}%</span>
          with a <span class="underlined">{loan.amortization.bind.toString}</span> year(s) amortization ({loan.amortizationInMonth.bind.toString} payments), your monthly payment will
          be <span class="underlined">${f"${loan.monthlyPayment.bind}%.2f"}</span>.</p>
      </div>
      <br/>
      <div class="container">
        <h5>Amortization table:</h5>
        <hr/>
        <table class="bordered">
          <thead>
            <tr>
              <th>Month</th>
              <th>Payment</th>
              <th>Principal</th>
              <th>Interrest Payment</th>
              <th>Balance</th>
            </tr>
          </thead>

          <tbody>
            {
              for {line <- loan.results.bind} yield {
                <tr>
                  <td>{line.monthNb.toString}</td>
                  <td>${f"${line.payment}%.2f"}</td>
                  <td>${f"${line.principal}%.2f"}</td>
                  <td>${f"${line.interrestPayment}%.2f"}</td>
                  <td>${f"${line.loan}%.2f"}</td>
                </tr>
              }
          }
          </tbody>
        </table>
      </div>
    </div>
  }

  override def name: String = "playground-binding.scala/loan-calculator"
  override def description: String =
    """
      |A Simple Loan Calculator with amortization table
    """.stripMargin
  override def link: String = s"#playground-binding.scala/loan-calculator"
  override def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/1RxSQj6/1")

}