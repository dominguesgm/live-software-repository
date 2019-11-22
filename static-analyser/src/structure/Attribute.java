package structure;

import java.io.IOException;

import org.json.JSONObject;

import communication.RepositoryInterface;

public class Attribute implements StructureComponent{
	
	private String name;
	private String type;
	
	public Attribute() {}
	
	public Attribute(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		
		return obj;
	}

	public void toRepository(int i) {
		JSONObject obj = constructJSONCore();
		obj.put("classId", i);
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("attributes", obj.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject constructJSONCore() {
		JSONObject obj = new JSONObject();
		obj.put("attributeName", name);
		obj.put("type", type);
		return obj;
	}
}
