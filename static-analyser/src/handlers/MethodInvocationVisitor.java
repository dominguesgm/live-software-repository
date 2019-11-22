package handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor{
	List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();
	List<IMethod> methods = new ArrayList<IMethod>();
	List<IMethod> originMethods = new ArrayList<IMethod>();
	
	
	@Override
	public boolean visit(MethodInvocation node) {
		MethodDeclaration decl = loopUntilEnclosingMethod(node);
		if(decl != null ) {
			IMethodBinding binding = node.resolveMethodBinding();
			IMethod method = (IMethod) binding.getJavaElement();
			
			IMethodBinding bindingDecl = decl.resolveBinding();
			IMethod methodDecl = (IMethod) bindingDecl.getJavaElement();
			
			if(methodDecl != null && method != null) {
				invocations.add(node);
				methods.add(method);
				originMethods.add(methodDecl);
			}
		}
		return super.visit(node);
	}
	
	public List<MethodInvocation> getInvocations() {
		return invocations;
	}
	
	/*
	 * Return all IMethods resolved keys for methods which are invoked from the given method
	 *   
	 */
	public ArrayList<String> getInvocationsFrom(IMethod comp) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < methods.size(); i++) {
			if(originMethods.get(i).isSimilar(comp)) {
				result.add(methods.get(i).getKey());
			}
		}
		return result;
	}
	
	
	/* Find method from which a given method was called 
	 * (Null is a possible result if a method is invoked when defining an attribute)
	 *
	**/
	public MethodDeclaration loopUntilEnclosingMethod(ASTNode node) {
		while(true) {
			if(node.getNodeType() == ASTNode.METHOD_DECLARATION)
				return (MethodDeclaration) node;
			else {
				if(node.getNodeType() == ASTNode.COMPILATION_UNIT || node.getParent() == null) {
					return null;
				} else {
					node = node.getParent();
				}
			}
		}
	}
	
}
