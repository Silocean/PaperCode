# encoding=utf-8

import requests
from lxml import etree
import codecs
import random

proxies = [
    {'http': 'http://14.125.21.7:8118'},
]

USER_AGENTS = [
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
    "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
    "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
    "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
    "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
    "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
    "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
    "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
    "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
]


def random_user_agent():
    headers = {'User-Agent': random.choice(USER_AGENTS)}
    return headers


response = requests.get('http://www.poi86.com/poi/amap/district/110108/1.html', headers=random_user_agent())
html = response.text
selector = etree.HTML(html)
# 总页数
pageNum = int(selector.xpath('//div[@class="pull-right"]/ul/li[last()]/a/text()')[0].split('/')[1])
print pageNum


# 获取商店信息
def get_shop_info(shopUrl):
    response = requests.get(shopUrl, headers=random_user_agent())
    html = response.text
    selector = etree.HTML(html)
    name = selector.xpath('/html/body/div[2]/div[1]/div[1]/h1/text()')[0]
    address = selector.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[4]/text()')[0]
    category = selector.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[6]/text()')[0]
    wgs_84 = selector.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[7]/text()')[0]
    gcj_02 = selector.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[8]/text()')[0]
    bd_09 = selector.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[9]/text()')[0]
    line = name + ',' + address + ',' + category + ',' + wgs_84 + ',' + gcj_02 + ',' + bd_09
    print name
    return line + '\n'


resultFile = codecs.open('haidian.csv', 'a+', encoding='utf-8')

i = 3124
while i <= pageNum:
    url = 'http://www.poi86.com/poi/amap/district/110108/' + str(i) + '.html'
    response = requests.get(url, headers=random_user_agent())
    html = response.text
    selector = etree.HTML(html)
    shopUrls = selector.xpath('//table[@class="table table-bordered table-hover"]/tr/td[1]/a/@href')
    for shopUrl in shopUrls:
        shopUrl = 'http://www.poi86.com' + shopUrl
        shopInfo = get_shop_info(shopUrl)
        resultFile.write(shopInfo)
    print 'page ' + str(i) + "'s shops have been saved!\n"

    if i == 3130:
        break

    i += 1

resultFile.close()
