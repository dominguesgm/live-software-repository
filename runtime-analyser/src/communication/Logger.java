package communication;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONObject;

public class Logger {
	
	private static Logger instance = null;
	
	private JSONObject core;
	
	private Logger() {
		core = new JSONObject();
		core.put("events", new JSONArray());
	};
	
	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
			return instance;
		} else {
			return instance;
		}
	}
	
	public void appendEvent(JSONObject event) {
		JSONObject timestampWrapper = new JSONObject();
		timestampWrapper.put("timestamp", new Timestamp(System.currentTimeMillis()));
		timestampWrapper.put("event", event);
		((JSONArray)core.get("events")).put(timestampWrapper);
	}
	
	public void writeToFile() {
		PrintWriter newfile;
		try {
			newfile = new PrintWriter("/home/gil/void/logs/"+"execution_logfile"+".json");

			newfile.write(core.toString());
			newfile.close();
		} catch (FileNotFoundException e) {
		}
		
		System.out.println("Finished analysis");
	}
	
	public void publishEvent(JSONObject event) {
		Startup.getInstance().appendEvent(event);
	}
}
