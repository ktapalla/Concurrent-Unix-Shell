package cs131.pa2.filter.concurrent;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements uniq command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class UniqFilter extends ConcurrentFilter {

	/**
	 * stores unique strings seen in input
	 */
	private Set<String> currUniq;

	private Thread curThread;
	/**
	 * Constructs a uniq filter.
	 */
	public UniqFilter() {
		super();
		currUniq = new HashSet<String>();
	}

	/**
	 * Overrides SequentialFilter.processLine() - only returns lines to
	 * {@link ConcurrentFilter#process()} that aren't duplicates.
	 */
	@Override
	protected String processLine(String line) {
		if (!currUniq.contains(line)) {
			currUniq.add(line);
			return line;
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Overrides run() in ConcurrentFilter to start uniq thread and 
	 * properly implement this command
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			try {
				String line = input.take();
				// does until poison pill is spotted since it indicates the end of expected input values
				while (!line.equals(PoisonPill.pill)){
					String processedLine = processLine(line);
					if (processedLine != null){
						output.put(processedLine);
					}
					line = input.take();
				}	
				// adds poison pill to output to indicate end of expected output for next filter
				output.add(PoisonPill.pill);
				// interrupts this thread since job is complete
				this.curThread.interrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
