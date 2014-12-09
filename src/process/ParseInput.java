package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import data.RawTile;
import data.Tile;
import data.Unit;

public class ParseInput {

	private char[][] in; // input file was converted from arraylist<char[]> to char[][];
	public Tile[] tiles; // parse 'in', find the tiles
	public Tile board;

	private RawTile maxTile = null;
	private RawTile minTile = null;

	private boolean allSameSizeTiles;
	private int singleTileSize;
	private int totalTileSquare;
	
	//all Tiles
	int[] tilesId;
	int[][] tilesX;
	int[][] tilesY;
	char[][] tilesCh;
	
	long start;
	long tmpStart;
	boolean brute = true;
	
	//Matrix Y
	HashMap<Integer, ArrayList<Integer>> mtrY = new HashMap<Integer, ArrayList<Integer>>();
	HashMap<Integer, ArrayList<Integer>> mtrX = new HashMap<Integer, ArrayList<Integer>>();
	
	//solution list, contain all the solutions.
	//each solution contains the row numbers in mtrY
	ArrayList<ArrayList<Integer>> soluList = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> bruteForceSolu = new ArrayList<ArrayList<Integer>>();
	
	//solution list Interface for Lin.
	//each solution contains all the tiles that compose the board.
	public ArrayList<HashMap<Integer,ArrayList<Unit>>> soluInterface = new ArrayList<HashMap<Integer,ArrayList<Unit>>>();

	public void setBruteFlag(boolean brute){
		this.brute = brute;
	}
	
	public void process() {
		start = System.currentTimeMillis();
		tmpStart = start;
		int soluNum;
		if( brute ) {
			bruteForce();
			soluNum = bruteForceSolu.size();
		}
		else {
			consY();
			consX();
			ArrayList<Integer> solution = new ArrayList<Integer>();
			start = System.currentTimeMillis();
			tmpStart = start;
			solve(mtrX, mtrY, solution);
			soluNum = soluList.size();
		}
		System.out.print("There are totally ");
		System.out.print(soluNum);
		System.out.println(" solutions");
		System.out.println("Done!");
		long end = System.currentTimeMillis();
		System.out.print("Total Time: ");
		System.out.println(end-start);
		convertSolu();
	}

