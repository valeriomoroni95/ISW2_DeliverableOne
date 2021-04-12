package deliverableone;

import java.util.logging.*;

//In this class I create 2 logs: a console log and a file log.

public class LoggerClass {
 
	
	private LoggerClass() {
		
		throw new IllegalStateException("Exception in LoggerClass!!");
	}
	
	private static final Logger Log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME );     
    
    //setting up my Logger
	public static void setupLogger() {
    	
        LogManager.getLogManager().reset();
        Log.setLevel(Level.ALL);
        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        Log.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler("Logger.log", true);
            fileHandler.setLevel(Level.FINE);
            Log.addHandler(fileHandler);
        } catch (java.io.IOException e) {            
        	Log.log(Level.SEVERE, "There are problems in the file logger!!!", e);
        }
        
    } 
	
	public static void errorLog(String message) {
    	
    	Log.severe(message);
    }
    
    public static void infoLog(String message ) {
    	
    	Log.info(message);
    }
    
   
    

}
