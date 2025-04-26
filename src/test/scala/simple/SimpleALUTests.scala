// See LICENSE.txt for license details.
package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.util.Random

class SimpleALUTests extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "SimpleALU"

  it should "perform correct math operations with random inputs" in {
    test(new SimpleALU) { dut =>
      val rnd = new Random(42) // 固定随机种子以便复现

      // 测试64组随机输入
      for (_ <- 0 until 64) {
        val a = rnd.nextInt(16)
        val b = rnd.nextInt(16)
        val opcode = rnd.nextInt(4)

        // 计算预期结果
        val expected = opcode match {
          case 0 => (a + b) & 0xF    // 加法并截断到4位
          case 1 => (a - b) & 0xF     // 减法并截断到4位
          case 2 => a                 // 直接输出a
          case 3 => b                 // 直接输出b
        }

        // 驱动输入
        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)
        dut.io.opcode.poke(opcode.U)

        // 推进时钟
        dut.clock.step()

        // 验证输出
        dut.io.out.expect(expected.U)
      }
    }
  }

  // 可选：使用Verilator后端测试
  it should "work with Verilator backend" in {
    test(new SimpleALU).withAnnotations(Seq(VerilatorBackendAnnotation)) { dut =>
      val rnd = new Random(42)

      // 测试10组随机输入
      for (_ <- 0 until 10) {
        val a = rnd.nextInt(16)
        val b = rnd.nextInt(16)
        val opcode = rnd.nextInt(4)

        val expected = opcode match {
          case 0 => (a + b) & 0xF
          case 1 => (a - b) & 0xF
          case 2 => a
          case 3 => b
        }

        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)
        dut.io.opcode.poke(opcode.U)
        dut.clock.step()
        dut.io.out.expect(expected.U)
      }
    }
  }
}