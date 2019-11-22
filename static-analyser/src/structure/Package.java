package structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.RepositoryInterface;


public class Package implements StructureComponent{
	private String name;
	private List<Class> classes;
	private boolean hasSubpackages;
	private String path;
	
	public Package(String name) {
		this.name = name;
		classes = new ArrayList<Class>();
	}

	public Package(String name, boolean hasSubpackages) {
		this.name = name;
		this.hasSubpackages = hasSubpackages;
		classes = new ArrayList<Class>();
	}
	
	public Package(String name, boolean hasSubpackages, String path) {
		this.name = name;
		this.hasSubpackages = hasSubpackages;
		this.path = path;
		classes = new ArrayList<Class>();
	}
	
	public void addClass(Class newClass) {
		classes.add(newClass);
	}
	
	public List<Class> getClasses() {
		return classes;
	}
	
	public void setClasses(ArrayList<Class> classList) {
		this.classes = classList;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumClasses() {
		return classes.size();
	}
																																															
	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		
		obj.put("classes", new JSONArray());
		
		for(Class cls : classes) {
			obj.append("classes", cls.toJson());
		}
		return obj;
	}
	
	public JSONObject toJsonRoot() {
		JSONObject obj = constructJSONCoreRoot();
		
		obj.put("classes", new JSONArray());
		
		for(Class cls : classes) {
			obj.append("classes", cls.toJson());
		}
		return obj;
	}

	public void setHasSubpackages(boolean hasSubpackages) {
		this.hasSubpackages = hasSubpackages;
	}
	

	public void toRepository(int i) {
		JSONObject obj = constructJSONCore();
		obj.put("projectId", i);
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("packages", obj.toString());
			
			// If request succeeded, process projects packages
			
			if(result != null) {
				for(Class cls : classes) {
					cls.toRepository((int)result.get("id"));
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public JSONObject constructJSONCore() {
		JSONObject obj = new JSONObject();
		obj.put("packageName", "src."+name);
		obj.put("packagePath", path);
		obj.put("classCount", classes.size());
		obj.put("hasSubpackages", String.valueOf(hasSubpackages));
		return obj;
	}
	
	public JSONObject constructJSONCoreRoot() {
		JSONObject obj = new JSONObject();
		obj.put("packageName", name);
		obj.put("packagePath", path);
		obj.put("classCount", classes.size());
		obj.put("hasSubpackages", String.valueOf(hasSubpackages));
		return obj;
	}
}
