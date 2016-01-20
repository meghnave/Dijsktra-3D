//package com.jcg;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.PyObject;
import org.python.core.PyString;

import java.io.*;
import java.lang.*;

//class for the node of graph
class GraphNode
{
	ArrayList<GraphNode> adjList;	
	int nodeID;
	public int x,y,z;
	
	public ArrayList<GraphNode> getAdjList(GraphNode g)
	{
		return g.adjList;
	}
	
	@Override
	public boolean equals(Object other)
	{
		GraphNode n = (GraphNode)other;
		return x == n.x && y == n.y && z == n.z;
	}
	
	@Override
	public int hashCode()
	{
		return x+y+z;
	}
}


//class to define edges of the cubes 
class Edge
{
	GraphNode start;
	GraphNode end;
	double weight;
	double distance;
	
	public Edge()
	{
		start=null; end=null;
		weight=0; distance=0;
	}
	public void setEdge(GraphNode v1,GraphNode v2,double dist)
	{
		start=v1;
		end=v2;
		distance=dist;
	}
	
	@Override
	public boolean equals(Object e)
	{
		boolean isEqual=false;
		if(e!=null && e instanceof Edge )
		{
			
			isEqual=(start.equals(((Edge)e).start)); 
		}
		return isEqual;
	}
	
	@Override
	public int hashCode() {
	    return this.start.x + this.end.y + this.end.z;
	}
}


class Cube 
{
	int cubeid;
	double weight;
	GraphNode[] v;
	ArrayList <Edge> edge;
	
	Cube()
	{
	v=new GraphNode[8];
	edge=new ArrayList<Edge>();
	}
	
	public void addEdge(Edge e)
	{
		edge.add(e);
	}	
}

class Dijkstra
{
	int noc,nnodes;
	public static GraphNode graphnode[];
	static Cube[] cubes;
	private Set<GraphNode> finished;
	private Set<GraphNode> unfinished;
	private Map<GraphNode,GraphNode> predecessors;
	private Map<GraphNode, Double> distance;
	
