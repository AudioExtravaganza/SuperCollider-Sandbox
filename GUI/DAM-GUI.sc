/*********************************************************************
Extension: DAM GUI
Authors: Mason Sidebottom
Purpose: Provides a basic interface for testing DAM Good Pedal
		 functionality virtually
Date: Feb. 2019
*********************************************************************/

/*********************************************************************
DAMComponent
	Serves as the base class for other components. Gives an interface
	for binding actions and setting up GUI components
**********************************************************************/

DAMComponent{
	// Displays the name of the component
	var <>label;

	// Dictionary of actions (simple interface to add/remove)
	var <>actions;

	// The componenet itself. Expects a GUI component that recieves input
	// 	EX. Knob, Button, Menu
	var <>component;

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent view, position in parent, and Name
		! If using with DAMDebugger, all names must be unique

		! In classes that extend this, set the component before calling
		! 	super.
		!	ex.
		!		this.component = Knob(...);
		!		super(parent, pos, name);
	*******************************************************************/
	build {

		arg parent = 0, pos = Rect(0,0, 100, 100) , name = "Blank Component";

		// Label Position
		var lPos = pos.asArray();
		// Offset 30 pixels from the bottom of the component
		lPos = Rect(lPos[0] - 15, lPos[1] + 50, lPos[2] + 30, 20);

		// Generate Static text for label, at LPos
		this.label = StaticText(parent, lPos);
		this.label.string = name;

		// Center Text
		this.label.align = \center;

		// Set font
		this.label.font_(Font("Lucida Sans", 12));

		// Use dictionary for actions
		this.actions = Dictionary(0);

		// Bind action of component
		// 	Call runActions so classes that extend this
		//	can overwrite runActions
		this.component.action_({
			arg comp;
			this.runActions(comp);
		});
	}

	/******************************************************************
	Run Actions
		Iterates through action dictionary calling functions.

		!Do not explicitly call, the component will call this on update
		! Instead use:
		!		this.component.valueAction = this.component.value;
		! or
		!		this.forceAction();
	*******************************************************************/
	runActions {
		arg comp;

		// For all actions
		this.actions.asArray.do{
			arg func, i;
			// Call actions, send name and value
			func.value(this.getName(), comp.value);
		}
	}

	/******************************************************************
	Add Action
		Adds an action
		Parameters: name of action and the action itself (function)
	*******************************************************************/
	addAction{
		arg name, func;
		this.actions.add(name -> func);
	}

	/******************************************************************
	Remove Action
		Removes an action
		Parameters: name of action

		! Assumes that an action with the name provided exists.
	*******************************************************************/
	removeAction{
		arg name;
		this.actions.removeAt(name);
	}

	/******************************************************************
	Force Action
		Safely forces actions to be called
	*******************************************************************/
	forceAction{
		this.component.valueAction = this.component.value;
	}

	/******************************************************************
	Get Name
		Gets name of component from label.

		Returns: Name of component
		! If using with DAMDebugger, names must be unique
	*******************************************************************/
	getName{
		^this.label.string;
	}
}

/*********************************************************************
DAMKnob
	Extends the DAMComponent to implement basic knobs
**********************************************************************/
DAMKnob : DAMComponent {

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent view, position in parent, and name
		! If using with DAMDebugger, all names must be unique
	*******************************************************************/
	build {
		arg parent = 0, rect = Rect(0,0, 50, 50), name = "Knob";

		// Set the component
		this.component = Knob.new(parent, rect);
		this.component.valueAction = 0.5;

		// Let the base class build itself
		super.build(parent, rect, name);
	}
}

