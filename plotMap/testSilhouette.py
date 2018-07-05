from pylab import *
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from mpl_toolkits.mplot3d import Axes3D
from amltlearn.datasets import make_blobs
from sklearn.cluster import KMeans
from amltlearn.metrics.cluster import calinski_harabasz_score, davies_bouldin_score
from sklearn.metrics import adjusted_mutual_info_score, silhouette_score

# blobs, blabels = make_blobs(n_samples=200, n_features=3,
#                             centers=[[1, 1, 1], [0, 0, 0], [-1, -1, -1]],
#                             cluster_std=[0.2, 0.1, 0.3])
#
# print blobs
# print '++++'
# print blabels
#
# fig = plt.figure(figsize=(10, 10))
#
# ax = fig.add_subplot(111, projection='3d')
# plt.scatter(blobs[:, 0], blobs[:, 1], zs=blobs[:, 2], depthshade=False, c=blabels, s=100)
# plt.show()
#
# km = KMeans(n_clusters=3, n_init=10, random_state=0)
# labels = km.fit_predict(blobs)
#
# print silhouette_score(blobs, blabels)
# print silhouette_score(blobs, labels)
# print type(blobs)


# blobs = [
#     [2, 3], [2, 4], [1, 4], [1, 3], [2, 2], [3, 2],
#     [8, 7], [8, 6], [7, 7], [7, 6], [8, 5],
#     [8, 19], [8, 20],
#     [7, 18], [7, 17]
# ]
#
# import numpy as np
#
# labels = [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2]
# print silhouette_score(np.array(blobs), np.array(labels))

x, y = make_blobs(n_samples=10, centers=3, n_features=2,
                  random_state=0)
print x
print y
