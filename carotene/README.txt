===== OnetHelper.java =====
To collect ONet SOCs, run the following.
./testonet

This will create an output file named $prefix_onet.txt
Job.Mode should be set COLLECT.

Job data
- Jobs500.txt

===== ClientTest.java =====
To test the SOC accuracy, a set of target expected SOCs is required.
Job.Mode should be set TEST.
Once the target data is ready, run the following.
./test

Job data
- jobs250_ONETs-EDITED.txt
- jobs250_ONETs-EDITED.csv

===== ClientTestBasic.java =====
Compares carotene titles with qinlong results.
So, it requires qinlong's data (output250_qinlong.txt).

Job data
- input250.txt: in json format

Requirements
- json-simple-1.1.jar

Rename json-simple-1.1.jar.txt to json-simple-1.1.jar

Once you get the base accuracy, you need to make decisions on the 
incorrect matches manually by reading the job description.

ONet
- OnetHelper.java is called by ClientTest.java
- ONetResponse.java is called by OnetHelper and ClientTest
  This class wraps the data structure of XML ONet response
- ONetCode.java is called by ONetResponse.java
  This class is a basic element unit for ONet code XML response.

ClicentTestBasic.java: ONet test without onet soc match
