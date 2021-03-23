package skipList;

public class SkipNode<T> {
	protected SkipNode<T>  above;//encima
	protected SkipNode<T>  below;//debajo
	protected SkipNode<T>  next;//siguiente
	protected SkipNode<T>  previous;//anterior
	protected T key;
	
	public SkipNode(T key) {
		this.key = key;
		this.above = null;
		this.below = null;
		this.next = null;
		this.previous = null;
	}
	/**
	 * 
	 * @param key
	 * @param next
	 */
	public SkipNode(T key, SkipNode<T>  next) {
		this.key = key;
		this.above = null;
		this.below = null;
		this.next = next;
		this.previous = null;
	}
	public SkipNode() {
		this.key = null;
		this.above = null;
		this.below = null;
		this.next = null;
		this.previous = null;
	}
	public SkipNode(SkipNode<T>  next,SkipNode<T>  above
			,SkipNode<T>  below, T key) {
		this.key = key;
		this.above = above;
		this.below = below;
		this.next = next;
		this.previous = null;
	}
}
