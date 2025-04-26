// See LICENSE.txt for license details.
package simple
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MulTester extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Mul (4x4 multiplier using LUT)"

  it should "correctly multiply all combinations of 4-bit inputs" in {
    test(new Mul).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // 测试所有16x16种输入组合
      for {
        x <- 0 until 16
        y <- 0 until 16
      } {
        dut.io.x.poke(x.U)
        dut.io.y.poke(y.U)
        dut.clock.step() // 可选，给信号稳定时间

        val product = x * y
        dut.io.z.expect(product.U,
          s"Multiplication failed: $x * $y should be $product")
      }
    }
  }

  it should "handle edge cases correctly" in {
    test(new Mul) { dut =>
      // 边界值测试
      val edgeCases = Seq(
        ((0, 0), 0), // 最小输入
        ((15, 15), 225), // 最大输入
        ((0, 15), 0), // 零乘数
        ((15, 0), 0), // 零被乘数
        ((1, 15), 15), // 边界值1
        ((15, 1), 15) // 边界值2
      )

      edgeCases.foreach { case ((x, y), expected) =>
        dut.io.x.poke(x.U)
        dut.io.y.poke(y.U)
        dut.io.z.expect(expected.U,
          s"Edge case failed: $x * $y should be $expected")
      }
    }
  }


  it should "work with random inputs" in {
    test(new Mul).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { dut =>
      for (x <- 0 until 16; y <- 0 until 16) {
        dut.io.x.poke(x.U)
        dut.io.y.poke(y.U)
        dut.clock.step()

        val expected = x * y
        dut.io.z.expect(expected.U, s"Failed for $x * $y, expected $expected")
      }
    }
  }
}