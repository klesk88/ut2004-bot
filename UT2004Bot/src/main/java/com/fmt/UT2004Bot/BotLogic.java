/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import java.util.logging.Level;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

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
public class BotLogic extends UT2004BotModuleController {

    private MovementLogic ml = null;
    private BlackBoard bb = BlackBoard.getInstance();
    private Sensors sensor = null;

    @Override
    public void prepareBot(UT2004Bot bot) {

        sensor = new Sensors(bot);
        ml = new MovementLogic(bot);
        
        /*
         * sensors initialization
         */
        this.sensor.navigation = navigation;
        this.sensor.config = config;
        this.sensor.log = log;
        this.sensor.world = world;
        this.sensor.info = info;
        this.sensor.players = players;
        this.sensor.act = act;
        this.sensor.body = body;
        this.sensor.combo = combo;
        this.sensor.ctf = ctf;
        this.sensor.descriptors = descriptors;
        this.sensor.game = game;
        this.sensor.items = items;
        this.sensor.listenerRegistrator = listenerRegistrator;
        this.sensor.navBuilder = navBuilder;
        this.sensor.random = random;
        this.sensor.senses = senses;
        this.sensor.shoot =shoot;
        this.sensor.stats = stats;
        this.sensor.visibility = visibility;
        this.sensor.weaponPrefs = weaponPrefs;
        this.sensor.weaponry = weaponry;
        
        /*
         * movement initialization
         */
        this.ml.pathExecutor = pathExecutor;
        this.ml.navigation = navigation;
        this.ml.world = world;
        this.ml.pathPlanner = pathPlanner;
        this.ml.fwMap = fwMap;
        this.ml.log = log;
        this.ml.raycasting = raycasting;
        this.ml.runStraight = runStraight;
        this.ml.getBackToNavGraph = getBackToNavGraph;
        this.ml.move = move;
        
        ml.init();
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

        navigation.getLog().setLevel(Level.INFO);
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
        pathExecutor.getLog().setLevel(Level.ALL);
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

        log.info("Enter Logic");
        this.sensor.updateMovement();
        this.ml.movementSelection();
    }
}
