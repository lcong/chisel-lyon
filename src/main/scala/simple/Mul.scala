// See LICENSE.txt for license details.
package simple

import chisel3._
import chisel3.util._
import scala.collection.mutable.ArrayBuffer

// Problem:
//
// Implement a four-by-four multiplier using a look-up table.
//
class Mul extends Module {
  val io = IO(new Bundle {
    val x   = Input(UInt(4.W))
    val y   = Input(UInt(4.W))
    val z   = Output(UInt(8.W))
  })
  // 使用Seq而不是ArrayBuffer
  val mulsValues = Seq.tabulate(16, 16) { (i, j) =>
    (i * j).U(8.W)
  }.flatten // 将二维Seq展平为一维

  // 使用VecInit创建查找表
  val tbl = VecInit(mulsValues)

  // 计算索引: x * 16 + y
  val index = (io.x << 4.U) | io.y
  io.z := tbl(index)

}


/**
 * An object extending App to generate the Verilog code.
 */
object Mul extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new Mul(),Array("--target-dir","generated"))
}