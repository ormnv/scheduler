/* Olga Romanova
 * Operating Systems    
 * Spring 2013 
 * Lab 2 - Scheduler 
 * 
 */

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.text.NumberFormat;
import java.lang.NumberFormatException;

public class Scheduler
{
    //will contain the input file in a string 
    private ArrayList<String> file;  
    private ArrayList<Process> processes;
    private LinkedList<Process> readyQ;
    private LinkedList<Process> blockedQ;
    //the running queue will only have one process 
    private LinkedList<Process> runningQ;


    //accepts file
    public Scheduler (ArrayList<String> t)
    {
        file = new ArrayList<String>(t);
        processes= new ArrayList<Process>();
        readyQ = new LinkedList<Process>();
        blockedQ = new LinkedList<Process>();
        runningQ = new LinkedList<Process>();
    }    


    //reads from tokens into arraylist of processes
    public void getProcess ()
    {
        int fsize = file.size();
      
        //count ranges from 0 to 3 for each respective int that represents a process
        int count =0;

        //ints to hold the id, cpu time, io time and arrival time variables of the process that is currently being parsed from file
        int currentID = -1;
        int currentCPU = -1;
        int currentIO = -1;
        int currentAT = -1;

        //represents current string of length 1
        String currentI="";

        //The for loop will iterate through the file, string by string  
        for (int i =0; i<fsize; i++)
            {   
                currentI=file.get(i);

                //parse the int. Each process is denoted by 4 ints. 
                try
                    { 
                        int value = Integer.parseInt(currentI);

                        switch(count)
                            {
                            case 0: // Process ID int 
                                currentID = value;
                                count++;
                                break;
                                    
                            case 1: //CPU time int
                                currentCPU = value;
                                count++;
                                break;

                            case 2: // I/O time int
                                currentIO=value;
                                count++;
                                break;

                            case 3: //arrival time int
                                currentAT=value;
                                count=0;
                                Process currentProcess = new Process(currentID, currentCPU, currentIO, currentAT);

                                //Add process to the scheduler at position [i], which corresponds with arrival time
                                processes.add(currentProcess);
                                break;

                            default:
                                System.out.println("SOMETHING WRONG.");
                                break;
                            }//switch

                    }//try
                
                //If  currentS is not an int, it must be the letter corresponding to a module or a symbol 
                catch ( NumberFormatException e )
                    {    
                        e.getMessage();
                    }//catch number exception 

            } //for loop

    }//getProcess


    //returns true if all processes have finished when the status for each process is d for done. 
    public boolean isAlldone()
    {
        char status;
        boolean isDone = true;
        
        //check for empty process queue, which would cause true to return incorrectly
        if(processes.size()==0)
            {
                isDone = false;
            }

        //all processes are returned to the queue with a status of 'd' for done 
        else
            {    

                for (int i =0; i<processes.size(); i++)
                    {   

                        status=processes.get(i).getStatus();
                        if(status != 'd')
                            {
                                isDone = false;
                            }    
                    
                    } 

                //if the other queues are not empty, allDone is not true 
                if(isUtilizingIO() || isUtlizingCPU() || isReady())
                    {
                        isDone = false;
                    }    

            }

        return isDone;
    }


    //checks if each process has a priority assigned 
    //this will be used for the first come first serve algorithm 
    public boolean arePrioritiesSet()
    {
        int priority=0;
        boolean areSet = true;

        for (int i =0; i<processes.size(); i++)
            {   

                priority=processes.get(i).getPriority();
                //-1 is the initial value for priority. If it has not been set to the actual priority, arePrioritiesSet is false

                if(priority == -1)
                    {
                        areSet = false;
                    }    

            } 

        return areSet;

    }

    
    //checks if a process is currently running / the runningQ is not empty
    public boolean isUtlizingCPU ()
    {

        boolean isUtil=false;

        //The runningQ only has one process. 
        if (runningQ.size()==0)
            {
                isUtil=false;
            }

        else
            {
                isUtil=true;
            }

        return isUtil;
    }
    

    //checks if a process is currently running, or that the blockedQ is not empty 
    public boolean isUtilizingIO ()
    {

        boolean isUtil=false;

        if (blockedQ.size()==0)
            {
                isUtil=false;
            }

        else
            {
                isUtil=true;
            }

        return isUtil;
    }


    //checks if a process is currently ready, or that the readyQ is not empty
    public boolean isReady ()
    {
        boolean isUtil=false;

        if (readyQ.peek()==null)
            {
                isUtil=false;
            }

        else
            {
                isUtil=true;
            }

        return isUtil;
    }


