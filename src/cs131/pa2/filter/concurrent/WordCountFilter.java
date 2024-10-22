package cs131.pa2.filter.concurrent;

/**
 * Implements wc command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class WordCountFilter extends ConcurrentFilter {

	/**
	 * word count in input - words are strings separated by space in the input
	 */
	private int wordCount;

	/**
	 * character count in input - includes ws
	 */
	private int charCount;

	/**
	 * line count in input
	 */
	private int lineCount;

	private Thread curThread;

	/**
	 * Constructs a wc filter.
	 */
	public WordCountFilter() {
		super();
		wordCount = 0;
		charCount = 0;
		lineCount = 0;
	}

	/**
	 * Overrides SequentialFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		lineCount++;
		wordCount += line.split(" ").length;
		charCount += line.length();
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides run() in ConcurrentFilter to start wc thread and 
	 * properly implement this command
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			try {
				String line = input.take();
				// does until poison pill is spotted since it indicates end of expected input values
				while (!line.equals(PoisonPill.pill)) {
					processLine(line);
					line = input.take();
				}
				output.put(lineCount + " " + wordCount + " " + charCount);
				// adds poison pill to output to indicate end of expected values for next filter
				this.output.put(PoisonPill.pill);
				// interrupts this thread since job is done
				this.curThread.interrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
