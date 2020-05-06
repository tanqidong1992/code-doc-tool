package com.hngd.tool.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.openapi.ProjectAnalysis;
import com.hngd.tool.RestJavaAPIGenerator;

import io.squark.nestedjarclassloader.NestedJarClassLoader;

public class ProjectUtils {

	
	public static String generateJarFileName(MavenProject mavenProject) {
		String artifactId = mavenProject.getArtifactId();
		String version = mavenProject.getVersion();
		return artifactId + "-" + version + ".jar";
	}
	
	public static String buildJarFilePath(MavenProject mavenProject) {
		String buildOutputPath = mavenProject.getBuild().getDirectory();
		String jarFileName = ProjectUtils.generateJarFileName(mavenProject);
		String jarFilePath = buildOutputPath + File.separator + jarFileName;
		return jarFilePath;
	}
	static final   Logger logger=LoggerFactory.getLogger(RestJavaAPIGenerator.class);
	public static List<Class<?>> loadSpringBootJar(String jarFilePath,String packageFilter){
	    NestedJarClassLoader loader=new NestedJarClassLoader(ProjectUtils.class.getClassLoader(),logger);
		File file=new File(jarFilePath);
		try {
			loader.addURLs(file.toURI().toURL());
		} catch (IOException e) {
			logger.error("",e);;
		}
		List<String> allClass=loader.listAllClass("default");
		List<Class<?>>clazzes=allClass.stream()
			.filter(name->name.startsWith(packageFilter))
	        .map(name->ProjectAnalysis.loadClassFromNestedJar(loader, name))
	        .filter(clazz -> clazz != null)
	        .collect(Collectors.toList());
		return clazzes;
	}
	
	
	public static List<Class<?>> loadProjectClass(List<File> classFilePaths,String packageFilter){
	    NestedJarClassLoader loader=new NestedJarClassLoader(ProjectUtils.class.getClassLoader(),logger);
		for(File file:classFilePaths) {
			try {
				loader.addURLs(file.toURI().toURL());
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		List<String> allClass=loader.listAllClass("default");
		List<Class<?>>clazzes=allClass.stream()
			.filter(name->name.startsWith(packageFilter))
	        .map(name->ProjectAnalysis.loadClassFromNestedJar(loader, name))
	        .filter(clazz -> clazz != null)
	        .collect(Collectors.toList());
		return clazzes;
	}
	
	
	public static List<File> getDependencies(MavenProject project,
			MavenSession session,
			ProjectDependenciesResolver projectDependenciesResolver,
			List<MavenProject> projects) throws MojoExecutionException{
	    Set<String> projectArtifacts =
		        projects
		            .stream()
		            .map(MavenProject::getArtifact)
		            .map(Artifact::toString)
		            .collect(Collectors.toSet());
		DependencyFilter ignoreProjectDependenciesFilter =
		        (node, parents) -> {
		          if (node == null || node.getDependency() == null) {
		            // if nothing, then ignore
		            return false;
		          }
		          if (projectArtifacts.contains(node.getArtifact().toString())) {
		            // ignore project dependency artifacts
		            return false;
		          }
		          // we only want compile/runtime deps
		          return Artifact.SCOPE_COMPILE_PLUS_RUNTIME.contains(node.getDependency().getScope());
		        };

		    try {
		      DependencyResolutionResult resolutionResult =
		          projectDependenciesResolver.resolve(
		              new DefaultDependencyResolutionRequest(project, session.getRepositorySession())
		                  .setResolutionFilter(ignoreProjectDependenciesFilter));
		     
		      List<File> files=resolutionResult
	              .getDependencies()
	              .stream()
	              .map(Dependency::getArtifact)
	              //.filter(org.eclipse.aether.artifact.Artifact::isSnapshot)
	              .map(org.eclipse.aether.artifact.Artifact::getFile)
	              .collect(Collectors.toList());
		      return files;
		    } catch (DependencyResolutionException ex) {
		      throw new MojoExecutionException("Failed to resolve dependencies", ex);
		    }
	}
	
	public static List<File> resolveAllClassPath(MavenProject project,
			MavenSession session,
			ProjectDependenciesResolver projectDependenciesResolver,
			List<MavenProject> projects) throws MojoExecutionException {
		List<File> files=new LinkedList<>();
		List<File> dependencies=ProjectUtils.getDependencies(project, session, projectDependenciesResolver, projects);
		if(dependencies!=null) {
			files.addAll(dependencies);
		}
		String outputClassPath=project.getBuild().getOutputDirectory();
		files.add(new File(outputClassPath));
		return files;
	}
	
	
	public static List<File> getGeneratedSourceRoots(MavenProject mavenProject) {
		List<File> sourceRoots=new LinkedList<>();
		String target=mavenProject.getBuild().getDirectory();
		File file=new File(target,"generated-sources");
		if(file.exists() || file.isDirectory()) {
			File[] files=file.listFiles();
			if(files!=null) {
				for(File f:files) {
					//TODO validate diractory?
					sourceRoots.add(f);
				}
			}
		}
		return sourceRoots;
	}
}