/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.ActionManager;
import MTC.MTC;
import java.util.logging.Level;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map.
 *
 * <p><p> BotLogic is able to handle movers as well as teleporters.
 *
 * <p><p> It also implements player-following, that is, if it sees a player, it
 * will start to navigate to it.
 *
 * <p><p> We recommend you to try it on map DM-1on1-Albatross or CTF-LostFaith
 * or DM-Flux2.
 *
 * <p><p> This bot also contains an example of {@link TabooSet} usage.
 *
 * <p><p> BotLogic also instantiates {@link UT2004PathAutoFixer} that
 * automatically removes bad-edges from navigation graph of UT2004. Note that
 * Pogamut bot's cannot achieve 100% safe navigation inside UT2004 maps mainly
 * due to edges that does not contain enough information on how to travel them,
 * we're trying our best, but some edges inside navigation graph exported from
 * UT2004 cannot be traveled with our current implementation.
 *
 * @author Rudolf Kadlec aka ik
 * @author Jakub Gemrot aka Jimmy
 */
@AgentScoped
public class BotLogic extends UT2004BotModuleController<UT2004Bot> {
   
    private MovementLogic ml = null;
    private BlackBoard bb;
    private Sensors sensor = null;
    public static BotLogic instance;
  
    public BotLogic() {
        // Exists only to defeat instantiation.
        instance = this;
        
    }

    public static BotLogic getInstance() {
       
        return instance;
    }
    
    
    @Override
    public void prepareBot(UT2004Bot bot) {
        
        sensor = new Sensors();
        ml = MovementLogic.getInstance();
        
         bb = BlackBoard.getInstance();

        log.info("bot Initialize");
          
    }
    
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("eftBot");
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @SuppressWarnings("unchecked")
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange config, InitedMessage init) {
        
        ml.init();
        sensor.init();
        navigation.getLog().setLevel(Level.OFF);
        ActionManager.getInstance().init();
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // receive logs from the navigation so you can get a grasp on how it is working
        pathExecutor.getLog().setLevel(Level.OFF);
 
    }

    
    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
    }
    
     /**
     * Called each time our bot die. Good for reseting all bot state dependent
     * variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
        bb.bot_killed = true;
        bb.currentPlan.clear();
    }
    
    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     */
    @Override
    public void logic() {

     
       
        this.sensor.update();
        log.info(("before weapon"));
        WeaponConfidence.getInstance().updateWeaponConfidence();
        this.bb.update();
         log.info(("after bb"));
         TargetManager.getInstance().update();
         log.info(("after replan"));
       
        // this.targetManager.update();
        // target manager also calls GOAPPlanner
        
        
        //TODO this should be removed
         
        this.sensor.updateMovement();
         log.info(("after movement"));
       
        //TODO this should be removed as well
        //for use the raycast part (bugged for the moment) call the 
        //method this.ml.raycast(); instead of this one
        this.ml.raycast();
         
          BotLogic.getInstance().writeToLog_HackCosIMNoob(" exit raycast");
        
        ActorSystem.getInstance().update();
        
        //this should handle navigation and action performance (e.g. shooting)
        //this.actorSystem.update();
        
    }
    
    public void writeToLog_HackCosIMNoob(String string)
    {
        log.info(string);
    }
    

}
