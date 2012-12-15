/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.flag.FlagListener;
import javax.vecmath.Vector3d;

/**
 *
 * @author Michele
 */
public class Sensor_RayCastSystem 
{
    private BlackBoard bb = BlackBoard.getInstance();
   
    
    // Constants for rays' ids. It is allways better to store such values
    // in constants instead of using directly strings on multiple places of your
    // source code
    private static final String FRONT = "frontRay";
    private static final String FRONTUP = "frontRayUp";
    private static final String FRONTDOWN = "frontRayDown";
    private static final String LEFT45 = "left45Ray";
    private static final String LEFT90 = "left90Ray";
    private static final String RIGHT45 = "right45Ray";
    private static final String RIGHT90 = "right90Ray";
    private AutoTraceRay left45, front, right45, left90, right90, front_up, front_down;

        // initialize rays for raycasting
    final int rayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 3);
    final int ray_length_front = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 4);
    final int ray_length_side90 = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 3);
    
    public Sensor_RayCastSystem()
    {
        this.init();
        //super();
    }
    
    private void init(){
         /**
         * Raycast
         */
        // settings for the rays
        boolean fastTrace = true;        // perform only fast trace == we just need true/false information
        boolean floorCorrection = false; // provide floor-angle correction for the ray (when the bot is running on the skewed floor, the ray gets rotated to match the skew)
        boolean traceActor = false;      // whether the ray should collid with other actors == bots/players as well
      
        // 1. remove all previous rays, each bot starts by default with three
        // rays, for educational purposes we will set them manually
        BotLogic.getInstance().getAct().act(new RemoveRay("All"));

        // 2. create new rays
        BotLogic.getInstance().getRaycasting().createRay(LEFT45, new Vector3d(1, -1, 0), rayLength, fastTrace, floorCorrection, traceActor);       
        BotLogic.getInstance().getRaycasting().createRay(FRONT, new Vector3d(1, 0, 0), ray_length_front, fastTrace, floorCorrection, traceActor);
        BotLogic.getInstance().getRaycasting().createRay(RIGHT45, new Vector3d(1, 1, 0), rayLength, fastTrace, floorCorrection, traceActor);
         BotLogic.getInstance().getRaycasting().createRay(LEFT90, new Vector3d(0, -1, 0), ray_length_side90, fastTrace, floorCorrection, traceActor);
         BotLogic.getInstance().getRaycasting().createRay(RIGHT90, new Vector3d(0, 1, 0), ray_length_side90, fastTrace, floorCorrection, traceActor);
         //BotLogic.getInstance().getRaycasting().createRay(FRONTUP, new Vector3d(1, 0, 0.5), ray_length_front, fastTrace, floorCorrection, traceActor);
         //BotLogic.getInstance().getRaycasting().createRay(FRONTDOWN, new Vector3d(1, 0, -0.5), ray_length_front, fastTrace, floorCorrection, traceActor);
      
        // register listener called when all rays are set up in the UT engine
        
         BotLogic.getInstance().getRaycasting().getAllRaysInitialized().addListener(new FlagListener<Boolean>() {

            public void flagChanged(Boolean changedValue) {
                // once all rays were initialized store the AutoTraceRay objects
                // that will come in response in local variables, it is just
                // for convenience
                left45 = BotLogic.getInstance().getRaycasting().getRay(LEFT45);              
                front = BotLogic.getInstance().getRaycasting().getRay(FRONT);
                right45 = BotLogic.getInstance().getRaycasting().getRay(RIGHT45);
                left90 = BotLogic.getInstance().getRaycasting().getRay(LEFT90);
                right90 = BotLogic.getInstance().getRaycasting().getRay(RIGHT90);
                //front_up = BotLogic.getInstance().getRaycasting().getRay(FRONTUP);
                //front_down = BotLogic.getInstance().getRaycasting().getRay(FRONTDOWN);
                
            }
        });
        // have you noticed the FlagListener interface? The Pogamut is often using {@link Flag} objects that
        // wraps some iteresting values that user might respond to, i.e., whenever the flag value is changed,
        // all its listeners are informed

        // 3. declare that we are not going to setup any other rays, so the 'raycasting' object may know what "all" is        
         BotLogic.getInstance().getRaycasting().endRayInitSequence();
            
        
         // The most important thing is this line that ENABLES AUTO TRACE functionality,
        // without ".setAutoTrace(true)" the AddRay command would be useless as the bot won't get
        // trace-lines feature activated
        BotLogic.getInstance().getAct().act(new Configuration().setDrawTraceLines(false).setAutoTrace(true));
        
    }
    
    public void update()
    {
            // if the rays are not initialized yet, do nothing and wait for their initialization 
        if (! BotLogic.getInstance().getRaycasting().getAllRaysInitialized().getFlag()) {
            BotLogic.getInstance().getLog().info("Exit Raycast ");
            //TODO maybe this log was useful, so maybe put it back in once i have the log here
            
            return;
        }

        
        // once the rays are up and running, move according to them

         bb.isWallFrontStraight = front.isResult();
         bb.isWallLeft45 = left45.isResult();
         bb.isWallRight45 = right45.isResult();
         bb.isWallLeft90 = left90.isResult();
         bb.isWallRight90 = right90.isResult();
        // bb.isWallFrontUp = front_up.isResult();
        // bb.isWallFrontDown = front_down.isResult();

        // is any of the sensor signalig?
        bb.sensor = bb.isWallFrontStraight || bb.isWallLeft45 || bb.isWallRight45 || bb.isWallLeft90 || bb.isWallRight90;
    }
    
}
