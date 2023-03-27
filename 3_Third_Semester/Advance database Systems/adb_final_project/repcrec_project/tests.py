import os
import sys
import subprocess

files = ["../inputfiles/" + f for f in os.listdir("../inputfiles")]
print(files)

if not files:
    print("No files found")

for file in files[1:]:
    print()
    print("*" * 10 + "  Running test for " + file  + " " + "*" * 10)
    sys.stdout.flush()
    sys.stderr.flush()
    subprocess.Popen(["java", "rep.Main", file],
                    stdout=sys.stdout,
                    stderr=sys.stderr).wait()
    sys.stdout.flush()
    sys.stderr.flush()