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
	    	/*Plugin running a different version of this class, a deleted one, no changes made 
	    	 * to this class are being reflected in the running of the plugin for instance the 
	    	 * log output still reads code inspection for unit test classes 
	    	 */
	    	getLog().info( "A Code inspection tool for unit test classes" );
	    	ToolMain.getClass();
	    }
	}
