#include "SC_PlugIn.h"
#include <math.h>
/****************************************************
 * Authors: Mason Sidebottom
 * Effect: Overdrive
 ***************************************************/

static InterfaceTable *ft;

struct Overdrive01 : public Unit {

};





static void Overdrive01_next_a(Overdrive01 * unit, int inNumSamples);
static void Overdrive01_next_k(Overdrive01 * unit, int inNumSamples);

static void Overdrive01_Ctor(Overdrive01 * unit);
static void Overdrive01_Dtor(Overdrive01 * unit);

void Overdrive01_Ctor(Overdrive01 * unit) {
    
    // If using audio rate set to audio
    if(INRATE(0) == calc_FullRate){
        SETCALC(Overdrive01_next_a);

    // Otherwise, using control rate
    } else {
        SETCALC(Overdrive01_next_k);
    }

    // Set struct variables

    // Calculate one sample of output
    Overdrive01_next_k(unit, 1);
}

// this must be named PluginName_Dtor.
void Overdrive01_Dtor(Overdrive01* unit) {
    // Free the memory.
    // RTFree(unit->mWorld, unit->buf);
}

void Overdrive01_next_a(Overdrive01 * unit, int inNumSamples) {
    float * out = OUT(0);
    float * in = IN(0);
    float amount = IN0(1);
    for(int i = 0; i < inNumSamples; ++i){
        out[i] = in[i] + (in[1] * amount);
    }

}

void Overdrive01_next_k(Overdrive01 * unit, int inNumSamples) {
    float * out = OUT(0);
    float * in = IN(0);

    for(int i = 0; i < inNumSamples; ++i){
        out[i] = in[i];
    }

}

// Called by the server when loading plugins
PluginLoad(Overdrive01) {
    // Save the pointer to the input table
    ft = inTable;
    DefineDtorUnit(Overdrive01);
}