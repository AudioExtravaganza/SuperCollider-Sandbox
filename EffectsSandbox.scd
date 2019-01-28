(
SynthDef(\impulse, {
	Out.ar(0, Pan2.ar(Saw.ar(440, Decay2.ar(Impulse.ar(1), 0.001, 0.1, 0.5)),0));
}).add;

SynthDef(\continuous, {
	Out.ar(0, Pan2.ar(SinOsc.ar(mul:0.1), 0.0));
}).add;
)

a = Group.basicNew(s, 1);

x = Synth.head(a, \impulse);

s.scope;

// Delay
(
SynthDef(\fxexampledelay, {
	| delayTime = 0.1 |
	var input, effect;

	input = In.ar(0,2);

	effect = DelayN.ar(input, 1, delayTime);

	Out.ar(0, effect);
}).add;
)

// Vibrato
(
SynthDef(\fxvibrato, {

	| freq = 5|
	var source, fx;

	source = In.ar(0,2);
	fx = DelayC.ar(source, 0.01, SinOsc.ar(freq, 0, 0.0025, 0.0075));

	Out.ar(0, fx);
}).add;
)

// Chorus
(
SynthDef(\fxchorus, {
	var source, fx, n = 10;

	source= In.ar(0, 2);
	fx = Mix.fill(n, {
		var maxDelayTime = rrand(0.01, 0.03);
		var half = maxDelayTime * 0.5;
		var quarter = maxDelayTime * 0.25;

		DelayC.ar(source, maxDelayTime, LFNoise1.kr(Rand(5,10), 0.01, 0.02));
	});

	Out.ar(0, fx);
}).add;)
x = Synth.head(a, \continuous);
x.free
y = Synth.tail(a, \fxchorus);
y.free
z.free
z = Synth.tail(a, \fxvibrato);
