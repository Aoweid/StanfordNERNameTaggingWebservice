import os
import sys
import subprocess
import commands
logfile = open("log.json", "w")
logfile.write( 'FeatureName={'+sys.argv[1]+'}\n' )
logfile.write( 'GitHash={'+commands.getoutput('git log -n 1 --pretty=format:"%H"')+'}\n' )
for subdir, dirs, files in os.walk('./CFT/DEV'):
    for file in files:
		if file.endswith(".template"):
			 logfile.seek(0, 2)
			 logfile.write( 'CFTName={'+file+'}\n' )

for subdir, dirs, files in os.walk('./target'):
    for file in files:
		if file.endswith(".war"):
			 logfile.seek(0, 2)
			 logfile.write( 'File={'+file+',acceptance-artifacts/'+sys.argv[1]+'/war/'+file+'}\n' )


