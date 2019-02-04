# DAM Good Pedal Plugins

This directory contains effects and other useful plugins to compile for the server.

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

These settings can be changed in plugins.json. If you modify the compilere you may need to change the make setting to blank, ex. ```windows['Make'] = "",```