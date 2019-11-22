import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import org.json.*;

import communication.Logger;
import communication.Startup;

public aspect MethodInvocation {
	pointcut methodInvocation() :
		call(* *(..)) && (!within(MethodInvocation)) && 
			(!within(communication.Logger)) && (!within(communication.RepositoryInterface)) &&
			(!within(communication.Startup)) ; //&&
			//(call(* *.NonExistentMethod()) /* || insert other calls here */);
	
	before() : methodInvocation(){
		
		Startup.getInstance();
		
		JSONObject event = new JSONObject();

		event.put("this", thisJoinPoint.getThis() == null ? "static" : "instance");
		event.put("target", thisJoinPoint.getTarget() == null ? "null" : "exists");
		event.put("kind",  thisJoinPoint.getKind());
		event.put("signature",  thisJoinPoint.getSignature());
		
		if(thisJoinPoint.getThis() != null)
			event.put("class",  thisJoinPoint.getThis().getClass().getCanonicalName());
		else
			event.put("class",  "null");
		event.put("sourceLocation",  thisJoinPoint.getSourceLocation());
		event.put("originClass",  thisEnclosingJoinPointStaticPart.getSignature().getDeclaringTypeName());
		event.put("destinationClass", thisJoinPoint.getSignature().getDeclaringTypeName());
		event.put("projectName", Startup.getInstance().getProjectName());
		event.put("projectId", Startup.getInstance().getProjectId());

		
		JSONArray arguments = new JSONArray();
		JSONObject argument;
		Object[] argArray = thisJoinPoint.getArgs();
		
		for(Object arg : argArray) {
			argument = new JSONObject();
			
			if(arg != null) {
				argument.put("value", "not_null");
				argument.put("type", arg.getClass().getCanonicalName());
			} else {
				argument.put("value", "null");
				argument.put("type", "null");
			}
			
			arguments.put(argument);
		}
		event.put("arguments", arguments);
		
		// Insert hashes
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
						
			messageDigest.update(thisEnclosingJoinPointStaticPart.getSignature().getDeclaringTypeName().getBytes());
			String originClassHash = String.format("%064x", new BigInteger(1, messageDigest.digest()));
			
			messageDigest.reset();
			
			messageDigest.update(thisJoinPoint.getSignature().getDeclaringTypeName().getBytes());
			String destinationClassHash = String.format("%064x", new BigInteger(1, messageDigest.digest()));
			
			event.put("originHash", originClassHash);
			event.put("destinationHash", destinationClassHash);
		} catch(NoSuchAlgorithmException e) {
			System.out.println("SHA-256 is not a valid hashing algorithm");
		}
		
		//Logger.getInstance().appendEvent(event);
		
		event.put("timestamp", new Timestamp(System.currentTimeMillis()));
		Logger.getInstance().publishEvent(event);
		
		//Logger.getInstance().writeToFile();
				
		/*System.out.println("This: " + thisJoinPoint.getThis());
		System.out.println("Target: " + thisJoinPoint.getTarget());
		System.out.println("Kind: " + thisJoinPoint.getKind());
		System.out.println("Signature: " + thisJoinPoint.getSignature());
		if(thisJoinPoint.getThis() != null)
			System.out.println("Class: " + thisJoinPoint.getThis().getClass().getCanonicalName());
		System.out.println("StaticPart: " + thisJoinPoint.getStaticPart());
		System.out.println("SourceLocation: " + thisJoinPoint.getSourceLocation());
		System.out.println("Origin class name: " + thisEnclosingJoinPointStaticPart.getSignature().getDeclaringTypeName());
		
		Object[] argArray = thisJoinPoint.getArgs();
		System.out.println("\nCall has " + argArray.length + " arguments");
		for(Object arg : argArray) {
			System.out.println("Argument of type " + arg.getClass().getCanonicalName() + " with value " + arg);
		}
		
		System.out.println("\nEnd point\n------------------------------------------------------------------\n\n\n");*/
		
		
	}
}
