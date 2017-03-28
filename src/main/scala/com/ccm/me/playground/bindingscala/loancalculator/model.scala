package com.ccm.me.playground.bindingscala.loancalculator

import java.lang.Math.pow

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.dom

case class MonthlyPayment(monthNb: Int,
                          payment: Double,
                          principal: Double,
                          interrestPaiement: Double,
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

      println("n: " + n)
      println("nbMonth: " + nbMonth)

      if (n > nbMonth)
        Seq.empty
      else
        MonthlyPayment(n, mp, principal, monthlyInterest, loanBalance - principal) +: compute(n + 1, loanBalance - principal)
    }

    Constants(compute(1, loan.bind): _*)
  }
}
