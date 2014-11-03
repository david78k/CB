# output file: out.csv
inputdir=/Users/tkang/github/CaroteneClassifier/carotene-classifier/src/src/main/resources/datasets/cascadeindex
version=v2
prefix=run13_calibration
logfile=$prefix.log
csvfile=$prefix.csv

time python titleOnlyChecker.py $inputdir $version > $logfile

mv out.csv $csvfile
