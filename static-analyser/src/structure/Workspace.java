package structure;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import communication.RepositoryInterface;

public class Workspace implements StructureComponent{
	private ArrayList<Project> projects;
	
	public Workspace() {
		this.projects = new ArrayList<Project>();
	}
	
	public Workspace(ArrayList<Project> projects) {
		this.projects = projects;
	}
	
	public void addProject(Project proj) {
		projects.add(proj);
	}
	
	public ArrayList<Project> getProjects(){
		return projects;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject obj = constructJSONCore();
		for(Project prj : projects) {
			obj.append("allProjectData", prj.toJson());
		}
		return obj;
	}
	
	public void toRepository() {
		for(Project proj : projects) {
			proj.toRepository();
		}
	}

	@Override
	public JSONObject constructJSONCore() {
		return new JSONObject();
	}
	
}
