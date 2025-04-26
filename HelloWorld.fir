FIRRTL version 1.2.0
circuit HelloWorld :
  module HelloWorld :
    input clock : Clock
    input reset : UInt<1>
    output io : { led : UInt<1>} @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 16:14]

    reg cntReg : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 21:23]
    reg blkReg : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 22:23]
    node _cntReg_T = add(cntReg, UInt<1>("h1")) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 24:20]
    node _cntReg_T_1 = tail(_cntReg_T, 1) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 24:20]
    cntReg <= _cntReg_T_1 @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 24:10]
    node _T = eq(cntReg, UInt<25>("h17d783f")) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 25:15]
    when _T : @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 25:28]
      cntReg <= UInt<1>("h0") @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 26:12]
      node _blkReg_T = not(blkReg) @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 27:15]
      blkReg <= _blkReg_T @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 27:12]
    io.led <= blkReg @[ws/chisel-lyon/src/main/scala/simple/Helloworld.scala 29:10]

