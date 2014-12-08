package data;

import java.util.HashSet;

public class RawTile implements Comparable<RawTile> {

	protected HashSet<Unit> units = new HashSet<Unit>();
	public HashSet<Unit> nomUnits = new HashSet<Unit>();

	protected int left = 0;
	protected int top = 0;
	protected int right = 0;
	protected int bottom = 0;

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getRight() {
		return right;
	}

	public int getBottom() {
		return bottom;
	}

	public int width() {
		return right - left + 1;
	}

	public int height() {
		return bottom - top + 1;
	}

	public int size() {
		return units.size();
	}

	public void addUnits(int x, int y, char ch) {
		if (units.isEmpty()) {
			left = right = x;
			top = bottom = y;
		} else {
			if (x < left)
				left = x;
			else if (x > right)
				right = x;
			if (y < top)
				top = y;
			else if (y > bottom)
				bottom = y;
		}
		units.add(new Unit(x, y, ch));
	}

	public void nomalize() {
		for (Unit unit : units) {
			nomUnits.add(new Unit(unit.x - left, unit.y - top, unit.ch));
		}
	}

	@Override
	public int compareTo(RawTile o) {
		if (o == null)
			return 1;
		return this.units.size() - o.units.size();
	}
}
