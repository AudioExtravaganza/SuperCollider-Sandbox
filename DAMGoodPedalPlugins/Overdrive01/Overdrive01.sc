// without mul and add.
Overdrive01 : UGen {
    *ar { arg in = 0.0;
        ^this.multiNew('audio', in);
    }
}