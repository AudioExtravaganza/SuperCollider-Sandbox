// without mul and add.
Overdrive01 : UGen {
    *ar { arg in = 0.0, p1, p2, p3, p4;
        ^this.multiNew('audio', in, p1, p2, p3, p4);
    }
}