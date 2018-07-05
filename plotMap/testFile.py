import os

path = 'D:\\IntellijWorkplace\\TrajectoryMining\\trajectory\\012\\'


def readFile(file):
    for line in file:
        print line
    print '====================='


for f in os.listdir(path):
    file = open(path + f)
    readFile(file)
