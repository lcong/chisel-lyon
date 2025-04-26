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

  // Perform multiplication
  io.z := io.x * io.y

}


/**
 * An object extending App to generate the Verilog code.
 */
object Mul extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new Mul(),Array("--target-dir","generated"))
}