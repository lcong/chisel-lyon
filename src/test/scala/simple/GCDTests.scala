// See LICENSE.txt for license details.
package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GCDTester extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "GCD"

  it should "correctly compute GCD values" in {
    test(new GCD) { dut =>
      val testCases = Seq(
        ((48, 32), 16),
        ((7, 3), 1),
        ((100, 10), 10)
      )

      for (((a, b), expected) <- testCases) {
        // 设置输入值
        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)

        // 触发计算
        dut.io.load.poke(true.B)
        dut.clock.step()
        dut.io.load.poke(false.B)

        // 等待结果就绪
        var cycles = 0
        while (!dut.io.valid.peek().litToBoolean && cycles < 100) {
          dut.clock.step()
          cycles += 1
        }

        // 验证结果
        if (cycles >= 100) {
          fail(s"Timeout waiting for GCD result for inputs ($a, $b)")
        } else {
          dut.io.out.expect(expected.U)
        }
      }
    }
  }

  // 可选：使用Verilator后端测试
  it should "work with Verilator backend" in {
    test(new GCD).withAnnotations(Seq(VerilatorBackendAnnotation)) { dut =>
      // 测试单个案例即可
      dut.io.a.poke(48.U)
      dut.io.b.poke(32.U)
      dut.io.load.poke(true.B)
      dut.clock.step()
      dut.io.load.poke(false.B)

      var cycles = 0
      while (!dut.io.valid.peek().litToBoolean && cycles < 100) {
        dut.clock.step()
        cycles += 1
      }

      dut.io.out.expect(16.U)
    }
  }
}