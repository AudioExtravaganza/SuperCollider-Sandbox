#include "SC_PlugIn.h"
#include "math.h"
#define fSampleRate (float)SAMPLERATE

/****************************************************
 * Authors: Ben Windheim
 * Effect: Bitcrusher
 ***************************************************/

static InterfaceTable *ft;

struct BitCrusher : public Unit {
    float bitRate;
    float outputMix;



};

static void BitCrusher_next(BitCrusher * unit, int inNumSamples);
static void BitCrusher_Ctor(BitCrusher * unit);

void BitCrusher_Ctor(BitCrusher * unit) {

    unit->bitRate = 0.0f;
    unit->outputMix = 0.0f;

    // Set struct variables

    // Calculate one sample of output
    SETCALC(BitCrusher_next);
    BitCrusher_next(unit, 1);
}

void BitCrusher_next(BitCrusher * unit, int inNumSamples) {
    float * in = IN(0);
    float * out = OUT(0);

    float rate = IN0(1);
    float mix = IN0(2);

    float count = unit->bitRate;
    float lastOut = unit->outputMix;
    long sample_rate = fSampleRate;
    float step, stepr, change, ratio;
    double dummy;

    if (mix >= 31.0f || mix <- 1.0f) {
        step = 0.0f;
        stepr = 1.0f;
    } else {
        step = pow(0.5f, mix - 0.999f);
        stepr = 1/step;
    }

    if (rate >= sample_rate) {
        ratio = 1.0f;
    } else {
        ratio = rate/sample_rate;
    }


    for(int i = 0; i < inNumSamples; ++i){
        count += ratio;
        if (count >= 1.0f) {
            count -= 1.0f;
            change = modf((in[i] + (in[i]<0?-1.0:1.0)*step*0.5) * stepr, &dummy) * step;
            lastOut = in[i] - change;
            out[i] = lastOut;
        } else {
            out[i] = lastOut;
        }
    }
    unit->bitRate = count;
    unit->outputMix = lastOut;

}

// Called by the server when loading plugins
PluginLoad(BitCrusher) {
    // Save the pointer to the input table
    ft = inTable;
    DefineSimpleUnit(BitCrusher);
}