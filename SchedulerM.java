/* Olga Romanova
 * Operating Systems    
 * Spring 2013 
 * Lab 1 - Linker 
 */

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class SchedulerM
{

    public static void main(String [] args)
    {
  
        Scanner keyboard = new Scanner(System.in);
        keyboard.useDelimiter("\n|\r\n|\\s+");
    
        System.out.println("Hello, welcome to the Scheduler.");
        System.out.println("Enter a file to read from.");
      
        String file = keyboard.next();

        //to enter while loop to prompt user for a valid file name
        boolean valid = false;
        //empty string representing the file that will be read
        String myFile="";

        //The while loop 
        while(valid==false)
            {

                try
                    {
     
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
            
                        //End while loop when there is a file ready to be read. 
                        if(reader.ready()==true)
                            {
                                valid=true;        
                            }

                        //Add the contents of the file line by line to myFile.
                        while((line = reader.readLine())!= null)
                            {
                                myFile+=line+'\n';
                            } 
                    }//try
		 
                catch (IOException e) 
                    {
                        System.out.println(e.getMessage() + "File "+ file + " not valid. Please run the program again with a valid file name. " );
                        file=keyboard.nextLine();
                    } 

            } //while(valid==false)

        //Once a valid file is found and the contents are in myFile, the StringTokenizer tokenizes myFile
        StringTokenizer tokenizer = new StringTokenizer(myFile);

        //The each token will be added to the tokens ArrayList 
        ArrayList<String> tokens = new ArrayList<String>();  
        String currentToken;
       
        while (tokenizer.hasMoreTokens()) 
            {
                currentToken=tokenizer.nextToken();
                tokens.add(currentToken);
            }

        Scheduler myScheduler =new Scheduler(tokens);
        myScheduler.getProcess();
      
        //Get the name of the file to use for the name of the output file 
        int nameFileStart=file.lastIndexOf("/");
        int nameFileEnd=file.lastIndexOf(".");
        String NameFile = file.substring((nameFileStart+1), nameFileEnd);
        System.out.println(NameFile);


        System.out.println("Please enter the algorithm type to use. " );
        System.out.println("Enter 0 for first come first serve. " );
        System.out.println("Enter 1 Round Robin with quantum 2. " );
        System.out.println("Enter 2 for shortest remaining job first (preemptive). " );
        valid=false;
        
        //to print out to file 
        String outputS="";

        while(valid==false)
            {
 
                int algorithm = keyboard.nextInt();

                switch(algorithm)
                    {
                    case 0: // First Come First Serve 
                        System.out.println( "You entered 0 for first come first serve. " );
                        outputS=myScheduler.fcfs();
                        System.out.println(myScheduler.toString());
                        NameFile=NameFile+"-0";
                        valid=true;
                        break;

                    case 1: //Round Robin with quantum 2
                        System.out.println("You entered 1 Round Robin with quantum 2. " );
                        outputS=myScheduler.roundrobin();
                        System.out.println(myScheduler.toString());
                        NameFile=NameFile+"-1";
                        valid=true;
                        break;

                    case 2:  //Shortest remaining job first     
                        System.out.println("You entered  2 for shortest remaining job first (preemptive). " );
                        NameFile=NameFile+"-2";
                        outputS=myScheduler.shortestRemaining();
                        System.out.println(myScheduler.toString());
                        valid=true;
                        break;

                    default:  System.out.println("Algorithm type "+ algorithm + " not valid. Please enter a valid type. " );
                        break;
                    } 

            } //while(valid==false)

        //write output to a file 

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(NameFile+".txt"));
            out.write(outputS);
            out.close();
            System.out.println("The name of the output file is "+ NameFile+".txt" );		

        }
        catch (IOException e)
            {
                System.out.println("Failed to print to write to output file!");		
            }

    }
      
}  

 