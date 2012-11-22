/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import java.util.List;

/**
 *
 * @author Tilman
 */
public class WorkingMemory {
    
    //SINGLETON START
    private static WorkingMemory instance = null;
    
    private WorkingMemory() {
        // Exists only to defeat instantiation.
    }

    public static WorkingMemory getInstance() {
        if (instance == null) {
            instance = new WorkingMemory();
        }
        return instance;
    }
    //SINGLETON END
    
    List<WorkingMemoryFact> currentFacts;
    
    
}