	//function which returns the weight of edge between two nodes based on dist and weight of cubes they lie in 
	public double getWeight(GraphNode v1,GraphNode v2)
	{
		for(int k=0;k<noc;k++) 					 //iterate in each cube 
		{
		for(int i=0;i<cubes[k].edge.size();i++)  //go through all edges in each cube
		{
			if((cubes[k].edge.get(i).start==v1 && cubes[k].edge.get(i).end==v2)||cubes[k].edge.get(i).start==v2 && cubes[k].edge.get(i).end==v1)
				{
				 int x1=cubes[k].edge.get(i).start.x; int x2=cubes[k].edge.get(i).end.x;
				 int y1=cubes[k].edge.get(i).start.y; int y2=cubes[k].edge.get(i).end.y;
				 int z1=cubes[k].edge.get(i).start.z; int z2=cubes[k].edge.get(i).end.z;
				double length=Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2)); 
				return (cubes[k].edge.get(i).weight)*length;
				}
		}
		}
		return 0;
	}

	/*Main Function*/
	public static void main(String args[]) throws Exception
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Smaller cubes of size 1 will be made inside the cube of larger length: ");
		System.out.println("Enter the length of the bigger cube: ");
		int side;
		side=sc.nextInt();
		
		int noc=side*side*side;  				//number of cubes
		int nnodes=(side+1)*(side+1)*(side+1);  //number of graph nodes
		int x,y,z,count=0,i,j,k;
		graphnode=new GraphNode[nnodes];		//for storing graph nodes
		int sourcex=0,sourcey=0,sourcez=0,destx=0,desty=0,destz=0,source=0,dest=0;
		
		//inout source coordinates
		System.out.println("Enter source coordinates, x,y,z one by one from 0 to "+side);
		sourcex=sc.nextInt();
		sourcey=sc.nextInt();
		sourcez=sc.nextInt();
		
		//Make new graph nodes array of size number of nodes 
		for(i=0;i<nnodes;i++)
		{
			graphnode[i]=new GraphNode();
		}
		
		//Initialise the graph nodes with the coordinates 
		for(x=0;x<=side;x++)
			for(y=0;y<=side;y++)
				for(z=0;z<=side;z++)
				{
					graphnode[count].nodeID=count;
					graphnode[count].x=x;
					graphnode[count].y=y;
					graphnode[count].z=z;
					count++;
				}	
		
		
		//create new array of structures for cubes
		cubes=new Cube[noc];
		for(i=0;i<noc;i++)
		{
			cubes[i]=new Cube();
		}
		
		//find source's graph node id 
		for(i=0;i<nnodes;i++)
		{
			if(graphnode[i].x==sourcex)
			{
				if(graphnode[i].y==sourcey)
				{
					if(graphnode[i].z==sourcez)
					{
						source=graphnode[i].nodeID;
					}
				}
			}
		}
		
		//prepare cubes data structure to store vertices and edges of all small cubes
		for(i=0;i<noc;i++)
				{
				
					cubes[i].cubeid=i;
					int zz=i%side;
					int yy=(i/side)%side;
					int xx=((i/side)/side)%side;
					int p=(xx*(side+1)*(side+1)+yy*(side+1)+zz);
					cubes[i].v[0]=graphnode[p];
					cubes[i].v[1]=graphnode[p+1];
					cubes[i].v[2]=graphnode[p+side+1];
					cubes[i].v[3]=graphnode[p+side+2];
					cubes[i].v[4]=graphnode[p+(side+1)*(side+1)];
					cubes[i].v[5]=graphnode[p+(side+1)*(side+1)+1];
					cubes[i].v[6]=graphnode[p+(side+1)*(side+1)+side+1];
					cubes[i].v[7]=graphnode[p+(side+1)*(side+1)+side+2];
					double r=Math.random();			//assign random weights between 0 and 1 to the cubes 
					cubes[i].weight=r;
					for(j=0;j<8;j++)
					{
						for(k=j+1;k<8;k++)
						{
							Edge edge= new Edge();
							double temp1=((cubes[i].v[j].x)-(cubes[i].v[k].x))*((cubes[i].v[j].x)-(cubes[i].v[k].x));
							double temp2=((cubes[i].v[j].y)-(cubes[i].v[k].y))*((cubes[i].v[j].y)-(cubes[i].v[k].y));
							double dist=(double) Math.sqrt(temp1+temp2);
							edge.setEdge(cubes[i].v[j],cubes[i].v[k],dist);
							//edge.setCube(i);
							cubes[i].addEdge(edge);
							
						}
					}
						
				}
		
		//to print cubes vertices
		/*  for(i=0;i<noc;i++)
		{
			System.out.println();
			for(j=0;j<cubes[i].v.length;j++)
			System.out.print(cubes[i].v[j].nodeID+" ");
		}*/
		
				
		//Define weights of edges,
		 //make the weights of common edges as average of the weights of cubes it lies in
		for(i=0;i<noc;i++)
			{
				for (j=0;j<12;j++)
				{
					Edge e=cubes[i].edge.get(j);
					for(k=0;k<noc;k++)
					{
						if(k!=i)
						{
							boolean c=false;
							c=cubes[k].edge.contains(e);
							if(c)
							{		
								if(e.weight!=0)
								e.weight=(e.weight+cubes[i].weight+cubes[k].weight)/3;
								else 
								e.weight=(cubes[i].weight+cubes[k].weight)/2;	
							}							
						}
					}
					if(e.weight==0) e.weight=cubes[i].weight;
				}
			}
			
		
			/*Create Adjacency List for all the graphnodes*/
			for(i=0;i<noc;i++)
			{
				for(j=0;j<cubes[i].v.length;j++)
				{
					int id=cubes[i].v[j].nodeID;
					if(graphnode[id].adjList==null)
						graphnode[id].adjList=new ArrayList<GraphNode>();
						for(k=0;k<cubes[i].v.length;k++)
						{
							int temp=cubes[i].v[k].nodeID;
							if(temp!=id)
							{
								if(!graphnode[id].adjList.contains(graphnode[temp]))
								graphnode[id].adjList.add(graphnode[temp]);
							}
						}
				}
			}
			
			//To print and check adjacency list-
			/*for(i=0;i<nnodes;i++)
			{
				//Collections.sort((List<T>) graphnode[i].adjList);
				System.out.println("for node "+i+": ");
				for(j=0;j<graphnode[i].adjList.size();j++)
				System.out.print(graphnode[i].adjList.get(j).nodeID+" ");
			}*/
			
			
			//Perform Dijkstra on the graph made
			Dijkstra d=new Dijkstra();
			d.doDijkstra(graphnode[source]);
			
			/*Write to file*/
			CSVFileWriter w=new CSVFileWriter();
			String s="file.csv";				//write the nodes to draw cube 
			w.writeFileEdges(cubes,s);
			String tfile="path.csv";			//write the shortest path
			
			
			//continuous entering of destination and showing the shortest path 
			System.out.println("\nEnter 'Y' to add destination, any other key to exit-");
			char t=(char) System.in.read();
			if(t=='Y'||t=='y')
			{
				while(t=='Y'||t=='y')
				{
					
					System.out.println("Enter destination coordinates, x,y,z one by one from 0 to "+side);
					destx=sc.nextInt();
					desty=sc.nextInt();
					destz=sc.nextInt();
					for(i=0;i<nnodes;i++)
					{
						if(graphnode[i].x==destx)
						{
							if(graphnode[i].y==desty)
							{
								if(graphnode[i].z==destz)
								{
									dest=graphnode[i].nodeID;
								}
							}
						}
					}
					
					//Store and show the minimum path
					ArrayList<GraphNode>path=d.showPath(graphnode[dest]);
					w.writeShortestPath(path,tfile);
					System.out.println("Min path-");
					for(GraphNode current: path)
					{
						int px=current.x;
						int py=current.y;
						int pz=current.z;
						System.out.print("("+px+","+py+","+pz+")");
						if(current!=path.get(path.size()-1))
							System.out.print("->");
					}
					System.out.println("\n");
					t='o';
					System.out.println("Enter 'Y' to add destination, any other key to exit-");
					t=(char) System.in.read();
				
				}
			}
			
			System.out.println("Program exited");
			
	}
	

	//Run Dijkstra on the graphnode
	public void doDijkstra(GraphNode g)
	{
		finished=new HashSet<GraphNode>();
		unfinished=new HashSet<GraphNode>();
		predecessors=new HashMap<GraphNode,GraphNode>();
		distance=new HashMap<GraphNode, Double>();
		distance.put(g, 0.0);
		unfinished.add(g);
		while(unfinished.size()>0)
		{
			GraphNode current=getMinimum(unfinished);
			unfinished.remove(current);

			//System.out.println("line 309");
			findShortestPath(current);
			//System.out.println("line 311");
			finished.add(current);
		}	
	}
	
	//the relax function
	public void findShortestPath(GraphNode g)
	{
		ArrayList<GraphNode> adj=g.getAdjList(g);
		for(GraphNode current: adj)
		{
			if(shortestDist(current)>shortestDist(g)+getWeight(g,current))
			{		distance.put(current,shortestDist(g));
			predecessors.put(current,g);
			unfinished.add(current);
			}
		}
	}
	
	//to get the node which is to be evaluated now
	public GraphNode getMinimum(Set<GraphNode> nodes)
	{
		GraphNode min=null;
		for(GraphNode n: nodes)
		{
			if (min==null)
				min=n;
			else  
			{
				if(shortestDist(n)<shortestDist(min))
					min=n;
			}
		}
		return min;
	}
	
	/*function to return the shortest dist of a node that has been found , otherwise infinity */
	public double shortestDist(GraphNode n)
	{
		Double d=distance.get(n);
		if(d==null)
			return Integer.MAX_VALUE;
		else
			return d;
	}
	
	//function to return the path that is found
	public ArrayList<GraphNode> showPath(GraphNode g)
	{
		
		ArrayList<GraphNode> shortestPath=new ArrayList<GraphNode>();
		GraphNode n1=g;
		if(predecessors.get(n1)==null)
			return null;
		shortestPath.add(n1);
		while(predecessors.get(n1)!=null)
		{
			n1=predecessors.get(n1);
			shortestPath.add(n1);
		}
		Collections.reverse(shortestPath);
		return shortestPath;
	}
}

