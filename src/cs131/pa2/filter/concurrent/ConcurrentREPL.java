package cs131.pa2.filter.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 * 
 * @author cs131a
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;

	/**exit
	 * 
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		int bckCount = 0;
		// set cwd here so that it can be reset by tests that run main() function
		currentWorkingDirectory = System.getProperty("user.dir");
		List<Thread> bkgrndFinalThreads = new LinkedList<Thread>();
		
		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);


		// whether or not shell is running
		boolean running = true;

		do {
			System.out.print(Message.NEWCOMMAND);

			// read user command, if its just whitespace, skip to next command
			String cmd = consoleReader.nextLine();


			if (cmd.isBlank()) {
				continue;
			}


			if (!cmd.endsWith("&") && !cmd.equals("repl_jobs") && !cmd.contains("kill ")) {
				// parse command into sub commands, then into Filters, add final PrintFilter if
				// necessary, and link them together - this can throw IAE so surround in
				// try-catch so appropriate Message is printed (will be the message of the IAE)
				try {
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
					// if we have only an ExitFilter, that means user typed "exit" or "exit"
					// surrounded by ws, stop the shell
					if (filters.size() == 1 && filters.get(0) instanceof ExitFilter) {
						running = false;
					} else {
						// otherwise, call process on each of the filters to have them execute
						// TODO - EDIT THIS SO I CAN IMPLEMENT T.JOIN (
						
						for (ConcurrentFilter filter : filters) {
							Thread t = new Thread(filter);
							t.start();
							if (filter == filters.get(filters.size()-1)) {
								try {
									t.join();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}
					}	
				} catch (IllegalArgumentException e) {
					System.out.print(e.getMessage());
				}
			} else if (cmd.equals("repl_jobs")) {
				// checks if jobs are alive and prints them, removes them from list otherwise
				for (int i = 0; i < bkgrndFinalThreads.size(); i++ ) {
					Thread checkThread = bkgrndFinalThreads.get(i);
					if (checkThread.isAlive()) {
						System.out.println("\t" + checkThread.getName());
					} else {
						bkgrndFinalThreads.remove(i);
						i--;
					}
				}
			}else if (cmd.startsWith("kill ")) {
				// kills background command using value of index which is part of their names by interrupting them
				String num = cmd.substring(cmd.indexOf(" ") + 1);
				int kInd = Integer.parseInt(num) - 1;				
				for (Thread t : bkgrndFinalThreads ) {
					if(t.getName().contains(num + ". ")) {
						t.interrupt();
						bkgrndFinalThreads.remove(t);
					}
				}

			} else {
				try {
					// parse command into sub commands, then into Filters, add final PrintFilter if
					// necessary, and link them together - this can throw IAE so surround in
					// try-catch so appropriate Message is printed (will be the message of the IAE)
					cmd = cmd.substring(0, cmd.indexOf("&"));
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
					
					// if we have only an ExitFilter, that means user typed "exit" or "exit"
					// surrounded by ws, stop the shell
					if (filters.size() == 1 && filters.get(0) instanceof ExitFilter) {
						running = false;
					} else {
						// otherwise, call process on each of the filters to have them execute
						
						for (ConcurrentFilter filter : filters) {
							Thread t = new Thread(filter);
							t.start();
							// adds final thread to list of final threads of background commands
							// used for kill and repl_jobs commands
							if (filter == filters.get(filters.size()-1)) {
								bckCount++;
								// index added as part of their name for easier killing of background commands
								t.setName(bckCount + ". " + cmd + "&");
								bkgrndFinalThreads.add(t);
							}
						}
					}
				} catch (IllegalArgumentException e) {
					System.out.print(e.getMessage());
				}

			}


		} while (running);
		System.out.print(Message.GOODBYE);

		consoleReader.close();

	}

}
