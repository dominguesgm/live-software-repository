package handlers;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IStartup;
import org.json.JSONObject;

import communication.RepositoryInterface;
import structure.Class;
import structure.Package;
import structure.Project;

public class Startup extends AbstractHandler implements IStartup{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IElementChangedListener listener = new IElementChangedListener() {

			@Override
			public void elementChanged(ElementChangedEvent eventDelta) {
				// TODO Auto-generated method stub
				System.out.println(eventDelta.getDelta());
				System.out.println(eventDelta.getType());
			}
			
		};
		
		JavaCore.addElementChangedListener(listener);
		
		return null;
	}

	@Override
	public void earlyStartup() {
		try {
			(new ProcessSource()).execute(null);
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error generating first structure version");
		}
		
		IElementChangedListener listener = new IElementChangedListener() {

			@Override
			public void elementChanged(ElementChangedEvent eventDelta) {		
				System.out.println("\n\n\n ONE EVENT\n");
				
				IJavaElementDelta highestElementDelta = eventDelta.getDelta();
				
				descendDeltaTree(highestElementDelta);
			}
			
			public void descendDeltaTree(IJavaElementDelta root) {
				for(IJavaElementDelta delta : root.getAffectedChildren()) {
					if((delta.getFlags() & IJavaElementDelta.F_CHILDREN) == 0 ||
							delta.getElement().getElementType() == IJavaElement.COMPILATION_UNIT) {
						processDelta(delta);
					}else {
						descendDeltaTree(delta);
					}
				}
			}
			
			public void processDelta(IJavaElementDelta delta) {				
				IJavaElement element = delta.getElement(); 
				
				
				switch(element.getElementType()) {
				
				case IJavaElement.COMPILATION_UNIT:
					System.out.println("Compilation unit");
					System.out.println(element);
					if(delta.getKind() == IJavaElementDelta.REMOVED) {
						// delete
						System.out.println(element.getElementName());
						String[] keys = {"projectName", "packageName", "className"};
						String[] values = {element.getJavaProject().getProject().getName(),
								"src."+element.getAncestor(IJavaElement.PACKAGE_FRAGMENT).getElementName(),
								element.getElementName().split("\\.")[0]};
						try {
							RepositoryInterface.getInstance().delete("i_classes", keys, values);
							System.out.println("Deleted class " + element.getElementName());
						} catch (IOException e) {
							System.out.println("Failed to communicate with server: delete class");
						}
					} else {
						try {
							ArrayList<Class> classes = ProcessSource.processCompUnit((ICompilationUnit)element);
							// post classes
							
							for(Class cls : classes){
								JSONObject clsJson = cls.toJson();
								clsJson.put("projectName", element.getJavaProject().getProject().getName());
								clsJson.put("packageName", "src."+element.getAncestor(IJavaElement.PACKAGE_FRAGMENT).getElementName());
								RepositoryInterface.getInstance().post("i_classes", clsJson.toString());
								System.out.println("Inserted class " +cls.getName());
							}
						} catch (JavaModelException e) {
							System.out.println("Project contains errors");
						} catch (NullPointerException e) {
							System.out.println("Project contains errors");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Failed to communicate with server: post class");
						}
					}
					break;
				
				case IJavaElement.PACKAGE_FRAGMENT:
					System.out.println("Package");
					System.out.println(element);
					if(delta.getKind() == IJavaElementDelta.REMOVED) {
						// delete
						String[] keys = {"projectName", "packageName"};
						String[] values = {element.getJavaProject().getProject().getName(),
								"src."+element.getAncestor(IJavaElement.PACKAGE_FRAGMENT).getElementName()};
						try {
							RepositoryInterface.getInstance().delete("packages", keys, values);
							System.out.println("Deleted package " + element.getElementName());
						} catch (IOException e) {
							System.out.println("Failed to communicate with server: delete package");
						}
					} else {
						try {
							Package pack = ProcessSource.processPackage((IPackageFragment)element, element.getJavaProject().getProject().getName());
							JSONObject packJson = pack.toJson();
							packJson.put("projectName", element.getJavaProject().getProject().getName());
							RepositoryInterface.getInstance().post("packages", packJson.toString());
							System.out.println("Inserted package "+ pack.getName());
						} catch (JavaModelException e) {
							System.out.println("Project contains errors");
						} catch (NullPointerException e) {
							System.out.println("Project contains errors");
						} catch (IOException e) {
							System.out.println("Failed to communicate with server: post package");
						}
					}
					break;
				
				case IJavaElement.JAVA_PROJECT:
					System.out.println("Project");
					try {
						Project proj = ProcessSource.processProject((IProject) element);
						RepositoryInterface.getInstance().post("projects", proj.toJson().toString());
					} catch (JavaModelException e) {
						System.out.println("Project contains errors");
					} catch (IOException e) {
						System.out.println("Failed to communicate with server: post project");
					}
					break;
				default:
					System.out.println("Unrecognized Element type");
					System.out.println(element.getElementType());
					break;
				}
			}
			
		};
		
		JavaCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE );
	}

}
