import random
import urllib2

proxyList = [
    '192.168.34.56',
    '192.168.67.2',
    '192.168.78.90',
    '192.168.12.2',
    '192.168.23.90'
]


def get():
    print random.choice(proxyList)
    try:
        urllib2.urlopen('http://www.poi86.com/poi/amap/district/110101/1.html')
    except urllib2.HTTPError:
        print 'error'
        get()


# get()

import requests

print requests.get('http://www.hupu.com').content
