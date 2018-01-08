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

public class calibrate {
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

	public static void main(String[] args) throws IOException {
		int p,i,j;
		ArrayList<Integer> ar = new ArrayList<>();
		HashMap<Integer, Integer> map = new HashMap<>();
		
		calibrate hm = new calibrate();
		ar.add(26);ar.add(22);ar.add(12);ar.add(8);ar.add(6);ar.add(4);
		map.put(26, 3);map.put(22, 4);map.put(12, 5);map.put(8, 6);map.put(6, 7);map.put(4, 8);
		
		char[][] a;
		BufferedWriter writer;
		
		for(int n: ar) {
			MAX_DEPTH=map.get(n);
			a = new char[n][n];
			hm.generate_calibration_board(a,n);
			node_count_begin=0;
			
			long start_time=System.nanoTime();
			
			st=start_time;
			hm.findBestMove(a,n);
			long end_time=System.nanoTime();
			/*System.out.println("Time Taken - " + (end_time-start_time)/1000000000.0);
			System.out.println("Nodes level 1 : " + node_count_begin);*/
			writer = new BufferedWriter(new FileWriter("calibration.txt",true));
			writer.write(""+(end_time-start_time)/1000000000.0);
			writer.write("\n");
			writer.close();
		}
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
		//print_result(newBoard,bestNode.i,bestNode.j,n);
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

		System.out.println(map.get(j+1)+""+(i+1));
		int gain=makeMove(a,i,j,n,null);
		apply_gravity(a,n);
		print(a,n);

		//Writing part - needed.
		/*BufferedWriter writer = new BufferedWriter(new FileWriter("./src/ai2/all/output.txt",true));
		writer.write("\n");
		writer.write(map.get(j)+""+(i+1));
		writer.write("\n");

		for(int x=0;x<n;x++) {
			for(int y=0;y<n;y++) {
				writer.write(String.valueOf(a[x][y]));
			}
			writer.write("\n");
		}
		writer.close();*/
	}
	
	private void generate_calibration_board(char[][] a, int n){
		int i,j;
		boolean one=true;
		boolean t;
		if(n%2==0)
			t=true;
		else 
			t=false;
		
		for(i=0;i<n;i++) {
			for(j=0;j<n;j++) {
				if(one) {
					a[i][j]='1';
					one=!one;
				}else {
					a[i][j]='0';
					one=!one;
				}
			}
			if(t)
				one=!one;
		}
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