circuit HelloWorld :
  module HelloWorld :
    input clock : Clock
    input reset : UInt<1>
    output io_led : UInt<1>

    reg cntReg : UInt<32>, clock with :
      reset => (UInt<1>("h0"), cntReg) @[Helloworld.scala 21:23]
    reg blkReg : UInt<1>, clock with :
      reset => (UInt<1>("h0"), blkReg) @[Helloworld.scala 22:23]
    node _cntReg_T = add(cntReg, UInt<1>("h1")) @[Helloworld.scala 24:20]
    node _cntReg_T_1 = tail(_cntReg_T, 1) @[Helloworld.scala 24:20]
    node _T = eq(cntReg, UInt<25>("h17d783f")) @[Helloworld.scala 25:15]
    node _blkReg_T = not(blkReg) @[Helloworld.scala 27:15]
    node _GEN_0 = mux(_T, UInt<1>("h0"), _cntReg_T_1) @[Helloworld.scala 24:10 25:28 26:12]
    node _GEN_1 = mux(_T, _blkReg_T, blkReg) @[Helloworld.scala 25:28 27:12 22:23]
    io_led <= blkReg @[Helloworld.scala 29:10]
    cntReg <= mux(reset, UInt<32>("h0"), _GEN_0) @[Helloworld.scala 21:{23,23}]
    blkReg <= mux(reset, UInt<1>("h0"), _GEN_1) @[Helloworld.scala 22:{23,23}]
