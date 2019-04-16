BitCrusher : UGen {
	*ar { arg in, rate=44100.0, bits=24, mul=1.0, add=0;
		^this->multiNew('audio', in, rate, bits).madd(mul, add)
	}
}