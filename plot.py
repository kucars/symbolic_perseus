import numpy as np
import matplotlib.pyplot as plt1
import matplotlib.pyplot as plt2
import matplotlib.pyplot as plt3
#import matplotlib.pyplot as plt4
 
###########################################################
# in boundedPersuesStartFromCurrent function after calling
# dpbackup function we get this data
############################################################
bestImprovement = []
bellmanErr      = []
maxAbsVal       = []
numnewAlphaMatrix= []
bval             =[]
alphaMatrixArrayListSize= []

dataFile1 = "performance_data_boundedPerseusStartFromCurrent1.txt"
with open(dataFile1, "r") as filestream:
    for line in filestream:
        currentline = line.split(",")
        bestImprovement.append(currentline[0])
        bellmanErr.append(currentline[1])
        #maxAbsVal.append(currentline[2])
        #numnewAlphaMatrix.append(currentline[4])
        #bval.append(currentline[5])
        #alphaMatrixArrayListSize.append(currentline[6])
        #print currentline[0], currentline[1], currentline[2]
       

fig1 = plt1.figure()
ax1  = fig1.add_subplot(111)
ax1.plot(bestImprovement,color='lightblue', linewidth=3, label='bestImprovement')
ax1.plot(bellmanErr,color='red', linewidth=3, label='bellmanErr')
#ax.plot(maxAbsVal,color='darkgreen', linewidth=3)
#ax.scatter([2,4,6], [5,15,25], color='darkgreen',marker='^')
#ax.set_xlim(1, 6.5)
ax1.legend(loc='best', shadow=True)
plt1.xlabel('time')
plt1.ylabel('value')
plt1.title('OCSymbolicPerseus: bestImprovement & bellmanErr')
plt1.savefig('OCSymbolicPerseus_bestImprovement_bellmanErr.png')
plt1.show()

##############################
# this data for the policy generated 
# policy[i] = alphamatrix action id
# policy value is the alphamatrix value
###############################
alphamatrix_actionId = []
alphamateix_value =[]

dataFile2 = "performance_data_boundedPerseusStartFromCurrent2.txt"
with open(dataFile2, "r") as filestream:
    for line in filestream:
        currentline = line.split(",")
        alphamatrix_actionId.append(currentline[0])
        alphamateix_value.append(currentline[1])
fig2 = plt2.figure()
ax2  = fig2.add_subplot(111)
ax2.plot(alphamatrix_actionId,color='lightblue', linewidth=3, label='alphamatrix_actionId')
ax2.plot(alphamateix_value,color='red', linewidth=3, label='alphamateix_value')
ax2.legend(loc='best', shadow=True)
plt2.xlabel('time')# not sure if stays or different
plt2.ylabel('value')# not sure if stays or different 
plt2.title('OCSymbolicPerseus: policy- alpahmatrix actionID and value')
plt2.savefig('OCSymbolicPerseus_policy_alpahmatrix_actionID_and_value.png')
plt2.show()

##################################
#data from dpbackup
##################################
smallestProb = []
bestAction = []
bestValue = []

dataFile3 = "performance_data_dpbackup1.txt"
with open(dataFile3, "r") as filestream:
    for line in filestream:
        currentline = line.split(",")
        smallestProb.append(currentline[0])
        bestAction.append(currentline[1])
        bestValue.append(currentline[2])
fig3 = plt3.figure()
ax3  = fig3.add_subplot(111)
ax3.plot(smallestProb,color='lightblue', linewidth=3, label='smallestProb')
ax3.plot(bestAction,color='red', linewidth=3, label='bestAction')
ax3.plot(bestValue,color='green', linewidth=3, label='bestValue')
ax3.legend(loc='best', shadow=True)
plt3.xlabel('time')# not sure if stays or different
plt3.ylabel('value')# not sure if stays or different 
plt3.title('OCSymbolicPerseus: dpbackupFun: smallestProb_bestAction_bestValue')
plt3.savefig('OCSymbolicPerseus_dpbackupFun_smallestProb_bestAction_bestValue.png')
plt3.show()

