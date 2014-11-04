# output file: out.csv
inputdir=/Users/tkang/github/CaroteneClassifier/carotene-classifier/src/src/main/resources/datasets/cascadeindex
version=v2
#prefix=run14_with_enrichments
prefix=run15_calibration_with_enrichments
logfile=$prefix.log
csvfile=$prefix.csv

time python titleOnlyChecker.py $inputdir $version > $logfile

mv out.csv $csvfile
