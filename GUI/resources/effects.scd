

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
	signal = FreeVerb.ar(signal, p1, p2, p3, p4);

	Out.ar(out, signal);
}).add;  // Add to the server !!!!! Dont forget to do this

SynthDef("PulseSig", {
	arg in, out, p1, p2, p3, p4;
	var signal;

	signal = In.ar(in, 2);
	signal = signal * LFPulse.kr(2);
	Out.ar(out, signal);
}).add;

// Add simple Sine synthdef
SynthDef("SimpleSine", {
	arg in = 0, out = 0, p1 = 0, p2 = 0 , p3 = 0, p4 = 0;
	var signal, freq;

	// Use frequencies 60 - 440
	freq = (p1 * 380) + 60;

	// Create Sine Oscillator (make stereo)
	signal = SinOsc.ar(freq, p2, p3, p4)!2;

	// Output to general out bus
	Out.ar(out, signal);

}).add; // Add to the server

// Add simple Square synthdef
SynthDef("SimpleSquare", {
	arg in = 0, out = 0, p1 = 0, p2 = 0 , p3 = 0, p4 = 0;
	var signal, freq;

	// Use frequencies 60 - 440
	freq = (p1 * 380) + 60;

	// Create Square Oscillator (make stereo)
	signal = LFPulse.ar(freq, p2, p3, p4)!2;

	// Output to general out bus
	Out.ar(out, signal);

}).add; // Add to the server

// Add simple Sine synthdef
SynthDef("SimpleSaw", {
	arg in = 0, out = 0, p1 = 0, p2 = 0 , p3 = 0, p4 = 0;
	var signal, freq;

	// Use frequencies 60 - 440
	freq = (p1 * 380) + 60;

	// Create Saw Oscillator (make stereo)
	signal = LFSaw.ar(freq, p2, p3, p4)!2;

	// Output to general out bus
	Out.ar(out, signal);

}).add; // Add to the server
