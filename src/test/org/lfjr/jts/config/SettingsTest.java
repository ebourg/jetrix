package  org.lfjr.jts.config;

import junit.framework.*;

public class SettingsTest extends TestCase
{
    public SettingsTest(String name)
    {
        super(name);
    }
    	
    public static void testNormalize1()
    {
    	Settings s = new Settings();    	
    	int[] tab = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };    	
    	s.normalize(tab);    	
    	assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize2()
    {
    	Settings s = new Settings();    	
    	int[] tab = { 0, 0, 0, 0, 0 };	
    	s.normalize(tab);    	
    	assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize3()
    {
    	Settings s = new Settings();    	
    	int[] tab = { 100, 200, 300, 50, 100, 50, 250, 300 };  	
    	s.normalize(tab);    	
    	assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize4()
    {
    	Settings s = new Settings();    	
    	int[] tab = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };  	
    	s.normalize(tab);    	
    	assertEquals("Erreur de normalisation", 100, sum(tab));
    }           
    
    public static void testNormalize5()
    {
    	Settings s = new Settings();
    	int[] tab4 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
    	int[] tab5 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
    	s.normalize(tab4);
    	assertTrue("Erreur de normalisation", equals(tab4, tab5));
    }    

    private static long sum(int[] tab)
    {
        long s = 0;        
        for(int i=0; i<tab.length; i++) s = s + tab[i];        
        return s;
    }
    
    private static boolean equals(int[] a, int[] b)
    {
        boolean equals = true;
        
        if (a.length == b.length)
        {
            int i=0;
            while(equals && i<a.length)
            {
            	equals = (a[i]==b[i]);
                i++;	
            }
        }
        else
        {
            equals = false;
        }
        
    	return equals;
    }

    public static Test suite()
    {
        return new TestSuite(SettingsTest.class);
    }

}

