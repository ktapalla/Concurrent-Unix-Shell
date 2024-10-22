package cs131.pa2.filter.concurrent;

/**
 * Implements printing as a {@link ConcurrentFilter} - overrides necessary
 * behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class PrintFilter extends ConcurrentFilter {

	private Thread curThread;

	public PrintFilter() {
	}


	/**
	 * Overrides SequentialFilter.processLine() to just print the line to stdout.
	 */
	@Override
	protected String processLine(String line) {
		if (!line.equals(null)) {
			System.out.println(line);			
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides run() in ConcurrentFilter to start print thread and 
	 * properly print the results
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			try {
				String line = input.take();
				// continues while poison pill isn't seen yet to indicate more possible expected input values
				while (!line.equals(PoisonPill.pill)){
					processLine(line);
					line = input.take();
				}
				// interrupts thread after seeing poison pill since it indicates the job is done
				this.curThread.interrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
