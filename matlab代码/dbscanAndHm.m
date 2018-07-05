
x = [6995, 2387; 9400, 5692; 14348, 8994; 19345, 14073; 25087, 17073];
bar(x);
set(gca,'XTickLabel',{36,72,108,144,180});
legend('DBSCAN','HM');
xlabel('number of persons');
ylabel('running time(ms)');