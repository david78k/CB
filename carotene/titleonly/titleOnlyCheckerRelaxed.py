from __future__ import division
import sys
import os
import requests

def main():
	inputDir = sys.argv[1]
	vers = sys.argv[2]
	print "processing categories in: " + inputDir

	lineno = 0
	corrects = 0
	soc_lineno = 0
	soc_corrects = 0
	soc_total_matches = 0
	results = []
	for root, dirs, files in os.walk(inputDir, topdown=False):
		for name in files:
			# soc11-categories.txt
			if name.find("DS_Store") == -1 and name.find(".xml") == -1:
				soc_lineno = 0
				soc_corrects = 0
				soc_matches = 0
				filename = os.path.join(root, name)
				orig_soc = os.path.splitext(os.path.basename(filename))[0]
				orig_soc = orig_soc[3:5]
				#print orig_soc 
				for title in loadTitlesFromFile(filename):
					caroteneTitle = getTopCaroteneTitle(title, name, vers)
					result = {}
					result["filename"] = filename
					result["orig_title"] = title
					result["orig_soc"] = orig_soc
					result["final_match"] = 0
					result["carotene_soc"] = ""
					if caroteneTitle != None:
						result["carotene_title"] = caroteneTitle["title"]
						result["confidence"] = caroteneTitle["confidence"]
						result["id"] = caroteneTitle["id"]
						result["carotene_soc"] = caroteneTitle["id"].split('.')[0]
				#		print result["carotene_soc"]
						if caroteneTitle["title"].lower() == title.lower():
							result["final_match"] = 1
							corrects += 1
							soc_corrects += 1
						else:
							print title + "\t" + caroteneTitle["title"]
					else:
						result["carotene_title"] = "unknown"
						result["confidence"] = "-1"
						result["id"] = ""
					results.append(result)
					lineno += 1
					soc_lineno += 1
				print "soc" + orig_soc + " Accuracy (%): " + str(100.0*soc_corrects/soc_lineno) + " (" + str(soc_corrects) + "/" + str(soc_lineno) + ")" 
	print "Total Accuracy (%): " + str(100.0*corrects/lineno) + " (" + str(corrects) + "/" + str(lineno) + ")" 
	writeOutput(results)


def loadTitlesFromFile(filename):
	print "loading files from " + filename
	title_file = open(filename)
	titles = []
	for line in title_file:
		i, titleAndClusterSize = line.split("|", 2)
		i = i.strip()
		titleAndClusterSize = titleAndClusterSize.strip()
		title = titleAndClusterSize[0:titleAndClusterSize.rfind("(")]
		#title, clusterSize = titleAndClusterSize.split("(")
		title = title.strip()
		#clusterSize = clusterSize.strip(")")
		if ((i != "#GID") & (title != "Other Topics")):
			titles.append(title)
			
	return titles

def getTopCaroteneTitle(title, filename,version):
	#version = "soc13"
	if ((filename == 'other-title-categories.txt') |
		(filename == 'v0.4-categories.txt')):
		version = "v1"
	elif (filename == 'v1_1-categories.txt'):
		version = "v1_1"

	payload = {}
	payload["version"] = version
	payload["title"] = title
	payload["language"] = "en"

	#r = requests.post("http://ec2-184-73-68-184.compute-1.amazonaws.com:8080/CaroteneClassifier/gettitle", data=payload)
	r = requests.post("http://localhost:8080/CaroteneClassifier/gettitle", data=payload)

	if r.status_code == 200:
		try:
			resp = r.json()
		except Exception, e:
			print title + ":" + filename
			print r.content
			print r.status_code
			raise e
		if resp["assignments"] != None:
			if len(resp["assignments"]) > 0:
				title = {}
				title["title"] = resp["assignments"][0]["pathToRoot"][0]
				title["confidence"] = resp["assignments"][0]["confidence"]
				title["id"] = resp["assignments"][0]["groupId"]
				return title
			else:
				return None
		else: None
	else:
		return None

def writeOutput(results):
	out = open('out.csv', "w")

	#out.write("%s,%s,%s,%s,%s\n" % ("File Name", "Original Title", "Carotene ID", "Carotene Title", "Confidence"))
	out.write("%s,%s,%s,%s,%s,%s\n" % ("Original SOC", "Original Title", "Carotene ID", "Carotene Title", "Confidence", "Final Match"))
	for r in results:
		out.write("%s,\"%s\",%s,\"%s\",%s,%s\n" % (r["orig_soc"], r["orig_title"], r["id"], r["carotene_title"], r["confidence"], r["final_match"]))
		#out.write("%s,\"%s\",%s,\"%s\",%s\n" % (r["filename"], r["orig_title"], r["id"], r["carotene_title"], r["confidence"]))

	out.close()

if __name__ == '__main__':
	main()
