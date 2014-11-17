# output file: out.csv
inputdir=/Users/tkang/github/CaroteneClassifier/carotene-classifier/src/src/main/resources/datasets/cascadeindex
version=v2
prefix=run25_enrichments_soc_fixed_qtbf4f_itbf0.9
#prefix=run15_calibration_with_enrichments
#prefix=run22_calibration_itbf0.9
#prefix=run22_calibration_itbf0.5
#prefix=run20_master
logfile=$prefix.log
csvfile=$prefix.csv
statfile=$prefix.stat

time python titleOnlyChecker.py $inputdir $version > $logfile

mv out.csv $csvfile

grep Accuracy $logfile > $statfile
cat $statfile
