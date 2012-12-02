/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.utils.exception.PogamutException;
import java.util.logging.Level;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;


/**
 *
 * @author klesk
 */

public class Main{
    
      public static void main(String args[]) throws PogamutException
     {
        
         new UT2004BotRunner(BotLogic.class, "eftBot").setMain(false).setLogLevel(Level.OFF).startAgent();
     }
}
