import numpy as np
import matplotlib.pyplot as plt
 
bestImprovement = []
bellmanErr      = []
maxAbsVal       = []

dataFile = "performance_data.txt"
with open(dataFile, "r") as filestream:
    for line in filestream:
        currentline = line.split(",")
        bestImprovement.append(currentline[0])
        bellmanErr.append(currentline[1])
        maxAbsVal.append(currentline[2])
        #print currentline[0], currentline[1], currentline[2]

fig = plt.figure()
ax  = fig.add_subplot(111)
ax.plot(bestImprovement,color='lightblue', linewidth=3, label='bestImprovement')
ax.plot(bellmanErr,color='red', linewidth=3, label='bellmanErr')
#ax.plot(maxAbsVal,color='darkgreen', linewidth=3)
#ax.scatter([2,4,6], [5,15,25], color='darkgreen',marker='^')
#ax.set_xlim(1, 6.5)
ax.legend(loc='best', shadow=True)
plt.xlabel('time')
plt.ylabel('value')
plt.title('OCSymbolicPerseus: test')
plt.savefig('test.png')
plt.show()
