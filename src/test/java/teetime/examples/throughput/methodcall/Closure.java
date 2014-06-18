package teetime.examples.throughput.methodcall;

public interface Closure<I, O> {

	O execute(I element);
}
