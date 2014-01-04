from urllib2 import *
from sys import argv
from random import shuffle
import json, io, datetime
import re

# made constant so as not to create a new object every time
url_pattern = "http://habrahabr.ru/companies/page"
headers = {'User-agent' : 'Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.5)', 'Content-type' : 'text/html; charset=UTF8'}

def build_url(index):
	global url_pattern
	return url_pattern + str(index)

def build_request(index):
	global headers
	return Request(build_url(index), None, headers)

# --- running stuff ---

start_time = datetime.datetime.now()

# ids overall range
start = 1
finish = 100000

def run(fromm, too, storage):
	global counter
	print "Getting clubs from", fromm, "to", too
	for i in range(too):
		try:
			print i
			response = urlopen(build_request(i)).read()
			oneline = response.replace("\n", "")
			"""
			<div class="description "> <div class="name"> <a href="/company/dsec/">Digital Security</a></div>
			"""
			matching = re.findall( r'<div class="name">\s*<a href="[^>]+">([^>]+)</a>\s*</div>', unicode(oneline, "utf8"), re.UNICODE)
			print matching	
			print "MATCHING COMPANIES", len(matching), "/20"
			for company in matching:
				print company
				storage.write(company.encode("utf8") + "\n")
		except Exception,e:
			print "Crap happened, moving on:",e

f = open("companies.csv", "w+")
run(start, finish, f)
f.close()

finish_time = datetime.datetime.now()
print "Done in just", (finish_time - start_time)
print "Loaded", counter,"/", finish - start + 1

