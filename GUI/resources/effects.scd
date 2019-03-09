

// This synth def is required by DAMScenes
SynthDef("Route", {
	arg in, out;
	var signal;
	signal = In.ar(in, 2);
	Out.ar(out, signal);
}).add;

// Arbitrary signal modification
SynthDef("Reverb_Base", {
	arg in, out, p1, p2, p3, p4;
	var signal;

	signal = In.ar(in, 2);

	// Meaningless operations
	signal = FreeVerb.ar(signal, p1, p2, 0.5, 1);

	Out.ar(out, signal);
}).add;  // Add to the server !!!!! Dont forget to do this

SynthDef("PulseSig", {
	arg in, out, p1, p2, p3, p4;
	var signal;

	signal = In.ar(in, 2);
	signal = signal * LFPulse.kr(2);
	Out.ar(out, signal);
}).add;


SynthDef.new('PlayBuffer', {
	arg buffer = 0, out;
	var source;

	// Set source to repeat audio from buffer
	source = PlayBuf.ar(1, buffer, BufRateScale.kr(buffer), loop: 1.0)!2;

	// Output the source to the audio
	//    bus from args
	Out.ar(out, source);

}).add; // Add synth to server


SynthDef.new('Distort', {
	arg in, out, p1, p2, p3, p4;
	var signal;

	signal = In.ar(in, 2).distort;


	Out.ar(out, signal);

}).add;

SynthDef.new('DAMLPF', {
	arg in, out, p1, p2 = 1, p3 = 0.5, p4;
	var signal, freq;
	freq = 80 + (p1 * 1600);
	signal = In.ar(in, 2);
	signal = LPF.ar(signal, freq, 1, 0);
	Out.ar(out, signal);

}).add;
