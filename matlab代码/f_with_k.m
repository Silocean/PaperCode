figure;
x = [5:15];
y = [0.64,0.74,0.81,0.86,0.74,0.64,0.66,0.67,0.53,0.4,0.32];
bar(y);
set(gca,'XTickLabel',x);
xlabel('nearest neighbor k');
ylabel('F-measure');