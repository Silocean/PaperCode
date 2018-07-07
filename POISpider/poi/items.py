# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class PoiItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    name = scrapy.Field()
    address = scrapy.Field()
    category = scrapy.Field()
    wgs_84 = scrapy.Field()
    gcj_02 = scrapy.Field()
    bd_09 = scrapy.Field()
    pass
