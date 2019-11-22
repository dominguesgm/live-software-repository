package handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;


public class MethodVisitor extends ASTVisitor{
	ArrayList<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	ArrayList<IMethod> methodInterfaces = new ArrayList<IMethod>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		IMethod method = (IMethod) binding.getJavaElement();
		if(method != null) {
			methods.add(node);
			methodInterfaces.add(method);
		}
		return super.visit(node);
	}
	
	public ArrayList<MethodDeclaration> getMethods() {
		return methods;
	}
	
	public ArrayList<IMethod> getIMethods() {
		return methodInterfaces;
	}
	
	public ArrayList<MethodDeclaration> getMethodDeclarations() {
		return methods;
	}
}
