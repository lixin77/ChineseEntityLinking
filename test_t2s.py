# -*- coding: utf-8 -*-
__author__ = 'v-tedl'

from langconv import Converter
import threading
import gc
import os
#from xml.etree import ElementTree as ET
#filename = 'lixin.xml'

class MyThread(threading.Thread):
    pnumber = int
    data = []
    def __init__(self, pnumber, data):
        threading.Thread.__init__(self)
        self.pnumber = pnumber
        self.data = data
    def run(self):
        path = '%s/redirectData/redirect%s.txt' % (os.getcwd(), pnumber)
        print 'process file %s...' % path
        fp = open(path, 'w+')
        fp.writelines(self.data)
        fp.close()


filename = 'zhwiki-latest-pages-articles.xml'
path = 'D:/LIXIN/WikiData/'
count = 0
#import xml.etree.cElementTree as ET
#tree = ET.ElementTree(path + filename)
#root = tree.getroot()
#print root.tag
from xml.etree.ElementTree import iterparse
doc = iterparse(path + filename, ('start', 'end'))
count = 1
pnumber = 0
#fp = open('redirect.txt', 'w+')

lines = []
lines.append('KBItem\tQueryItem\n')
for (event, ele) in doc:

    if ele.tag != 'page':
        continue
    if event == "end":
        continue
    # event == begin and ele.tag == 'page'
    all_subs = list(ele)
    for sub in all_subs:
        if not (sub.tag == 'title' or sub.tag == 'redirect'):
            continue
        if sub.tag == "title" and sub.text:
            redirect_item = sub.text
        if sub.tag == "redirect" and sub.get('title'):
            count += 1
            KBItem = sub.get('title')
            lines.append('%s\t%s\n' % (KBItem.encode('utf8'), redirect_item.encode('utf8')))
            if count % 1000 == 0:
                print "process %s pages" % count
            if count % 100000 == 0:
                MyThread(pnumber, lines).start()
                pnumber += 1
                # garbage collection
                del lines
                lines = []
                lines.append('KBItem\tQueryItem\n')
    ele.clear()

if lines:
    fp = open('%s/redirectData/redirect%s.txt' % (os.getcwd(), pnumber), 'w+')
    fp.writelines(lines)
    fp.close()



