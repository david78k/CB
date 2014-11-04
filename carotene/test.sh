jsonjar=json-simple-1.1.jar
input=input250.txt
output=output250.txt
#dir=CaroteneClassifier
dir=.

# compile
rm $dir/*.class
javac -cp $jsonjar ClientTest.java JobQuery.java
#javac -cp $jsonjar $dir/*.java

# run
#cd $dir
java -cp $jsonjar:. ClientTest $input $output
#java -cp $jsonjar:. CaroteneClassifier/ClientTest $input $output
#java -cp $jsonjar:. $dir/ClientTest $input $output