	// parse the input file and convert it into a whole two dimensional array
	public void parse(String filePath) {
		ArrayList<char[]> fullInput = new ArrayList<char[]>();
		try {
			String encoding = "ASCII";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					fullInput.add(lineTxt.toCharArray());
				}
				read.close();
				// find the tiles and target board
				findTilesAndBoard(fullInput);

			} else {
				System.out.println("Can not find the file");
			}
		} catch (Exception e) {
			System.out.println("There are some problems when reading the file");
			e.printStackTrace();
		}
	}

	public void findTilesAndBoard(ArrayList<char[]> inList) {
		this.in = new char[inList.size()][];
		for (int i = inList.size(); i-- > 0;)
			in[i] = inList.get(i);

		RawTile raw;
		ArrayList<RawTile> rawTiles = new ArrayList<RawTile>();
		for (int j = 0; j < in.length; j++) {
			for (int i = 0; i < in[j].length; i++) {
				if (in[j][i] != ' ') {
					// detect & explore raw piece and clear the points using ' '
					raw = new RawTile();
					exploreTile(in, i, j, raw);
					raw.nomalize();
					rawTiles.add(raw);
					if (maxTile == null) {
						maxTile = raw;
						minTile = raw;
					} else if (raw.compareTo(maxTile) > 0) {
						maxTile = raw;
					} else if (raw.compareTo(minTile) < 0) {
						minTile = raw;
					}

				}
			}
		}
		board = new Tile(0, maxTile, maxTile.width(), maxTile.height(), true);
		rawTiles.remove(maxTile);
		tiles = new Tile[rawTiles.size()];

		singleTileSize = rawTiles.get(0).size();
		allSameSizeTiles = true;
		int i = 0;
		totalTileSquare = 0;
		for (RawTile rawTile : rawTiles) {
			tiles[i] = new Tile(i, rawTile, maxTile.width(), maxTile.height(), false);
			totalTileSquare += tiles[i].size();
			if (singleTileSize != tiles[i].size())
				allSameSizeTiles = false;
			i++;
		}
		i = 1;
	}

	private void exploreTile(char[][] in, int x, int y, RawTile piece) {
		piece.addUnits(x, y, in[y][x]);
		char blank = ' ';
		in[y][x] = blank;

		if (withinBound(in, x - 1, y - 1) && in[y - 1][x - 1] != blank) {
			exploreTile(in, x - 1, y - 1, piece);
		}
		if (withinBound(in, x - 1, y) && in[y][x - 1] != blank) {
			exploreTile(in, x - 1, y, piece);
		}
		if (withinBound(in, x - 1, y + 1) && in[y + 1][x - 1] != blank) {
			exploreTile(in, x - 1, y + 1, piece);
		}

		if (withinBound(in, x, y - 1) && in[y - 1][x] != blank) {
			exploreTile(in, x, y - 1, piece);
		}
		if (withinBound(in, x, y + 1) && in[y + 1][x] != blank) {
			exploreTile(in, x, y + 1, piece);
		}

		if (withinBound(in, x + 1, y - 1) && in[y - 1][x + 1] != blank) {
			exploreTile(in, x + 1, y - 1, piece);
		}
		if (withinBound(in, x + 1, y) && in[y][x + 1] != blank) {
			exploreTile(in, x + 1, y, piece);
		}
		if (withinBound(in, x + 1, y + 1) && in[y + 1][x + 1] != blank) {
			exploreTile(in, x + 1, y + 1, piece);
		}
	}

	private boolean withinBound(char[][] in, int x, int y) {
		return y >= 0 && y < in.length && x >= 0 && x < in[y].length;
	}
	
	public void consY() {
		
		int totalTilesNum = 0;
		for( int i = 0; i < this.tiles.length; i++ ) {
			totalTilesNum += this.tiles[i].oCount;
		}
		
		tilesId = new int[totalTilesNum];
		tilesX = new int[totalTilesNum][];
		tilesY = new int[totalTilesNum][];
		tilesCh = new char[totalTilesNum][];
		
		int index = 0;
		int indexY = 0;
		for( int i = 0; i < this.tiles.length; i++ )
			for( int j = 0; j < tiles[i].oCount; j++ ) {
				tilesId[index] = tiles[i].id;
				tilesX[index] = new int[tiles[i].size()];
				tilesY[index] = new int[tiles[i].size()];
				tilesCh[index] = new char[tiles[i].size()];
				
				ArrayList<Integer> rowY = new ArrayList<Integer>();
				rowY.add(tiles[i].id);
				
				for( int k = 0; k < tiles[i].size(); k++ ){
					tilesX[index][k] = tiles[i].pX[j][k];
					tilesY[index][k] = tiles[i].pY[j][k];
					tilesCh[index][k] = tiles[i].pCh[j][k];
					
					for( int l = 0; l < this.board.size(); l++ ) {
						if( sameUnit(tiles[i].pX[j][k],tiles[i].pY[j][k],tiles[i].pCh[j][k],board,l)){
							rowY.add(l+tiles.length);
							break;
						}
					}
				}
				if( rowY.size() == (tiles[i].size()+1) ) {
					mtrY.put(indexY, rowY);
					indexY ++;
				}
				index++;
			}
	}
	
	public boolean sameUnit(int x, int y, char ch, Tile board, int l){
		if( x == board.pX[0][l] && y == board.pY[0][l] && ch == board.pCh[0][l])
			return true;
		else return false;
	}
	
	public void consX() {
		
		ArrayList<Integer> rowX;
		for( int i = 0; i < (tiles.length+board.size()); i++ ){
			rowX = new ArrayList<Integer>();
			for( int key : mtrY.keySet() ){
				ArrayList<Integer> value = mtrY.get(key);
				if( value.contains(new Integer(i)))
					rowX.add(key);
			}
			mtrX.put(i, rowX);
		}
		int index = 1;
	}
	
	public void solve(HashMap<Integer, ArrayList<Integer>> X, HashMap<Integer, ArrayList<Integer>> Y, ArrayList<Integer> solution){
		if (X.isEmpty()) {
			if (!soluList.contains(solution)) {
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				for (int i = 0; i < solution.size(); i++)
					tmp.add(solution.get(i));
				this.soluList.add(tmp);
				
				System.out.print("Solution ");
				System.out.print(soluList.indexOf(tmp));
				System.out.print(": ");
				for (int i : solution) {
					System.out.print(i);
					System.out.print(' ');
				}
				
				long end = System.currentTimeMillis();
				System.out.print("  Time: ");
				System.out.print(end-tmpStart);
				tmpStart = end;
				System.out.println("\n");
			}
			
		} else {
			int c = minInX(X);
//			if( c == 0 ) return;
			ArrayList<Integer> list = X.get(c);
			HashMap<Integer, ArrayList<Integer>> cols;
			for (int i = 0; i < list.size(); i++) {
				int r = list.get(i);
				solution.add(r);
				cols = select(X, Y, r);
				solve(X, Y, solution);
				deselect(X, Y, r, cols);
				solution.remove(solution.size() - 1);
			}
		}
		int index = 1;
	}
	
	public int minInX( HashMap<Integer, ArrayList<Integer>> X ){
		int minNum = Integer.MAX_VALUE;
		int index=0;
		for( int key : X.keySet() ) {
			ArrayList<Integer> value = X.get(key);
			int k = value.size();
			if( k < minNum ){
				minNum = k;
				index = key;
			}
		}
		return index;
	}
	
	public HashMap<Integer, ArrayList<Integer>> select(HashMap<Integer, ArrayList<Integer>> X, HashMap<Integer, ArrayList<Integer>> Y, int r) {
		HashMap<Integer, ArrayList<Integer>> cols = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> rowY = Y.get(r);
		for( int j : rowY ){
			ArrayList<Integer> rowX = X.get(j);
			if (rowX != null) {
				for (int i : rowX) {
					ArrayList<Integer> rowY2 = Y.get(i);
					for (int k : rowY2) {
						if (k != j) {
							ArrayList<Integer> remove = X.remove(k);
							// if( remove == null ) remove = new
							// ArrayList<Integer>();
							if (remove != null) {
								remove.remove(new Integer(i));
								if (remove != null)
									X.put(k, remove);
							}
						}
					}
				}
			}
			ArrayList<Integer> p = X.remove(j);
			cols.put(j,p);
		}
		return cols;
	}
	
	public void deselect(HashMap<Integer, ArrayList<Integer>> X, HashMap<Integer, ArrayList<Integer>> Y, int r, HashMap<Integer, ArrayList<Integer>> cols) {
		ArrayList<Integer> rowY = Y.get(r);
		for( int f = rowY.size()-1; f >=0; f--) {
			int j = rowY.get(f);
//		for( int j : rowY ) {
			ArrayList<Integer> rowX = cols.remove(j);
			X.put(j, rowX);
			for( int i : rowX ){
				ArrayList<Integer> rowY2 = Y.get(i);
				for (int k : rowY2) {
					if (k != j) {
						ArrayList<Integer> remove = X.remove(k);
						if (remove == null)
							remove = new ArrayList<Integer>();
						if (!remove.contains(new Integer(i))) {
							remove.add(new Integer(i));
							X.put(k, remove);
						}
					}
				}
			}
		}
	}
	
	public void convertSolu(){
		if (brute == false) {
			for (int i = 0; i < this.soluList.size(); i++) {
				ArrayList tmp = this.soluList.get(i);
				HashMap<Integer, ArrayList<Unit>> solu = new HashMap<Integer, ArrayList<Unit>>();
				for (int j = 0; j < tmp.size(); j++) {
					ArrayList<Integer> rowY = mtrY.get(tmp.get(j));
					ArrayList<Unit> tile = new ArrayList<Unit>();
					int id = rowY.get(0);
					for (int k = 1; k < rowY.size(); k++) {
						int unitId = rowY.get(k) - this.tiles.length;
						Unit unit = new Unit(this.board.pX[0][unitId],
								this.board.pY[0][unitId],
								this.board.pCh[0][unitId]);
						tile.add(unit);
					}
					solu.put(id, tile);
				}
				this.soluInterface.add(solu);
			}
		}
		else {
			for (int i = 0; i < this.bruteForceSolu.size(); i++) {
				HashMap<Integer, ArrayList<Unit>> solu = new HashMap<Integer, ArrayList<Unit>>();
				for(int j = 0; j < this.tiles.length; j++ ) {
					ArrayList<Unit> tile = new ArrayList<Unit>();
					solu.put(j, tile);
				}
				for( int j = 0; j < bruteForceSolu.get(i).size(); j++ ) {
					int id = bruteForceSolu.get(i).get(j)-1;
					Unit unit = new Unit(this.board.pX[0][j], this.board.pY[0][j], this.board.pCh[0][j]);
					solu.get(id).add(unit);
				}
				this.soluInterface.add(solu);
			}
		}
		int index = 1;
	}
	
	public void bruteForce(){
		ArrayList<Integer> solu = new ArrayList<Integer>();
		for( int i = 0; i < this.board.size(); i++ )
			solu.add(0);
		bruteForceSearch(0, solu);
	}
	
	public void bruteForceSearch(int depth, ArrayList<Integer> solu){
		//for tile 'depth', consider the transition/rotation/flipption situation
//		System.out.print("Depth ");
//		System.out.print(depth);
//		System.out.println("\n");
		for( int i = 0; i < tiles[depth].oCount; i++ ) {
			ArrayList<Integer> subSolu = new ArrayList<Integer>(solu);
			
			//for tile 'depth', it contains totally k units
			Boolean flag = false; //if the board contains this tile, then true;
			for( int k = 0; k < tiles[depth].size(); k++ ){
				flag = false;
				//check if the kth units is in the board
				for( int l = 0; l < this.board.size(); l++ ) {
					if( sameUnit(tiles[depth].pX[i][k],tiles[depth].pY[i][k],tiles[depth].pCh[i][k],board,l) && subSolu.get(l) == 0){
						subSolu.set(l, depth+1);
						flag = true;
						break;
					}
				}
				if( flag == false ) break;
			}
			
			if( depth == tiles.length-1 && flag == true) {
				if( !bruteForceSolu.contains(subSolu))
					bruteForceSolu.add(subSolu);
				System.out.print("Solution ");
				System.out.print(": ");
				for (int j : subSolu) {
					System.out.print(j);
					System.out.print(' ');
				}
				
				long end = System.currentTimeMillis();
				System.out.print("  Time: ");
				System.out.print(end-tmpStart);
				tmpStart = end;
				System.out.println("\n");
			}
			else if( flag == true && depth < tiles.length-1 ){
				//ArrayList<Integer> subsubSolu = new ArrayList<Integer>(solu);
				bruteForceSearch(depth+1, subSolu);
			}
		}
	}
}
