// See LICENSE.txt for license details.
package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ByteSelectorTester extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "ByteSelector"

  it should "correctly select bytes from input word" in {
    test(new ByteSelector) { dut =>
      val testIn = 12345678  // 0x00BC614E in hex
      val expectedBytes = Seq(0x4E, 0x61, 0xBC, 0x00)  // Little-endian byte order

      for (offset <- 0 until 4) {
        // 设置输入
        dut.io.in.poke(testIn.U)
        dut.io.offset.poke(offset.U)

        // 不需要step()，因为是组合逻辑
        // 验证输出
        dut.io.out.expect(expectedBytes(offset).U)
      }
    }
  }

  // 可选：使用Verilator后端测试
  it should "work with Verilator backend" in {
    test(new ByteSelector).withAnnotations(Seq(VerilatorBackendAnnotation)) { dut =>
      // 测试单个偏移量即可
      dut.io.in.poke(0x12345678.U)
      dut.io.offset.poke(2.U)  // 选择第3个字节(0x34)
      dut.io.out.expect(0x34.U)
    }
  }
}