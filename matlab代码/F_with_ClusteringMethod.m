%x = [23, 12, 43, 21];
y = [0.87, 0.21, 0.65, 0.32];
bar([1,2,3,4], y);
%set(gca,'XTick',[1,2,3,4]);
set(gca, 'XTickLabel', {'SBCM','K-means','DBSCAN','AGNES'});
ylabel('F-measure');