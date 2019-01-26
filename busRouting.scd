s.boot;

(
/// Put cursor here, press ctrl-enter'

// Modify path to some wav file.
~filePath = "~/Documents/SC/wavs/guitar_riff.wav".standardizePath.postln ;

b = Buffer.read(s, ~filePath); // remember to free the buffer later.

// Create In and out group
~inputGroup = Group.new(s);
~outputGroup = Group.after(~inputGroup);

// Create 2 audio busses with 2 channels
~bus0 = Bus.audio(s, 2);
~bus1 = Bus.audio(s, 2);

~nullBus = Bus.audio(s, 2);
// define synths
SynthDef.new(\stereoIn, {
	arg buffer = 0, outBus;
	var source;

	// Set source to repeat audio from buffer
	source = PlayBuf.ar(2, buffer, BufRateScale.kr(buffer), loop: 1.0);

	// Output the source to the audio
	//    bus from args
	Out.ar(outBus, source);

}).add; // Add synth to server


SynthDef.new(\stereoRoute, {
	arg inBus, outBus;
	var source;

	// Set source to audio from input bus
	//    Specify 2 channels
	source = In.ar(inBus, 2);

	// Output source to outbus
	Out.ar(outBus, source);
}).add;

)

// [0,1] is multichannel output.

// Create a stereoIn instance using buffer, output to defualt
//     buffer -> Default output
~stIn = Synth(\stereoIn, [buffer: b, outBus: [0,1]], ~inputGroup);


// Send audio from input to bus0 (No more output)
//     buffer -> bus0
~stIn.set(\outBus, ~bus0);

// Create a stereoRoute instance using bus0 as input, default output
//     bus0 -> Default output
//     audio should come back
~stRoute = Synth(\stereoRoute, [inBus: ~bus0, outBus: [0,1]], ~outputGroup);

// Route audio to bus1 (No more output)
//     bus0 -> bus1
~stRoute.set(\outBus, ~bus1);

// Create a stereoRoute instance using bus0 as input, default output
//     bus1 -> Default output
//     Use after to make sure that is the final node in the tree.
~stFinal = Synth.after(~stRoute,\stereoRoute, [inBus: ~bus1, outBus: [0,1]], ~outputGroup );

// Stop sending output from input
~stIn.set(\outBus, ~nullBus);

// Start sending input again.
~stIn.set(\outBus, ~bus0);

// These show you the signal on the busses
~bus0.scope;
~bus1.scope;