    public String fcfs()
    {

        int clockTime=0;     

        //get priorities 
        int minPrior=1;
        int soonest=-1;
        int whileCount=0;
        int anyDupes=0;
        int lowestID=-1;
        
        //will be incremented for ever clockTime for which the runningQ is not empty
        int cpuUtil=0;

        //will be incremented for ever clockTime for which !isAlldone
        int finishingTime=0;

        //will be used for output file
        String output="";
        
        while(!arePrioritiesSet())
            {    

                for (int i =0; i<processes.size(); i++)
                    {   

                        if(processes.get(i).getPriority()==-1)
                            {    

                                if(processes.get(i).getArrivalTime() < soonest)
                                    {
                                        soonest=processes.get(i).getArrivalTime();
                                    } 

                                //first time for loop is executed, the process that arrives first is found. On the subsequent executions of the for loop, the first process is asigned
                                //first priority. Next, the second soonest arriving process is found in the loop and asigned the next priority in the subsequent loop...and so on. 
                                if(processes.get(i).getArrivalTime() == soonest)
                                    {

                                        //for the purposes of checking for duplicates
                                        lowestID=processes.get(i).getID();  

                                        //check for other processes with the same arrival time! 

                                        //If more than one duplicate arrival time exists, it will be asingned the priority when it is the current i in the for loop.
                                        //Only the lowest duplicate arrival time that has not yet been assigned a priority needs to be found.
                                        for (int j =0; j<processes.size(); j++)
                                            {  

                                                if(processes.get(j).getPriority()==-1)
                                                    {

                                                        if ((soonest == processes.get(j).getArrivalTime()) && (lowestID > processes.get(j).getID()) )
                                                            {
                                                                //only check for processes with the same arrival time AND a lower process ID
                                                                anyDupes++;
                                                                lowestID=processes.get(j).getID();
                                                            }    
                                                    }
                                            }    

                                        //after finding the lowest ID arriving at that time, assign the priority to that process 
                                        for (int j =0; j<processes.size(); j++)
                                            {  

                                                if(lowestID==processes.get(j).getID())
                                                    {
                                                        processes.get(j).setPriority(minPrior);
                                                        minPrior++;
                                                    }    
                                            }

                                        //reset 
                                        soonest=-1;
                                        anyDupes=0;
                                    }   
                            
                                if(soonest==-1 && processes.get(i).getPriority()==-1)
                                    {
                                        soonest=processes.get(i).getArrivalTime();
                                    }        
                       
                            }//if(processes.get(i).getPriority()==-1)

                    }//FOR

            }//while(!arePrioritiesSet())

        //For a first come first serve algorithm, the priority corresponds to the order in which the processes will be queued. 

        //Used to store the arrival time for a process to compare with the current clock time 
        int processAtime=-1;

        //Used to store the remaining CPU time left
        int currentCPUtimeLeft=-1;
      
        //if the CPU time is an odd number, this will handle rounding to next cycle 
        int halfCPUtime=0;

        //Used to store the remaining IO time left
        int currentIOtimeLeft=-1;

        //for first come first serve, the lowest ready process will be the process with the highest priority, which is based on arrival time with preference given to lower IDs
        int lowestReady=-1;
       
        //All done means all processes have returned back to processes arrayList with a status 'd' 
        while (!isAlldone())
            {

                for (int i =0; i<processes.size(); i++)
                    {   
                        //when the process arrival time matches the clock time, a process arrived at the clock cycle
                        processAtime=processes.get(i).getArrivalTime();
                        
                        if(clockTime == processAtime)
                            {
                                //Under first come first serve, if a process is running when another process arives, the new process is added to the readyQ
                                if(runningQ.size()!=0)
                                    {    
                                        processes.get(i).setStatus('r');
                                        readyQ.add(processes.remove(i));
                                    }

                                //if a process is not running, check for processes in the readyQ or blocked processes with IO times of zero. If the readyQ is empty and no such blocked processes exist,
                                // in the  run the process that arrived 
                                if(runningQ.size()==0)
                                    {    

                                        //if a process with a higher priority aka lower ID is in the ready queue 
                                        //move the running process from the running queue to the ready queue and run the process with the higher priority
                                        if(isReady())
                                            {   
                                                //find the process with the lowest ID in the ready queue
                                                for(int k=0; k<readyQ.size(); k++)
                                                    {

                                                        if(readyQ.get(k).getPriority() < processes.get(i).getPriority())
                                                            {

                                                                if(lowestReady==-1)
                                                                    {
                                                                        lowestReady= readyQ.get(k).getPriority();
                                                                    }

                                                                if( readyQ.get(k).getPriority() < lowestReady)
                                                                    {
                                                                        lowestReady= readyQ.get(k).getPriority();
                                                                    }    

                                                            }

                                                    }//first for loop

                                            }//if(isReady())

                                        //the readyQ and blockedQ must be checked if they are not empty before attempting to find a process within them. 
                                        if(isUtilizingIO())
                                            {
                                                //check for a blocked process with a lower ID that just finished the IO time 
                                                for(int k=0; k<blockedQ.size(); k++)
                                                    {

                                                        if(lowestReady==-1)
                                                            {
                                                                lowestReady= blockedQ.get(k).getPriority();
                                                            }
                                                        if(blockedQ.get(k).getPriority() < lowestReady && blockedQ.get(k).getIOtimeLeft()==0)
                                                            {
                                                                lowestReady= blockedQ.get(k).getPriority();
                                                            }

                                                    }//second for loop

                                            }//(isUtilizingIO())
                                                                               
                                        //Once the lowest ready priority was found in either or both loops above, find the matching process in the readyQ or blockedQ
                                        if(isReady())
                                            {  

                                                //run the ready process with the lowest priority number
                                                for(int k=0; k<readyQ.size(); k++)
                                                    {

                                                        if(lowestReady== readyQ.get(k).getPriority())
                                                            {
                                                                //set the status of the process to running 
                                                                readyQ.get(k).setStatus('u');
                                                                //add the arriving process to the readyQ
                                                                processes.get(i).setStatus('r');
                                                                readyQ.add(processes.remove(i));
                                                                //add the process that was ready to runningQ
                                                                runningQ.add(readyQ.remove(k));
                                                            }

                                                    }//2nd for loop

                                            }  // if(isReady())
                                        
                                        if(isUtilizingIO())
                                            {
                                                //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                                for(int k=0; k<blockedQ.size(); k++)
                                                    {

                                                        if(lowestReady== blockedQ.get(k).getPriority() && blockedQ.get(k).getIOtimeLeft()==0 )
                                                            {
                                                                //set the status of the process to running
                                                                blockedQ.get(k).setStatus('u');
                                                                processes.get(i).setStatus('r');
                                                                //add the running process to the ready Q
                                                                readyQ.add(processes.remove(i));
                                                                //add to runningQ
                                                                runningQ.add(blockedQ.remove(k));
                                                            }

                                                    }//2nd for loop

                                            }  //if(isUtilizingIO())              

                                        //if lowestReady is still -1, a ready process with a lower ID or a blocked process that finished IO with a lower ID was not found
                                        //add the arrived process to runningQ
                                        if( lowestReady==-1);
                                        {
                                            processes.get(i).setStatus('u');
                                            runningQ.add(processes.remove(i));
                                        }

                                        //reset lowestReady
                                        lowestReady=-1;

                                    } //if runningQ is empty 

                            }  //if  clocktime==processtime  

                    }//for loop
                
                if(isUtlizingCPU())
                    {    
                        //check the remaining CPU time as it may be half over and the running process may need to enter IO, if applicable
                        currentCPUtimeLeft=runningQ.peek().getCPUtimeLeft();

                        //the total CPU time may be an odd number, so rounding up to the next full cycle would be necessary
                        if(runningQ.peek().getCPUtime()%2==1)
                            {
                                halfCPUtime=(runningQ.peek().getCPUtime()+1)/2;
                            }
                        else
                            {
                                halfCPUtime=runningQ.peek().getCPUtime()/2;
                            }

                        //only block processes that have an IO time > 0
                        //only enter this condition on the 1/2 of CPU time
                        if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0)
                            {
                                //Change the status of the process to blocked. 
                                runningQ.peek().setStatus('b');

                                //find the process in the ready queue with the lowest process ID or the blocked queue that has a IO time left of 0 
                                if (isReady())
                                    {    

                                        for(int k=0; k<readyQ.size(); k++)
                                            {
                                                                  
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getPriority();
                                                    }
                                                if( readyQ.get(k).getPriority() < lowestReady)
                                                    {
                                                        lowestReady= readyQ.get(k).getPriority();
                                                    }                                                                
                                            }

                                    }

                                //check for a blocked process with a lower ID that just finished the IO time. If the readyQ was empty, lowestReady would still be -1
                                if(isUtilizingIO())
                                    {   
                                        for(int k=0; k<blockedQ.size(); k++)
                                            {

                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= blockedQ.get(k).getPriority();
                                                    }

                                                if(blockedQ.get(k).getPriority() < lowestReady && blockedQ.get(k).getIOtimeLeft()==0 )
                                                    {
                                                        lowestReady= blockedQ.get(k).getPriority();
                                                    }

                                            }

                                    }

                                //run the ready process with the lowest priority number, if applicable. 
                                if (isReady())
                                    { 

                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(lowestReady== readyQ.get(k).getPriority())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        //add the running process to the ready Q
                                                        blockedQ.add(runningQ.remove());                                                                
                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }

                                    }

                                //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                if(isUtilizingIO())
                                    {   

                                        for(int k=0; k<blockedQ.size(); k++)
                                            {

                                                if(lowestReady== blockedQ.get(k).getPriority())
                                                    {
                                                        blockedQ.get(k).setStatus('u');
                                                        runningQ.peek().setStatus('b');
                                                        //add the running process to the ready Q
                                                        blockedQ.add(runningQ.remove());                                                                
                                                        //add to runningQ
                                                        runningQ.add(blockedQ.remove(k));
                                                    }

                                            }
                                                                                                   
                                    }

                                //if lowestReady is still -1, a ready process with a lower ID or a blocked process that finished IO with a lower ID was not found

                                if( lowestReady==-1)
                                    {
                                        runningQ.peek().setStatus('b');
                                        blockedQ.add(runningQ.remove());                                                                
                                    }
                                
