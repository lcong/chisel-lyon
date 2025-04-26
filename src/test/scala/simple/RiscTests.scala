// See LICENSE.txt for license details.
package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RiscTester extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Risc"

  // 定义指令构建辅助函数
  def buildInstruction(op: UInt, rc: Int, ra: Int, rb: Int): BigInt = {
    ((op.litValue & 0xFF) << 24) |
      ((rc & 0xFF) << 16) |
      ((ra & 0xFF) << 8) |
      (rb & 0xFF)
  }

  it should "execute simple program correctly" in {
    test(new Risc(memSize = 128)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // 定义辅助函数
      def writeMemory(addr: BigInt, data: BigInt): Unit = {
        dut.io.isWr.poke(true.B)
        dut.io.wrAddr.poke(addr.U)
        dut.io.wrData.poke(data.U)
        dut.clock.step()
      }

      def bootProcessor(): Unit = {
        dut.io.isWr.poke(false.B)
        dut.io.boot.poke(true.B)
        dut.clock.step()
      }

      def executeCycle(): Unit = {
        dut.io.isWr.poke(false.B)
        dut.io.boot.poke(false.B)
        dut.clock.step()
      }

      // 测试程序
      val program = Seq(
        buildInstruction(dut.imm_op, 1, 0, 1),  // r1 <- 1
        buildInstruction(dut.add_op, 1, 1, 1),   // r1 <- r1 + r1
        buildInstruction(dut.add_op, 1, 1, 1),   // r1 <- r1 + r1
        buildInstruction(dut.add_op, 255, 1, 0)  // rh <- r1
      )

      // 初始化内存
      writeMemory(0, 0) // 跳过复位
      program.zipWithIndex.foreach { case (inst, addr) =>
        writeMemory(addr, inst)
      }

      // 启动处理器
      bootProcessor()

      // 执行程序
      var cycles = 0
      while (dut.io.valid.peek().litToBoolean == false && cycles < 10) {
        executeCycle()
        cycles += 1
      }

      // 验证结果
      cycles should be < 10 // 确保不超时
      dut.io.out.expect(4.U) // 预期结果: 1+1+1+1 = 4
    }
  }

  // 可选：使用Verilator后端测试
  it should "work with Verilator backend" in {
    test(new Risc).withAnnotations(Seq(VerilatorBackendAnnotation)) { dut =>
      // 简化的测试程序
      val program = Seq(
        buildInstruction(dut.imm_op, 1, 0, 1),  // r1 <- 1
        buildInstruction(dut.add_op, 1, 1, 1)    // r1 <- r1 + r1
      )

      // 写入程序
      dut.io.isWr.poke(true.B)
      program.zipWithIndex.foreach { case (inst, addr) =>
        dut.io.wrAddr.poke(addr.U)
        dut.io.wrData.poke(inst.U)
        dut.clock.step()
      }

      // 启动并执行
      dut.io.isWr.poke(false.B)
      dut.io.boot.poke(true.B)
      dut.clock.step()
      dut.io.boot.poke(false.B)

      var cycles = 0
      while (dut.io.valid.peek().litToBoolean == false && cycles < 5) {
        dut.clock.step()
        cycles += 1
      }

      dut.io.valid.expect(true.B)
      dut.io.out.expect(2.U) // 1+1 = 2
    }
  }
}