/*********************************************************************
DAMMenu
	Extends the DAMComponent to implement a dropdown menu.
**********************************************************************/
DAMMenu : DAMComponent {
	// Menu items
	var <> items;

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent view, position in parent, and name
		! If using with DAMDebugger, all names must be unique
	*******************************************************************/
	build {
		arg parent = 0, rect = Rect(0,0, 50, 50), name = "Menu";

		// Create menu
		this.component = PopUpMenu(parent, rect);

		// Set items to empty list
		this.items = List(0);

		// Set menu items to item list, cast to array
		this.component.items = this.items.asArray();

		// Let the base class build itself
		super.build(parent, rect, name);

		// Make label invisible
		this.label.visible = false;
	}

	/******************************************************************
	Run Actions
		Override run actions from base class

		!Do not explicitly call, the component will call this on update
		! Instead use:
		!		this.component.valueAction = this.component.value;
		! or
		!		this.forceAction();
	*******************************************************************/
	runActions {
		arg m;

		// If no menu items exist, action is meaningless
		if(this.items.size > 1){

			// For all actions
			this.actions.asArray.do{
				arg func, i;

				// Send the action function parameters in form
				//	(name, [index, menu_item])
				func.value(this.getName(), [m.value, this.items.at(m.value)]);
			};
		};
	}

	/******************************************************************
	Add Item
		Adds an Item to the menu
		Parameters: String of item to add to menu.
	*******************************************************************/
	addItem {
		arg item;
		this.items.add(item);

		// Update menu items to item list, cast to array
		this.menu.items = this.items.asArray();
	}

	/******************************************************************
	Remove Item
		Adds an Item to the menu
		Parameters: String of item to be removed
	*******************************************************************/
	removeItem {
		arg item;

		// Linear search for item
		this.items.do{
			arg it, i;
			// If found, remove it
			if(it == item){
				this.items.removeAt(i);
			};
		};

		// Update menu items to item list, cast to array
		this.menu.items = this.items.asArray();
	}
}

/*********************************************************************
DAMMenu
	Extends the DAMComponent to implement a pedal using buttons
**********************************************************************/
DAMPedal : DAMComponent {

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent view, position in parent, and name
		! If using with DAMDebugger, all names must be unique
	*******************************************************************/
	build {
		arg parent = 0, rect = Rect(0,0, 50, 50), name = "Pedal";

		// Build button
		this.component = Button(parent, rect);

		// Set the states to:
		//	0: Off
		// 	1: On
		this.component.states = [["Off", Color.white, Color.red], ["On", Color.black, Color.green]];

		// Default to off
		this.component.value = 0;

		// Let the base class build itself
		super.build(parent, rect, name);
	}

	/******************************************************************
	Run Actions
		Override run actions from base class

		!Do not explicitly call, the component will call this on update
		! Instead use:
		!		this.component.valueAction = this.component.value;
		! or
		!		this.forceAction();
	*******************************************************************/
	runActions {
		arg b;
		this.actions.asArray.do{
			arg func, i;

			// Send the action function parameters in form
			//	(name, [index, button state])
			func.value(this.getName, [b.value, this.component.states.at(b.value).at(0)]);
		}
	}
}


/*********************************************************************
DAMDebugger
	Displays information about DAMComponents.
**********************************************************************/
DAMDebugger {

	// View (allows relative positioning)
	var <> view;

	// Current x and y values
	var <>x, <>y;

	// All fields in Debugger
	var <>fields;

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: parent view, position in parent
	*******************************************************************/
	build {
		arg parent = 0, rect = Rect(0, 0, 50, 50);

		// Create new view
		this.view = CompositeView.new(parent, rect);

		// Set fields to empty dictionary
		this.fields = Dictionary(0);

		// Set start postion
		this.x = 5;
		this.y = 10;
	}


	/******************************************************************
	Add Field
		Adds a text field for a new DAMComponent.
		Parameters: Name of DAMComponent
	*******************************************************************/
	addField {
		arg name;

		var label, value;

		// Build 2 StaticTexts one for label and the other to show value
		label = StaticText(this.view, Rect(this.x, this.y, 140, 20));
		value = StaticText(this.view, Rect(this.x + 140, this.y, 80, 20));

		// Set fonts
		label.font_(Font("Lucida Sans", 12));
		value.font_(Font("Lucida Sans", 12));

		// Setup label and default value to "Not Set"
		label.string = name ++ " value:";
		value.string = "Not Set";

		// Add the two text fields to the dictionary
		this.fields.add(name ->[label, value]);

		// Increment y by 20
		this.y = this.y + 20;

		// If out of bounds, reset y and move to the right 250.
		if(this.y > (this.view.bounds.asArray()[3])){
			this.y = 10;
			this.x = this.x + 250;
		};
	}

	/******************************************************************
	Bind Float
		Binds the debugger to a DAMComponent that outputs floats
		Parameters: DAMComponenet

		! Make sure all DAMComponents that are bound to the DAMDebugger
		! have unique names
	*******************************************************************/
	bindFloat {
		arg item;

		// Create a field for the component
		this.addField(item.getName());

		// Add an action to the DAMComponent
		item.addAction("debugAction", {
			arg name, value;

			// Using the name of the component, modify the value of
			// The StaticText
			this.fields.at(name)[1].string = value.asStringPrec(3)
		});

		// Force Action on item to update debugger
		item.forceAction();
	}

	/******************************************************************
	Bind String
		Binds the debugger to a DAMComponent that outputs as a string
		Parameters: DAMComponenet

		! Make sure all DAMComponents that are bound to the DAMDebugger
		! have unique names
	*******************************************************************/
	bindString {
		arg item;

		// Create a field for the component
		this.addField(item.getName());

		// Add an action to the DAMComponent
		item.addAction("debugAction", {
			arg name, value;

			// Using the name of the component, modify the value of
			// The StaticText
			this.fields.at(name)[1].string = value
		});

		// Force Action on item to update debugger
		item.forceAction();
	}
}




