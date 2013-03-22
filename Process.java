/* Olga Romanova
   Operating Systems    
   Spring 2013 
   Lab 2 - Scheduler 

   Code for integers
   1st : Process ID
   2nd: CPU time
   3rd: I/O time
   4th: Arrival time
*/

public class Process
{
    private String s;
    
    //While the 4 digit int associated with a module is not a uselist for all types, the variable will be called this for simplicity. 
    private int id;
    private int cpuTime;
    private int cpuTimeLeft;
    private int ioTime;
    private int ioTimeLeft;
    private int arrivalTime;
    private char status ;
    private int priority;
    private int turnaround;
    //the status can be n = notStarted, u = running, b = blocked, r = ready, d = done. Processes in notStarted or done will not be displayed in the output. 


    public Process(int i, int cpu, int io, int atime)
    {
        this.id = i;
        this.cpuTime = cpu;
        //remaining CPU time and IO time set to full times
        //round remaining CPU time because half cycles should be rounded up to a full cycle. If io is 0 and the number of cycles is odd, cputime should not be rounded up because
        //all cpu time will be executed at once rather than 
         if(cpu % 2 != 0) 
            { 
                this.cpuTimeLeft = cpu+1;
            }
        else {
            this.cpuTimeLeft = cpu;
                }  
        this.ioTime = io;
        this.ioTimeLeft=io;
        this.arrivalTime = atime;
        this.status= 'n';
        this.priority=-1;
        this.turnaround=0;
    }  

    public int getID()
    {
        return id;
    }   

    public int getCPUtime()
    {
        return cpuTime;
    }   

    public int getIOtime()
    {
        return ioTime;
    }   

    public int getCPUtimeLeft()
    {
        return cpuTimeLeft;
    }   

    public int getIOtimeLeft()
    {
        return ioTimeLeft;
    }   

    public void setCPUtimeLeft(int t)
    {
        this.cpuTimeLeft=t;
    }   

    public void setIOtimeLeft(int t)
    {
        this.ioTimeLeft=t;
    }   

    public int getArrivalTime()
    {
        return arrivalTime;
    }   

    public char  getStatus()
    {

        return status;
    } 

    public void setStatus(char stat)
    {
        this.status=stat;
    } 

    public int  getPriority()
    {

        return priority;
    } 

    public void setPriority(int p)
    {
        this.priority=p;
    } 


    public int getTurnaround()
    {
        return turnaround;
    } 

    //add to the turnaround time 
    public void setTurnaround(int t)
    {
        this.turnaround=t;
    } 

    //smaller toStribg 
    public String toString()
    {
        String statusString="";

        switch(this.status)
            {
            case 'n': statusString= "not started " ;
                    break;
            case 'u': statusString = "running ";
                    break;
            case 'b': statusString = "blocked ";
                    break;
            case 'r': statusString = "ready ";
                    break;
            case 'd': statusString = "done ";
                    break;
            default: statusString = "PROBLEM!"  ;
                    break;
            }

        String processInfo= " " + id + ": " + statusString ;
        return processInfo;
        
        
    }    
    

    //used for testing 
    public String toFullString()

    {

        String statusString="";
        
        switch(this.status)
            {
            case 'n': statusString= "not started " ;
                    break;
            case 'u': statusString = "running ";
                    break;
            case 'b': statusString = "blocked ";
                    break;
            case 'r': statusString = "ready ";
                    break;
            case 'd': statusString = "done ";
                    break;
            default: statusString = "PROBLEM!"  ;
                    break;
            }

        String processInfo= "id " +id + "; cpu time " +cpuTime + " ; cpu time left" +cpuTimeLeft + " ; io time " + ioTime + "; io timeLeft " + ioTimeLeft +"; arrival time " + arrivalTime + " : " +statusString + " priority " + priority + "; turnaround is " + turnaround + "\n";
        return processInfo;
        
    }    

}












