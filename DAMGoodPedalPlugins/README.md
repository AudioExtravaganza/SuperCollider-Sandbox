# DAM Good Pedal Plugins

This directory contains effects and other useful plugins to compile for the server.

## Making A new Plugin

### Overview

The basic plugins use structs; however, classes can be used instead. For examples of this, explore [SC-3 Plugins](https://github.com/supercollider/sc3-plugins). The source directory may be the most helpful. If you use classes, be sure to follow naming guidelines.

### Setup
Use __DirectOut__ as a starting place.

- Copy this directory and all of its contents, and change the name to the new plugin.
- In the new directory, change the file names to `<plugin_name>.cpp` and `<plugin_name>.sc`. These __must__ match the name of the directory to use `buildPlugins.py`.
- Modify line 1 of `CMakeLists.txt` so that FILENAME gets set to the new name of the `<plugin_name>.cpp`.
- Add the new directory name to `plugins.json`.

### Creating Your Plugin

#### In `<plugin_name>.sc`:

Change `DirectOut` to the new plugin name. Add all parameters the arg line, and also append them to the argument list in the call to `multiNew`. For example:
```c++
DirectOut : UGen {
  *ar {
      arg in, modifier_1, modifier_2;
      ^this.multiNew('audio', in, modifier_1, modifier_2);
  }
...
```
Ar specifies the audio rate behavior for the UGen. Kr specifies the control rate for the UGen.

#### In `<plugin_name>.cpp`:
Change all occurances of `DirectOut` to new plugin name. 

__IMPORTANT__: Because this will be ran in realtime, do not use any methods or functions that can block the main thread. If you need a helper function try to define it asynchronously. To allocate memory use `RTAlloc(unit->mWorld, TYPE * SIZE)`, to free use `RTFree(unit->mWorld, NAME)`. For more documentation 

Define any important variables in the struct, ex:

```c++
struct <plugin_name> : public Unit {
  float modifier_1;
  float modifier_2;
};
```

In the constructor, set any important values, ex:

```c++
void <plugin_name>_Ctor(<plugin_name> * unit) {
    
    // If using audio rate set to audio
    if(INRATE(0) == calc_FullRate){
        SETCALC(<plugin_name>_next_a);

    // Otherwise, using control rate
    } else {
        SETCALC(<plugin_name>_next_k);
    }

    // Set struct variables
    
    // Get values from param list
    unit->modifier_1 = IN(1);
    unit->modifier_2 = IN(2);
    
    // Calculate one sample of output
    <plugin_name>_next_k(unit, 1);

}

```

In `next_a` and\or `next_k` define the behavior of the plugin.

```c++
void <plugin_name>_next_a(DirectOut * unit, int inNumSamples) {
    // Output signal
    float * out = OUT(0);
    
    // Input signal
    float * in = IN(0);

    for(int i = 0; i < inNumSamples; ++i){
        // Arbitrary modification of the signal.
        out[i] = ((in[i] + 0.2) * 0.5) + (in[i] * 0.5) ;
    }

}
```

## Building Effects

From this directory, use:
- ```python buildPlugins.py all```.
- ```python buildPlugins.py plugin1 plugin2 ...``` _Note: can provide one or many plugin names_

This will create a directory named __plugins__ move this into your supercollider extensions folder.

_If you run into issues, verify you have the compiler and tools being used._

## Windows Dependencies

- CMake
- MinGW (gcc, g++ make)
- SuperCollider Source (expected in ```C:/supercollider/)

These settings can be changed in plugins.json. If you modify the compiler you may need to change the make setting to blank, ex. ```windows['Make'] = "",```