//class to write data to CSV files 
class CSVFileWriter
{
	static final String COMMA_DELIMITER=",";
	static final String NEW_LINE_SEPARATOR="\n";
	private FileWriter fw;
	//Function to write the shortest path found of a destination to a CSV file
	public void writeShortestPath(ArrayList<GraphNode> path, String t)
	{
		fw=null;
		
		
		try
		{
			fw=new FileWriter(t);
			for(int i=0;i<path.size();i++)
			{
				fw.append(String.valueOf(path.get(i).x)); fw.append(COMMA_DELIMITER);
				fw.append(String.valueOf(path.get(i).y)); fw.append(COMMA_DELIMITER);					
				fw.append(String.valueOf(path.get(i).z)); fw.append(NEW_LINE_SEPARATOR);										
			}
		}
		catch(Exception e1)
		{
			System.out.println("Error in CSV writer!!");
			e1.printStackTrace();
		}
		finally
		{
			try {
				fw.flush();
				fw.close();
			}
			catch(Exception e2)
			{
				System.out.println("error while flushing or closing");
			}
					
		}

		
	}

	//function to write the cube to the CSV file
	public void writeFileEdges(Cube[] c,String fn)
	{
		FileWriter fw=null;
		try
		{
			fw=new FileWriter(fn);
			for(Cube c1:c)
			{

					fw.append(String.valueOf(c1.v[0].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[0].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[0].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[0].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[0].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[0].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[2].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[2].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[2].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[0].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[0].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[0].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[4].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[4].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[4].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[3].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[3].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[3].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[5].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[5].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[5].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[1].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[2].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[2].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[2].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[3].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[3].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[3].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[2].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[2].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[2].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[6].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[6].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[5].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[5].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[5].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].z)); fw.append(NEW_LINE_SEPARATOR);
				

					fw.append(String.valueOf(c1.v[4].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[4].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[4].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[6].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[4].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[4].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[4].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[5].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[5].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[5].z)); fw.append(NEW_LINE_SEPARATOR);
					

					fw.append(String.valueOf(c1.v[3].x)); fw.append(COMMA_DELIMITER);
					fw.append(String.valueOf(c1.v[3].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[3].z)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].x)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].y)); fw.append(COMMA_DELIMITER);					
					fw.append(String.valueOf(c1.v[7].z)); fw.append(NEW_LINE_SEPARATOR);
					
				}
		}
		catch(Exception e1)
		{
			System.out.println("Error in CSV writer!!");
			e1.printStackTrace();
		}
		finally
		{
			try {
				fw.flush();
				fw.close();
			}
			catch(Exception e2)
			{
				System.out.println("error while flushing or closing");
			}
					
		}
	}
}
