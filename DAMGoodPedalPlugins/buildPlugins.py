
import os
import sys
from shutil import rmtree, copy2, move
from platform import platform
import json

# Helper function to get the right slashes in paths


def getPath(path):
    if "Windows" in platform(terse=1):
        return path.replace("/", "\\")
    return path


def main():

    # Handle command line options

    if len(sys.argv) == 1:
        print("Specify plugin to build. or use all.")
        print("\t python updateUGens.py all")
        print("\t python updateUGens.py <name1> <name2> ...")
        exit()

    # Get settings from file
    file = open("plugins.json")
    pluginData = None

    if file:
        pluginData = json.load(file)

    platformName = platform().split("-")[0]
    try:
        settings = pluginData[platformName]
    except:
        print ("Settings not found for platform:", platformName)
        exit()

    # Locate supercollider extensions folder
    extension = os.path.expanduser("~") + getPath(settings['ExtensionDir'])
    print(extension)
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
        # os.system("dir")
        localPath = getPath("../../%s/." % p)
        cmd = 'cmake -G "%s" %s -DSC_PATH=%s' % (
            settings['Generator'], localPath, getPath(settings['SC_PATH']))
        print (cmd)
        os.system(cmd)

        if(os.system(pluginData[platformName]["Make"]) != 0):
            print( "\n\nMake Failed!")
            print ("Expected reason, could not find Supercollider source at:", settings[
                'SC_PATH'], "\n\n")

        try:
            rmtree("CMakeFiles")
            os.remove("Makefile")
            os.remove("CMakeCache.txt")
            os.remove("cmake_install.cmake")
        except:
            print("Files didnt exist")

        src = getPath("../../%s/%s.sc" % (p, p))
        dest = getPath("./%s.sc" % p)
        copy2(src, dest)
        os.chdir("..")

    os.chdir("..")

    print("Move plugins to %s" % extension)


main()
