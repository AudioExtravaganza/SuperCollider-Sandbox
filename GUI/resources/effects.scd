SynthDef.new(\PlayBuffer, {
		arg buffer = 0, out;
		var source;

		// Set source to repeat audio from buffer
		source = PlayBuf.ar(1, buffer, BufRateScale.kr(buffer), loop: 1.0)!2;

		// Output the source to the audio
		//    bus from args
		Out.ar(out, source);

}).add; // Add synth to server



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



SynthDef( \recLoop, {
	arg inBus = 0, clockBus = 0, gate = 1, buffer, loopSamples;

	var inSignal;
	var timer = Line.kr(0, loopSamples, loopSamples);
	var trigGate = 1 - (gate + Done.kr(timer));

	// the signal to be recorded
	inSignal = In.ar(inBus, 2);

	// send time used for recording back to language
	SendTrig.kr(trigGate, 0, timer);

			// release synth when recGate closes
	FreeSelf.kr(trigGate);

	// record input signal
	RecordBuf.ar(inSignal, buffer, In.kr(clockBus), recLevel: 1, preLevel: 1);

}).add;

SynthDef( \playLoop, {
	arg outBus, clockBus, buffer, loopSamples;

	// Index the buffer using Phasor
	Out.kr(clockBus, Phasor.kr(0, 1.0, 0.0, loopSamples, 0.0));

	// Send the contents of the buffer to output
	Out.ar(outBus, PlayBuf.ar(2, buffer, loop:1));
}).add;