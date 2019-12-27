import com.ccm.me.playground.bindingscala.calc._
import org.scalatest._
import org.scalatest.prop._

import scala.collection.immutable._
import scala.util.Success
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CalcModelSpec extends AnyFlatSpec with TableDrivenPropertyChecks with Matchers {
  val examples =
    Table(
      ("tokens", "result"),
      ("", Success(0d)),
      ("0 0", Success(0d)),
      ("5", Success(5d)),
      ("21", Success(21d)),
      ("2 + 3", Success(3d)),
      ("2 + 3 =", Success(5d)),
      ("20 + 30 =", Success(50d)),
      ("2 - 3 =", Success(-1d)),
      ("2 * 3 =", Success(6d)),
      ("2 / 3 =", Success(2d / 3d)),
      ("2 + 3 *", Success(3d)),
      ("2 + 3 * 6", Success(6d)),
      ("2 * 3 +", Success(6d)),
      ("2 + 3 * 1", Success(1d)),
      ("2 + 3 * 1 -", Success(5d)),
      ("2 + 3 * 1 - 5 =", Success(0d)),
      ("2 + 3 * 1 - 5 * 12 =", Success(-55d)),
      ("12 + +", Success(24d)),
      ("12 + + =", Success(48d)),
      ("0012 + 0021 =", Success(33d))
    )

  it should "produce correct calculation" in {
    forAll(examples) { (tokens, result) =>
      val calc = parseTokens(tokens).foldLeft(CalcModel())((calc, token) => calc(token))

      calc.result should equal(result)
    }
  }

  private def parseTokens(s: String): Seq[Token] = {
    s.filter(!_.isSpaceChar).map {
      case n if n.isDigit => Digit(n.asDigit)
      case '.' => Dot()
      case '+' => Plus()
      case '-' => Minus()
      case '*' => Multiply()
      case '/' => Divide()
      case 'c' => Clear()
      case '=' => Result()
      case c@_ => fail(s"Unexpected char: $c")
    }
  }
}
