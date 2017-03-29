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

import java.lang.Math.pow

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.dom

case class MonthlyPayment(monthNb: Int,
                          payment: Double,
                          principal: Double,
                          interrestPayment: Double,
                          loan: Double)

case class Loan(loan: Var[Double],
                interrestRate: Var[Double],
                amortization: Var[Int]) {
  @dom val interrestRatePerMonth = interrestRate.bind / 12
  @dom val amortizationInMonth = amortization.bind * 12

  /**
    * @return the amount of the monthly payment of the loan
    */
  @dom val monthlyPayment = {
    val term = pow((1 + interrestRatePerMonth.bind / 100), amortizationInMonth.bind)

    (loan.bind * interrestRatePerMonth.bind * term / 100) / (term - 1)
  }


  @dom def results = {
    val irpm = interrestRatePerMonth.bind / 100
    val mp = monthlyPayment.bind
    val nbMonth = amortizationInMonth.bind

    def compute(n: Int, loanBalance: Double): Seq[MonthlyPayment] = {
      val monthlyInterest = irpm * loanBalance
      val principal = mp - monthlyInterest

      if (n > nbMonth)
        Seq.empty
      else
        MonthlyPayment(n, mp, principal, monthlyInterest, loanBalance - principal) +: compute(n + 1, loanBalance - principal)
    }

    Constants(compute(1, loan.bind): _*)
  }
}