                                //reset lowestReady
                                lowestReady=-1;

                            }//if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0 && runningQ.peek().getIOtimeLeft()!=0 )

                     
                        //if the CPU time is over, remove the process from the running queue after the statistics are added 
                        if(currentCPUtimeLeft == 0)
                            {
                                runningQ.peek().setStatus('d');

                                //add a process from the ready queue to the running ueue
                                if (isReady())
                                    {    
                                     
                                        for(int k=0; k<readyQ.size(); k++)
                                            {
                                                                  
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getPriority();
                                                    }

                                                if( readyQ.get(k).getPriority() < lowestReady)
                                                    {
                                                        lowestReady= readyQ.get(k).getPriority();
                                                    }                                                                
                                            }

                                    }       

                                //check for a blocked process with a lower ID that just finished the IO time. If the readyQ was empty, lowestReady would still be -1
                                if(isUtilizingIO())
                                    {
                                        for(int k=0; k<blockedQ.size(); k++)
                                            {
                                                if(blockedQ.get(k).getPriority() < lowestReady || lowestReady==-1)
                                                    {

                                                        if(lowestReady==-1)
                                                            {
                                                                lowestReady= blockedQ.get(k).getPriority();
                                                            }

                                                        if( blockedQ.get(k).getPriority() < lowestReady)
                                                            {
                                                                lowestReady= blockedQ.get(k).getPriority();
                                                            }    
                                                    }

                                            }

                                    }

                                //run the ready process with the lowest priority number

                                if (isReady())
                                    {   

                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(lowestReady== readyQ.get(k).getPriority())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        //remove from running queue and add finished process back to the end of the process queue
                                                        processes.add(runningQ.remove());
                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }
                                    }

                                //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                if(isUtilizingIO())
                                    {

                                        for(int k=0; k<blockedQ.size(); k++)
                                            {

                                                if(lowestReady== blockedQ.get(k).getPriority())
                                                    {
                                                        blockedQ.get(k).setStatus('u');
                                                        //remove from running queue and add finished process back to the end of the process queue
                                                        processes.add(runningQ.remove());                                                            
                                                        //add to runningQ
                                                        runningQ.add(blockedQ.remove(k));
                                                    }

                                            }

                                    }
                                       
                                //if a process with a lower ID was not found, run the next ready process
                                if(lowestReady==-1)
                                    {

                                        processes.add(runningQ.remove());    
                                                                      
                                        if(isReady())
                                            {   
                                                runningQ.add(readyQ.remove()); 
                                            }

                                    }

                                //reset lowestReady
                                lowestReady=-1;

                            }//second if utilizing, for done process                
                                  
              
                        //decrement CPU after checking for the above so that the status is not changed an iteration of clock time too soon 
                        //if none of the above conditions were true, decrement the CPU time for the one process in the running queue
                        //decrementing occurs in the next (isUtilizingCPU() block

                    }//outer if (isUtilizingCPU())

                if (isUtlizingCPU())
                    {    
                                                                                                  
                        //if a process with a higher priority aka lower ID is in the ready queue 
                        //move the running process from the running queue to the ready queue and run the process with the higher priority

                        //find the process with the lowest ID in the ready queue
                        for(int k=0; k<readyQ.size(); k++)
                            {

                                if(lowestReady==-1)
                                    {
                                        lowestReady= readyQ.get(k).getPriority();
                                    }

                                if(readyQ.get(k).getPriority() < runningQ.peek().getPriority())
                                    {

                                        if( readyQ.get(k).getPriority() < lowestReady)
                                            {
                                                lowestReady= readyQ.get(k).getPriority();
                                            }    

                                    }

                            }

                        //check for a blocked process with a lower ID that just finished the IO time 
                        for(int k=0; k<blockedQ.size(); k++)
                            {

                                if(lowestReady==-1)
                                    {
                                        lowestReady= blockedQ.get(k).getPriority();
                                    }

                                if(blockedQ.get(k).getPriority() < runningQ.peek().getPriority() && blockedQ.get(k).getIOtimeLeft()==0)
                                    {

                                        if( blockedQ.get(k).getPriority() < lowestReady)
                                            {
                                                lowestReady= blockedQ.get(k).getPriority();
                                            }    
                                    }

                            }


                        //if the lowestReady found is less than the id of the current running process, run the process with the lowest ID
                        if(lowestReady < runningQ.peek().getPriority())
                            {    

                                //run the ready process with the lowest priority number
                                for(int k=0; k<readyQ.size(); k++)
                                    {

                                        if(lowestReady== readyQ.get(k).getPriority())
                                            {
                                                readyQ.get(k).setStatus('u');
                                                runningQ.peek().setStatus('r');
                                                //add the running process to the ready Q
                                                readyQ.add(runningQ.remove());
                                                //add to runningQ
                                                runningQ.add(readyQ.remove(k));
                                            }

                                    }//2nd for loop

                                //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                for(int k=0; k<blockedQ.size(); k++)
                                    {

                                        if(lowestReady== blockedQ.get(k).getPriority() && blockedQ.get(k).getIOtimeLeft()==0)
                                            {
                                                blockedQ.get(k).setStatus('u');
                                                runningQ.peek().setStatus('r');
                                                //add the running process to the ready Q
                                                readyQ.add(runningQ.remove());
                                                //add to runningQ
                                                runningQ.add(blockedQ.remove(k));
                                            }

                                    }//2nd for loop

                            } // if lowestReady    
                        
                        //reset lowestReady
                        lowestReady=-1;
                     
                        //decrement the CPU time left on the running process and add to turnaround time 
                        if(isUtlizingCPU())
                            {    
                                runningQ.peek().setCPUtimeLeft((runningQ.peek().getCPUtimeLeft()-1));  
                                runningQ.peek().setTurnaround(runningQ.peek().getTurnaround()+1);
                            }

                    }//if (isUtilizingCPU())

                if (isUtilizingIO())
                    {
                       
                        for(int k=0; k<blockedQ.size(); k++)
                            {

                                currentIOtimeLeft=blockedQ.get(k).getIOtimeLeft();
                                                                   
                                if(currentIOtimeLeft == 0)
                                    {

                                        blockedQ.get(k).setStatus('r');
                                        //io time is done, move process at k to ready queue if a process is running
                                        //if there is no process in the running queue, the process can go directly to running

                                        if(isUtlizingCPU())
                                            {
                                                readyQ.add(blockedQ.remove(k));
                                            }

                                        else
                                            {
                                                blockedQ.get(k).setStatus('u');
                                                runningQ.add(blockedQ.remove(k));
                                                //decrement in current loop
                                                runningQ.peek().setCPUtimeLeft((runningQ.peek().getCPUtimeLeft()-1));  
                                                runningQ.peek().setTurnaround(runningQ.peek().getTurnaround()+1);
                                            }

                                    }

                            }   

                        //check for condition of IO time == 0 first to avoid decermenting one clock time iteration early
                        //check that the above conditions have not emptied the blocked queue
                        if(isUtilizingIO())
                            {
                                for(int k=0; k<blockedQ.size(); k++)
                                    {
                                        blockedQ.get(k).setIOtimeLeft(blockedQ.get(k).getIOtimeLeft()-1);
                                        blockedQ.get(k).setTurnaround(blockedQ.get(k).getTurnaround()+1);
                                    }
                            }

                    } // if (isUtliingIO())

                //add 1 to turnaround for ready process 
                for(int k=0; k<readyQ.size(); k++)
                    {
                        readyQ.get(k).setTurnaround(readyQ.get(k).getTurnaround()+1);
                    }

                if(runningQ.size()!=0)
                    {
                        cpuUtil++;
                    }

                if(!isAlldone())
                    {
                        output+=clockTime + " " + toString()+"\n";
                        finishingTime++;
                    }
                

                clockTime++ ;
                   
            }

        NumberFormat format2 = NumberFormat.getInstance();
		format2.setMinimumFractionDigits(2);

        output+= "\n" + "Finishing time: " + (finishingTime-1) + "\n" + "CPU utilization: " + (double)Math.round((cpuUtil/((double) finishingTime))*100)/100 + "\n";//format2.format((double)cpuUtil/((double) finishingTime)) + "\n";
        
        //to add processes to output sorted by ID
        int [] pIDs = new int [processes.size()];

        for(int q=0; q<processes.size(); q++)
            {
                pIDs[q]=processes.get(q).getID();
            }

        Arrays.sort(pIDs);

        for(int r=0; r<processes.size(); r++)
            {

                for(int q=0; q<processes.size(); q++)
                    {
                        if(processes.get(q).getID()==pIDs[r])

                            {
                                output+="Turnaround process " + processes.get(q).getID() +": " +  processes.get(q).getTurnaround() +"\n";
                            }

                    }

            }
        
        return output;

    }


    public String roundrobin()
    {

        int clockTime=0; 
        //if two processes become ready at the same time, the lower ID will be stored in this variable 
        int lowestID=-1;
       
        //priorities are not set for roundrobin 
        //variables used for fcs have the same use in roundrobin, except for lowestID

        int processAtime=-1;
        int currentCPUtimeLeft=-1;

        //if the CPU time is an odd number, this will handle rounding to next cycle 
        int halfCPUtime=0;

        int currentIOtimeLeft=-1;

        int lowestReady=-1;

        //counts running duration for current running process
        //after two run cycles, the running of the process terminates 
        int runCycles=0; 

        //will be incremented for ever clockTime for which the runningQ is not empty
        int cpuUtil=0;

        //will be incremented for ever clockTime for which !isAlldone
        int finishingTime=0;

        //will be used for output file
        String output="";
       
        while (!isAlldone())
            {
                                    
                for (int i =0; i<processes.size(); i++)
                    {   

                        processAtime=processes.get(i).getArrivalTime();
                        
                        if(clockTime == processAtime)
                            {
                               
                                if(isReady())
                                    {   

                                        //find the process with the lowest ID in the ready queue
                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(readyQ.get(k).getID() < processes.get(i).getID())
                                                    {

                                                        if(lowestReady==-1)
                                                            {
                                                                lowestReady= readyQ.get(k).getID();
                                                            }

                                                        if( readyQ.get(k).getPriority() < lowestReady)
                                                            {
                                                                lowestReady= readyQ.get(k).getID();
                                                            }    

                                                    }

                                            }//first for loop
                                    }//if(isReady())

                               
                                //compare the lowest ID in the readyQ with the ID of the arriving process 
                                if(isReady() && lowestReady!=-1 && !isUtilizingIO())
                                    {  

                                        //run the ready process with the lowest priority number
                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(lowestReady== readyQ.get(k).getID())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        processes.get(i).setStatus('r');
                                                        //add the arrived process to the ready Q
                                                        readyQ.add(processes.remove(i));
                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }//2nd for loop

                                    }  // if(isReady())
   

                                //if lowestReady is still -1, a ready process with a lower ID was not found, and there is no process currently running
                                //add the newly arrived process to the readyQ
                                if( lowestReady==-1 && isUtlizingCPU())
                                    {
                                        processes.get(i).setStatus('r');

                                        readyQ.add(processes.remove(i));
                                    }

                                if( lowestReady==-1 && !isUtlizingCPU())
                                    {
                                        processes.get(i).setStatus('u');

                                        runningQ.add(processes.remove(i));
                                    }

                                //reset lowestReady
                                lowestReady=-1;

                            }    

                    }

                if(isUtlizingCPU())
                    {    
                                               
                        currentCPUtimeLeft=runningQ.peek().getCPUtimeLeft();
                        if(runningQ.peek().getCPUtime()%2==1)
                            {
                                halfCPUtime=(runningQ.peek().getCPUtime()+1)/2;
                            }

                        else
                            {
                            halfCPUtime=runningQ.peek().getCPUtime()/2;
                            }

                        //only block processes that have an IO time > 0 
                        //only enter this condition on the 1/2 of CPU time 
                        if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0 && runningQ.peek().getIOtimeLeft()!=0 )
                            {
                                runningQ.peek().setStatus('b');

                                //find the process in the ready queue with the lowest process ID 
                                if (isReady())
                                    {    

                                        for(int k=0; k<readyQ.size(); k++)
                                            {
                                                                  
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getID();
                                                    }

                                                if( readyQ.get(k).getPriority() < lowestReady)
                                                    {
                                                        lowestReady= readyQ.get(k).getID();
                                                    }                                                                
                                            }

                                    }                 

                                //run the ready process with the lowest priority number
                                if (isReady())
                                    { 

                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(lowestReady== readyQ.get(k).getID())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        runningQ.peek().setStatus('b');
                                                        //add the running process to the ready Q
                                                        blockedQ.add(runningQ.remove()); 
                                                        //reset clock cycles when a process is removed from running queue
                                                        runCycles=0;
                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }
                                    }
                                        
                                //if lowestReady is still -1, a ready process with a lower ID or a blocked process that finished IO with a lower ID was not found
                                if( lowestReady==-1)
                                    {
                                        runningQ.peek().setStatus('b');       
                                        blockedQ.add(runningQ.remove());    
                                        runCycles=0;
                                    }
                                
                                //reset lowestReady
                                lowestReady=-1;

                            }//if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0 && runningQ.peek().getIOtimeLeft()!=0 )

                     
                        //if the CPU time is over, remove the process from the running queue after the statistics are added 
                        if(currentCPUtimeLeft == 0)
                            {
                                runningQ.peek().setStatus('d');

                                //add a process from the ready queue to the running ueue
                                if (isReady())// || isUtilizingIO() )
                                    {    
                                     
                                        for(int k=0; k<readyQ.size(); k++)
                                            {              
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getID();
                                                    }
                                                if( readyQ.get(k).getID() < lowestReady)
                                                    {
                                                        lowestReady= readyQ.get(k).getID();
                                                    }                                                                
                                            }

                                                        
                                        //run the ready process with the lowest priority number
                                        for(int k=0; k<readyQ.size(); k++)
                                            {
                                                if(lowestReady== readyQ.get(k).getID())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        //remove from running queue and add finished process back to the end of the process queue
                                                        processes.add(runningQ.remove());
                                                        runCycles=0;

                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }//2nd for loop
                                       
                                    }

                                //if a process with a lower ID was not found, run the next ready process
                                if(lowestReady==-1)
                                    {
                                        //runningQ.peek.setStatus('r');

                                        //runCycles is reset whenever the running process is removed from the runningQ
                                        processes.add(runningQ.remove());    
                                        runCycles=0;
                                                                      
                                        if(isReady())
                                            {   
                                                
                                                readyQ.peek().setStatus('u');
                                                runningQ.add(readyQ.remove()); 

                                            }

                                    }
                                //reset lowestReady
                                lowestReady=-1;

                            }//second if utilizing, for done process                
                                                
                        //decrement CPU after checking for the above so that the status is not changed an iteration of clock time too soon 
                        //if none of the above conditions were true, decrement the CPU time for the one process in the running queue
                        //decrementing occurs in the next block 

                    }//outer if (isUtilizingCPU())


                //check here if 2 clockTime cycles have passed, find the next ready process with the lowest ID
                if (isUtlizingCPU() && runCycles==2)
                    {    
     
                        //if a process with a higher priority aka lower ID is in the ready queue 
                        //move the running process from the running queue to the ready queue and run the process with the higher priority

                        //find the process with the lowest ID in the ready queue
                        for(int k=0; k<readyQ.size(); k++)
                            {

                                if(lowestReady==-1)
                                    {
                                        lowestReady= readyQ.get(k).getID();
                                    }

                                if( readyQ.get(k).getID() < lowestReady)
                                    {
                                        lowestReady= readyQ.get(k).getID();
                                    }    

                            }//first for loop

                        //run the ready process with the lowest priority number
                        for(int k=0; k<readyQ.size(); k++)
                            {

                                if(lowestReady== readyQ.get(k).getID())
                                    {
                                        readyQ.get(k).setStatus('u');
                                        runningQ.peek().setStatus('r');

                                        //add the running process to the ready Q
                                        readyQ.add(runningQ.remove());
                                        runCycles=0;

                                        //add to runningQ
                                        runningQ.add(readyQ.remove(k));
                                    }

                            }//2nd for loop

                        //reset lowestReady
                        lowestReady=-1;

                    }//if (isUtilizingCPU())

                //decrement the CPU time left on the running process and increment runCycles and turnAround 
                if(isUtlizingCPU())
                    {    
                        runCycles++;
                        runningQ.peek().setCPUtimeLeft((runningQ.peek().getCPUtimeLeft()-1));  
                        runningQ.peek().setTurnaround(runningQ.peek().getTurnaround()+1);
                    }
                
                if (isUtilizingIO())
                    {

                        for(int k=0; k<blockedQ.size(); k++)
                            {

                                currentIOtimeLeft=blockedQ.get(k).getIOtimeLeft();
                                                                   
                                if(currentIOtimeLeft == 0)
                                    {
                                        blockedQ.get(k).setStatus('r');
                                        //io time is done, move process at k to ready queue if a process is running
                                        //if there is no process in the running queue, the process can go directly to running

                                        if(isUtlizingCPU())
                                            {
                                                readyQ.add(blockedQ.remove(k));
                                            }

                                        else
                                            {
                                                blockedQ.get(k).setStatus('u');
                                                runningQ.add(blockedQ.remove(k));
                                                //decrement in current loop
                                                runCycles++;
                                                runningQ.peek().setCPUtimeLeft((runningQ.peek().getCPUtimeLeft()-1));  
                                                runningQ.peek().setTurnaround(runningQ.peek().getTurnaround()+1);
                                            }
                                    }

                            }  

                        //check for condition of IO time == 0 first to avoid decermenting one clock time iteration early
                        //check that the above conditions have not emptied the blocked queue

                        if(isUtilizingIO())
                            {
                                for(int k=0; k<blockedQ.size(); k++)
                                    {
                                        blockedQ.get(k).setIOtimeLeft(blockedQ.get(k).getIOtimeLeft()-1);
                                        blockedQ.get(k).setTurnaround(blockedQ.get(k).getTurnaround()+1);
                                    }
                            }

                    } // if (isUtliingIO())
              
                //add 1 to turnaround for ready process 
                for(int k=0; k<readyQ.size(); k++)
                    {

                        readyQ.get(k).setTurnaround(readyQ.get(k).getTurnaround()+1);
                    }                                     

                if(runningQ.size()!=0)
                    {
                        cpuUtil++;
                    }

                if(!isAlldone())
                    {
                        output+=clockTime + " " + toString()+"\n";
                        finishingTime++;
                    }
                
                  clockTime++ ;
 
            }
        
        NumberFormat format2 = NumberFormat.getInstance();
		format2.setMinimumFractionDigits(2);

        output+= "\n" + "\n" + "Finishing time: " + (finishingTime-1) + "\n" + "CPU utilization: " + (double)Math.round((cpuUtil/((double) finishingTime))*100)/100 + "\n";
        
        int [] pIDs = new int [processes.size()];

        for(int q=0; q<processes.size(); q++)
            {
                pIDs[q]=processes.get(q).getID();
            }

        Arrays.sort(pIDs);

        for(int r=0; r<processes.size(); r++)
            {

                for(int q=0; q<processes.size(); q++)
                    {
                        if(processes.get(q).getID()==pIDs[r])

                            {
                                output+="Turnaround process " + processes.get(q).getID() +": " +  processes.get(q).getTurnaround() +"\n";
                            }

                    }

            }
        
        return output;

    }

    
    public String shortestRemaining()
    {
        //For a shortest remaining CPU time algorithm, the priority is based on the remaining CPU time

        int clockTime=0;   
        //for shortest remaining, several processes may have equal CPU time left remaining, so this variable is necessary to find the process with the lowest ID among them
        //the variable was used above to find priorities  
        int lowestID=-1;

        //Used to store the arrival time for a process to compare with the current clock time 
        int processAtime=-1;

        //Used to store the remaining CPU time left
        int currentCPUtimeLeft=-1;
     
        //if the CPU time is an odd number, this will handle rounding to next cycle 
        int halfCPUtime=0;

        int currentIOtimeLeft=-1;
        int lowestReady=-1;

        //will be incremented for ever clockTime for which the runningQ is not empty
        int cpuUtil=0;

        //will be incremented for ever clockTime for which !isAlldone
        int finishingTime=0;

        //will be used for output file
        String output="";
               
        while (!isAlldone())
            {
                                    
                for (int i =0; i<processes.size(); i++)
                    {   

                        processAtime=processes.get(i).getArrivalTime();
                        
                        if(clockTime == processAtime)
                            {
                                 
                                if(isUtlizingCPU())
                                    {
                                        lowestReady=runningQ.peek().getCPUtimeLeft();
                                        lowestID=runningQ.peek().getID();
                                    }

                                if(isReady())
                                    {   

                                        //find the process with the lowest remaining CPUtime in the ready queue
                                        for(int l=0; l<readyQ.size(); l++)
                                            {

                                                if(readyQ.get(l).getCPUtimeLeft() <= lowestReady || lowestReady==-1)
                                                    {

                                                        if(readyQ.get(l).getCPUtimeLeft() == lowestReady)
                                                            {
                                                                //if the remaining CPU time is the same, find the lowest process ID
                                                                if(readyQ.get(l).getID() <lowestID)
                                                                    {
                                                                        lowestReady= readyQ.get(l).getCPUtimeLeft();
                                                                        lowestID=readyQ.get(l).getID();
                                                                    }
                                                            }

                                                        if  (readyQ.get(l).getPriority() < lowestReady || lowestReady==-1)
                                                            {
                                                                lowestReady= readyQ.get(l).getCPUtimeLeft();
                                                                lowestID=readyQ.get(l).getID();
                                                            }    

                                                    }

                                            }
                                    }

                                if(isUtilizingIO())
                                    {

                                        //check for a blocked process with a lower ID that just finished the IO time 
                                        for(int l=0; l<blockedQ.size(); l++)
                                            {

                                                if((blockedQ.get(l).getCPUtimeLeft() <= lowestReady && blockedQ.get(l).getIOtimeLeft()==0)|| lowestReady==-1)
                                                    {

                                                        if(blockedQ.get(l).getCPUtimeLeft() == lowestReady && blockedQ.get(l).getIOtimeLeft()==0 )
                                                            {
                                                                //if the remaining CPU time is the same, find the lowest process ID
                                                                if(blockedQ.get(l).getID() <lowestID && blockedQ.get(l).getIOtimeLeft()==0)
                                                                    {
                                                                        lowestReady= blockedQ.get(l).getCPUtimeLeft();
                                                                        lowestID=blockedQ.get(l).getID();
                                                                    }
                                                            }

                                                        if  ((blockedQ.get(l).getCPUtimeLeft() < lowestReady && blockedQ.get(l).getIOtimeLeft()==0 )|| lowestReady==-1)
                                                            {
                                                                lowestReady= blockedQ.get(l).getCPUtimeLeft();
                                                                lowestID=blockedQ.get(l).getID();
                                                            }    

                                                    }

                                            }

                                    }

                                //compare the lowestReady and lowestID to the arriving process CPU time left and ID
                                if(processes.get(i).getCPUtimeLeft()<=lowestReady || lowestReady==-1)
                                    {

                                        if(processes.get(i).getCPUtimeLeft()<lowestReady || lowestReady==-1)
                                            {
                                                lowestReady= processes.get(i).getCPUtimeLeft();
                                                lowestID=processes.get(i).getID();
                                            }
 
                                        if(processes.get(i).getCPUtimeLeft()==lowestReady)
                                            {
                                                if (processes.get(i).getID() <lowestID)
                                                    {
                                                        lowestReady= processes.get(i).getCPUtimeLeft();
                                                        lowestID=processes.get(i).getID();
                                                    }
                                            }     
                                            
                                    }       

                             
                                //now that the lowest ready and lowestID have been found in the loops, find the matching process and compare it to the arriving process
                                        
                                if(isUtlizingCPU())
                                    {
                                        //if the current running process was found to be the process with the lowest CPU time remaining or with a CPU time remainng equal to another process but a lower ID
                                        //continue running the current process and add the arriving process to the readyQ
                                               
                                        if((processes.get(i).getCPUtimeLeft() ==lowestReady && processes.get(i).getID()==lowestID) || lowestReady==-1)
                                            {

                                                processes.get(i).setStatus('u');
                                                runningQ.peek().setStatus('r');
                                                readyQ.add(runningQ.remove());
                                                runningQ.add(processes.remove(i)); 
                                            }       

                                        if(lowestReady==runningQ.peek().getCPUtimeLeft() &&  lowestID==runningQ.peek().getID())
                                            {
                                                processes.get(i).setStatus('r');
                                                readyQ.add(processes.remove(i));
                                            }   
                                                
                                    }

                                if(isReady())
                                    {  
                                        //find the process that matches lowestReady (and lowestID, more than one process has the same lowestReady)
                                        //run the process from the readyQ and put the process that just finished the IO into the ready Q
                                        for(int j=0; j<readyQ.size(); j++)
                                            {

                                                if(lowestReady== readyQ.get(j).getCPUtimeLeft() && lowestID ==readyQ.get(j).getID())
                                                    {
                                                                
                                                        //run the ready process and add the running process to the readyQ
                                                        readyQ.get(j).setStatus('u');
                                                        //add the arrived process to the ready Q
                                                        processes.get(i).setStatus('r');
                                                        readyQ.add(processes.remove(i));

                                                        //if a process is running, add the running process to the readyQ
                                                        if(isUtlizingCPU())
                                                            {
                                                                runningQ.peek().setStatus('r');
                                                                readyQ.add(runningQ.remove());
                                                            }
                                                               
                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(j));
                                                    }
                                            }
                                                               
                                    }  // if(isReady())

                                if(isUtilizingIO())
                                    {
                                        //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                        for(int j=0; j<blockedQ.size(); j++)
                                            {

                                                if(lowestReady== blockedQ.get(j).getCPUtimeLeft() && lowestID ==blockedQ.get(j).getID() && blockedQ.get(j).getIOtimeLeft()==0)
                                                    {

                                                        blockedQ.get(j).setStatus('u');
                                                        //add the running process to the ready Q

                                                        if(isUtlizingCPU())
                                                            {
                                                                runningQ.peek().setStatus('r');
                                                                readyQ.add(runningQ.remove());
                                                            }

                                                        runningQ.add(blockedQ.remove(j));
                                                    }

                                            }//2nd for loop
                                            
                                    }
                                 
                                //compare the lowestReady and lowest ID found above with the CPU time and process ID of the arriving process 
                                //if the arriving process has a lower CPU time or an equal CPU time with another process but a lower ID, run the arriving process
                                //if lowestReady ==-1 at this point, all queues are empty. Run the arriving process. 
                                   
                                if(!isUtlizingCPU())
                                    {
                                        processes.get(i).setStatus('u');
                                        runningQ.add(processes.remove(i));
                                    }
                                           
                                //reset lowestReady
                                lowestReady=-1;

                            }//process arrival time == clock time 

                    }   //for 

   
                if(isUtlizingCPU())
                    {    
                                               
                        currentCPUtimeLeft=runningQ.peek().getCPUtimeLeft();

                        if(runningQ.peek().getCPUtime()%2==1)
                            {
                                halfCPUtime=(runningQ.peek().getCPUtime()+1)/2;
                            }

                        else
                            {
                            halfCPUtime=runningQ.peek().getCPUtime()/2;
                            }

                        //only block processes that have an IO time > 0 
                        //only enter this condition on the 1/2 of CPU time 
                        if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0 && runningQ.peek().getIOtimeLeft()!=0 )
                            {

                                runningQ.peek().setStatus('b');

                                //find the process in the ready queue with the lowest process ID 
                                if (isReady()) 
                                    {    

                                        for(int k=0; k<readyQ.size(); k++)
                                            {
                                                                  
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                    }
                                                if( readyQ.get(k).getCPUtimeLeft() < lowestReady)
                                                    {
                                                        lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                    }                                                                
                                            }
                                    
                                        //run the ready process with the lowest priority number
                                        for(int k=0; k<readyQ.size(); k++)
                                            {

                                                if(lowestReady== readyQ.get(k).getCPUtimeLeft())
                                                    {
                                                        readyQ.get(k).setStatus('u');
                                                        runningQ.peek().setStatus('b');
                                                        //add the running process to the ready Q
                                                        blockedQ.add(runningQ.remove()); 

                                                        //add to runningQ
                                                        runningQ.add(readyQ.remove(k));
                                                    }

                                            }//2nd for loop

                                    }

                                //if lowestReady is still -1, a ready process with a lower ID or a blocked process that finished IO with a lower ID was not found
                                //add the arrived process to running

                                if( lowestReady==-1)
                                    {
                                        runningQ.peek().setStatus('b');

                                        blockedQ.add(runningQ.remove());    
                                    }
                                
                                //reset lowestReady
                                lowestReady=-1;
                                lowestID=-1;

                            }//if(currentCPUtimeLeft == halfCPUtime && runningQ.peek().getIOtime()!= 0 && runningQ.peek().getIOtimeLeft()!=0 )

                     
                        //if the CPU time is over, remove the process from the running queue after the statistics are added 
                        if(currentCPUtimeLeft == 0)
                            {

                                runningQ.peek().setStatus('d');
                               
                                //add a process from the ready queue to the running ueue
                                if (isReady())// || isUtilizingIO() )
                                    {    
                                     
                                        for(int k=0; k<readyQ.size(); k++)
                                            {
              
                                                if(lowestReady==-1)
                                                    {
                                                        lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                        lowestID=readyQ.get(k).getID();
                                                    }

                                                if( readyQ.get(k).getCPUtimeLeft() <= lowestReady)
                                                    {

                                                        if(readyQ.get(k).getCPUtimeLeft() == lowestReady)
                                                            {

                                                                if(readyQ.get(k).getID()< lowestID)
                                                                    {
                                                                        lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                                        lowestID=readyQ.get(k).getID();
                                                                    }

                                                            }

                                                        if(readyQ.get(k).getCPUtimeLeft() <lowestReady)
                                                            {
                                                                lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                                lowestID=readyQ.get(k).getID();
                                                            }

                                                    }     
                                                           
                                            } //for

                                        if (isUtilizingIO())
                                            {
                                                ///check for a blocked process with a lower ID that just finished the IO time. If the readyQ was empty, lowestReady would still be -1
                                                for(int k=0; k<blockedQ.size(); k++)
                                                    {

                                                        if(lowestReady==-1 &&  blockedQ.get(k).getIOtimeLeft()==0)
                                                            {
                                                                lowestReady= blockedQ.get(k).getCPUtimeLeft();
                                                                lowestID=blockedQ.get(k).getID();
                                                            }

                                                        if(blockedQ.get(k).getCPUtimeLeft() <= lowestReady)
                                                            {

                                                                if( blockedQ.get(k).getCPUtimeLeft()== lowestReady)
                                                                    {
                                                                        if(blockedQ.get(k).getID()<lowestID)
                                                                            {
                                                                                lowestReady= blockedQ.get(k).getPriority();
                                                                                lowestID=blockedQ.get(k).getID();
                                                                            }

                                                                    }    

                                                                if( blockedQ.get(k).getCPUtimeLeft() < lowestReady)
                                                                    {
                                                                        lowestReady= blockedQ.get(k).getPriority();
                                                                        lowestID=blockedQ.get(k).getID();

                                                                    }    
                                                            }

                                                    }
                                            }//if IO

                                        //run the ready process with the lowest priority number
                                        if(isReady())
                                            {

                                                for(int k=0; k<readyQ.size(); k++)
                                                    {

                                                        if(readyQ.get(k).getCPUtimeLeft()==lowestReady && readyQ.get(k).getID()==lowestID )
                                                            {
                                                                readyQ.get(k).setStatus('u');
                                                                //remove from running queue and add finished process back to the end of the process queue
                                                                processes.add(runningQ.remove());

                                                                //add to runningQ
                                                                runningQ.add(readyQ.remove(k));
                                                            }

                                                    }//2nd for loop

                                            }//if isReady

                                        //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                        if (isUtilizingIO())
                                            {

                                                for(int k=0; k<blockedQ.size(); k++)
                                                    {

                                                        if( blockedQ.get(k).getCPUtimeLeft()==lowestReady && blockedQ.get(k).getID()==lowestID )
                                                            {
                                                                blockedQ.get(k).setStatus('u');
                                                                //remove from running queue and add finished process back to the end of the process queue
                                                                processes.add(runningQ.remove());                                                            
                                                                //add to runningQ
                                                                runningQ.add(blockedQ.remove(k));
                                                            }

                                                    }//2nd for loop 
                                        
                                            }

                                    }

                                //if a process with a lower ID was not found, run the next ready process
                                if(lowestReady==-1)
                                    {

                                        processes.add(runningQ.remove());    

                                        if(isReady())
                                            {   
                                                runningQ.add(readyQ.remove()); 
                                            }

                                    }

                                //reset lowestReady and lowestID
                                lowestReady=-1;
                                lowestID=-1;

                            }//second if utilizing, for done process                
                                                
                        //decrement CPU after checking for the above so that the status is not changed an iteration of clock time too soon 
                        //if none of the above conditions were true, decrement the CPU time for the one process in the running queue

                    }//outer if (isUtilizingCPU())

                //check here if 2 clockTime cycles have passed, find the next ready process with the lowest ID
                if (isUtlizingCPU() && isReady())
                    {    
                                                                    
                        //if a process with a higher priority aka lower ID is in the ready queue 
                        //move the running process from the running queue to the ready queue and run the process with the higher priority

                        //find the process with the lowest ID in the ready queue
                        for(int k=0; k<readyQ.size(); k++)
                            {
                                //set lowestReady to the current running process CPU time left 
                                if(lowestReady==-1)
                                    {
                                        lowestReady= runningQ.peek().getCPUtimeLeft();  
                                        lowestID=runningQ.get(k).getID();
                                    }    
                                
                                if(readyQ.get(k).getCPUtimeLeft() <= runningQ.peek().getCPUtimeLeft())
                                    {

                                        if(readyQ.get(k).getCPUtimeLeft()==runningQ.peek().getCPUtimeLeft())
                                            {

                                                if(readyQ.get(k).getID()<runningQ.peek().getID())
                                                    {
                                                        lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                        lowestID=readyQ.get(k).getID();
                                                    }
                                              
                                            }
                                    
                                        if( readyQ.get(k).getCPUtimeLeft() < lowestReady)
                                            {
                                                lowestReady= readyQ.get(k).getCPUtimeLeft();
                                                lowestID=readyQ.get(k).getID();
                                            }    
                                    }

                            }//for
 
                        //run the ready process with the lowest priority number
                        for(int k=0; k<readyQ.size(); k++)
                            {
                                if(lowestReady== readyQ.get(k).getCPUtimeLeft() && lowestID == readyQ.get(k).getID())
                                    {
                                        readyQ.get(k).setStatus('u');
                                        //add the running process to the ready Q
                                        runningQ.peek().setStatus('r');
                                        readyQ.add(runningQ.remove());
                                        //add to runningQ
                                        runningQ.add(readyQ.remove(k));
                                    }

                            }//2nd for loop
                       
                        //reset lowestReady and lowestID
                        lowestReady=-1;
                        lowestID=-1;

                    }//if (isUtilizingCPU())
               
                if (isUtilizingIO())
                    {

                        for(int k=0; k<blockedQ.size(); k++)
                            {

                                currentIOtimeLeft=blockedQ.get(k).getIOtimeLeft();

                                if(currentIOtimeLeft == 0)
                                    {
     
                                        if(isUtlizingCPU())
                                            {
                                                lowestReady=runningQ.peek().getCPUtimeLeft();
                                                lowestID=runningQ.peek().getID();
                                            }

                                        if(isReady())
                                            {   
                                                //find the process with the lowest remaining CPUtime in the ready queue
                                                for(int l=0; l<readyQ.size(); l++)
                                                    {

                                                        if(readyQ.get(l).getCPUtimeLeft() <= lowestReady || lowestReady==-1)
                                                            {

                                                                if(readyQ.get(l).getCPUtimeLeft() == lowestReady)
                                                                    {
                                                                        //if the remaining CPU time is the same, find the lowest process ID
                                                                        if(readyQ.get(l).getID() <lowestID)
                                                                            {
                                                                                lowestReady= readyQ.get(l).getCPUtimeLeft();
                                                                                lowestID=readyQ.get(l).getID();
                                                                            }
                                                                    }

                                                                if  (readyQ.get(l).getCPUtimeLeft() < lowestReady)
                                                                    {
                                                                        lowestReady= readyQ.get(l).getCPUtimeLeft();
                                                                        lowestID=readyQ.get(l).getID();

                                                                    }    

                                                                if(lowestReady==-1)
                                                                    {
                                                                        lowestReady= readyQ.get(l).getCPUtimeLeft();
                                                                        lowestID=readyQ.get(l).getID();
                                                                    }

                                                            }

                                                    }//first for loop

                                            }//if(isReady())

                                        //check blockedQ for any processes that have IO time left == 0 and CPU time left < lowest ready
                                        //if a blocked process with an IO time left ==0 is found with a CPU time left = lowestReady, find the process with the lower ID

                                        //check for a blocked process with a lower ID that just finished the IO time 
                                        for(int l=0; l<blockedQ.size(); l++)
                                            {

                                                if((blockedQ.get(l).getCPUtimeLeft() <= lowestReady && blockedQ.get(l).getIOtimeLeft()==0)|| (lowestReady==-1 && blockedQ.get(l).getIOtimeLeft()==0) )
                                                    {
                                                        if(blockedQ.get(l).getCPUtimeLeft() == lowestReady)
                                                            {
                                                                //if the remaining CPU time is the same, find the lowest process ID
                                                                if(blockedQ.get(l).getID() <lowestID)
                                                                    {
                                                                        lowestReady= blockedQ.get(l).getCPUtimeLeft();
                                                                        lowestID=blockedQ.get(l).getID();
                                                                    }
                                                            }

                                                        if  (blockedQ.get(l).getCPUtimeLeft() < lowestReady)
                                                            {
                                                                lowestReady= blockedQ.get(l).getCPUtimeLeft();
                                                                lowestID=blockedQ.get(l).getID();
                                                            }    

                                                        if(lowestReady==-1)
                                                              
                                                            {
                                                                lowestReady= blockedQ.get(l).getCPUtimeLeft();
                                                                lowestID=blockedQ.get(l).getID();                                                                  
                                                            }

                                                    }//second for loop

                                            }//(isUtilizingIO()) 

                                        //compare the lowest ID in the readyQ with the ID of the arriving process 
                                       
                                        //find the process that matches lowestReady (and lowestID, more than one process has the same lowestReady)
                                        //check all queues and run the process when found
                                        //the next running process needs to match lowestReady and lowestID. To clarify the lowestID may not actually be the lowest ID out of all running processes,
                                        //but the lowest ID out of processes with the same CPU time left. 

                                        if(isUtlizingCPU())
                                            {
                                                //if the current running process IS the process with lowest CPU time left or the process with the lowest ID among several processes with the same CPU Time left
                                                //add the process that just finished IO to the ready queue

                                                if(lowestReady==runningQ.peek().getCPUtimeLeft() && lowestID ==runningQ.peek().getID())
                                                    {
                                                        blockedQ.get(k).setStatus('r');
                                                        readyQ.add(blockedQ.remove(k));
                                                    }   
                                            }

                                        if(isReady())// && isUtilizingIO())
                                            {  
                                                //find the process that matches lowestReady (and lowestID, more than one process has the same lowestReady)
                                                //run the process from the readyQ and put the process that just finished the IO into the ready Q

                                                for(int j=0; j<readyQ.size(); j++)
                                                    {
                                                        if(lowestReady== readyQ.get(j).getCPUtimeLeft() && lowestID ==readyQ.get(j).getID())
                                                            {
                                                                readyQ.get(j).setStatus('u');
                                                                //add the arrived process to the ready Q
                                                                blockedQ.get(k).setStatus('r');
                                                                readyQ.add(blockedQ.remove(k));
                                                                //add to runningQ
                                                                runningQ.add(readyQ.remove(j));
                                                            }

                                                    }//2nd for loop

                                            }  // if(isReady())

                                        //if a process in the blockedQ has a lower ID than the running process and has finished the IO, move the running process to ready and run the blocked process

                                        if(isUtilizingIO())
                                            {
                                                //Two loops for the ready and blocked queues are necessary because the lowest ID process that is ready may be in either one 
                                                for(int j=0; j<blockedQ.size(); j++)
                                                    {

                                                        if((lowestReady== blockedQ.get(j).getCPUtimeLeft() && lowestID ==blockedQ.get(j).getID()))
                                                            {

                                                                if(isUtlizingCPU())
                                                                    {
                                                                        runningQ.peek().setStatus('r');
                                                                        readyQ.add(runningQ.remove());
                                                                    }                                                                   
                                                                blockedQ.get(j).setStatus('u');
                                                                runningQ.add(blockedQ.remove(j));
                                                            }

                                                    }//2nd for loop
                                            }
                                    }

                            }   

                        //check for condition of IO time == 0 first to avoid decermenting one clock time iteration early
                        //check that the above conditions have not emptied the blocked queue

                        if(isUtilizingIO())
                            {

                                for(int k=0; k<blockedQ.size(); k++)
                                    {
                                        blockedQ.get(k).setIOtimeLeft(blockedQ.get(k).getIOtimeLeft()-1);
                                        blockedQ.get(k).setTurnaround(blockedQ.get(k).getTurnaround()+1);
                                    }
                            }

                    } // if (isUtliingIO())
  
                //decrement the CPU time left on the running process 
                if(isUtlizingCPU())
                    {    
                        runningQ.peek().setCPUtimeLeft((runningQ.peek().getCPUtimeLeft()-1));  
                        runningQ.peek().setTurnaround(runningQ.peek().getTurnaround()+1);
                    }


                //add 1 to turnaround for ready process 
                for(int k=0; k<readyQ.size(); k++)
                    {

                        readyQ.get(k).setTurnaround(readyQ.get(k).getTurnaround()+1);
                    }
                
                lowestReady=-1;
                lowestID=-1;                                 

                if(runningQ.size()!=0)
                    {
                        cpuUtil++;
                    }

                if(!isAlldone())
                    {
                        output+=clockTime + " " + toString()+"\n";
                        finishingTime++;
                    }
                
                clockTime++ ;
                   
            }
        

        NumberFormat format2 = NumberFormat.getInstance();
		format2.setMinimumFractionDigits(2);

        output+= "\n" + "\n" + "Finishing time: " + (finishingTime-1) + "\n" + "CPU utilization: " + (double)Math.round((cpuUtil/((double) finishingTime))*100)/100 + "\n";
        
        int [] pIDs = new int [processes.size()];


        for(int q=0; q<processes.size(); q++)
            {
                pIDs[q]=processes.get(q).getID();
            }

        Arrays.sort(pIDs);

        for(int r=0; r<processes.size(); r++)
            {

                for(int q=0; q<processes.size(); q++)
                    {

                        if(processes.get(q).getID()==pIDs[r])

                            {
                                output+="Turnaround process " + processes.get(q).getID() +": " +  processes.get(q).getTurnaround() +"\n";
                            }

                    }

            }
        
        return output;

    }


    public String toString()
    {

        //represents the sum of all sizes of runningQ, blockedQ and readyQ
        int totalSize= runningQ.size()+ blockedQ.size() + readyQ.size();
        int [] ids = new int [totalSize];

        String out="";

        //iterate through all arrayLists to find the process ID == j for the iteration and add it to the output string

        for (int i =0; i<runningQ.size(); i++)
            {   
                ids[i]=runningQ.get(i).getID();
            } 

        for (int k =0; k<blockedQ.size(); k++)
            {                 
                ids[runningQ.size()+k]=blockedQ.get(k).getID();
            } 

        for (int l =0; l<readyQ.size(); l++)
            {   
                ids[runningQ.size()+blockedQ.size()+l]=readyQ.get(l).getID(); 
            } 
        
        //sort the Ids 
        Arrays.sort(ids);
          
        for(int j=0; j<totalSize; j++)

            {
                for (int i =0; i<runningQ.size(); i++)
                    {   
                        if(runningQ.get(i).getID()==ids[j])
                            {
                                out+=runningQ.get(i).toString();   
                            }
                    } 

                for (int i =0; i<blockedQ.size(); i++)
                    {                 
                        if(blockedQ.get(i).getID()==ids[j])
                            {
                                out+=blockedQ.get(i).toString();   
                            }
                    } 

                for (int i =0; i<readyQ.size(); i++)
                    {   
                        if(readyQ.get(i).getID()==ids[j])
                            {
                                out+=readyQ.get(i).toString();   
                            }  
                    } 
        
            } 

        return out;

    }  
}