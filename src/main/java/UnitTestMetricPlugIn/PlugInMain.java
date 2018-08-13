package UnitTestMetricPlugIn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugins.annotations.Mojo;
	
@Mojo(name = "metrictesting")

public class PluginMain extends AbstractMojo 	{
	
	Parameter 
	ToolMain;
	
	    public void execute() throws MojoExecutionException
	    {
	    	getLog().info( "Code inspection for unit test classes" );
	    	ToolMain.getClass();
	    }
	}


