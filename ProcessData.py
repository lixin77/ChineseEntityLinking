# -*- coding: utf-8 -*-
__author__ = 'v-tedl'

import os
from langconv import Converter
path = r'%s/redirectData/' % os.getcwd()
KBItem2RedirectItem = dict()
for file in os.listdir(path):
    print "process %s..." % file
    fp = open(path + file, 'r')
    for line in fp:
        KBItem, RedirectItem = line.strip('\n').split('\t')
        if KBItem == "KBItem":
            continue
        SimRedirectItem = Converter('zh-hans').convert(RedirectItem.decode('utf8')).encode('utf8')
        if KBItem in KBItem2RedirectItem.iterkeys():
            KBItem2RedirectItem[KBItem].append(RedirectItem)
            if SimRedirectItem != RedirectItem:
                KBItem2RedirectItem[KBItem].append(SimRedirectItem)
        else:
            KBItem2RedirectItem[KBItem] = [RedirectItem]
            if SimRedirectItem != RedirectItem:
                KBItem2RedirectItem[KBItem].append(SimRedirectItem)
    fp.close()
lines = []
for key in KBItem2RedirectItem.iterkeys():
    simKey = Converter('zh-hans').convert(key.decode('utf8')).encode('utf8')
    if key == simKey:
        line = '\t'.join([key] + KBItem2RedirectItem[key])
    else:
        line = '\t'.join([key, simKey] + KBItem2RedirectItem[key])
    lines.append('%s\n' % line)
fp = open('redirect_total.txt', 'w+')
fp.writelines(lines)
fp.close()





