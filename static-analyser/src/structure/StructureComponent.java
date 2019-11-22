package structure;

import org.json.JSONObject;

import okhttp3.MediaType;

public interface StructureComponent {
	public JSONObject toJson();
	
	public JSONObject constructJSONCore();
}
