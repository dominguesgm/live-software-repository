package handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class GetInfo extends AbstractHandler {
	
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects  = root.getProjects();
		// Loop over all projects
		for (IProject project : projects) {
			try {
				if(project.isNatureEnabled(JDT_NATURE)) {
					analyzeMethods(project);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void analyzeMethods(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		//parse(JavaCore.create(project))
		for(IPackageFragment mypackage : packages) {
			if(mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				createAST(mypackage);
			}
		}
	}
	
	private void createAST(IPackageFragment mypackage) throws JavaModelException{
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			System.out.println("\n####################");
			// Create AST  for the ICompilationUnits
			CompilationUnit parse = parse(unit);
			System.out.println("Class name: " + unit.getElementName());
		
			MethodVisitor visitor = new MethodVisitor();
			parse.accept(visitor);
			
			for(MethodDeclaration method : visitor.getMethods()) {
				System.out.println("/////////////////////////");
				System.out.println("Method name: " + method.getName()
						+ "\tReturn type: " + method.getReturnType2()
						+ "\tOrigin Class: " + ((AbstractTypeDeclaration)method.getParent()).getName());
			}
			
			MethodInvocationVisitor invVisitor = new MethodInvocationVisitor();
			parse.accept(invVisitor);
			for(MethodInvocation invocation : invVisitor.getInvocations()) {
				System.out.println("-------------------------");
				MethodDeclaration originMethod = findParentMethodDeclaration(invocation);
				TypeDeclaration originClass = findParentTypeDeclaration(invocation);
				IMethodBinding invokedMethod = invocation.resolveMethodBinding().getMethodDeclaration();
				ITypeBinding invokedType = invokedMethod.getDeclaringClass();
				
				if(invocation != null) {
					System.out.println("Expression? " + (invocation.getExpression() == null ? "no" : "yes")); // System.out for example
					System.out.println("Invoked method: " + invocation.getName());
				}
				if(originMethod != null) System.out.println("\tInvoking method: " + originMethod.getName()); // As expected, doesn't work for method invocations outside method declarations
				if(originClass != null) System.out.println("\tOrigin Class: " + originClass.getName());
				if(invokedMethod != null) System.out.println("Invoked Method " + invokedMethod.getName());
				if(invokedType != null) System.out.println("Invoked Class " + invokedType.getName());
			}
		}
	}
	
	/**
	 * Read a ICompilationUnit and creates the AST DOM for manipulating
	 * the java source file
	 * @param unit
	 * @return
	 */
	
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
	
	/**
	 * Iterate over node types until a method declaration is found
	 * @param origin
	 * @return method declaration node
	 */
	
	private static MethodDeclaration findParentMethodDeclaration(ASTNode origin) {
		ASTNode resultingNode = origin;
		while(resultingNode.getNodeType() != ASTNode.METHOD_DECLARATION) {
			if(resultingNode.getRoot().equals(resultingNode)) {
				return null;
			}
			resultingNode = resultingNode.getParent();
		}
		return (MethodDeclaration) resultingNode;
	}
	
	/**
	 * Iterate over node types until a type (class) declaration is found
	 * @param origin
	 * @return method declaration node
	 */
	
	private static TypeDeclaration findParentTypeDeclaration(ASTNode origin) {
		ASTNode resultingNode = origin;
		while(resultingNode.getNodeType() != ASTNode.TYPE_DECLARATION) {
			if(resultingNode.getRoot().equals(resultingNode)) {
				return null;
			}
			resultingNode = resultingNode.getParent();
		}
		return (TypeDeclaration) resultingNode;
	}
	


}
