import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.Math;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.FontMetrics;

/*
    TODO:
        - Differentiate between directed an undirected graph. (Possibly use arrows.)
        - Ability to add edge weights.
        - Visualize algorithms.

*/

public class GraphVisualizer extends JFrame{

    public GraphVisualizer(){
        setTitle("Graph Visualizer");
        setSize(960, 960);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private int[] nodeWiseCount, nodeOrder;
    private HashMap<Integer,ArrayList<Integer>> outgoingMap = null;
    private int nodes;
    private Edge[] edges;
    private boolean isWeighted, isDirected;
    //Graph edge
    private class Edge{
        private int src, dest;
        Edge(int src, int dest){
            this.src = src;
            this.dest = dest;
            nodeWiseCount[src] += 1;
        }
    }

    private int[] getCoordinates(int level, int num){
        int NODE_PADDING = 70;
        int SIDE = 50;

        int x = NODE_PADDING + (NODE_PADDING + SIDE)*num;
        int y = NODE_PADDING + (NODE_PADDING + SIDE)*level;
        return new int[]{x,y};
    }

    private int[] rotatePoint(int pivotX, int pivotY, int x, int y, double sin, double cos){
        /*
        Transform the coordinates - 
        x’ = x cos β – y sin β
        y’ = x sin β + y cos β
        */
        x -= pivotX;
        y -= pivotY;
        int rotatedX = (int)((x * cos) - (y * sin));
        int rotatedY = (int)((x * sin) + (y * cos));
        rotatedX += pivotX;
        rotatedY += pivotY;
        return new int[]{rotatedX, rotatedY};
    }

    private int[][] getArrowCoords(int x1, int y1, int x2, int y2){
        double angle = (double) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if(angle < 0) angle += 360;
        angle -= 90;
        System.out.println(angle);
        angle = Math.toRadians(angle);
        //Return value -> {{x1,x2,x3}, {y1,y2,y2}}
        int[][] coords = new int[2][3];
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //Pivot point
        coords[0][0] = x2;
        coords[1][0] = y2;
        //Setting other coordinates
        int[] coord = rotatePoint(x2, y2, x2+5, y2-10, sin, cos);
        coords[0][1] = coord[0];
        coords[1][1] = coord[1];
        coord = rotatePoint(x2, y2, x2-5, y2-10, sin, cos);
        coords[0][2] = coord[0];
        coords[1][2] = coord[1];
        return coords;
    }

    public void paint(Graphics g) {
        if(outgoingMap == null) return;
        //Calculate number of levels.
        int levels = (int)(Math.log(nodes)/Math.log(2));
        int width = (nodes%levels == 0) ? nodes/levels : (nodes/levels)+1;
        FontMetrics fm = g.getFontMetrics();
        int curr_nodes = 0;
        HashMap<Integer,int[]> coords_map = new HashMap();
        for(int i=0; i<width; i++){
            for(int j=0; j<levels; j++){
                if(curr_nodes >= nodes) break;
                int[] coords = getCoordinates(j, i);
                //Draw Oval
                int x = coords[0];
                int y = coords[1];
                g.setColor(Color.BLACK);
                g.drawOval(x, y, 50, 50);
                g.setColor(Color.RED);
                g.fillOval(x+22, y+22, 5, 5);
                //Store node coordinate details.
                coords_map.put(nodeOrder[curr_nodes], coords);
                //Fill text
                String text = String.valueOf(nodeOrder[curr_nodes]);
                double textWidth = fm.getStringBounds(text, g).getWidth();
                g.setColor(Color.BLACK);
                g.drawString(text, (int) (x - 5),(int) (y + fm.getMaxAscent() / 2));
                curr_nodes += 1;
            }
        }

        //Draw edges between nodes.
        for(int i=0; i<edges.length; i++){
            Edge curr_edge = edges[i];
            int src = curr_edge.src;
            int dest = curr_edge.dest;
            int x1 = coords_map.get(src)[0] + 25;
            int y1 = coords_map.get(src)[1] + 25;
            int x2 = coords_map.get(dest)[0] + 25;
            int y2 = coords_map.get(dest)[1] + 25;
            if(y1 == y2){
                //Check direction of arrow.(left or right)
                if(x1 < x2) x2 -= 25;
                else x2 += 25;
            } 
            else if(x1 == x2){
                //Check direction of arrow (up or down)
                if(y1 < y2) y2 -= 25;
                else y2 += 25;
            }
            else{
                //Check quadrant the arrow is coming from.
                if(x1 < x2 && y1 < y2){
                    y2 -= 25;
                } else if(x1 > x2 && y1 < y2){
                    y2 -= 25;
                } else if(x1 > x2 && y1 > y2){
                    y2 += 25;
                } else {
                    y2 += 25;
                }
            }
            g.setColor(Color.BLACK);
            g.drawLine(x1, y1, x2, y2);
            //Check if graph is directed and draw arrows.
            if(isDirected){
                int[][] arrowCoords = getArrowCoords(x1, y1, x2, y2);
                g.drawPolygon(arrowCoords[0], arrowCoords[1], 3);
            }
            //Check if graph is weighted and put in weights.
        }

    }

    public void visualize(Scanner scanner, GraphVisualizer visualizer) {
        char directed = '/';
        while(directed != 'y' && directed != 'n'){
            System.out.println("Is the graph directed? (y/n)");
            directed = scanner.next().charAt(0);
        }
        isDirected = (directed == 'y') ? true : false;
        char weighted = '/';
        while(weighted != 'y' && weighted != 'n'){
            System.out.println("Is the graph weighted? (y/n)");
            weighted = scanner.next().charAt(0);
        }
        isWeighted = (weighted == 'y') ? true : false;
        int n = scanner.nextInt();
        this.nodes = n;
        int m = scanner.nextInt();
        edges = new Edge[m];

        //Initialize outgoing node count array
        nodeWiseCount = new int[n+1];

        //Get input edges
        for(int i=0; i<m; i++){
            int src = scanner.nextInt();
            int dest = scanner.nextInt();
            edges[i] = new Edge(src, dest);
        }

        //Map that has key of number of outgoing edges and value is an array of vertices
        outgoingMap = new HashMap();
        int max_count = Integer.MIN_VALUE;
        for(int i=1; i<=n; i++){
            int currCount = nodeWiseCount[i];
            if(currCount > max_count) max_count = currCount;
            ArrayList<Integer> nodes = null;
            if(outgoingMap.containsKey(currCount)){
                nodes = outgoingMap.get(currCount);
            } else {
                nodes = new ArrayList<>();
            }
            nodes.add(i);
            outgoingMap.put(currCount, nodes);
        }

        nodeOrder = new int[n];
        int index = 0;
        for(int i=max_count; i>=0; i--){
            if(outgoingMap.containsKey(i)){
                for(Integer node: outgoingMap.get(i)){
                    nodeOrder[index++] = node;
                }
            }
        }

        //Start drawing circles for nodes.
        repaint();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        GraphVisualizer visualizer = new GraphVisualizer();
        visualizer.visualize(scanner, visualizer);
        scanner.close();
    }
}