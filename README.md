# README - Concurrent UNIX Shell 

This program is a concurrent a UNIX shell, which is a command line interpreter. It allows users to interact with a computer's operating system through a set of specific commands. Java Threads are used to implement the program's concurrency in two different ways. One way is that the subcommands of compound commands are run concurrently, and another is that users are able to choose whether to execute the commands in the background or foreground. This program was made for an Operating Systems class, and files we were told not to edit have not had any code or comments changed from the original files provided. 

## Installation and Execution 

Get the files from GitHub through your shell, move into the directory you have it in. Then you'll want to go into the src folder as well. 

When you're in the src folder, compile the project with: 
``` bash
javac cs131/pa2/filter/*.java cs131/pa2/filter/concurrent/*.java 
```
After compiling, start the project with:
``` bash
java cs131/pa2/filter/concurrent/ConcurrentREPL
```

Your terminal/console should now show you a message saying:
``` bash
Welcome to the Unix-ish command line.
> 
```

This indicates that the program is up and running, and that you can use it to interact with your computer's operating system. 

## Accepted Commands 

|  Command  |  Description  |
|:----------|:--------------|
| ``` pwd ``` |  Returns current working directory  |
| ``` ls ``` |  Lists the files in the current working directory  |
| ``` cd <dir> ``` |  Changes the current working directory to ``` <dir> ``` |
| ``` cat <file> ``` |  Writes ``` <file>```'s contents into pipe or stdout  |
| ``` head/tail ``` |  Returns up to the first/last 10 lines from piped input  |
| ``` grep <query> ``` |  Returns all lines from piped input that contain ``` <query> ``` |
| ``` wc ``` |  Counts the number of lines, words, and characters in the piped input  |
| ``` uniq ``` |  Returns all lines from piped input that are not the same as the previous line  |
| ``` > <file> ``` |  Redirects output of preceding command, writes it to file ``` <file> ```  |
| ``` exit ``` |  Prints "goodbye" and terminates the REPL  |
| ``` & ``` |  Indicates a background command (example of use shown below)  |
| ``` repl_jobs ``` |  Lists all of the currently running background commands  |
| ``` kill <#> ``` |  Kill/End command ``` <#> ``` (example of use shown below) |

## Notes 

### Parameters: 

Parameters are the arguments specified in the angle brackets ``` <> ``` above that are towards the right of the subcommands. All parameters are required to successfully run those commands. If not included, and error message will be given to the user instead. 

### Comound Commands/Pipelines: 

The REPL can take pipelines, or compound commands, where the processes are chained together so that the output of each process is passed into the input of the next command. The compound commands are read from left to right, using the pipe operator **|**. For example, compound commands would be put in like this: 

``` command1 ``` **|** ``` command2 ``` **|** ``` command3 ```

For the above example, the output of ```command1``` would be the input of ```command2```, and the output of ```command2``` would be the input of ```command3```. 

### Background/Foreground Commands: 

Commands can be run as background or foreground commands. One way to put is it that background commands are executed 'behind the scenes,' which means they don't stop the user from having the REPL execute other different commands until that one is finished. On the other hand, a foreground command would perform in an opposite manner. If a user executes a foreground command on the REPL, the console will not accept any other different commands until the foreground command has been fully executed. An ampersand **&** is used to indicate a background command, while a foreground command would be typed the same, but without the ampersand. Note that there shouldn't be a pipe (**|**) symbol before the background (**&**) symbol. The following is an example of how to use background and foreground commands: 

``` > cat large-file.txt & ```

The above command would be executed as a background command, as it has the ampersand at the end of the line. Users that type this command into the REPL will still be able to execute other commands even if that background command hasn't comepleted. Below is the foreground command version of it: 

``` > cat large-file.txt ```

Since there is no ampersand at the end, the line above is executed as a foreground command. This means that it must finish before the user can enter a new command into the REPL. As long as not all of large-file.txt's contents have been read and returned, then the REPL will not accept any new commands from the user. 

### Killing/Ending Commands: 

Users are able to 'kill,' or end, any background commands that are currently still running. To see which are still running, they run the following command: 

``` > repl_jobs ```

This will print the currently running commands to the console. The different background commands will be assigned a number to differentiate them from each other, which can then be used to kill said command. Here's an example of how that's done:

``` > repl_jobs ```
``` 1. backgroundcommand1 & ```
``` 2. backgroundcommand2 & ```
``` 3. backgroundcommand3 & ```
``` 4. backgroundcommand4 & ```

The above block indicates that when the user runs ``` > repl_jobs ```, there are 4 background commands still up and running. To kill the ``` backgroundcommand3 & ``` command, the user would then type in the following: 

``` > kill 3 ```

Doing the above would kill ``` backgroundcommand3 & ``` by stopping the execution of every subcommand included in it. 
