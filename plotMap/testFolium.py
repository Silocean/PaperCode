import folium, string, os, random

osm = folium.Map(location=[39.994238, 116.326786],
                 zoom_start=10,
                 attr='my style',
                 tiles='https://api.mapbox.com/styles/v1/silocean/cizjbcwg2005g2spalb57ekwp/tiles/256/{z}/{x}/{y}?access_token=pk.eyJ1Ijoic2lsb2NlYW4iLCJhIjoiY2l6amJiNTdvMDM5ejMybDR4bWk2aHNlYyJ9.Rh6UgVRagKRrJmylNagSgg')
# folium.Marker([39.994238,116.326786]).add_to(osm)
# geo_path = r'bus.json'
# osm.choropleth(geo_path=geo_path)


path = 'D:\\PycharmProjects\\plotMap\\trajectory\\'


def random_color():
    r = str(hex(random.randint(0, 256)))[2:]
    g = str(hex(random.randint(0, 256)))[2:]
    b = str(hex(random.randint(0, 256)))[2:]
    if len(r) == 1:
        r = '0' + str(r)
    if len(g) == 1:
        g = '0' + g
    if len(b) == 1:
        b = '0' + b
    return '#' + r + g + b



def read_file(file):
    color = random_color()
    for line in file:
        lat = string.atof(line.split(",")[0])
        lon = string.atof(line.split(",")[1])
        osm.circle_marker(location=[lat, lon], radius=5, fill_color=color, line_color=color)
    print '====================='


for f in os.listdir(path):
    file = open(path + f)
    read_file(file)

osm.save(outfile='map.html')

