package UnitTestMetricPlugIn;

import org.apache.maven.plugin.*;


import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "metrictesting")

public class PlugInMain extends AbstractMojo 	{
	
    public void execute() throws MojoExecutionException
    {
    	/*Hopefully should run an instance of the bytecodereader class when plugin is built 
    	Not been able to test due to issues with running the plugin via command line and plug in set up
    	 */
    	getLog().info( "Code inspection for unit test classes" );
       
    }
}
