The scheduler lab assignment was written in Java and consists of the SchedulerM (the main) and objects Scheduler and Process. The program tests three process scheduling algorithms: first come first serve, round robin, and shortest time remaining. A process is identified by four integers, representing Process ID, CPU time, I/O time and Arrival time respectively. The convention for process IDs is to start with 0 and be numbered sequentially. A test input is included (scheduler_in.txt). The output shows the state of processes at each clock cycle and statistics about the length of time to finish all processes. The output file has the name of the input file followed by 1, 2 or 3 representing the selected scheduling algorithm.  

To run the program, compile with javac

javac SchedulerM.java
java SchedulerM

The user is prompted to enter a valid file path for the input file located on the user's machine. For example, a valid path would be 

Example/example/example/.../Romanova-Olga-linker/input-1.txt 

Then then the program prompts the user to enter 0 1 or 2 for the scheduling algorithms. The program creates an output file with the required naming convention and shows the name of the file created. 

