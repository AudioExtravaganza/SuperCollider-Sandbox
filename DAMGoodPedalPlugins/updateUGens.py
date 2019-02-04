
import os 
import sys
from shutil import rmtree, copy2, move
from platform import platform
import json

def main():
    if len(sys.argv) == 1:
        print("Specify plugin to build. or use all.")
        print("\t python updateUGens.py all")
        print("\t python updateUGens.py <name1> <name2> ...")
        exit()
    file = open("plugins.json")

    pluginData = None


    if file:
        pluginData = json.load(file)

    
    settings = pluginData[platform().split("-")[0]]
    extension = os.path.expanduser("~") + settings['ExtensionDir'].replace("/", "\\")
    
    if not os.path.exists("plugins"):
        os.system("mkdir plugins")

    os.chdir("plugins")
    plugins = 0
    if sys.argv[1] == "all":
        plugins = pluginData['plugins']
    else:
        plugins = [x for x in sys.argv[1:]]


    for p in plugins:
        os.system("mkdir %s" % p)
        os.chdir(p)
        os.system("dir")
        os.system('cmake -G "%s" ..\\..\\%s\\. -DSC_PATH=%s' % (settings['Generator'], p, settings['SC_PATH'].replace("/", "\\")))
        
        if(os.system("mingw32-make.exe") != 0):
            print("\n\nMake Failed!")
            print("Expected reason, could not find Supercollider source at:")
            print(settings['SC_PATH'])
        
        try:
            rmtree("CMakeFiles")
            os.remove("Makefile")
            os.remove("CMakeCache.txt")
            os.remove("cmake_install.cmake")
        except:
            print("Files didnt exist")
        copy2("..\\..\\%s\\%s.sc" % (p, p), ".\%s.sc" % p)
        os.chdir("..")

    
    
    os.chdir("..")
    
    print("Move plugins to %s" % extension)

main()