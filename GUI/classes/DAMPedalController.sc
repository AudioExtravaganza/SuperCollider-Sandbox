/*********************************************************************
Extension: DAM Pedal Controller
Authors: Mason Sidebottom
Purpose: Provides backend functionallity for triggering effects
Date: Feb. 2019
*********************************************************************/

DAMPedalController {
	// Dictionary of functions
	var <> receivers;

	// Array of control busses
	var <> knobs;

	// Master groups for pedal
	var <> inGroup;
	var <> outGroup;

	// The active Scences
	var <> currentScene;

	var <> scenes;

	var <> inBus;
	var <> outBus;
	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent (should be the server)
	*******************************************************************/
	build {
		arg parent;
		this.inGroup = Group.new(parent);
		this.outGroup = Group.after(this.inGroup);
		this.outBus = [0,1];
		this.inBus = [0,1];
		this.receivers = Dictionary(0);
		this.knobs = Array.fill(4, {arg i; Bus.control(parent, 1)});
		this.scenes = Dictionary(0);
	}

	/******************************************************************
	Add Scene
		Adds a DAM Scene to the Pedal Controller
		Parameters: name, string to be used as key
					scene a DAMScene
	*******************************************************************/
	addScene {
		arg name, scene;
		this.scenes.add(name -> scene);
	}

	/******************************************************************
	Remove Scene
		Removes a DAM Scene from the Pedal Controller
		Parameters: name, string to be used as key

		! Assumes there is a scene associated with the key
	*******************************************************************/
	removeScene {
		arg name;
		this.scenes.removeAt(name);
	}

	/******************************************************************
	Set Input Bus
		Changes the entire pedals main input
		Parameters: New input bus
	*******************************************************************/
	setInputBus {
		arg bus;
		this.inBus = bus;
		if(this.currentScene != nil){
			this.currentScene.updateInputBus(this.inBus);
		}
	}

	/******************************************************************
	Set Output Bus
		Changes the entire pedals output
		Parameters: New output bus
	*******************************************************************/
	setOutputBus {
		arg bus;
		this.outBus = bus;
		if(this.currentScene != nil){
			this.currentScene.updateOutBus(this.outBus);
		}
	}

	/******************************************************************
	Update Switch
		Alerts current scene of new footswitch value
		Parameters: state, the new switch state
					index, index of the switch
	*******************************************************************/
	updateSwitch {
		arg state, index;
		if(this.currentScene != nil){
			this.currentScene.updateSwitch(state, index);
		};
	}

	/******************************************************************
	Update Scene
		Starts up a new scene if it should be started
		Parameters: name of new scene
	*******************************************************************/
	updateScene {
		arg name;

		// Check that a scene is running
		if(this.currentScene != nil){
			// If we are trying to launch the same scene that is
			//   already running, dont
			if(this.currentScene.getName() == name){
				^-1;
			}{
				// Otherwise, kill it (frees things that are allocated)
				//	 when scene is triggered
				this.currentScene.kill;

			};
		};

		// Update current scene
		this.currentScene = this.scenes.at(name);

		// Trigger the scene
		this.currentScene.trigger(this.inGroup, this.knobs, this.inBus, this.outBus);
	}

	/******************************************************************
	Bind OSC
		Adds listener for a message with start as name, use as handler
			name as well

		Parameters: name, the beggining of the message ex '/Knob 1'
					type, 'k' -> Knob, 'p' -> pedal, 'm' -> menu
					num, index of control (ex pedal 1 -> 1)
	*******************************************************************/
	bindOSC {
		arg name, type, num = 1;
		// Add the function to the dictionary
		this.receivers.add(name -> {
			arg msg, time, addr;

			// If this is the right handler
			if(msg[0] == name){
				// Switch on the type
				switch(type,
					// If knob, update knob bus
					'k', {this.knobs[num - 1].set(msg[1]);},

					// If pedal, update pedal state
					'p', {this.updateSwitch(msg[1], num - 1);},

					// If menu, update menu string
					'm', {this.updateScene(msg[1]);}
				);

			};

		});

		// Add this new function to the OSC receivers
		thisProcess.addOSCRecvFunc(this.receivers.at(name));
	}

	/******************************************************************
	Remove OSC
		Removes listener for a message with start as name

		Parameters: name of handler

		! Assumes handler with name provided exists
	*******************************************************************/
	removeOSC {
		arg name;

		// Remove from OSC Recievers
		thisProcess.removeOSCRecvFunc(this.receivers.at(name));

		// Remove from local Dict
		this.receivers.removeAt(name);
	}

	/******************************************************************
	Free
		Frees all allocated members
	*******************************************************************/
	free {

		// For all recievers
		this.receivers.keys.do{
			arg k;

			// Remove the OSC binding (frees in function)
			this.removeOSC(k);
		};

		// For all knob control busses
		this.knobs.do{
			arg item;

			// Free
			item.free;
		}
	}
}

