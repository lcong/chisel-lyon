// See LICENSE.txt for license details.
package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Random
import scala.collection.mutable.Stack


class SimpleStackTester extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Stack"

  it should "correctly support basic stack operations" in {
    test(new SimpleStack(depth = 8)) { dut =>
      val rnd = new Random(42) // 固定随机种子以便复现
      val refStack = Stack[Int]()
      var expectedDataOut = 0

      // 随机测试16个操作
      for (t <- 0 until 16) {
        val enable = rnd.nextInt(2)
        val push = rnd.nextInt(2)
        val pop = rnd.nextInt(2)
        val dataIn = rnd.nextInt(256)

        // 更新参考模型
        if (enable == 1) {
          if (push == 1 && refStack.size < dut.depth) {
            refStack.push(dataIn)
          } else if (pop == 1 && refStack.nonEmpty) {
            refStack.pop()
          }
          expectedDataOut = if (refStack.nonEmpty) refStack.top else 0
        }

        // 驱动DUT输入
        dut.io.push.poke(push.B)
        dut.io.pop.poke(pop.B)
        dut.io.en.poke(enable.B)
        dut.io.dataIn.poke(dataIn.U)

        // 推进时钟
        dut.clock.step()

        // 验证输出
        dut.io.dataOut.expect(expectedDataOut.U)
      }
    }
  }

  it should "pass specific test sequence" in {
    test(new SimpleStack(depth = 8)) { dut =>
      // 初始状态
      dut.io.push.poke(false.B)
      dut.io.pop.poke(true.B)
      dut.io.dataIn.poke(232.U)
      dut.io.en.poke(true.B)
      dut.clock.step()

      // 打印当前输出
      println(s"dataOut ${dut.io.dataOut.peek().litValue}")

      // 推入90同时弹出
      dut.io.push.poke(true.B)
      dut.io.pop.poke(true.B)
      dut.io.dataIn.poke(90.U)
      dut.clock.step(2) // 推进2个周期

      println(s"dataOut ${dut.io.dataOut.peek().litValue}")

      // 推入33同时弹出
      dut.io.push.poke(true.B)
      dut.io.pop.poke(true.B)
      dut.io.dataIn.poke(33.U)
      dut.clock.step(2)

      println(s"dataOut ${dut.io.dataOut.peek().litValue}")

      // 仅弹出
      dut.io.push.poke(false.B)
      dut.io.pop.poke(true.B)
      dut.io.dataIn.poke(22.U)
      println(s"dataOut ${dut.io.dataOut.peek().litValue}")
    }
  }

  // 可选：使用Verilator后端测试
  it should "work with Verilator backend" in {
    test(new SimpleStack(depth = 8)).withAnnotations(Seq(VerilatorBackendAnnotation)) { dut =>
      // 简单测试推入和弹出
      dut.io.push.poke(true.B)
      dut.io.pop.poke(false.B)
      dut.io.dataIn.poke(123.U)
      dut.io.en.poke(true.B)
      dut.clock.step()

      dut.io.push.poke(false.B)
      dut.io.pop.poke(true.B)
      dut.clock.step()
      dut.io.dataOut.expect(123.U)
    }
  }
}