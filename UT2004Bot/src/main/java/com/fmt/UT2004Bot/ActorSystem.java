/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import java.util.Stack;

/**
 * This class should manage ALL actions the bot takes. 
 * 
 * Classes e.g. for moving the bot should be managed by this class (e.g. deciding which movement module gets )
 * @author Tilman
 */
public class ActorSystem {
    
    BlackBoard bb;
    
    public ActorSystem()
    {
        bb = BlackBoard.getInstance();
    }
    
    public void update()
    {
        //ask current action about its status
        //if action terminated ask blackboard which action is next in the goap plan
        
        //low level action like navigation
     
        //if(bb.currentPlan.IsEmpty) 
        //  bb.replan = true;
        
    }
    
}
