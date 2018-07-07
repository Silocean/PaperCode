# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
import json, codecs


class PoiPipeline(object):
    def __init__(self):
        self.file = codecs.open('result.csv', 'w', encoding='utf-8')

    def process_item(self, item, spider):
        # line = json.dumps(dict(item), ensure_ascii=False) + '\n'
        line = item['name'][0] + ',' + item['address'][0] + ',' + item['category'][0] + ',' + item['bd_09'][0] + '\n'
        self.file.write(line)
        return item
