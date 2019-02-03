#ifndef OVERDRIVE01_H
#define OVERDRIVE01_H
#include "SC_PlugIn.h"

/****************************************************
 * Authors: Mason Sidebottom
 * Effect: Overdrive
 ***************************************************/
extern static InterfaceTable *ft;

struct Overdrive01 : public Unit {
 
};





static void Overdrive01_next_a(Overdrive01 * unit, int inNumSamples);
static void Overdrive01_next_k(Overdrive01 * unit, int inNumSamples);

static void Overdrive01_Ctor(Overdrive01 * unit);
static void Overdrive01_Dtor(Overdrive01 * unit);

#endif