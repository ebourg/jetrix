package  org.lfjr.jts.config;

import junit.framework.*;

public class ServerConfigTest extends TestCase
{
    public ServerConfigTest(String name)
    {
        super(name);
    }
    	
    public static void testGetInstance()
    {
    	try 
    	{    		
            ServerConfig conf = ServerConfig.getInstance();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    public static Test suite()
    {
        return new TestSuite(ServerConfigTest.class);
    }

}