/*********************************************************************
DAM Scene
	Class that contains effects and effect chains.
*********************************************************************/
DAMScene {

	// Local refference to the Knob busses
	var <> knobs;

	// Array of synthdef names
	var <> effects;

	// Name of this instance
	var <> name;

	// Array of active synths
	var <> synths;

	// Individual groups
	var <> group;

	// Array of local audio busses (stereo)
	var <> busses;

	/******************************************************************
	Prebuild
		Instantiates basic members that are not pertinent to audio
		processing.

		This should be called before sending to the pedal controller
	*******************************************************************/
	prebuild {
		arg name;

		// Set name
		this.name = name;

		// Init knobs and synth to null
		this.knobs = nil;
		this.synths = nil;

		// Set effect names to tombstone values
		this.effects = Array.fill(3, {arg i; -1});
	}

	/******************************************************************
	Get name
		Gets name of scene
	*******************************************************************/
	getName {
		^this.name;
	}

	/******************************************************************
	Trigger
		Allocates and sets up busses and synth routing

		Params: node  -> parent group, will place new group below this
							generall the input group
				knobs -> Array of 4 control busses
				inputbus -> Input audio signal (as audio bus)
				outputBus -> Output audio bus)

		! Do not call this outside of the pedal controller. This should
		! only be called by DAMPedalController::updateScene
	*******************************************************************/
	trigger {
		arg node, knobs, inputBus, outputBus;

		// Add group after node
		// 	it is expexcted  that node is the input group
		this.group = Group.after(node);

		// Do not free busses[0] or busses[3]
		this.busses = [inputBus, Bus.audio(Server.internal, 2), Bus.audio(Server.internal, 2), outputBus];
		this.busses.postln;
		// Reference to knobs !Do not free in this class
		this.knobs = knobs;

		// Create array of size 3
		this.synths = [0, 0, 0];

		// Make route in -> bus[1] -> bus[2] -> out
		// Each arrow can be replaced with an effect.
		this.synths[0] = Synth.new("Route", [\in, this.busses[0], \out, this.busses[1]], this.group);
		this.synths[1] = Synth.after(this.synths[0], "Route", [\in, this.busses[1], \out, this.busses[2]]);
		this.synths[2] = Synth.after(this.synths[1], "Route", [\in, this.busses[2], \out, this.busses[3]]);
	}

	/******************************************************************
	Update Input Bus
		Remaps input bus
		Param: bus -> new bus
	*******************************************************************/
	updateInputBus{
		arg bus;
		this.busses[0] = bus;
	}

	/******************************************************************
	Update Output Bus
		Remaps output bus
		Param: bus -> new bus
	*******************************************************************/
	updateOutBus {
		arg bus;
		this.busses[3] = bus;
	}


	/******************************************************************
	Update Switch
		Toggles individual effects/effect chains
		Params: state -> 1 (on) or 0 (off)
		index -> index of the switch [0,1,2]
	*******************************************************************/
	updateSwitch {
		arg state, index;
		var args;

		// Make sure that synths are defined
		if(this.synths == nil){
			^-1;
		};

		// Make sure that there is a synthdef for this switch
		if(this.effects[index] == -1){
			^-1;
		};

		// Free previous synth (there should always be a synth running
		//	at each index, because of route);
		this.synths[index].free;

		// Predefine args (same for every call)
		args = [\in, this.busses[index], \out, this.busses[index+1]];
		args.postln;

		// If starting a new synth;
		if(state == 1){

			// If this is the first synth, set it before the 2nd synth
			if(index == 0){
				this.synths[index] = Synth.before(this.synths[index + 1], this.effects[index], args);

			// Otherwise, set is after the previous synth
			}{
				this.synths[index] = Synth.after(this.synths[index -1], this.effects[index], args);

			};

			// Map knobs to the synth
			// TODO: Allow mapping to be specified per each synth
			this.synths[index].map(
				\p1, this.knobs[0].index,
				\p2, this.knobs[1].index,
				\p3, this.knobs[2].index,
				\p4, this.knobs[3].index
			);

		// If stopping a synth
		}{
			// If this is the first synth, route the in->out before the 2nd synth
			if(index == 0){
				this.synths[index] = Synth.before(this.synths[index + 1], "Route", args);

			// If this is the first synth, route the in->out after the previous synth
			}{
				this.synths[index] = Synth.after(this.synths[index - 1], "Route", args);
			};
		};
		this.synths[index].postln;
	}

	/******************************************************************
	Set Effect
		Sets effect to specific index;
		Params: effect -> name of some synth def
				index -> index to store the effect at, direct mapping to
						 the switches
	*******************************************************************/
	setEffect {
		arg effect, index;
		this.effects[index] = effect;
	}

	/******************************************************************
	Kill
		Frees all resources allocated by trigger. Allows the same scene
			to be triggered again later without freeing it all.
	*******************************************************************/
	kill {
		// Only get rid of the 2 busses we allocated
		this.busses[1].free;
		this.busses[2].free;

		// Free all synths
		this.synths.do{
			arg item, i;
			if(item != nil){
				item.free;
			};
		};
		// Get rid of this group
		this.group.free;

	}
}
