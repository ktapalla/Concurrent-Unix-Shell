package cs131.pa2.filter.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements tail command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class TailFilter extends ConcurrentFilter {

	/**
	 * number of lines passed to output via tail
	 */
	private static int LIMIT = 10;

	/**
	 * line buffer
	 */
	private List<String> buf;

	private Thread curThread;

	/**
	 * Constructs a tail filter.
	 */
	public TailFilter() {
		super();
		buf = new LinkedList<String>();
	}

	/**
	 * Overrides run() in ConcurrentFilter to start tail thread and 
	 * properly implement this command
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			// until the input is empty, add line to end of buffer if buffer reached LIMIT,
			// remove the head (LinkedList makes this O(1)), could also use Queue/Deque
			// removing the head removes the oldest line seen so far - this way buf will
			// hold the last 10 lines of the input (or as many lines were in the input if
			// the input had < 10 lines)
			try {
				String line = input.take();
				// does it until it spots poison pill since that indicates no more expected input values
				while (!line.equals(PoisonPill.pill)) {
					buf.add(line);
					if (buf.size() > LIMIT) {
						buf.remove(0);
					}
					line = input.take();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// once we're done with the input (and have identified the last 10 lines), add
			// them to the output in the order in which they appeared in the input
			while (!buf.isEmpty()) {
				try {
					output.put(buf.remove(0));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// adds poison pill to output to indicate end of expected values for next filter
			try {
				this.output.put(PoisonPill.pill);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// interrupts thread to indicate job is complete
			this.curThread.interrupt();
		}
	}

	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

}
