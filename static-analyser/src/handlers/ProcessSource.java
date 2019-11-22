package handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.Document;
import org.json.JSONObject;

import structure.Argument;
import structure.Attribute;
import structure.Class;
import structure.Method;
import structure.Package;
import structure.Project;
import structure.Workspace;


public class ProcessSource extends AbstractHandler {

	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects  = root.getProjects();
		// Loop over all projects
		Workspace wspc = new Workspace();
		for (IProject project : projects) {
			try {
				if(project.isNatureEnabled(JDT_NATURE)) {
					try {
						Project proj = processProject(project);
						wspc.addProject(proj);
						System.out.println("Analyzed: " + proj.getName());
					} catch( NullPointerException e) {
						System.out.println("Project \"" + project.getName() + "\" is not in a buildable state. Please check for errors.");
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		try {
			//wspc.toRepository();
			//wspc.toRepositoryDeep();
			
			for(Project proj : wspc.getProjects()) {
				proj.toRepositoryDeep();
				JSONObject obj = new JSONObject();
				obj.append("allProjectData", proj.toJson());
				PrintWriter newfile = new PrintWriter("/home/gil/void/logs/"+"static_" + proj.getName()+ "_logfile"+".json");
				newfile.write(obj.toString());
				newfile.close();
			}
			
			System.out.println("Finished analysis");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Project processProject(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		Project proj = new Project(project.getName());
				
		for(IPackageFragment pkg : packages) {
			if(pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
				Package pkgRes = processPackage(pkg, proj.getName());
				if(pkgRes.getNumClasses() != 0) {
					proj.addPackage(pkgRes);
				}
			}
		}
		
		List<Package> cloned = new ArrayList<Package>(proj.getPackages());
		for(Package pkg : cloned) {
			
			String[] packageNames = pkg.getName().split("\\.");
			String packageName = "";
			for(int i = 0; i < packageNames.length; i++) {
				
				packageName += packageNames[i];
				if(!proj.hasPackage(packageName)) {
					Package newPack = new Package(packageName);
					newPack.setHasSubpackages(true);
					proj.addPackage(newPack);
				}
				packageName += ".";
			}
		}
		return proj;
	}

	public static Package processPackage(IPackageFragment pkg, String projName) throws JavaModelException {
		ICompilationUnit[] compUnits = pkg.getCompilationUnits();
		Package pkgRes = new Package(pkg.getElementName());
		pkgRes.setHasSubpackages(pkg.hasSubpackages());
		String dir = buildPackagePath(pkg);
		pkgRes.setPath(dir + pkg.getElementName());
		
		for(ICompilationUnit compUnit : compUnits) {
			ArrayList<Class> classRes = processCompUnit(compUnit);
			for(Class cls : classRes) {
				pkgRes.addClass(cls);
			}
		}
		return pkgRes;
	}


	public static ArrayList<Class> processCompUnit(ICompilationUnit compUnit) throws JavaModelException {
		IType[] types = compUnit.getTypes();
		ArrayList<Class> classes = new ArrayList<Class>();
		for(IType type : types) {
			// If a class is found
			if(type.isClass()) {
				Class tempClass = new Class(type.getElementName());
				tempClass.setQualifiedName(type.getFullyQualifiedName());
				
				
				// Process attributes of the class
				ArrayList<Attribute> attrRes = processFields(type);
				tempClass.setAttributes(attrRes);
				
				// Generate CompilationUnit from the class so it can accept visitors
				CompilationUnit compUnitNI = parse(compUnit);
				
				// Process class method declarations
				MethodVisitor visitor = new MethodVisitor();
				MethodInvocationVisitor invocVisitor = new MethodInvocationVisitor();
				compUnitNI.accept(visitor);
				compUnitNI.accept(invocVisitor);
				ArrayList<Method> methodRes = processMethods(visitor, invocVisitor);
				tempClass.setMethods(methodRes);
				
				Document doc = new Document(type.getSource());
				tempClass.setLineCount(doc.getNumberOfLines());
				classes.add(tempClass);
			}
		}
		return classes;
	}

	public static ArrayList<Attribute> processFields(IType type) throws JavaModelException {
		IField[] fields = type.getFields();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(IField field : fields) {
			Attribute tempAttr = new Attribute(field.getElementName(), Signature.toString(field.getTypeSignature()));
			attributes.add(tempAttr);
		}
		return attributes;
	}

	public static ArrayList<Method> processMethods(MethodVisitor visitor,
			MethodInvocationVisitor invocVisitor) throws JavaModelException {
		
		ArrayList<IMethod> methods = visitor.getIMethods();
		ArrayList<MethodDeclaration> declarations = visitor.getMethodDeclarations();
		ArrayList<Method> mthdRes = new ArrayList<Method>();
		for(int i = 0; i < methods.size(); i++) {
			IMethod method = methods.get(i);
			ASTNode implementation = declarations.get(i);
			
			// Create new method for json
			Method tempMethod = new Method(method.getElementName(), Signature.toString(method.getReturnType()));
			tempMethod.setKey(method.getKey());
			
			// Add arguments
			String[] names = method.getParameterNames();
			String[] types = method.getParameterTypes();
			for(int j = 0; j < names.length; j++) {
				Argument argument = new Argument(names[j], Signature.toString(types[j]));
				tempMethod.addArgument(argument);
			}
			
			// Add start and length
			tempMethod.setCharStart(implementation.getStartPosition());
			tempMethod.setCharLength(implementation.getLength());
			
			// Add Lines of code
			Document doc = new Document(method.getSource());
			tempMethod.setLineCount(doc.getNumberOfLines());
			
			// Add invocations inside method
			tempMethod.setMethodInvocations(invocVisitor.getInvocationsFrom(method));
			
			// Add method to method list
			mthdRes.add(tempMethod);
		}
		return mthdRes;
	}


	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS9);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		return (CompilationUnit) parser.createAST(null);
	}
	
	/*private static String buildPackagePath(IPackageFragment pkgFrag) {
		String result = "";
		IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot) pkgFrag.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
		int safeCounter = 15;
		while(!fragmentRoot.getElementName().equals("src")) {
			result = fragmentRoot.getElementName() + "." + result;
			fragmentRoot = (IPackageFragmentRoot) fragmentRoot.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			safeCounter--;
			if(safeCounter < 0)
				break;
		}
		result = fragmentRoot.getElementName() + "." + result;
		
		return result;
	}*/
	
	private static String buildPackagePath(IPackageFragment pkgFrag) {
		String result="";
		String identifier = pkgFrag.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT).getHandleIdentifier();
		boolean first = true;
		for(String slice : identifier.split("\\\\/")) {
			if(first == false)
				result += slice+".";
			else {
				result += slice.split("/")[1]+".";
			}
			first = false;
		}
		return result;
	}
}
