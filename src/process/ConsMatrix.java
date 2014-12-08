package process;

import data.Tile;

public class ConsMatrix {
	
	ParseInput parseInput;
	int[][] id;
	int[][] tilesX;
	int[][] tilesY;
	char[][] tilesCh;
	
	public ConsMatrix( ParseInput parseInput ){
		this.parseInput = parseInput;
	}
	
	public void ConsY() {
		
		int tileSize = this.parseInput.tiles.length;
		Tile[] tiles = new Tile[tileSize];
		
		int totalTilesNum=0;
		for( int i = 0; i < tileSize; i++ ) {
			tiles[i] = this.parseInput.tiles[i];
		}
		
		for( int i = 0; i < tiles.length; i++ ) {
			for( int j = 0; j < tiles[i].pX.length; j++ ){
				
			}
		}
	}

}
