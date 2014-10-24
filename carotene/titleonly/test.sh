# output file: out.csv
inputdir=/Users/tkang/github/CaroteneClassifier/carotene-classifier/src/src/main/resources/datasets/cascadeindex
version=v2
logfile=run7.log

time python titleOnlyChecker.py $inputdir $version > $logfile
