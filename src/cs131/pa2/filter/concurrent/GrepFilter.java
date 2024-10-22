package cs131.pa2.filter.concurrent;

import cs131.pa2.filter.Message;

/**
 * Implements grep command - includes parsing grep command by overriding
 * necessary behavior of SequentialFilter.
 * 
 * @author Chami Lamelas
 *
 */
public class GrepFilter extends ConcurrentFilter {

	/**
	 * holds the grep query
	 */
	private String query;

	private Thread curThread;
	/**
	 * constructs GrepFilter given grep command
	 * 
	 * @param cmd cmd is guaranteed to either be "grep" or "grep" followed by a
	 *            space.
	 * @throws IllegalArgumentException if query parameter was not provided
	 */
	public GrepFilter(String cmd) {

		// find index of space, if there isn't a space that means we got just "grep" =>
		// grep needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, query will be trimmed string after space
		query = cmd.substring(spaceIdx + 1).trim();
	}

	/**
	 * Overrides  SequentialFilter.processLine() - only returns lines to
	 * {@link ConcurrentFilter#process()} that contain the query parameter specified
	 * in the command passed to the constructor.
	 */
	@Override
	protected String processLine(String line) {

		// only have SequentialFilter:process() add lines to the output queue that
		// include the query string
		if (line.contains(query)) {
			return line;
		}

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides run() in ConcurrentFilter to start grep thread and 
	 * properly implement this command
	 */
	@Override
	public void run()  {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			try {
				String line = input.take();
				// makes sure poison pill isn't seen yet to indicate there is still possible input
				while (!line.equals(PoisonPill.pill)){
					String processedLine = processLine(line);
					if (processedLine != null){
						output.put(processedLine);
					}
					line = input.take();
				}
				// inserts poison pill to output to indicate no more expected values
				this.output.put(PoisonPill.pill);
				// interrupts thread to end process since its job is done
				this.curThread.interrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

}
