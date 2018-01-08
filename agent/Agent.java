package ai2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Agent {
	
	static int n,p;
	static int r_score=0,s_score=0;
	static Rohan ro = new Rohan();
	
	private static final String INPUT1_FILENAME = "./src/ai2/agent/input1.txt";
	private static final String INPUT2_FILENAME = "./src/ai2/agent/input2.txt";
	private static final String OUTPUT_FILENAME = "./src/ai2/agent/output.txt";
	
	static Map<Character, Integer> map = new HashMap<>();

	public static void main(String[] args) throws IOException {
		set_map();
		Random r = new Random();
		int t = r.nextInt(2);
		if(t==0)
			System.out.println("Rohan is Playing first.");
		else
			System.out.println("Samarth is Playing first.");
		
		Scanner sc = new Scanner(new File(INPUT1_FILENAME));
		
		n=sc.nextInt();
		int i,j;
		p=sc.nextInt();
		Double start_time=sc.nextDouble();
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
		Double r_rem=300.0,s_rem=300.0;
		
		while(!ro.emptyBoard(a, n) && r_rem>0.0 && s_rem>0.0) {
			if(t==0) {
				//rohan
				long r_stime = System.nanoTime();
				Rohan.run();
				long r_etime = System.nanoTime();
				r_rem = r_rem-((r_etime-r_stime)/1000000000.0);
				read(a,true);
				a=write(r_rem,true);
				t=1;
			}else {
				//samarth
				long s_stime = System.nanoTime();
				Samarth.run();
				long s_etime = System.nanoTime();
				s_rem = s_rem-((s_etime-s_stime)/1000000000.0);
				read(a,false);
				a=write(s_rem,false);
				t=0;
			}
		}
		
		if(ro.emptyBoard(a, n))
			System.out.println("Empty board");
		if(r_rem<0.0)
			System.out.println("Rohan time finished");
		if(s_rem<0.0)
			System.out.println("Samarth time finished");
		
		if(r_score>s_score)
			System.out.println("Rohan wins - " + r_score);
		else 
			System.out.println("Samarth wins - " + s_score);
	}

	private static void read(char[][] a, boolean b) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(OUTPUT_FILENAME));
		String value = sc.nextLine();
		sc.close();
		
		char col = value.charAt(0);
		int row = Integer.parseInt(value.substring(1));
		int res = ro.makeMove(a, row-1, map.get(col), n, null);
		res=res*res;
		if(b)
			r_score+=res;
		else 
			s_score+=res;
	}
	
	private static char[][] write(Double rem_time, boolean b) throws IOException {
		Scanner sc = new Scanner(new File(OUTPUT_FILENAME));
		String value = sc.nextLine();
		
		char[][] a = new char[n][n];
		int i,j;
		
		for(i=0;i<n;i++) {
			if(sc.hasNext()) {
				String temp = sc.next();
				for(j=0;j<n;j++) {
					a[i][j]=temp.charAt(j);
				}

			}			
		}
		sc.close();
		BufferedWriter writer;
		
		if(b)
			writer = new BufferedWriter(new FileWriter(INPUT2_FILENAME));
		else 
			writer = new BufferedWriter(new FileWriter(INPUT1_FILENAME));
		
		writer.write(""+n);
		writer.write("\n");
		writer.write(""+p);
		writer.write("\n");
		writer.write(""+rem_time);
		writer.write("\n");

		for(int x=0;x<n;x++) {
			for(int y=0;y<n;y++) {
				writer.write(String.valueOf(a[x][y]));
			}
			writer.write("\n");
		}
		writer.close();
		
		return a;
	}
	
	private static void set_map() {
		map.put('A',0);
		map.put('B',1);
		map.put('C',2);
		map.put('D',3);
		map.put('E',4);
		map.put('F',5);
		map.put('G',6);
		map.put('H',7);
		map.put('I',8);
		map.put('J',9);
		map.put('K',10);
		map.put('L',11);
		map.put('M',12);
		map.put('N',13);
		map.put('O',14);
		map.put('P',15);
		map.put('Q',16);
		map.put('R',17);
		map.put('S',18);
		map.put('T',19);
		map.put('U',20);
		map.put('V',21);
		map.put('W',22);
		map.put('X',23);
		map.put('Y',24);
		map.put('Z',25);
	}

}
