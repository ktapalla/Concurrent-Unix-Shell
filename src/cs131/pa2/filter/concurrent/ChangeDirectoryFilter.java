package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import cs131.pa2.filter.Filter;
import cs131.pa2.filter.Message;

/**
 * Implements cd command - includes parsing cd command, detecting if input or
 * output filter was linked, as well as overriding necessary behavior of
 * SequentialFilter.
 * 
 * @author Chami Lamelas
 *
 */
public class ChangeDirectoryFilter extends ConcurrentFilter {

	/**
	 * absolute path to directory cd will cause cwd to be changed to
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	private Thread curThread;

	/**
	 * Constructs a ChangeDirectoryFilter given a cd command.
	 * 
	 * @param cmd cmd is guaranteed to either be "cd" or "cd" followed by a space.
	 * @throws IllegalArgumentException if the directory in the command cannot be
	 *                                  found or if a directory parameter was not
	 *                                  provided
	 */
	public ChangeDirectoryFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setPrevFilter and setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just "cd" =>
		// cd needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, directory will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		// if we have a non-special destination directory, append it to cwd and set it
		// to dest
		if (!relativeDest.equals(".") && !relativeDest.equals("..")) {
			dest = ConcurrentREPL.currentWorkingDirectory + Filter.FILE_SEPARATOR + relativeDest;

			// make sure that this is a valid directory, if not throw appropriate IAE
			File destFile = new File(dest);
			if (!destFile.isDirectory()) {
				throw new IllegalArgumentException(Message.DIRECTORY_NOT_FOUND.with_parameter(cmd));
			}

			// if specified relative destination is . or .., just set that as dest
		} else {
			dest = relativeDest;
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

	/**
	 * Overrides run() in ConcurrentFilter to start cd thread and 
	 * properly implement this command
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.curThread = Thread.currentThread();
		while (!this.curThread.isInterrupted()) {
			// if .., then find parent of the cwd
			if (dest.equals("..")) {
				String parent = new File(ConcurrentREPL.currentWorkingDirectory).getParent();

				// if a parent exists, change cwd to parent, else leave it (this handles case if
				// user keeps doing cd .., cd .., etc this will eventually just stop them in the
				// directory that has no root (C:/) on windows)
				if (parent != null) {
					ConcurrentREPL.currentWorkingDirectory = parent;
				}

				// if a relative dest was specified and included in dest in constructor, change
				// cwd, otherwise (user did cd .) leave cwd as is
			} else if (!dest.equals(".")) {
				ConcurrentREPL.currentWorkingDirectory = dest;
			}
			// interrupts thread after changing directory so it ends after completing its job
			this.curThread.interrupt();
		} 

	}

	/**
	 * Overrides SequentialFilter.setPrevFilter() to not allow a {@link Filter} to
	 * be placed before {@link ChangeDirectoryFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter before this one (since cd doesn't take input)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}

	/**
	 * Overrides SequentialFilter.setNextFilter() to not allow a {@link Filter} to
	 * be placed after {@link ChangeDirectoryFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter after this one (since cd doesn't make output)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}
}
