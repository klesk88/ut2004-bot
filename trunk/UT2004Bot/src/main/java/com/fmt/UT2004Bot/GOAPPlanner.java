/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import java.util.Stack;

/**
 *
 * @author Michele,Tilman
 */
public class GOAPPlanner {
    
    BlackBoard bb;
    Stack<Action> finalPlan;
    
    public GOAPPlanner()
    {
        bb = BlackBoard.getInstance();
    }
    
    public void replan()
    {
    // get world state
    // apply action
    // check whether goal is achieved
    // if not, apply other action or go back        
        
        
        //write planb to blackboard
        bb.currentPlan = finalPlan;
        
    }
}
