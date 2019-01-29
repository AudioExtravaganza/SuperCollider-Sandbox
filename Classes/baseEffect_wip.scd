

// Platform.userExtensionDir.postln;
// Want this file to be in that folder
// once added use ctrl-shift-L to recompile class libs

EffectContainer : Group {

	// Default output to stereo system out.
	// !REFACTOR! Pending BBx15 setup might need to change these values
	// All instances share these (like static)
	classvar masterIn = #[0,1];
	classvar masterOut = #[0,1];

	var group;
	var busIn, busOut;
	var signal;
	var effectChain;


}

// Basic route. 1 input bus, 1 output bus

Effect{


	// Route in/out can only be set with specific methods
	//     can be read
	var <>busIn, <>busOut;
	var <>signal;
	var <>outSynth;
	var <>channels = 2;
	var <>group;
	var <>effect = -1;
	// Bus can only be read,
	//    it is maintained by this instance
	new {
		arg channels, group = Server.internal;
		this.group = group;
		this.channels = channels;
		this.effect

		^this;
	}


	bindIn {
		arg busIn;
		var source;
		this.busIn = busIn;
	}

	mod {
		this.signal = SinOsc.ar(440,0, 1, 0);
	}

	start {
		arg busOut;
		var source;
		this.busOut = busOut;
		this.outSynth = this.effect.play();
	}

	bind {
		this.signal = In.ar(this.busIn, this.channels);
		this.outSynth = Out.ar(this.busOut, this.signal);
	}

	stop {
		this.outSynth.free;
	}


}