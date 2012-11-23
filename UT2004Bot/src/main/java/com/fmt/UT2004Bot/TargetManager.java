/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

/**
 * Select goals for goap
 * Select target enemies
 * 
 * @author Michele, Tilman
 */
public class TargetManager 
{
    //SINGLETON START
    private static TargetManager instance = null;
    
    private TargetManager() {
        // Exists only to defeat instantiation.
    }

    public static TargetManager getInstance() {
        if (instance == null) {
            instance = new TargetManager();
        }
        return instance;
    }
    //SINGLETON END
    
    public void update()
    {
        // if goal achieved create new target and replan
        
        // ask blackboard whether to replan
        
    }
    
    
}
