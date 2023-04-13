package com.group2.storm;



//[1]patternParser
//[2]writeToFile
//[3]readFromFile

//[2]writeToFile
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.nio.file.StandardOpenOption.*;


//[3]readFromFile
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class for handling the errors during file load
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner; // Import the Scanner class for reading the text files


/**
 * <br> ✔ Class for miscellaneous tasks
 * <br> ✔ In this class, we define four methods for flagging:
* <br> ✔ First, we flag if a username if its username text or description text contains a 'bot' word
* <br> ✔ Secondly, we flag a username if its description or its tweets have some URLs in it.
* <br> ✔ We also flag if a username has imbalance number of followers and friends. As we stated in our assignment 1, we define the ratio as 1:100. One is for the number of followers, and one hundred is for the number of friends.
* <br> ✔ Lastly, we flag a username, if there are some empty field such as in their description or location.
 * @author Syahirul Faiz
 * @version 2020.03
 * @since 2020-05-08
 *
 */
public class Helper{


	 /**
	  * <br> ✔ 	[1]patternParser
	* <br> ✔ parse a string based on the pattern given.
	* <br> ✔ if match, return the matched pattern
	 * @param string any String input that want to be checked
	 * @param pola a pattern we want to check
	 * @return the matched pattern
	 */
	public String patternParser(String string, String pola){
		   String parsedPattern = "";
		   string = string.trim().toLowerCase();
		   	Pattern pattern = Pattern.compile(pola); 
			Matcher matcher = pattern.matcher(string);
				if (matcher.find())  
				{parsedPattern = matcher.group();}
			return parsedPattern;
			}
 
	
	   /**
	    * <br> ✔ [2]writeToFile
	 * <br> ✔ write the array string in the given file path
	 * @param tweet tweet vector to be written
	 * @param path the relative path for writing the file
	 * @throws IOException if there is some error with IO
	 */
	public void writeToFile(String[] tweet, String path) throws IOException {
	    	String toBeWritten=""; 
	    	try (FileWriter writer = new FileWriter(new File(path),true)) {

	    		 for(int i=0; i<tweet.length;i++) {
						toBeWritten = toBeWritten + tweet[i] +";";
				}
	    		 
	    	      StringBuilder sb = new StringBuilder();
	    	      //---------------------------------
	    	      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    	      Date date = new Date();
	    	      String dateToBeWritten = dateFormat.format(date);
	    	      toBeWritten = toBeWritten+dateToBeWritten+";"+"\n";
	    	      //---------------------------------
	    	      sb.append(toBeWritten);

	    	      writer.write(sb.toString());

	    	    } catch (FileNotFoundException e) {
	    	      System.out.println(e.getMessage());
	    	      e.printStackTrace();
	    	    }
		} 
	 
    
    /**
     * <br> ✔   [3]readFromFile
	 * <br> ✔ for reading data from a local file
     * @param path the relative path where the file is located
     */
    public void readFromFile(String path) {
        try {
          File myObj = new File(path);
          Scanner myReader = new Scanner(myObj);
          while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println("INFO from_FileReader:\n"+data);
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.err.println("An error occurred.");
          e.printStackTrace();
        }
      }
    
  

    /**
     * <br> ✔   [4]flagIfBotWordExist
    * <br> ✔ method for flagging a username, if it contains 'bot' word (i.e: either in the name, description, etc)
     * @param anyString input string for checking
     * @return 1 (true) or 0 (false)
     */
    public int flagIfBotWordExist(String anyString) {
    int flag = 0;
    	if (anyString.contains("bot")){flag = 1;}
    return flag;
    }
   

    /**
     * <br> ✔   [5]flagIfURLExist
  * <br> ✔ method for flagging a username, if its description, or tweets contains URL 
     * @param anyString input string for checking
     * @return 1 (true) or 0 (false)
     */
    public int flagIfURLExist(String anyString) {
        int flag = 0;
        String pattern = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (patternParser(anyString.toLowerCase(),pattern)!=null && !patternParser(anyString.toLowerCase(),pattern).isEmpty())
        	{flag = 1;}
        return flag;
    }
    

    /**
     * <br> ✔     [6]flagIfImbalanceFollowerFriends
    * <br> ✔ for checking the follower:friends ratio (1:100)
     * @param followers number of followers
     * @param friends number of friends
     * @return 1 (true) or 0 (false)
     */
    public int flagIfImbalanceFollowerFriends(String followers, String friends) {
    	 int flag = 0;
    	 int numOfFollowers =Integer.parseInt(followers);
    	 int numOfFriends =Integer.parseInt(friends);

    	 float comparison = (float) numOfFollowers / numOfFriends;
    	 if (comparison <= 0.1) {flag =1;}
    	 
    	 return flag;
    }
    

    /**
     * <br> ✔     [7]flagIfNull
    * <br> ✔ for flagging the specific columns, if they are empty
     * @param  anyString input string for checking
     * @return 1 (true) or 0 (false)
     */
    public int flagIfNull(String anyString) {
    	int flag=0;
    	if (anyString==null || anyString.isEmpty() || anyString.contains("FALSE")) {flag =1;}
    	return flag;
    }
    
    

    /**
     * <br> ✔     [8]flagIfNameIsRandom
    * <br> ✔ Credit to : <a href='https://mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/'>https://mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/</a>
    * <br> ✔ to check whether a username contains random non-sense string
     * @param  anyString input string for checking
     * @return 1 (true) or 0 (false)
     */
    public int flagIfNameIsRandom(String anyString) {
    	int flag=0;
    	String pattern = "^[a-z0-9_-]{3,15}$";
        if (patternParser(anyString.toLowerCase(),pattern)==null && patternParser(anyString.toLowerCase(),pattern).isEmpty())
        	{flag = 1;}
    	return flag;
    }
    
    
   
    
//	 //[2]writeToFile	 
//  public void writeToFileTxt(String[] tweet, String path){
//     String toBeWritten="";
//     try {
//         for(int i=0; i<tweet.length;i++) {
//  					toBeWritten = toBeWritten + tweet[i] +";";
// 				}
//         //toBeWritten = toBeWritten + "\r\n" ;
//         toBeWritten = "\r\n" + toBeWritten;
//        Files.write(Paths.get(path), toBeWritten.getBytes(), APPEND);
//     }catch (IOException e) {
//  	   e.printStackTrace();
//     }
//  }
    
    

 /**
 * <br> ✔	 [9]writeToFile
  * <br> ✔   method helper for appending the result from OutputBolt
 *@param anystring the string that should be recorded
 *@param path the relative path to local file
 */
public void writeToFileTxt(String anystring, String path){
    String toBeWritten="";
    try  (FileWriter writer = new FileWriter(new File(path),true)){
        toBeWritten = "\r\n <br/> User: @" + anystring +" is indicated as BOT : <a href='https://twitter.com/"+anystring+"' target='_blank'>"+anystring+"</a> (recorded at "+Calendar.getInstance().getTime()+")";
       Files.write(Paths.get(path), toBeWritten.getBytes(), APPEND);
    }catch (IOException e) {
 	   e.printStackTrace();
    }
 }
    
    
}
