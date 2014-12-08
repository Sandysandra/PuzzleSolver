package ui;

import java.util.ArrayList;
import java.util.HashMap;

import data.Unit;
import process.ConsMatrix;
import process.ParseInput;

public class MyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filePath = "/home/shanshan/Documents/Puzzle/tilepuzzle/src/IQCreator.txt";
		ParseInput in = new ParseInput();
		in.parse(filePath);
		in.process();
//		ConsMatrix matrix = new ConsMatrix(in);
		System.out.print("There are total ");
		System.out.print(in.soluInterface.size());
		System.out.println(" solutions");
		System.out.println("Done!");
		
		/*//traverse the solution list
		for( int i = 0; i < in.soluInterface.size(); i++ ) {
			//get the ith solution --> HashMap<Integer,Arraylist<Unit>>
			//each solution is expressed by a hashmap
			// in the hashmap, there are many tiles
			
			//HashMap<Integer,ArrayList<Unit>>, Integer stands for the tile id
			//ArrayList<Unit> stands for the tile, each tile contains several unit<x,y,char>
			
			//get the ith solution
			HashMap<Integer,ArrayList<Unit>> solu = in.soluInterface.get(i);
			
			//get the tiles in the solution
			for( int j = 0; j < in.tiles.length; j++ ) {
				int tileId = j;
				ArrayList<Unit> tile = solu.get(j);
				
				//traver the ArrayList to get each unit of the tile
				for( int k = 0; k < tile.size(); k++ ) {
					Unit u = tile.get(k);
					int x = u.getX();
					int y = u.getX();
					char ch = u.getCh();
				}
				
			}
		}*/
	}

}
