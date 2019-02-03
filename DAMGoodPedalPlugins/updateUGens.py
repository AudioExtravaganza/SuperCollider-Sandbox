
import os 
from shutil import rmtree, copy2, move
from platform import platform
import json


def main():
    file = open("plugins.json")

    pluginData = None



    if file:
        pluginData = json.load(file)

    
    settings = pluginData[platform().split("-")[0]]
    extension = os.path.expanduser("~") + settings['ExtensionDir'].replace("/", "\\")
    
    if not os.path.exists("plugins"):
        os.system("mkdir plugins")

    os.chdir("plugins")

    for p in pluginData['plugins']:
        os.system("mkdir %s" % p)
        os.chdir(p)
        os.system("dir")
        os.system('cmake -G "%s" ..\\..\\%s\\. -DSC_PATH=%s' % (settings['Generator'], p, settings['SC_PATH'].replace("/", "\\")))
        os.system("mingw32-make.exe")
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