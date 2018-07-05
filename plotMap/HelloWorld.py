import geoplotlib

thedata = geoplotlib.utils.read_csv('bus.csv')
geoplotlib.tiles_provider('toner-lite')
geoplotlib.dot(thedata)
#geoplotlib.geojson('bus.json')
geoplotlib.show()
