# Graph-Visualizer
A java-based tool to visualize graphs from their edge representations. The main aim of this tool is to be used as visual add-on when trying to study/understand graphs. This is the product of only 3 hours of work so it only has the most basic of features. I plan on working a lot more on this and building a lot more features.<br/>
The tool currently supports - <br/>
1. Directed graphs.
2. Undirected graphs.

Work in progress for - <br/>
1. Weighted graphs.
2. Handling of overlapping edges when graphs scale.

Use the below input format to generate your graphs - <br/>
n m <br/>
src<sub>i</sub> dest<sub>i</sub> <br/>
src<sub>i</sub> dest<sub>i</sub> <br/>
... <br/>
src<sub>i</sub> dest<sub>i</sub> <br/>

where, <br/>
n - number of nodes <br/>
m - number of edges, such that 1 <= i <= m <br/>
src - source node in the i<sup>th</sup> edge <br/>
dest - destination node in the i<sup>th</sup> edge <br/>

![Screenshot](docs/demo.png)
