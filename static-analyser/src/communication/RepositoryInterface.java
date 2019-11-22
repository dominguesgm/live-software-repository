package communication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RepositoryInterface {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private OkHttpClient client;
	
	private static RepositoryInterface instance = null;
	private boolean deployment = true;
	
	private String url_development = "http://0.0.0.0";
	private String url_deployment = "";
	private String port = "3000";
	
	private RepositoryInterface() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout((long)120, TimeUnit.SECONDS);
		builder.readTimeout((long)120, TimeUnit.SECONDS);
		builder.writeTimeout((long)120, TimeUnit.SECONDS);
		
		if(System.getenv("LIVESD_SERVER") == null) {
			url_deployment = "https://live-sd.herokuapp.com/";
			System.out.println("It was null");
		} else {
			url_deployment = System.getenv("LIVESD_SERVER");
		}
		
		client = builder.build();
	}
	
	public static RepositoryInterface getInstance() {
		if(instance == null) {
			instance = new RepositoryInterface();
			return instance;
		} else {
			return instance;
		}
	}
	
	public JSONObject post(String endpoint, String json) throws IOException {
		String finalURL = deployment ? url_deployment + endpoint : (url_development + ":" + port + "/" + endpoint);
		
		RequestBody body = RequestBody.create(JSON,  json);
		Request request = new Request.Builder()
				.url(finalURL)
				.post(body)
				.build();
		Response response = client.newCall(request).execute();
		if(response.code() == 201) {
			return new JSONObject(response.body().string());
		} else {
			System.out.println("[Error] Creation request unsuccesful");
			System.out.println("Code: " + response.code());
			System.out.println("Content: " + response.toString());
			return null;
		}
	}
	
	public JSONObject delete(String endpoint, String[] keys, String[] values) throws IOException {
		String finalURL = deployment ? url_deployment + endpoint : (url_development + ":" + port + "/" + endpoint);
		
		if(keys.length > 0) {
			finalURL += "?";
		}
		for(int i = 0; i < keys.length; i++) {
			finalURL += keys[i] + "="+ values[i];
			if(i < keys.length -1)
				finalURL += "&";
		}
		
		Request request = new Request.Builder()
				.url(finalURL)
				.delete()
				.build();
		Response response = client.newCall(request).execute();
		if(response.code() == 200) {
			String response_body = response.body().string();
			return new JSONObject(response_body);
		} else {
			return null;
		}
		
	}
}
