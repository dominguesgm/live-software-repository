package structure;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.RepositoryInterface;


public class Class implements StructureComponent{
	private String name;
	private String qualifiedName;
	private List<Attribute> attributes;
	private List<Method> methods;
	private int lineCount;
	private String hash;
	
	
	public Class(String name) {
		this.name = name;
		attributes = new ArrayList<Attribute>();
		methods= new ArrayList<Method>();
	}
	
	public void addMethod(Method newMethod) {
		methods.add(newMethod);
	}
	
	public List<Method> getMethods() {
		return methods;
	}
	
	public void addAttributes(Attribute newAttribute) {
		attributes.add(newAttribute);
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumAttributes() {
		return attributes.size();
	}
	
	public int getNumMethods() {
		return methods.size();
	}
	
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	
	public int getLineCount() {
		return lineCount;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(qualifiedName.getBytes());
			String encryptedString = String.format("%064x", new BigInteger(1, messageDigest.digest()));
			this.hash = encryptedString;
		} catch(NoSuchAlgorithmException e) {
			System.out.println("SHA-256 is not a valid hashing algorithm");
		}
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		obj.put("attributes", new JSONArray());
		obj.put("methods", new JSONArray());
		
		for(Attribute attr : attributes) {
			obj.append("attributes", attr.toJson());
		}
		
		for(Method mth : methods ) {
			obj.append("methods", mth.toJson());
		}
		return obj;
	}

	public void toRepository(int i) {
		JSONObject obj = constructJSONCore();
		obj.put("packageId", i);
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("i_classes", obj.toString());
			
			// If request succeeded, process projects packages
			
			if(result != null) {
				for(Attribute attr : attributes) {
					attr.toRepository((int)result.get("id"));
				}
				
				for(Method mth : methods) {
					mth.toRepository((int)result.get("id"));
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject constructJSONCore() {
		JSONObject obj = new JSONObject();
		obj.put("className", name);
		obj.put("qualifiedName", qualifiedName);
		obj.put("methodCount", methods.size());
		obj.put("attributeCount", attributes.size());
		obj.put("linesOfCode", lineCount);
		obj.put("hash", hash);
		return obj;
	}
	
	
}
