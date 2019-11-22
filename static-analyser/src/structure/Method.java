package structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.RepositoryInterface;

public class Method implements StructureComponent{
	
	private String name;
	private int charStart;
	private int charLength;
	private int lineCount;
	private List<Argument> arguments;
	private String returnType;
	private List<String> methodInvocations;
	private String methodKey;
	
	public Method(String name, String returnType) {
		this.name = name;
		this.returnType = returnType;
		arguments = new ArrayList<Argument>();
		methodInvocations = new ArrayList<String>();
	}
	
	public int getCharStart() {
		return charStart;
	}
	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}
	public int getCharLength() {
		return charLength;
	}
	public void setCharLength(int charLength) {
		this.charLength = charLength;
	}
	public int getLineCount() {
		return lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public String getName() {
		return name;
	}
	public List<Argument> getArguments() {
		return arguments;
	}
	public void addArgument(Argument arg) {
		arguments.add(arg);
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	public void setMethodInvocations(List<String> methodInvocations) {
		this.methodInvocations = methodInvocations;
	}
	
	public void addMethodInvocation(String methodInvocation) {
		this.methodInvocations.add(methodInvocation);
	}
	
	public String getMethodInvocation(int index) {
		return this.methodInvocations.get(index);
	}
	
	public List<String> getMethodInvocations() {
		return this.methodInvocations;
	}
	
	public String getKey() {
		return methodKey;
	}

	public void setKey(String key) {
		methodKey = key;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		
		obj.put("methodInvocations", new JSONArray());
		obj.put("arguments", new JSONArray());
		
		for(String invocation : methodInvocations) {
			obj.append("methodInvocations", invocation);
		}
		
		for(Argument arg : arguments) {
			obj.append("arguments", arg.toJson());
		}
		return obj;
	}

	public void toRepository(int i) {
		JSONObject obj = constructJSONCore();
		obj.put("classId", i);
		
		// Send request to add project to repository and retrieve ID
		
		try {
			JSONObject result = RepositoryInterface.getInstance().post("i_methods", obj.toString());
			
			// If request succeeded, process projects packages
			
			if(result != null) {
				for(String invocation : methodInvocations) {
					JSONObject jsonInvocation = new JSONObject();
					jsonInvocation.put("invocation", invocation);
					jsonInvocation.put("methodId", (int)result.get("id"));
					RepositoryInterface.getInstance().post("method_invocations", jsonInvocation.toString());
				}
				
				for(Argument arg: arguments) {
					arg.toRepository((int)result.get("id"));
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
		obj.put("methodName", name);
		obj.put("startOfMethod", charStart);
		obj.put("lengthOfMethod", charLength);
		obj.put("linesOfCode", lineCount);
		obj.put("returnType", returnType);
		obj.put("argumentCount", arguments.size());
		obj.put("key", methodKey);
		return obj;		
	}
	
	

}
