package deliverableone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;


public final class CalculatingFixedBugs {
	
	private static int ticketWithoutCommit = 0;     //ticket senza commit
	private static int commitWithoutDate =0;        //commit senza data
	private static int bugsPerMonth = 0 ;           //bugs per ogni mese
	private static List<BugHandler> bugManager ;
	private static List<Date> latestCommitsDates ;  //date degli ultimi commit
	
	
	private CalculatingFixedBugs() {
		
	}
	
	private static Calendar toCalendar(Date date){        //Creo un calendario prendendo la time zone locale
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);
		  return calendar;
		}

	public static void handleTicketWithoutCommit(List<RevCommit> fixedCommits) {
		
		Date authorDate;
		Date date;
		
		if(fixedCommits.isEmpty()) {                 
			
			setTicketWithoutCommit(getTicketWithoutCommit() + 1);
		
		}else {
			
			authorDate = fixedCommits.get(0).getAuthorIdent().getWhen();
			
			for(int i=1;i<fixedCommits.size();i++) {                           //prendo l'ultimo commit tra tutti quelli relativi allo stesso ticket
				
				date =fixedCommits.get(i).getAuthorIdent().getWhen();
				if(date.after(authorDate)){
					authorDate = date;
				}
			}
			latestCommitsDates.add(authorDate);
			
		}
	}
	
	public static void handleCommitWithoutDate(List<String> ticketList , List<RevCommit> commitList ) {
		
		for(String ticket : ticketList) {                                      //per ogni ticket prendi l'ultimo commit
			
			List<RevCommit> fixedCommits = new ArrayList<>();

			for (RevCommit commit : commitList) {
				
				if(commit.getFullMessage().contains((ticket + " "))) {        //prendi tutti i commit che contengono lo stesso ticket ID
					fixedCommits.add(commit);
					if(commit.getAuthorIdent().getWhen()== null) {
						setCommitWithoutDate(getCommitWithoutDate() + 1);
					}
				}
			}
		
			handleTicketWithoutCommit( fixedCommits );
			
		}
	}
	
	public static void initializeBugsPerMonth() {                             
		
		Calendar calendar1 = toCalendar(latestCommitsDates.get(0));           //inizializzo il conteggio dei bug mensili
		int year1 = calendar1.get(Calendar.YEAR);
		int month1= calendar1.get(Calendar.MONTH)+1;
		
		Calendar calendar2 = toCalendar(latestCommitsDates.get(latestCommitsDates.size()-1));
		int year2 = calendar2.get(Calendar.YEAR);
		int month2= calendar2.get(Calendar.MONTH)+1;
		
		int totalMonths = (year2-year1)*12 + (month2-month1)+1;
		
		int month = month1;
		int year =  year1 ;
		
		String monthDate;
		
		for(int i=0;i<totalMonths;i++) {
			if(month > 12) {
				month = 1;
				year += 1;
			}
			if(month<10) {
				monthDate = "0" + month + "-"+ year;
			}
			else {
				monthDate = month + "-"+ year;
			}
			bugManager.add(new BugHandler(monthDate,bugsPerMonth));
			month += 1;
		}
	}
	
	public static void writeCsvBugsPerMonth(String csvName) {
		
		        //creo un file CSV con il numero di fixedBugs mensili
		
				File file = new File(csvName); 
				
				//creo un FileWriter dando il file come parametro 
			
			    try (FileWriter output = new FileWriter(file)){ 

			        //creo un CSVWriter 
			        CSVWriter writer = new CSVWriter(output); 
			  
			        //aggiungo l'header
			        String[] header = { "Month", "FixedBugs"}; 
			        writer.writeNext(header); 
			  
			        //aggiungo i dati al CSV 
			        for(BugHandler monthlyBugs : bugManager) {
			        	String[] data = {monthlyBugs.getDate(), String.valueOf(monthlyBugs.getBugsNumber())}; 
			        	writer.writeNext(data); 
			        
			        }
			       
			        writer.close(); 
			        
			        LoggerClass.infoLog("Monthly fixed bugs calculations ended succesfully! \n");
			        LoggerClass.infoLog("Created CSV file: : " + csvName + "\n");
			    } 
			    catch (IOException e) { 
			    	
			        LoggerClass.errorLog("Error writing monthly fixed bugs CSV \n");
			        StringWriter sw = new StringWriter();
			        PrintWriter pw = new PrintWriter(sw);
			        e.printStackTrace(pw);
			        LoggerClass.errorLog(sw.toString());
			    } 
	}
	
	
	public static void writeCsvCommitsPerMonth(String csvCommitName) {
		
	  //creo un file CSV con il numero dei commit mensili	
	    
	    File commitFile = new File(csvCommitName); 
		
		//creo un FileWriter dando il file come parametro 
	
	    try (FileWriter output = new FileWriter(commitFile)){ 

	        //creo un CSVWriter  
	        CSVWriter writer = new CSVWriter(output); 
	  
	        //aggiungo l'header 
	        String[] header = {"Month", "CommitsPerMonth"}; 
	        writer.writeNext(header); 
	  
	        //aggiungo i dati al CSV
	        for(BugHandler monthlyBugs : bugManager) {
	        	String[] data = {monthlyBugs.getDate(), String.valueOf(monthlyBugs.getCommitNumber())}; 
	        	writer.writeNext(data); 
	        
	        }
	        
	        writer.close(); 
	        
	        LoggerClass.infoLog("Monthly commits calculations ended succesfully! \n");
	        LoggerClass.infoLog("Created CSV file: " + csvCommitName + "\n");
	    } 
	    catch (IOException e) { 
	    	
	        LoggerClass.errorLog("Error writing monthly commits CSV \n");
	        StringWriter stringWriter = new StringWriter();
	        PrintWriter printWriter = new PrintWriter(stringWriter);
	        e.printStackTrace(printWriter);
	        LoggerClass.errorLog(stringWriter.toString());
	    } 
	}
	
	
	public static void getCommitsPerMonth(List<Date> commitsDate , BugHandler monthlyBugs ) {
		
		int commitPerMonth = 0;                                    //vengono calcolati i commit per ogni mese
		
		for(Date date : commitsDate) {
			
			Calendar calendar = toCalendar(date);
			int dateMonth = calendar.get(Calendar.MONTH)+1;
			int dateYear = calendar.get(Calendar.YEAR);
			String defDate;
			if(dateMonth<10) {
				defDate = "0" + dateMonth + "-"+ dateYear;
			}
			else {
				defDate = dateMonth + "-"+ dateYear;
			}
			if(defDate.contentEquals(monthlyBugs.getDate())) {
				
				commitPerMonth = commitPerMonth+1;
			}
				
		}
		monthlyBugs.setCommitNumber(commitPerMonth);
	}
	
	
	
