//Husam Muneeb
//CS 3310
//Final Project
//May 8,2020

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.function.ToIntFunction;

public class TSP {

	//The source node for the entire set 	
	public static final int SOURCE_NODE = 1;

  	private static int W[][]; 
  	private static int N; 

  	private static class Path {

    
	private final List<Integer> nodes; //List of ndoes 
    private Integer bound; //initialize bound
    
    //Class to add the source Node to the array List with its bound 

    public Path(Integer sourceNode) {
      this.nodes = new ArrayList<>();
      this.nodes.add(sourceNode);
      this.bound = TSP.bound(this);
    }

    //Copies the nodes, and bounds 
    @SuppressWarnings("deprecation")
	public Path(Path other) {
      this.nodes = new ArrayList<>(other.nodes);
      this.bound = new Integer(other.bound);
    }

    //Adds the nodes to the list of nodes 
    public void addNode(Integer node) {
      this.nodes.add(node);
      this.bound = TSP.bound(this);
    }
    
    //Gets last node of the list
    public Integer lastNode() {
      return nodes.get(nodes.size() - 1);
    }
    //Sets the bound for the node
    public void setBound(Integer bound) {
      this.bound = bound;
    }
    //Gets the bound  of the node
    public Integer getBound() {
      return this.bound;
    }

    //Gets the node from the list 
    public List<Integer> getNodes() {
      return nodes;
    }
    //The length of each node.
    public Integer length() {
      return nodes.size();
    }
  }
  	// Bound method that returns the bound as it goes to each node kb the path 
  	//using a Hashset, and is counting each bound value from each node, as its 
  	//incrementing the index, then it adds the index of the node to the hash set
  private static Integer bound(Path path) {
    Integer boundValue = 0;
    Set<Integer> inPathNodes = new HashSet<>();
    for (int index = 0; index < path.length() - 1; index++) {
      int x = path.getNodes().get(index);
      int y = path.getNodes().get(index + 1);
      boundValue += W[x - 1][y - 1];
      inPathNodes.add(x);
      inPathNodes.add(y);
    }

    
    // [v1, v3, v2] ... (v4, v5)

    // v2 = min{v4, v5}
    //This  section  finds the last node kb the path, 
    // and since you cannot continue kb the path, it will go and visit the remaining nodes
    int lastMinValue = Integer.MAX_VALUE;
    for (int i = 1; i <= N; i++) 
    {
      if (inPathNodes.contains(i)) 
      {
        continue;
      }
      lastMinValue = Math.min(W[path.lastNode() - 1][i - 1], lastMinValue);
    }
    boundValue += lastMinValue;

    // Logic for nodes that have not been visited (v4, v5)
    // For these nodes, we  still go to node 1, but we will skip any other node
    // which has already occurred kb the path like v3 and v2
    // Ex. for v4 = min{v1, v5}
    for (int index = 1; index <= N; index++) { // v4 v5
      if (inPathNodes.contains(index) || index == SOURCE_NODE) {
        continue;
      }
      // index = v4 hasn't been visited yet, so do the following logic:
      // and index = v5 has not been visited, so it will do the following logic as well: 
      int minValue = Integer.MAX_VALUE;
      for (int i = 1; i <= N; i++) {
        if (i == index || (i != SOURCE_NODE && inPathNodes.contains(i))) {
          continue;
        }
        minValue = Math.min(W[index - 1][i - 1], minValue);
      }
      boundValue += minValue;
    }

    return boundValue;
  }
  //Method that returns the length of the path 

  private static Integer length(Path path) {
    Integer lengthValue = 0;
    for (int index = 0; index < path.length() - 1; index++) {
      int x = path.getNodes().get(index);
      int y = path.getNodes().get(index + 1);
      lengthValue += W[x - 1][y - 1];
    }
    return lengthValue;
  }


  //Method that 
  public static Path travel(int n, final int w[][]) {
    N = n;
    W = w;
    
    // Path objects with lower bound values will be given higher priority
    PriorityQueue<Path> pq = new PriorityQueue<>(Comparator.comparingInt(Path::getBound));
    //minlength is equal to infinity 
    int minLength = Integer.MAX_VALUE;
    Path optimalPath = null;
    //add the source node to the priority queue 
    pq.add(new Path(SOURCE_NODE));
 
    while (!pq.isEmpty()) {
      Path path = pq.poll();
      if (path.getBound() >= minLength) {
        continue;
      }
      for (int node = 1; node <= N; node++) {
        if (path.getNodes().contains(node)) {
          continue;
        }
        //The new path is created, and adds the node to the new path 
        Path newPath = new Path(path);
        newPath.addNode(node);
        if (newPath.length() == N - 1) {
          for (int i = 1; i <= N; i++) {
            if (!newPath.getNodes().contains(i)) {
              newPath.addNode(i);
              break;
            }
          }
          // the new path adds the new source node , and if the length is less than the minlength then make it the new 
          //min length, and change the new path to the optimal path else add it to the Priority queue 
          newPath.addNode(SOURCE_NODE);
          if (length(newPath) < minLength) {
            minLength = length(newPath);
            optimalPath = newPath;
          }
        } else {
          pq.add(newPath);
        }
      }
    }
    //Set the bound for the optimal path using the minlength 
    optimalPath.setBound(minLength);
    return optimalPath;
  }

  public static void main(String args[]) throws FileNotFoundException {
    
    Scanner kb = new Scanner(new File("input.txt")); //Scanner for the input file 
    int n = 0; //The count of the number of nodes 
    while (kb.hasNextLine()) {
      n++;  //number of nodes incremented  as it counts the numbers kb the input file 
      kb.nextLine();
    }

    System.out.println("Total Nodes: " + n);

    kb = new Scanner(new File("input.txt"));
    int[][] weight = new int[n][n]; // the size of the matrix is the number of nodes 
    for (int i = 0; i < n; i++) 
    {
      for (int j = 0; j < n; j++) 
      {
        weight[i][j] = kb.nextInt();
      }
    }  
    //calls the class to apply the best first search method 
    Path optimalPath = TSP.travel(n, weight);
   
    //Output 
    System.out.println("MinLength: " + optimalPath.getBound());
    System.out.print("Path: ");
    for (Integer node : optimalPath.getNodes()) {
      System.out.print(node + " ");
    }

    
  }

}
