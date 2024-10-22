package cs131.pa2.filter.concurrent;

/**
 * Implements head command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class HeadFilter extends ConcurrentFilter {

	/**
	 * number of lines read so far
	 */
	private int numRead;

	/**
	 * number of lines passed to output via head
	 */
	private static int LIMIT = 10;

	private Thread curThread;

	/**
	 * Constructs a head filter.
	 */
	public HeadFilter() {
		super();
		numRead = 0;
	}

	/**
	 * Overrides run() in ConcurrentFilter to start head thread and 
	 * properly implement this command
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			try {
				String line = input.take();			
				// makes sure poison pill isn't seen yet to indicate there's still possible input
				// limits to 10 first lines
				while (!line.equals(PoisonPill.pill) && numRead < LIMIT) {
					output.put(line);
					numRead++;
					line = input.take();
				}
				// inserts poison pill after 10 to indicate no more values should be expected
				this.output.put(PoisonPill.pill);
				// interrupts thread to terminate process since job is done
				this.curThread.interrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