public static void getBugsPerMonth(List<Date> latestCommitsDates , BugHandler monthlyBugs ) {
		
		bugsPerMonth = 0;
		                                                           //vengono calcolati i bug per ogni mese
		for(Date date : latestCommitsDates) {
			Calendar calendar = toCalendar(date);
			int dateMonth = calendar.get(Calendar.MONTH)+1;
			int dateYear = calendar.get(Calendar.YEAR);
			String defDate;
			if(dateMonth<10) {
				defDate = "0" + dateMonth + "-"+ dateYear;
			}
			else {
				defDate = dateMonth + "-"+ dateYear;
			}
			if(defDate.contentEquals(monthlyBugs.getDate())) {
				
				bugsPerMonth+=1;
			}
				
		}
		monthlyBugs.setFixedBugs(bugsPerMonth);
		
}
	
	
	
	public static void run(String projectName,String pathname) throws IOException, JSONException, GitAPIException{
		
		LoggerClass.infoLog("Starting operations to define the bugs...\n");
		
		String csvName = projectName +"fixedBugs"+ ".csv" ;
		String csvCommitName = projectName + "Commits" + ".csv" ;
		
				
		File file = new File(pathname);
		
		if(!file.exists()) {
		Git.cloneRepository()
		  .setURI("https://github.com/apache/"+ projectName)
		  .setDirectory(new File(pathname))
		  .call();
		}
		
		List<String> tickets = new ArrayList<>();                               //creo un arraylist di tickets
		ParserJson.getIdFixedTicketList(projectName,tickets);                   //crea una lista contenente tutti i ticket relativi ai fixed bugs			
		Git git = Git.open(new File(pathname));
		Iterable<RevCommit> projectLog = git.log().call();    					//prendo nel log tutti i commit usando le api jgit
		List<RevCommit> commitList = new ArrayList<>();                         //mi creo una lista di commit
		List<Date> commitsDate = new ArrayList<>();                             //ed una lista di date di questi ultimi
		
		for (RevCommit commitLog : projectLog) {
			
			commitList.add(commitLog);
			
			if(commitLog.getAuthorIdent().getWhen() != null) {                  //se non vuoto lo aggiungo al log
				
				commitsDate.add(commitLog.getAuthorIdent().getWhen());
			}
	
		}
		
		latestCommitsDates = new ArrayList<>();
		bugManager = new ArrayList<>();
		
		handleCommitWithoutDate(tickets,commitList);
			 
		Collections.sort(latestCommitsDates, (date1, date2) ->  date1.compareTo(date2));
        
		initializeBugsPerMonth();
		
		for(BugHandler monthlyBugs : bugManager) {
			
			getBugsPerMonth(latestCommitsDates , monthlyBugs);
				
			getCommitsPerMonth(commitsDate , monthlyBugs);
			
		}
		
		
		writeCsvBugsPerMonth(csvName);
		
		writeCsvCommitsPerMonth(csvCommitName);
		
	}

	public static int getTicketWithoutCommit() {                        //getters and setters added to solve code smells.
		return ticketWithoutCommit;
	}

	public static void setTicketWithoutCommit(int ticketWithoutCommit) {
		CalculatingFixedBugs.ticketWithoutCommit = ticketWithoutCommit;
	}

	public static int getCommitWithoutDate() {
		return commitWithoutDate;
	}

	public static void setCommitWithoutDate(int commitWithoutDate) {
		CalculatingFixedBugs.commitWithoutDate = commitWithoutDate;
	}   
	
}
