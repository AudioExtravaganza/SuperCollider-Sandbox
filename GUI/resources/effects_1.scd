
SynthDef(\Chorus, {
	arg in, out;
	var source;
	var delay = \p2.kr(0.5) * 0.1;
	var fx;


	source = In.ar(in, 2);
	fx = Mix.fill(16, {

		//%half+(quarter*LPF.ar(WhiteNoise.ar,rrand(1.0,10)))

		DelayC.ar(source, delay, LFNoise1.kr(Rand(0.1,0.5),0.01,0.02))
	});
	// Out.ar(out, fx);
}).add;