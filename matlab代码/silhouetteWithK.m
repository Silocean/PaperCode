figure;
x = [12:26];
y = [0.74406,0.74406,0.53091,0.53174,0.54026,0.49579,0.26232,0.18173,0.15049,0.10517,0.31769,0.29537,0.39284,0.38945,0.38956];
plot(x,y,'-o', 'Color', 'k');
xlabel('nearest neighbor k');
ylabel('Silhouette index');