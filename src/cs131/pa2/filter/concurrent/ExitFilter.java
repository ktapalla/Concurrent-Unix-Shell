package cs131.pa2.filter.concurrent;

import cs131.pa2.filter.Filter;
import cs131.pa2.filter.Message;

/**
 * Implements part of exit command - includes detecting if input/output filter
 * was linked. Actual exiting behavior is done in SequentialREPL.java.
 * 
 * @author Chami Lamelas
 *
 */
public class ExitFilter extends ConcurrentFilter {

	private Thread curThread;
	
	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs an ExitFilter from an exit command
	 * 
	 * @param cmd - exit command, will be "exit" or "exit" surrounded by whitespace
	 */
	public ExitFilter(String cmd) {
		super();
		// save command as a field, we need it when we throw an exception in
		// setPrevFilter and setNextFilter
		command = cmd;
	}

	/**
	 * Overrides run() in ConcurrentFilter to immediately terminate the thread
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		this.curThread.interrupt();
	}
	
	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides SequentialFilter.setPrevFilter() to not allow a
	 * {@link Filter} to be placed before {@link ExitFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter before this one (since exit doesn't take input)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}

	/**
	 * Overrides SequentialFiltersetNextFilter() to not allow a
	 * {@link Filter} to be placed after {@link ExitFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter after this one (since exit doesn't make output)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}
}
