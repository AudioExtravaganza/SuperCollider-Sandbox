(

s.waitForBoot({

	// Change this to the location that you have cloned to
	~src = "~/Desktop/SuperCollider-Sandbox/GUI/".standardizePath;

	// Loads all scenes into a list called ~scenes
	(~src ++ "/resources/scenes.scd").load;

	// Loads all synthdefs for effects into server
	(~src ++ "/resources/effects.scd").load;



	// Debugging on, send data over OSC
	g = DAMGUI.new();
	g.build(true, true);

	// Setup the server
	c = DAMPedalController.new();
	c.build(s);

	// For all scenes in scenes.scd
	~scenes.do{
		arg item;
		// Add to the GUI
		g.addScene(item.getName());

		// Add scene to the controller
		c.addScene(item.getName(), item);
	};



	// Bind all OSC messages
	c.bindOSC('/Knob 1', 'k', 1);
	c.bindOSC('/Knob 2', 'k', 2);
	c.bindOSC('/Knob 3', 'k', 3);
	c.bindOSC('/Knob 4', 'k', 4);

	c.bindOSC('/Pedal 1', 'p', 1);
	c.bindOSC('/Pedal 2', 'p', 2);
	c.bindOSC('/Pedal 3', 'p', 3);

	c.bindOSC('/Menu', 'm');

	g.forceOSCUpdate;

	// Setup wav file as input, change to false to disable
	if(true){
		// Change this path to point to your WAV file
		~inputWav = "~/Documents/SC/wavs/guitar_riff.wav".standardizePath.postln ;
		~wavBus = Bus.audio(s, 2);
		~wavBuf = Buffer.read(s, ~inputWav); // remember to free the buffer later.
		~wavSynth = Synth.new("PlayBuffer", [\buffer, ~wavBuf,  \out, ~wavBus], c.inGroup);
		c.setInputBus(~wavBus);
	};

	// Free the controller, then stop the server.
	g.addCloseAction({c.free; s.quit;});
});

)

s.scope;

//Good for debugging

OSCFunc.trace(true, true); // Show OSC messages
OSCFunc.trace(false); // Stop showing OSC messages
s.queryAllNodes; // Show all groups and nodes

