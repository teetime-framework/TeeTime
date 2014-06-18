package teetime.examples.throughput.methodcall;

public interface Closure<I, O> {

	public abstract O execute(I element);
}
