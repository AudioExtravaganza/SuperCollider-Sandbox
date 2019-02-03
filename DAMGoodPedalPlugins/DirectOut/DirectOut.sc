// without mul and add.
DirectOut : UGen {
    *ar {
        arg in;
        ^this.multiNew('audio', in);
    }
    *kr{
        arg in;
        ^this.multiNew('control', in)
    }

}