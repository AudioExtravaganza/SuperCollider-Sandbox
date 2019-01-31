
@echo off
git clone --recursive https://github.com/supercollider/supercollider
cd supercollider
echo "Setting $SC_PATH to: %cd%" 
SETX SC_PATH "%cd%"
echo "Please restart the command line to refresh your env variables"