/*********************************************************************
DAM GUI
	Class that implements the virtual front end of the DAM GOOD Pedal
**********************************************************************/
DAMGUI {

	// Window
	var <>win;

	// DAMComponents
	var <>knobs;
	var <>menu;
	var <>pedals;

	// Other output
	var <>dbg;
	var <>scope;

	/******************************************************************
	Build
		Basic constructor. Must be explicitly called after an instance
		is created
		Parameters: Debugging (true/false)
	*******************************************************************/
	build {
		arg debug = false;

		var x, y, h;

		// Default height to 400
		h = 400;

		// If debugging active, set to 600
		if(debug){
			h = 600;
		};

		// Create window
		this.win = Window.new("DAM Good Pedal", Rect(200, 200, 800, h)).front;

		// Setup arrays of X, Y values for knobs
		x = [25, 125, 25, 125];
		y = [40, 90, 120, 170];

		// Create 4 DAMKnobs
		this.knobs = Array.fill(4, {arg i; DAMKnob.new();});

		// Build the Knobs with the correct names and positions
		this.knobs.do{
			arg item, i;
			item.build(this.win, Rect(x[i], y[i], 50, 50,), ("Knob " ++ (i+1).asDigit));
		};

		// Set X values for the DAMPedals
		x = [175, 375, 575];

		// Create 3 DAMPedals
		this.pedals = Array.fill(3, {arg i; DAMPedal.new();});

		// Build the Pedals with the correct names and positions
		this.pedals.do{
			arg item, i;
			item.build(this.win, Rect(x[i], 300, 50, 50), ("Pedal " ++ (i + 1).asDigit));
		};

		// Create and build the menu
		this.menu = DAMMenu.new();
		this.menu.build(this.win, Rect(200, 20, 400, 40), "Menu");

		// Create the Frequency Scope
		this.scope = FreqScopeView(this.win, Rect(200, 60, 400, 200));
		this.scope.active = true;

		// Draw Rectangle around pedal interface
		this.win.drawFunc = {
			Color.black.set;
			Pen.moveTo(10@10);
			Pen.lineTo(10@390);
			Pen.lineTo(790@390);
			Pen.lineTo(790@10);
			Pen.lineTo(10@10);
			Pen.stroke;

			// If Debugging, draw rectangle around debugging
			//	interface
			if(debug){
				Pen.moveTo(10@410);
				Pen.lineTo(10@590);
				Pen.lineTo(790@590);
				Pen.lineTo(790@410);
				Pen.lineTo(10@410);
				Pen.stroke;
			}
		};
		this.win.refresh;

		// If debugging, initialize debugger
		if(debug){
			this.initDebug();
		}
	}

	/******************************************************************
	Init Debug
		Sets up the debugger

		! Do not call outside of this class. If an instance exists with
		! debug set to false, it cannot add the debugger to the window
		! because there will not be enough space.
	*******************************************************************/
	initDebug {

		// Create and build the debugger
		this.dbg = DAMDebugger.new();
		this.dbg.build(this.win, Rect(10, 410, 780, 590));

		// Bind the knobs
		this.knobs.do{
			arg item, i;
			this.dbg.bindFloat(item);
		};

		// Bind the menu
		this.dbg.bindString(menu);

		// Bind the pedals
		this.pedals.do{
			arg item, i;
			this.dbg.bindString(item);
		};
	}
}
