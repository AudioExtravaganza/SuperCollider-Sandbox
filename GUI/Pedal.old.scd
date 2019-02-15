(
w = Window.new("DAM Good Pedal", Rect(200, 200, 800, 600)).front;
~dropdown = PopUpMenu(w, Rect(300, 20, 200, 40));

~dropdown.items = [
	"overdrive", "delay", "reverb",
	"bitcrusher", "chorus"
];

~dropdown.font_(Font("Courier", 20));
~dropdown.action = { arg menu; };

~synth = 0;
~activeS = 0;

f = FreqScopeView(w, Rect(300, 60, 200, 100));
f.active = true;

~knobSize = 50;

~k1 = DAMKnob.new();
~k1.build(5);

// ~k1 = Knob.new(w, Rect(100, 10, ~knobSize, ~knobSize));
// ~k1.value = 0.5;
// ~k1.action = {
// 	arg k;
// 	~k1W.string = "Param 1 Value:" + k.value.asStringPrec(3);
// 	if(~activeS != 0){
// 		~activeS.set(\p1, k.value);
// 	}
// };
// ~k1L = StaticText(w, Rect(100, 60, 80, 20));
// ~k1L.string = "Param 1";


~k2 = Knob.new(w, Rect(200, 40, ~knobSize, ~knobSize));
~k2.value = 0.5;
~k2.action = {
	arg k;
	~k2W.string = "Param 2 Value:" + k.value.asStringPrec(3);
	if(~activeS != 0){
		~activeS.set(\p2, k.value);
	}
};

~k2L = StaticText(w, Rect(200, 90, 80, 20));
~k2L.string = "Param 2";


~k3 = Knob.new(w, Rect(100, 90, ~knobSize, ~knobSize));
~k3.value = 0.5;
~k3.action = {
	arg k;
	~k3W.string = "Param 3 Value:" + k.value.asStringPrec(3);
	if(~activeS != 0){
		~activeS.set(\p3, k.value);
	}
};

~k3L = StaticText(w, Rect(100, 140, 80, 20));
~k3L.string = "Param 3";


~k4 = Knob.new(w, Rect(200, 120, ~knobSize, ~knobSize));
~k4.value = 0.5;
~k4.action = {
	arg k;
	~k4W.string = "Param 2 Value:" + k.value.asStringPrec(3);
	if(~activeS != 0){
		~activeS.set(\p4, k.value);
	}

};
~k4L = StaticText(w, Rect(200, 170, 80, 20));
~k4L.string = "Param 4";


~re = Knob.new(w, Rect(600, 60, ~knobSize * 2, ~knobSize * 2));
~re.value = 0.5;
~re.mode = \round;
~re.centered = true;
~re.action = {
	arg k;
	~reW.string = "Rotary Encoder:" + k.value.asStringPrec(3);
};
~reL = StaticText(w, Rect(200, 170, 80, 20));
~reL.string = "Param 4";



~k1W = StaticText(w, Rect(20, 400, 200, 20));
~k1W.string = "Param 1 Value: 0.5";
~k2W = StaticText(w, Rect(20, 420, 200, 20));
~k2W.string = "Param 2 Value: 0.5";
~k3W = StaticText(w, Rect(20, 440, 200, 20));
~k3W.string = "Param 3 Value: 0.5";
~k4W = StaticText(w, Rect(20, 460, 200, 20));
~k4W.string = "Param 4 Value: 0.5";
~reW = StaticText(w, Rect(20, 480, 200, 20));
~reW.string = "Rotary Encoder: 0";


~pe1 = Button(w, Rect(175, 300, 50, 50));
~pe1.states = [["off", Color.white, Color.red], ["on", Color.black, Color.green]];
~pe1.action = {
	arg button;
	button.value.postln;
	~dropdown.item.postln;
	if(button.value == 1){
		~synth1 = Synth(~dropdown.item, args:(
			in:0,
			out: 0,
			p1: ~k1.value,
			p2: ~k2.value,
			p3: ~k3.value,
			p4: ~k4.value,
		));
		~activeS = ~synth1;
	}
	{
		if(~synth1 != 0)
		{
			~synth1.free;
			~synth1 = 0;
		}
	}

};
~pe1W = StaticText(w, Rect(180, 350, 200, 20));
~pe1W.string = "Pedal 1";


~pe2 = Button(w, Rect(375, 300, 50, 50));
~pe2.states = [["off", Color.white, Color.red], ["on", Color.black, Color.green]];
~pe2.action = {
	arg button;

	if(button.value == 1){
		~synth2 = Synth(~dropdown.item, args:(
			in:0,
			out: 0,
			p1: ~k1.value,
			p2: ~k2.value,
			p3: ~k3.value,
			p4: ~k4.value,
		));
		~activeS = ~synth2;
	}
	{
		if(~synth2 != 0)
		{
			~synth2.free;
			~synth2 = 0;
		}
	}

};

~pe2L = StaticText(w, Rect(380, 350, 200, 20));
~pe2L.string = "Pedal 2";

~pe3 = Button(w, Rect(575, 300, 50, 50));
~pe3.states = [["off", Color.white, Color.red], ["on", Color.black, Color.green]];
~pe3.action = {
	arg button;
	if(button.value == 1){
		~synth3 = Synth(~dropdown.item, args:(
			in:0,
			out: 0,
			p1: ~k1.value,
			p2: ~k2.value,
			p3: ~k3.value,
			p4: ~k4.value,
		));
		~activeS = ~synth3;
	}
	{
		if(~synth3 != 0)
		{
			~synth3.free;
			~synth3 = 0;
		}
	}

};

~pe3L = StaticText(w, Rect(580, 350, 200, 20));
~pe3L.string = "Pedal 3";


SynthDef("overdrive", {
	arg in, out, p1, p2, p3, p4;
	var signal;
	signal = SinOsc.ar();
	signal = Overdrive01.ar(signal, p1, p2, p3, p4)!2;
	Out.ar(out, signal);
}).add;

SynthDef("delay", {
	arg in, out;
	var signal;

	signal = In.ar(in);
	Out.ar(out, signal);

}).add;
SynthDef("reverb", {
	arg in, out;
	var signal;

	signal = In.ar(in);
	signal.postln;
	Out.ar(out, signal);
}).add;
SynthDef("bitcrusher", {
	arg in, out;
	var signal;

	signal = In.ar(in);
	Out.ar(out, signal);
}).add;
SynthDef("chorus", {
	arg in, out;
	var signal;

	signal = In.ar(in);
	Out.ar(out, signal);
}).add;

)


Server.local.queryAllNodes;
