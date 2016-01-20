from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib.path import Path
import matplotlib.patches as patches
import numpy as np
from numpy import genfromtxt
from itertools import product, combinations
fig = plt.figure()
ax = fig.gca(projection='3d')
ax.set_aspect("equal")
data=genfromtxt("/media/meghna/Docs n Music/My Stationery/EclipseWorkspace/Dijkstra/file.csv",delimiter=',',names=['x1','y1','z1','x2','y2','z2'])

path=genfromtxt("/media/meghna/Docs n Music/My Stationery/EclipseWorkspace/Dijkstra/path.csv",delimiter=',',names=['x','y','z'])

#draw cubes
for i in range (len(data)):
	ax.plot([data[i]['x1'],data[i]['x2']],[data[i]['y1'],data[i]['y2']],[data[i]['z1'],data[i]['z2']],'green')

#draw path
for i in range (len(path)-1):
	ax.plot([path[i]['x'],path[i+1]['x']],[path[i]['y'],path[i+1]['y']],[path[i]['z'],path[i+1]['z']],'red',ls='solid')

plt.show()

