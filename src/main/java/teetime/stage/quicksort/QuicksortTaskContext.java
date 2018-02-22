package teetime.stage.quicksort;

class QuicksortTaskContext {

	private final int[] elements;
	private final int[] stack;
	private int top = -1;

	private int lowestIndex;
	private int highestIndex;
	private int pivotIndex;

	public QuicksortTaskContext(final int[] elements) {
		this.elements = elements;
		this.stack = new int[elements.length];
	}

	public int getTop() {
		return top;
	}

	void push(final int lowestIndex, final int highestIndex) {
		stack[++top] = lowestIndex;
		stack[++top] = highestIndex;
	}

	void setRange() {
		this.highestIndex = stack[top--];
		this.lowestIndex = stack[top--];
	}

	public void setPivotIndex(final int pivotIndex) {
		this.pivotIndex = pivotIndex;
	}

	public int getLowestIndex() {
		return lowestIndex;
	}

	public int getHighestIndex() {
		return highestIndex;
	}

	public int getPivotIndex() {
		return pivotIndex;
	}

	public int[] getElements() {
		return elements;
	}
}
