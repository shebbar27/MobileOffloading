# MobileOffloading

Mobile offloading project
In this project, we are going to develop a distributed computing infrastructure using only mobile phones:
Task list
1) Develop a Master mobile application that can be used to start a program that collects battery
levels from mobile phones and lists it in a file
2) Develop a service discovery application, that sends queries to available mobile phones in close
proximity through Bluetooth or WiFi to request participation
3) Develop a dispatcher application that choses a mobile phone among the ones that accepted the
request based on matching some requirements. Requirements can be minimum battery level,
and location proximity (you have to use GPS on slave mobile phones)
4) Based on the chosen set, send requests to start battery monitoring application on the slave side.
5) Develop a slave application that can receive a request from a master through Bluetooth or WiFi
6) The application can also monitor the battery level and current location and send it back to the
master if the user decides to consent.
7) The application can then run a code snippet in the slave end to start the periodic monitoring
application
8) Using this framework, solve the problem of distributed Matrix Multiplication
a. The master instantiates the Matrix
b. Sends parts of the Matrix to the Slave
c. Slave computes on the Matrix
d. Sends data back to the Master
e. Master combines and shows the result
13) Write a failure recovery algorithm in the Master, where if a slave fails the master can reassign
immediately to another available slave node
14) Estimate execution time of the Matrix Multiplication if done only on the Master
15) Estimate execution time If done using the distributed approach with no failure
16) Estimate execution time if done using the distributed approach with failure
17) Estimate power consumption of Master and Slave nodes without distributed computation
18) Estimate power consumption of Master and Slave nodes with distributed computation
