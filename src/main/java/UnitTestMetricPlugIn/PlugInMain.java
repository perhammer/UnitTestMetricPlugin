package UnitTestMetricPlugIn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugins.annotations.Mojo;
	
@Mojo(name = "metrictesting")

public class PlugInMain extends AbstractMojo 	{
	
	Parameter 
	ToolMain;
	
	    public void execute() throws MojoExecutionException
	    {
	    	getLog().info( "A Code inspection tool for unit test classes" );
	    	ToolMain.getClass();
	    }
	}
