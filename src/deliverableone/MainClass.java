package deliverableone;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;

public class MainClass {

	public static void main(String[] args) {
		
		
		String projectName = "MAHOUT" ;
		
		//path in locale /Users/Valerio/Desktop/ISPW2/Progetti/
		
		String pathname ="/Users/Valerio/Desktop/ISPW2/Progetti/"+projectName;
		
		LoggerClass.setupLogger();
		
		
		try {
			CalculatingFixedBugs.run(projectName,pathname);
			
		} catch (IOException | JSONException | GitAPIException e) {
			
			LoggerClass.errorLog("Error while calculating fixed bugs per month. \n");
	        StringWriter stringWriter = new StringWriter();
	        PrintWriter printWriter = new PrintWriter(stringWriter);
	        e.printStackTrace(printWriter);
	        LoggerClass.errorLog(stringWriter.toString());
		}
		
	}

}