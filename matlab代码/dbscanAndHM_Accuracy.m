% Clear
clc
clear all

% Create figure
figure1 = figure;

% Create axes
axes1 = axes('Parent',figure1,...
    'GridLineStyle','-.',...
    'FontSize',14);
box(axes1,'on');
hold(axes1,'all');

% Set figure size
% set(gca,'position',[0,0,1,0.5])        % [0,0,1,1]数值分别对应左下宽高,默认即可
set(gcf,'position',[600,400,800,450])    % gcf是当前figure，可以改变当前图框的大小

% 转换后Data
a1=[43.431, 44.838, 48.148, 36.048]; %map3
a2=[0.65, 0.93]; %map4

bar1 = bar(a2);

set(gca,'XTickLabel',{'','DBSCAN','','HM',''})
set(gca,'ylim',[0 1]); 

% Create xlabel and ylabel
ylabel('准确率%');
%ylabel('算法准确率/%');

