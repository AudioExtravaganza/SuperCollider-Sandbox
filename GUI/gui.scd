(


// Add simple Sine synthdef
SynthDef("SimpleSine", {
	arg in = 0, p1 = 0, p2 = 0 , p3 = 0, p4 = 0;
	var signal, freq;

	// Use frequencies 60 - 440
	freq = (p1 * 380) + 60;

	// Create Sine Oscillator (make stereo)
	signal = SinOsc.ar(freq, p2, p3, p4)!2;

	// Output to general out bus
	Out.ar(0, signal);

}).add; // Add to the server

// Debugging on, send data over OSC
g = DAMGUI.new();
g.build(true, true);

// Add the synth to the GUI
g.addSynth("SimpleSine");

// Setup the server
c = DAMPedalController.new();
c.build(s);

// Bind all OSC messages
c.bindOSC('/Knob 1', 'k', 1);
c.bindOSC('/Knob 2', 'k', 2);
c.bindOSC('/Knob 3', 'k', 3);
c.bindOSC('/Knob 4', 'k', 4);

c.bindOSC('/Pedal 1', 'p', 1);
c.bindOSC('/Pedal 2', 'p', 2);
c.bindOSC('/Pedal 3', 'p', 3);

c.bindOSC('/Menu', 'm');

// Forcefully send OSC message to make GUI
//    match server
g.forceOSCUpdate;

g.addCloseAction({c.free});
)

