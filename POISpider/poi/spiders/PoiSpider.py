from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors.sgml import SgmlLinkExtractor

from poi.items import PoiItem

class PoiSpider(CrawlSpider):
    district = '110108'
    name = 'poi'
    allowed_domains = ['poi86.com']
    start_urls = (
        'http://www.poi86.com/poi/amap/district/' + district + '/1.html',
    )

    rules = (
        Rule(SgmlLinkExtractor(allow=(r'http://www.poi86.com/poi/amap/district/' + district + '/\d+.html'))),
        Rule(SgmlLinkExtractor(allow=(r'http://www.poi86.com/poi/amap/\d+.html')), callback='parse_item'),
    )

    def parse_item(self, response):
        item = PoiItem()
        # '/html/body/div[2]/div/div[1]/h1'
        item['name'] = response.xpath('/html/body/div[2]/div[1]/div[1]/h1/text()').extract()
        item['address'] = response.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[4]/text()').extract()
        item['category'] = response.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[6]/text()').extract()
        item['wgs_84'] = response.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[7]/text()').extract()
        item['gcj_02'] = response.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[8]/text()').extract()
        item['bd_09'] = response.xpath('/html/body/div[2]/div[1]/div[2]/ul/li[9]/text()').extract()
        yield item
