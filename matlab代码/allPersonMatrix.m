function allPersonMatrix(eps)
	matrix = xlsread('ss.csv');
	figure;
	imagesc(matrix);
	colormap(flipud(gray));
	%title(['eps=', num2str(eps)]);
	%set(gca, 'XTick', [1:181]);
	%set(gca, 'YTick', [1:181]);
	xlabel('user No');
	ylabel('user No');