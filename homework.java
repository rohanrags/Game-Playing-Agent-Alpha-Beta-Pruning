package ai2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class homework {
	static int MAX_DEPTH=4;
	static long node_count=0;
	static long node_count_begin=0;
	static Double REMAINING_TIME=0.0;

	class Node {
		int i,j;
		char num;
		int val;

		public Node() {}

		public Node(int i, int j) {
			this.i = i;
			this.j = j;
		}

		public Node(int i, int j, char num) {
			this.i = i;
			this.j = j;
			this.num=num;
		}

		public Node(int i, int j, char num, int val) {
			this.i = i;
			this.j = j;
			this.num=num;
			this.val = val;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + j;
			result = prime * result + num;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (i != other.i)
				return false;
			if (j != other.j)
				return false;
			if (num != other.num)
				return false;
			return true;
		}
	}
	
	static long st=0;
	static HashMap<Integer, Double> map = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
		int n,p,i,j;
		//Double time=300.00;
		long start_time=System.nanoTime();
		st=start_time;
		
		/*Scanner s = new Scanner(new File("./src/ai2/calibration.txt"));*/
		Scanner s = new Scanner(new File("calibration.txt"));
		map.put(26, s.nextDouble());
		map.put(22, s.nextDouble());
		map.put(12, s.nextDouble());
		map.put(8, s.nextDouble());
		map.put(6, s.nextDouble());
		map.put(4, s.nextDouble());
		
		//System.out.println("Start-time - " + start_time);
		/*Scanner sc = new Scanner(new File("./src/ai2/examples/input9.txt"));*/
		Scanner sc = new Scanner(new File("input.txt"));
		
		homework hm = new homework();
		n = sc.nextInt();
		p=sc.nextInt();
		REMAINING_TIME=sc.nextDouble();

		char[][] a = new char[n][n];
		for(i=0;i<n;i++) {
			if(sc.hasNext()) {
				String temp = sc.next();
				for(j=0;j<n;j++) {
					a[i][j]=temp.charAt(j);
				}

			}			
		}

		sc.close();
		hm.findBestMove(a,n);
		
		/*long end_time=System.nanoTime();
		System.out.println("Time Taken - " + (end_time-start_time)/1000000000.0);
		System.out.println("Nodes level 1 :" + node_count_begin);
		System.out.println("Nodes :" + node_count);
		System.out.println("Speed :" + node_count/((end_time-start_time)/1000000000.0));*/
	}
	

	private void findBestMove(char[][] newBoard, int n) throws IOException {
		int i,j;

		int alpha=Integer.MIN_VALUE,beta=Integer.MAX_VALUE;
		int bestVal=Integer.MIN_VALUE;
		Node bestNode = new Node();
		char[][] a;
		Node node;
		HashSet<Node> hs=new HashSet<>();
		ArrayList<Node> al = new ArrayList<>();
		
		for(i=0;i<n;i++) {
			for(j=0;j<n;j++) {
				if(newBoard[i][j]!='*') {
					Node newNode = new Node(i,j,newBoard[i][j]);
					if(hs.contains(newNode))
						continue;
					
					int curValue = count_only(newBoard,i,j,n,hs);
					curValue=curValue*curValue;
					newNode.val=curValue;
					al.add(newNode);
				}
			}
		}
		
		DSort(al);
		node_count_begin+=al.size();
		node_count+=al.size();
		
		if(node_count_begin>484) {
			if(REMAINING_TIME>map.get(26)*4)
				MAX_DEPTH=3;
			else
				MAX_DEPTH=3;
		} else if(node_count_begin<=484 && node_count_begin>144) {
			if(REMAINING_TIME>map.get(22)*4)
				MAX_DEPTH=4;
			else
				MAX_DEPTH=3;
		}else if(node_count_begin<=144 && node_count_begin>64) {
			if(REMAINING_TIME>map.get(12)*4)
				MAX_DEPTH=5;
			else
				MAX_DEPTH=4;
		}else if(node_count_begin<=64 && node_count_begin>36) {
			if(REMAINING_TIME>map.get(8)*4)
				MAX_DEPTH=6;
			else
				MAX_DEPTH=5;
		}else if(node_count_begin<=36 && node_count_begin>16) {
			if(REMAINING_TIME>map.get(6)*4)
				MAX_DEPTH=7;
			else
				MAX_DEPTH=6;
		}else if(node_count_begin<16) {
			if(REMAINING_TIME>0)
				MAX_DEPTH=8;
		}
		
		
		for(Node nd : al) {
			a=new char[n][n];
			deepCopy(newBoard,a,n);
			
			makeMove(a,nd.i,nd.j,n,hs);
			apply_gravity(a,n);
			
			int val = minMax(a,1,false,nd.val,n,hs,alpha,beta);
			alpha=Math.max(alpha, val);
			
			if(alpha>bestVal) {
				bestVal=val;
				bestNode.i=nd.i;
				bestNode.j=nd.j;
				bestNode.val=val;
			}
		}
		print_result(newBoard,bestNode.i,bestNode.j,n);
	}	
	
	private int minMax(char[][] a, int depth, boolean isMax, int gain, int n, HashSet<Node> hs, int alpha, int beta) {

		if(emptyBoard(a,n))
			return gain;

		if(depth==MAX_DEPTH)
			return gain;

		if(isMax) {
			//Maximizer
			/*int best=-1000;*/
			int i,j;
			HashSet<Node> hs1=new HashSet<>();
			char[][] newBoard;
			ArrayList<Node> al = new ArrayList<>();
			
			for(i=0;i<n;i++) {
				for(j=0;j<n;j++) {
					if(a[i][j]!='*') {
						Node newNode = new Node(i,j,a[i][j]);
						if(hs1.contains(newNode))
							continue;
						
						int curValue = count_only(a,i,j,n,hs1);
						curValue=curValue*curValue;
						newNode.val=gain+curValue;
						al.add(newNode);
					}
				}
			}
			
			DSort(al);
			node_count+=al.size();
			
			for(Node nd : al) {
				newBoard=new char[n][n];
				deepCopy(a,newBoard,n);
				
				makeMove(newBoard,nd.i,nd.j,n,hs1);
				apply_gravity(newBoard,n);
				
				int val = minMax(newBoard,1+depth,!isMax,nd.val,n,hs1,alpha,beta);
				alpha=Math.max(alpha, val);
				if(beta<=alpha)
					return alpha;
			}
			return alpha;
		}else {
			//Minimizer
			/*int best=1000;*/
			int i,j;
			HashSet<Node> hs2=new HashSet<>();
			char[][] newBoard;
			ArrayList<Node> al = new ArrayList<>();
			
			for(i=0;i<n;i++) {
				for(j=0;j<n;j++) {
					if(a[i][j]!='*') {
						Node newNode = new Node(i,j,a[i][j]);
						if(hs2.contains(newNode))
							continue;
						
						int curValue = count_only(a,i,j,n,hs2);
						curValue=curValue*curValue;
						newNode.val=gain-curValue;
						al.add(newNode);
					}
				}
			}
			
			ASort(al);
			node_count+=al.size();
			
			for(Node nd : al) {
				newBoard=new char[n][n];
				deepCopy(a,newBoard,n);
				
				makeMove(newBoard,nd.i,nd.j,n,hs2);
				apply_gravity(newBoard,n);
				
				int v = minMax(newBoard,1+depth,!isMax,nd.val,n,hs2,alpha,beta);
				beta = Math.min(beta, v);

				// Alpha Beta Pruning
				if (beta <= alpha)
					return beta;
			}
			return beta;
		}
	}
	
	private void print_result(char[][] a, int i, int j,int n) throws IOException {
		Map<Integer, Character> map = new HashMap<>();
		map.put(1, 'A');
		map.put(2, 'B');
		map.put(3, 'C');
		map.put(4, 'D');
		map.put(5, 'E');
		map.put(6, 'F');
		map.put(7, 'G');
		map.put(8, 'H');
		map.put(9, 'I');
		map.put(10, 'J');
		map.put(11, 'K');
		map.put(12, 'L');
		map.put(13, 'M');
		map.put(14, 'N');
		map.put(15, 'O');
		map.put(16, 'P');
		map.put(17, 'Q');
		map.put(18, 'R');
		map.put(19, 'S');
		map.put(20, 'T');
		map.put(21, 'U');
		map.put(22, 'V');
		map.put(23, 'W');
		map.put(24, 'X');
		map.put(25, 'Y');
		map.put(26, 'Z');
		
		//System.out.println(map.get(j+1)+""+(i+1));
		int gain=makeMove(a,i,j,n,null);
		apply_gravity(a,n);
		//print(a,n);
		
		//Writing part - needed.
		/*BufferedWriter writer = new BufferedWriter(new FileWriter("./src/ai2/output.txt"));*/
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		writer.write(map.get(j+1)+""+(i+1));
		writer.write("\n");

		for(int x=0;x<n;x++) {
			for(int y=0;y<n;y++) {
				writer.write(String.valueOf(a[x][y]));
			}
			writer.write("\n");
		}
		writer.close();
	}
	
	private int count_only(char[][] a, int i, int j, int n, HashSet<Node> hs) {
		int count=1;
		char t=a[i][j];

		Node newNode = new Node(i,j,a[i][j]);
		if(hs!=null) {
			if(hs.contains(newNode)) {
				return 0;
			}
			hs.add(newNode);
		}

		if(i>=1 && a[i-1][j]==t) {
			count+=count_only(a,i-1,j,n, hs);
		}
		if(i<n-1 && a[i+1][j]==t) {
			count+=count_only(a,i+1,j,n, hs);
		}
		if(j>=1 && a[i][j-1]==t) {
			count+=count_only(a,i,j-1,n, hs);
		}
		if(j<n-1 && a[i][j+1]==t) {
			count+=count_only(a,i,j+1,n, hs);
		}

		return count;
	}

	private int makeMove(char[][] a, int i, int j, int n, HashSet<Node> hs) {
		int count=1;
		char t=a[i][j];

		if(hs!=null) {
			Node newNode = new Node(i,j,a[i][j]);
			hs.add(newNode);
		}

		if(i>=1 && a[i-1][j]==t) {
			a[i][j]='*';
			count+=makeMove(a,i-1,j,n, hs);
		}
		if(i<n-1 && a[i+1][j]==t) {
			a[i][j]='*';
			count+=makeMove(a,i+1,j,n, hs);
		}
		if(j>=1 && a[i][j-1]==t) {
			a[i][j]='*';
			count+=makeMove(a,i,j-1,n, hs);
		}
		if(j<n-1 && a[i][j+1]==t) {
			a[i][j]='*';
			count+=makeMove(a,i,j+1,n, hs);
		}

		a[i][j]='*';

		return count;
	}

	private void apply_gravity(char[][] a, int n) {
		int i,j,k;
		for(j=0;j<n;j++) {
			i=n-1;k=n-1;

			while(i>=0 && k>=0) {
				if(a[i][j]!='*') {
					if(i<k){
						char t = a[i][j];
						a[i][j] = a[k][j];
						a[k][j] = t;
					} 
					i--;k--;
				} else {
					i--;
				}
			}
		}
	}

	private char[][] deepCopy(char[][] oldBoard, char[][] newBoard, int n) {
		int i,j;
		for(i=0;i<n;i++) {
			for(j=0;j<n;j++) {
				newBoard[i][j]=oldBoard[i][j];
			}
		}
		return newBoard;
	}
	
	private void DSort(ArrayList<Node> al) {
		al.sort(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				if(o1.val>o2.val) {
					return -1;
				} else if(o1.val<o2.val) {
					return 1;
				}
				return 0;
			}
			
		});
	}
	
	private void ASort(ArrayList<Node> al) {
		al.sort(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				if(o1.val<o2.val) {
					return -1;
				} else if(o1.val>o2.val) {
					return 1;
				}
				return 0;
			}
			
		});
	}
	
	private boolean emptyBoard(char[][] a, int n) {
		int i,j;
		for(i=0;i<n;i++) {
			for(j=0;j<n;j++) {
				if(a[i][j]!='*')
					return false;
			}
		}
		return true;
	}

	private static void print(char[][] a, int n) {
		int x,y;
		for(x=0;x<n;x++) {
			for(y=0;y<n;y++) {
				System.out.print(a[x][y]);
			}
			System.out.println("");
		}
	}

}