x = [2964,1745;3798,1978;4084,2710;4888,3178;6972,4073];
bar(x);
set(gca,'XTickLabel',{36,72,108,144,180});
legend('DBSCAN','SC');
xlabel('number of persons');
ylabel('running time(ms)');