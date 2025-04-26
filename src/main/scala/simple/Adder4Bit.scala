package simple

import chisel3._

class Adder4Bit extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val sum = Output(UInt(4.W))
  })

  io.sum := io.a + io.b
}



object Adder4BitApp extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new Adder4Bit(),Array("--target-dir","generated"))
}