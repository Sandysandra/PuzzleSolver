package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Tile {

	public int id;
	public RawTile raw;
	private int size;
	private int right = 0;
	private int bottom = 0;

	private int boardWidth;
	private int boardHeight;
	public int[][] pX;
	public int[][] pY;
	public char[][] pCh;
	int[][] tDim; // 0 = height, 1 = width
	public int oCount;
	int curO;

	public static final int ROTATE_0 = 0x01;
	public static final int ROTATE_90 = 0x02;
	public static final int ROTATE_180 = 0x04;
	public static final int ROTATE_270 = 0x08;
	public static final int FLIP_ROTATE_0 = 0x10;
	public static final int FLIP_ROTATE_90 = 0x20;
	public static final int FLIP_ROTATE_180 = 0x40;
	public static final int FLIP_ROTATE_270 = 0x80;

	public static final int[] ROTATION = new int[] { ROTATE_0, ROTATE_90,
			ROTATE_180, ROTATE_270 };

	public static final int[] ORIENTATION = new int[] { ROTATE_0, ROTATE_90,
			ROTATE_180, ROTATE_270, FLIP_ROTATE_0, FLIP_ROTATE_90,
			FLIP_ROTATE_180, FLIP_ROTATE_270 };

	public static final int RF_90_270 = ROTATE_90 | ROTATE_270 | FLIP_ROTATE_90
			| FLIP_ROTATE_270;

	public Tile(int id, RawTile raw, int boardWidth, int boardHeight, boolean isBoard) {
		this.id = id;
		this.raw = raw;
		this.size = raw.size();
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		if( isBoard )
			process(new int[] { ROTATE_0 });
		else
			process(ORIENTATION);
	}

	public void process(int[] orientation) {
		int rawLeft = raw.getLeft();
		int rawTop = raw.getTop();
		this.right = raw.getRight() - rawLeft;
		this.bottom = raw.getBottom() - rawTop;

		int o;
		ArrayList<Unit[]> lstPoints = new ArrayList<Unit[]>();
		ArrayList<int[]> lstDimention = new ArrayList<int[]>();
		oCount = 0;
		int adjX;
		int index;
		Unit[] points;
		Unit[] ref;
		boolean matched;
		int x, y;
		for (int oi = 0; oi < orientation.length; oi++) {
			o = orientation[oi];

/*			adjX = Integer.MAX_VALUE;
			for (Unit sq : raw.units) {
				x = sq.x - rawLeft;
				y = sq.y - rawTop;

				if (getY(x, y, o) == 0) {
					if (getX(x, y, o) <= adjX) {
						adjX = getX(x, y, o);
					}
				}
			}*/

			index = 0;
			points = new Unit[this.size];
			for (Unit sq : raw.units) {
				x = sq.x - rawLeft;
				y = sq.y - rawTop;

				// points[index++] = new Unit(getX(x, y, o) - adjX,
				// getY(x, y, o), sq.ch);
				points[index++] = new Unit(getX(x, y, o), getY(x, y, o), sq.ch);
			}
			Arrays.sort(points, new Comparator<Unit>() {
				@Override
				public int compare(Unit o1, Unit o2) {
					return (o1.y - o2.y) != 0 ? o1.y - o2.y : o1.x - o2.x;
				}
			});

			// if new, add to lstPoints
			matched = false;
			for (curO = oCount; curO-- > 0 && !matched;) {
				ref = lstPoints.get(curO);
				for (index = size; index-- > 0;) {
					if (!ref[index].equals(points[index])) {
						break;
					}
				}
				matched = index == -1;
			}
			if (!matched) {
				lstPoints.add(points);
				if ((o & RF_90_270) == 0)
					lstDimention.add(new int[] { bottom + 1, right + 1 });
				else
					lstDimention.add(new int[] { right + 1, bottom + 1 });
				oCount++;
			}
		}
		
		/////////////////////////////////////
		oCount = 0;
		ArrayList<Unit[]> tileList = new ArrayList<Unit[]>();
		ArrayList<int[]> tileLstDimention = new ArrayList<int[]>();
		index = 0;
		
		for( curO = 0; curO < lstPoints.size(); curO++ ){
			int tileB = this.boardHeight - lstDimention.get(curO)[0]+1;
			int tileR = this.boardWidth - lstDimention.get(curO)[1]+1;
			for( int i = 0; i < tileB; i++ )
				for( int j =0; j < tileR; j++ ){
					index = 0;
					ref = lstPoints.get(curO);
					points = new Unit[this.size];
					for( int k = 0; k < this.size; k++ ){
						points[index++] = new Unit(ref[k].x+j, ref[k].y+i, ref[k].ch);
					}
					tileList.add(points);
					tileLstDimention.add(lstDimention.get(curO));
					oCount++;
				}
		}
		/////////////////////////////////////////
		
		pX = new int[oCount][size];
		pY = new int[oCount][size];
		pCh = new char[oCount][size];
		tDim = new int[oCount][];
		for (curO = 0; curO < tileList.size(); curO++) {
			ref = tileList.get(curO);
			for (index = 0; index < size; index++) {
				pX[curO][index] = ref[index].x;
				pY[curO][index] = ref[index].y;
				pCh[curO][index] = ref[index].ch;
			}
			tDim[curO] = tileLstDimention.get(curO);
		}
		curO = 0;
	}

	protected int getX(Unit sq, int orientation) {
		return getX(sq.x, sq.y, orientation);
	}

	protected int getX(int x, int y, int orientation) {
		switch (orientation) {
		case ROTATE_90:
			return y;
		case ROTATE_180:
			return right - x;
		case ROTATE_270:
			return bottom - y;
		case FLIP_ROTATE_0:
			return right - x;
		case FLIP_ROTATE_90:
			return y;
		case FLIP_ROTATE_180:
			return x;
		case FLIP_ROTATE_270:
			return bottom - y;
		default:
			return x;
		}
	}

	protected int getY(Unit sq, int orientation) {
		return getY(sq.x, sq.y, orientation);
	}

	protected int getY(int x, int y, int orientation) {
		switch (orientation) {
		case ROTATE_90:
			return right - x;
		case ROTATE_180:
			return bottom - y;
		case ROTATE_270:
			return x;
		case FLIP_ROTATE_0:
			return y;
		case FLIP_ROTATE_90:
			return x;
		case FLIP_ROTATE_180:
			return bottom - y;
		case FLIP_ROTATE_270:
			return right - x;
		default:
			return y;
		}
	}

	public int size() {
		return raw.size();
	}

}