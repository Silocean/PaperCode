from sklearn.neighbors import NearestNeighbors

samples = [[0, 1], [1, 0], [1, 1], [1, 2], [1, 5], [3, 3], [3, 4], [4, 3], [4, 4], [5, 1]]
neigh = NearestNeighbors()
neigh.fit(samples)

arr = neigh.kneighbors(samples, 4, return_distance=False)
print arr


def count_common(arr1, arr2):
    return len(set(arr1).intersection(set(arr2)))


with open('graph.txt', 'w+') as f:
    for i in xrange(0, len(arr)):
        for j in xrange(i + 1, len(arr)):
            if count_common(arr[i], arr[j]) >= 3:
                f.write(str(i) + ' ' + str(j) + '\n')

with open('graph.txt') as f:
    e = len(f.readlines())

with open('graph.txt', 'r+') as f:
    content = f.read()
    f.seek(0, 0)
    f.write(str(len(arr)) + '\n' + str(e) + '\n' +  content)

