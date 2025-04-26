package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class Adder4BitTester extends AnyFlatSpec with ChiselScalatestTester {
  "Add4" should "correctly add two 4-bit numbers" in {
    test(new Adder4Bit).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // 测试基本加法
      dut.io.a.poke(2.U)
      dut.io.b.poke(3.U)
      dut.io.sum.expect(5.U)

      // 测试边界情况
      dut.io.a.poke(0.U)
      dut.io.b.poke(0.U)
      dut.io.sum.expect(0.U)

      // 测试最大值相加 (15 + 1 = 0 因为4位溢出)
      dut.io.a.poke(15.U)
      dut.io.b.poke(1.U)
      dut.io.sum.expect(0.U)

      // 测试所有可能的输入组合
      for (a <- 0 until 16) {
        for (b <- 0 until 16) {
          val expected = (a + b) % 16 // 4位模运算
          dut.io.a.poke(a.U)
          dut.io.b.poke(b.U)
          dut.io.sum.expect(expected.U)
        }
      }

      // 测试一些特定组合
      val testCases = Seq(
        (1, 1, 2),
        (4, 5, 9),
        (8, 8, 0), // 8 + 8 = 16 -> 0 (溢出)
        (15, 15, 14) // 15 + 15 = 30 -> 14 (30-16=14)
      )

      testCases.foreach { case (a, b, expected) =>
        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)
        dut.io.sum.expect(expected.U)
      }
    }

  }
}