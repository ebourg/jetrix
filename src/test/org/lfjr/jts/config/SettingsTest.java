package  org.lfjr.jts.config;

import junit.framework.*;

public class SettingsTest extends TestCase
{
    public SettingsTest(String name)
    {
        super(name);
    }
    	
    public static void testNormalize()
    {
    	Settings s = new Settings();
    	
    	int[] tab1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    	int[] tab2 = { 0, 0, 0, 0, 0 };
    	int[] tab3 = { 100, 200, 300, 50, 100, 50, 250, 300 };
    	int[] tab4 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
    	
    	assertEquals("Erreur de normalisation", 100, sum(s.normalize(tab1)));
    	assertEquals("Erreur de normalisation", 100, sum(s.normalize(tab2)));
    	assertEquals("Erreur de normalisation", 100, sum(s.normalize(tab3)));
    	assertEquals("Erreur de normalisation", 100, sum(s.normalize(tab4)));
    	assertTrue("Erreur de normalisation", equals(tab4, s.normalize(tab4)));
    }

    private long sum(int[] tab)
    {
        long s = 0;        
        for(int i=0; i<tab.length; i++) s = s + tab[i];        
        return s;
    }
    
    private boolean equals(int[] a, int[] b)
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

