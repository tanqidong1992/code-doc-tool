package com.hngd.tool;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;

import com.hngd.tool.utils.ProjectUtils;

public abstract class BaseMojo extends AbstractMojo {

	@Parameter(defaultValue = "${session}", readonly = true)
	protected MavenSession session;
	// TODO: This is internal maven, we should find a better way to do this
	@Component
	protected ProjectDependenciesResolver projectDependenciesResolver;
	
	@Parameter(defaultValue = "${reactorProjects}", required = true, readonly = true)
	protected List<MavenProject> projects;
	
	@Component
	protected MavenProject mavenProject;
	
	
	protected List<File> getSourceRoots(){
		List<File> sourceRoots=mavenProject.getCompileSourceRoots().stream()
			    .map(File::new)
			    .collect(Collectors.toList());
			//add generate source if needed;
			List<File> generatedSourceRoots=ProjectUtils.getGeneratedSourceRoots(mavenProject);
			generatedSourceRoots.forEach(sourceRoots::add);
			return sourceRoots;
	}
}
