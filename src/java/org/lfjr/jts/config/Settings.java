package org.lfjr.jts.config;

import java.util.*;

public class Settings
{
    private int startingLevel;
    private int stackHeight;
    private int linesPerLevel;
    private int linesPerSpecial;    
    private int levelIncrease;
    private int specialAdded;
    private int specialCapacity;
    private int blockOccurancy[];
    private int specialOccurancy[];
    private boolean averageLevels;
    private boolean classicRules;

    public static final int BLOCK_LEFTL     = 0;
    public static final int BLOCK_LEFTZ     = 1;
    public static final int BLOCK_SQUARE    = 2;
    public static final int BLOCK_RIGHTL    = 3;
    public static final int BLOCK_RIGHTZ    = 4;
    public static final int BLOCK_HALFCROSS = 5;
    public static final int BLOCK_LINE      = 6;
    
    public static final int SPECIAL_ADDLINE      = 0;
    public static final int SPECIAL_CLEARLINE    = 1;
    public static final int SPECIAL_NUKEFIELD    = 2;
    public static final int SPECIAL_RANDOMCLEAR  = 3;
    public static final int SPECIAL_SWITCHFIELD  = 4;
    public static final int SPECIAL_CLEARSPECIAL = 5;
    public static final int SPECIAL_GRAVITY      = 6;
    public static final int SPECIAL_QUAKEFIELD   = 7;
    public static final int SPECIAL_BLOCKBOMB    = 8;
           

    public Settings() 
    {
    	blockOccurancy = new int[7];
    	specialOccurancy = new int [9];
    }
    
    
    public Settings(Settings conf) 
    {
    	this();    
    	
    	if (conf != null)
    	{
	startingLevel   = conf.getStartingLevel(); 
	stackHeight     = conf.getStackHeight(); 
	linesPerLevel   = conf.getLinesPerLevel(); 
	linesPerSpecial = conf.getLinesPerSpecial();  
	levelIncrease   = conf.getLevelIncrease(); 
	specialAdded    = conf.getSpecialAdded(); 
	specialCapacity = conf.getSpecialCapacity(); 
	averageLevels   = conf.getAverageLevels();  
	classicRules    = conf.getClassicRules(); 
	
	for (int i=0; i<blockOccurancy.length; i++) { blockOccurancy[i] = conf.getBlockOccurancy(i); }
	for (int i=0; i<specialOccurancy.length; i++) { specialOccurancy[i] = conf.getSpecialOccurancy(i); }
	}
    }
    
    
    public int getStartingLevel() { return startingLevel; }
    public int getStackHeight() { return stackHeight; }
    public int getLinesPerLevel() { return linesPerLevel; }
    public int getLinesPerSpecial() { return linesPerSpecial; }    
    public int getLevelIncrease() { return levelIncrease; }   
    public int getSpecialAdded() { return specialAdded; }   
    public int getSpecialCapacity() { return specialCapacity; }   
    public boolean getAverageLevels() { return averageLevels; }   
    public boolean getClassicRules() { return classicRules; }   
    public int getBlockOccurancy(int piece) { return blockOccurancy[piece]; }
    public int getSpecialOccurancy(int special) { return specialOccurancy[special]; }


    public void setStartingLevel(int startingLevel) { this.startingLevel=startingLevel; }
    public void setStackHeight(int stackHeight) { this.stackHeight=stackHeight; }
    public void setLinesPerLevel(int linesPerLevel) { this.linesPerLevel=linesPerLevel; }
    public void setLinesPerSpecial(int linesPerSpecial) { this.linesPerSpecial=linesPerSpecial; }    
    public void setLevelIncrease(int levelIncrease) { this.levelIncrease=levelIncrease; }   
    public void setSpecialAdded(int specialAdded) { this.specialAdded=specialAdded; }   
    public void setSpecialCapacity(int specialCapacity) { this.specialCapacity=specialCapacity; }   
    public void setAverageLevels(boolean averageLevels) { this.averageLevels=averageLevels; }   
    public void setClassicRules(boolean classicRules) { this.classicRules=classicRules; }   
    public void setBlockOccurancy(int piece, int occurancy) { blockOccurancy[piece] = occurancy; }
    public void setSpecialOccurancy(int special, int occurancy) { specialOccurancy[special] = occurancy; }    


    public void clearBlockOccurancy() 
    {
    	Arrays.fill(blockOccurancy, 0);
    }

    public void normalizeBlockOccurancy() 
    {
    	normalize(blockOccurancy);
    }
    
    private void normalize(int[] tab)
    {
    	int sum = 0;
    	
    	// calcul de la somme    	
    	for (int i=0; i<tab.length; i++)
    	{
    	    sum = sum + tab[i];
    	}
    	
    	if (sum != 100)
    	{
    	    // égalisation
    	    if (sum==0)
    	    {
    	    	int v = 100/tab.length;
    	    	for (int i=0; i<tab.length; i++) { tab[i] = v; }    	    	
    	    }
    	    else
    	    {
    	    	float f = 100f/sum;
    	    	System.out.println("f = "+f);
    	    	for (int i=0; i<tab.length; i++) { tab[i] = (int)(tab[i] * f); }    	    	
    	    }
    	    
    	    // distribution des points restants.
    	    for (int i=0; i<tab.length; i++) { sum = sum + tab[i]; } 
    	    int r = 100 - sum;
    	    int i = 0;    	        	    
    	    while( i < tab.length && r > 0)
    	    {
    	        tab[i] = tab[i] + 1;
    	        r = r - 1;
    	        i = i + 1;
    	    }    	        	        	       	        	    
    	}    	
    }
    
    public void clearSpecialOccurancy()
    {
    	Arrays.fill(specialOccurancy, 0);
    }
        
    public void normalizeSpecialOccurancy()
    {
    	normalize(specialOccurancy);
    }    

}