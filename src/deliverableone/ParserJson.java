package deliverableone;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;


public class ParserJson {
	
	private ParserJson() {
		
	}
	
	 private static String readAll(Reader rd) throws IOException {
	     
		 StringBuilder sb = new StringBuilder();
	     int cp;
	     while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	     }
	     return sb.toString();
	 }
	 
	 public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
	     
	     try (InputStream is = new URL(url).openStream();){
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.name()));
	        String jsonText = readAll(rd);
	        return new JSONArray(jsonText);
	     }
	       
	 }
	 
	 public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	     
	     try (InputStream is = new URL(url).openStream();) {
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.name()));
	        String jsonText = readAll(rd);
	        return new JSONObject(jsonText);
	     }
	 }
	 
	 public static void getIdFixedTicketList(String projName,List<String> tickets) throws IOException, JSONException {
		   
		  Integer j = 0;
		  Integer i = 0;
		  Integer total = 1;
		  
	      //Get JSON API for closed bugs w/ AV in the project
	      do {
	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	         j = i + 1000;
	         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
	                + i.toString() + "&maxResults=" + j.toString();
	         JSONObject json = readJsonFromUrl(url);
	         JSONArray issues = json.getJSONArray("issues");
	         total = json.getInt("total");
	         for (; i < total && i < j; i++) {
	            //Iterate through each bug
	            String key = issues.getJSONObject(i%1000).get("key").toString();
	            tickets.add(key);
	         }  
	      } while (i < total);
	   }

}
