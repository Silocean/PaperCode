function drawMatrix(mat)
    % mat = rand(n);           %# A 5-by-5 matrix of random values from 0 to 1
    imagesc(mat);            %# Create a colored plot of the matrix values
    colormap(flipud(gray));  %# Change the colormap to gray (so higher values are
                             %#   black and lower values are white)

    textStrings = num2str(mat(:),'%0.2f');  %# Create strings from the matrix values
    textStrings = strtrim(cellstr(textStrings));  %# Remove any space padding
    [x,y] = meshgrid(1:7);   %# Create x and y coordinates for the strings
    hStrings = text(x(:),y(:),textStrings(:),...      %# Plot the strings
                    'HorizontalAlignment','center');
    midValue = mean(get(gca,'CLim'));  %# Get the middle value of the color range
    textColors = repmat(mat(:) > midValue,1,3);  %# Choose white or black for the
                                                 %#   text color of the strings so
                                                 %#   they can be easily seen over
                                                 %#   the background color
    set(hStrings,{'Color'},num2cell(textColors,2));  %# Change the text colors

    set(gca,'XTick',1:7,...                         %# Change the axes tick marks
            'XTickLabel',{'1','2','3','4','5','6','7'},...  %#   and tick labels
            'YTick',1:7,...
            'YTickLabel',{'1','2','3','4','5','6','7'},...
            'TickLength',[0 0]);
    xlabel('用户编号');
    ylabel('用户编号');