/*********************************************************************
Extension: DAM Pedal Controller
Authors: Mason Sidebottom
Purpose: Provides backend functionallity for triggering effects
Date: Feb. 2019
*********************************************************************/

DAMPedalController {
	// Dictionary of functions
	var <> receivers;

	// Array of pedal states
	var <> pedals;

	// Array of control busses
	var <> knobs;

	// String of current menu item
	var <> menu;

	// Master group for pedal
	var <> group;

	// Array of 3 scences
	var <> activeScenes;

	// Index of the input bus
	var <> busIn;

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent (should be the server)
	*******************************************************************/
	build {
		arg parent;
		this.group = Group.after(parent);
		this.receivers = Dictionary(0);
		this.menu = "";
		this.knobs = Array.fill(4, {arg i; Bus.control(parent, 1)});
		this.pedals = [0, 0, 0];
		this.activeScenes = Array.fill(3, {arg i; nil});
		this.busIn = 0
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
					'p', {this.handlePedal(msg[1], num - 1);},

					// If menu, update menu string
					'm', {this.menu = msg[1];}
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
	Handle Pedal
		Fires/Removes a synth when a pedal is toggles

		Parameters: new pedal state, index of the pedal
	*******************************************************************/
	handlePedal{
		arg state, index;

		// Update state
		this.pedals[index] = state;

		// If new state is on
		if(state == 1){
			// Create synth based off of string in menu
			this.activeScenes[index] = Synth.new(this.menu, [this.busIn]);

			// Bind busses to the synth, allows knobs to update params
			this.activeScenes[index].map(
				\p1, this.knobs[0].index,
				\p2, this.knobs[1].index,
				\p3, this.knobs[2].index,
				\p4, this.knobs[3].index);

		// If new state is off
		}{
			// Check that the active scene is not null
			if(this.activeScenes[index] != nil){

				// Free the scence, nullify
				this.activeScenes[index].free;
				this.activeScenes[index] = nil;
			}
		};
	}

	/******************************************************************
	Free
		Frees all allocated members
	*******************************************************************/
	free {
		// Free group
		this.group.free;

		// For all recievers
		this.receivers.keys.do{
			arg k;

			// Remove the OSC binding (frees in function)
			this.removeOSC(k);
		};

		// For all active scenes
		this.activeScenes.do{
			arg item;

			// Check that not null
			if(item != nil){

				// Free
				item.free;
			};
		};

		// For all knob control busses
		this.knobs.do{
			arg item;

			// Free
			item.free;
		}
	}
}
