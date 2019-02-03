#include "SC_PlugIn.h"

/****************************************************
 * Authors: Mason Sidebottom
 * Effect: Overdrive
 ***************************************************/

static InterfaceTable *ft;

struct DirectOut : public Unit {

};

static void DirectOut_next_a(DirectOut * unit, int inNumSamples);
static void DirectOut_next_k(DirectOut * unit, int inNumSamples);

static void DirectOut_Ctor(DirectOut * unit);
static void DirectOut_Dtor(DirectOut * unit);

void DirectOut_Ctor(DirectOut * unit) {
    
    // If using audio rate set to audio
    if(INRATE(0) == calc_FullRate){
        SETCALC(DirectOut_next_a);

    // Otherwise, using control rate
    } else {
        SETCALC(DirectOut_next_k);
    }

    // Set struct variables

    // Calculate one sample of output
    DirectOut_next_k(unit, 1);
}

// this must be named PluginName_Dtor.
void DirectOut_Dtor(DirectOut* unit) {
    // Free the memory.
    // RTFree(unit->mWorld, unit->buf);
}

void DirectOut_next_a(DirectOut * unit, int inNumSamples) {
    float * out = OUT(0);
    float * in = IN(0);

    for(int i = 0; i < inNumSamples; ++i){
        out[i] = in[i];
    }

}

void DirectOut_next_k(DirectOut * unit, int inNumSamples) {
    float * out = OUT(0);
    float * in = IN(0);

    for(int i = 0; i < inNumSamples; ++i){
        out[i] = in[i];
    }

}

// Called by the server when loading plugins
PluginLoad(DirectOut) {
    // Save the pointer to the input table
    ft = inTable;
    DefineDtorUnit(DirectOut);
}