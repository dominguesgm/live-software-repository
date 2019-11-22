package communication;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Startup {

	private static Startup instance = null;
	private JSONObject eventQueue;
	private String projectName;
	private int projectId;
	
	private Startup() {
		try {
			System.out.println(System.getProperty("user.dir"));
			String[] directories;
			if(System.getProperty("os.name").equals("Linux"))
				directories = System.getProperty("user.dir").split("/");
			else
				directories = System.getProperty("user.dir").split("\\\\");
			
			projectName = directories[directories.length-1];
			String[] keys = {"name"};
			String[] values = {projectName};
			JSONArray res = RepositoryInterface.getInstance().list("projects", keys, values);
			projectId = (int)((JSONObject)res.get(0)).get("id");
			
			System.out.println("Project "+projectId);
			System.out.println("Project name "+projectName);
			
			RepositoryInterface.getInstance().clear("events");
			System.out.println("Cleared event table");
			eventQueue = new JSONObject();
			eventQueue.put("events", new JSONArray());
			startEventLauncherThread();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not clear event table");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Repository does not contain this project");
		}
	}
	
	public static Startup getInstance() {
		if(instance == null) 
			instance = new Startup();
		return instance;
	}
	
	
	public void startEventLauncherThread() {
		
		new Thread(new Runnable( ) {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(5000);
						synchronized(RepositoryInterface.getInstance()){
							RepositoryInterface.getInstance().post("events", eventQueue.toString());
							eventQueue.put("events", new JSONArray());
						}
					} catch (InterruptedException e) {
					} catch (IOException e) {
					}
				}
			}
			
		}).start();
	}
	
	public void appendEvent(JSONObject event) {
		synchronized(RepositoryInterface.getInstance()) {
			eventQueue.append("events", event);
		}
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public int getProjectId() {
		return projectId;
	}
	
}
