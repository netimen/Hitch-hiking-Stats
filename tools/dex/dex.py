#!/usr/bin/python
from os import system, path
from sys import argv

max_depth = argv[1] if len(argv) > 1 else 3
d = path.dirname(__file__)
system('java -jar ' + path.join(d, 'dex-method-counts.jar') + ' --max-depth=' + max_depth +  ' ' + path.join(d, '../../app/build/outputs/apk/app-debug.apk'))

# I TRIED TO USE GRADLE PLUGINS INSTEAD OF THIS SCRIPT, BUT THEY DON'T WORK WITH MULTI-DEX :(
