package structure;

import java.io.IOException;

import org.json.JSONObject;

import communication.RepositoryInterface;

public class Argument implements StructureComponent{
	
	private String name;
	private String type;
	
	public Argument() {	}
	
	public Argument(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		return obj;
	}

	public void toRepository(int i) {
		JSONObject obj = constructJSONCore();
		obj.put("methodId", i);
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("arguments", obj.toString());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject constructJSONCore() {
		JSONObject obj = new JSONObject();
		obj.put("argumentName", name);
		obj.put("type", type);
		return obj;
	}
	
	
	
}
