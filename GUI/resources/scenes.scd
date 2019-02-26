
~scenes = List(0);


~temp = DAMScene.new();
~temp.prebuild('(Sine | Saw | Square)');
~temp.setEffect('SimpleSine', 0);
~temp.setEffect('SimpleSaw', 1);
~temp.setEffect('SimpleSquare',2);

~scenes.add(~temp);



~temp = DAMScene.new();
~temp.prebuild('(Saw | Pulser | Reverb)');
~temp.setEffect('SimpleSaw', 0);
~temp.setEffect('PulseSig', 1);
~temp.setEffect('Reverb_Base', 2);

~scenes.add(~temp);