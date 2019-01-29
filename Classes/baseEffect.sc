

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
	var effectChain;


}

AudioRoute : Bus {
	// Route in/out can only be set with specific methods
	//     can be read
	var routeIn;
	var routeOut;
	var channels = 2;
	// Bus can only be read,
	//    it is maintained by this instance

	*new {

	}

	bindIn {
		arg routeIn;
		var source;
		this.routeIn = routeIn;
		source = In.ar(this.routeIn, this.channels);
		Out.ar(this, source);
	}

	bindOut {
		arg routeOut;
		var source;

		this.routeIn = routeIn;

		source = In.ar(this, this.channels);
		Out.ar(this.routeOut, channels);
	}

	free {
		super.free;
	}
}

Effect {


}