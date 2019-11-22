package structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.RepositoryInterface;

public class Project implements StructureComponent {
	private String name;
	private List<Package> packages;
	
	public Project(String name) {
		this.name = name;
		packages = new ArrayList<Package>();
	}
	
	public void addPackage(Package newPackage) {
		packages.add(newPackage);
	}
	
	public List<Package> getPackages() {
		return packages;
	}
	
	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumPackages() {
		return packages.size();
	}

	public boolean hasPackage(String name) {
		for(Package pkg : packages) {
			if(pkg.getName().equals(name))
				return true;
		}
		return false;
	}

	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		
		obj.put("packages", new JSONArray());

		for(Package pkg : packages) {
			obj.append("packages", pkg.toJson());
		}
		obj.append("packages", (new Package("src", true, "src")).toJsonRoot());

		return obj;
	}

	public void toRepository() {
		JSONObject obj = constructJSONCore();
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("projects", obj.toString());
			
			// If request succeeded, process projects packages
			
			if(result != null) {
				for(Package pkg : packages) {
					pkg.toRepository((int)result.get("id"));
				}
				
				// Insert src package as root for all packages
				Package root = new Package("src", true);
				JSONObject rootObj = root.constructJSONCoreRoot();
				rootObj.put("projectId", (int)result.get("id"));				
				try {
					JSONObject rootResult = RepositoryInterface.getInstance().post("packages", rootObj.toString());					
				} catch (IOException e) {
					e.printStackTrace();
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
		obj.put("projectName", name);
		obj.put("numPackages", packages.size());
		return obj;
	}
	
	public void toRepositoryDeep() {
		JSONObject obj = toJson();
		
		try {			
			JSONObject result = RepositoryInterface.getInstance().post("projects", obj.toString());
			
			System.out.println(result);
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
