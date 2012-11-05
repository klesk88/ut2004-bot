/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AdrenalineCombo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AgentConfig;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.*;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.Visibility;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Random;

/**
 *
 * @author klesk
 */
public class Sensors{

    UT2004Bot bot;
    /**
     * Random number generator that is usually useful to have during decision
     * making.
     */
    public Random random;
    /**
     * Command module that is internally using {@link UT2004PathExecutor} for
     * path-following and {@link FloydWarshallMap} for path planning resulting
     * in unified class that can solely handle navigation of the bot within the
     * environment. <p><p> In contrast to {@link UT2004PathExecutor} methods of
     * this module may be recalled every {@link UT2004BotModuleController#logic()}
     * iteration even with the same argument (which is not true for {@link UT2004PathExecutor#followPath(cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture)}.
     * <p><p> Note that this class is actually initialized with instances of {@link UT2004BotModuleController#pathExecutor}
     * and {@link UT2004BotModuleController#fwMap} so you must take care if
     * using add/remove stuck detectors or reinitilize this property to your
     * liking (you can do that in {@link UT2004BotModuleController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)}
     * method. <p><p> May be used since first {@link UT2004BotModuleController#logic()}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     */
    public IUT2004Navigation navigation;
    public LogCategory log = null;
    /**
     * Memory module specialized on general info about the game - game type,
     * time limit, frag limit, etc. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Game game;
    /**
     * Memory module specialized on general info about the agent whereabouts -
     * location, rotation, health, current weapon, who is enemy/friend, etc.
     * <p><p> May be used since first {@link Self} message is received, i.e,
     * since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public AgentInfo info;
    /**
     * Memory module specialized on whereabouts of other players - who is
     * visible, enemy / friend, whether bot can see anybody, etc. <p><p> May be
     * used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Players players;
    /**
     * Sensory module that provides mapping between {@link ItemType} and {@link ItemDescriptor}
     * providing an easy way to obtain item descriptors for various items in
     * UT2004. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public ItemDescriptors descriptors;
    /**
     * Memory module specialized on items on the map - which are visible and
     * which are probably spawned. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Items items;
    /**
     * Memory module specialized on agent's senses - whether the bot has been
     * recently killed, collide with level's geometry, etc. <p><p> May be used
     * since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Senses senses;
    /**
     * Memory module specialized on info about the bot's weapon and ammo
     * inventory - it can tell you which weapons are loaded, melee/ranged, etc.
     * <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Weaponry weaponry;
    /**
     * Memory module specialized on the agent's configuration inside UT2004 -
     * name, vision time, manual spawn, cheats (if enabled at GB2004). <p><p>
     * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public AgentConfig config;
    /**
     * Wraps all available commands that can be issued to the virtual body of
     * the bot inside UT2004. <p><p> May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public CompleteBotCommandsWrapper body;
    /**
     * Shortcut for <i>body.getAdvancedShooting()</i> that allows you to shoot
     * at opponent. <p><p> Note: more weapon-handling methods are available
     * through {@link UT2004BotModuleControllerNew#weaponry}. <p><p> May be used
     * since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public ImprovedShooting shoot;
    /**
     * Module specialized on CTF games. Enabled only for CTF games, check {@link CTF#isEnabled()}.
     * <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public CTF ctf;
    /**
     * Module for adrenaline combos.
     */
    public AdrenalineCombo combo;
   
    /**
     * Navigation graph builder that may be used to manually extend the
     * navigation graph of the UT2004. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public NavigationGraphBuilder navBuilder = null;
    /**
     * Listener registrator that probes declared methods for the presence of {@link EventListener}, {@link ObjectClassEventListener},
     * {@link ObjectClassListener}, {@link ObjectEventListener} and {@link ObjectListener}
     * annotations and automatically registers them as listeners on a specific
     * events. <p><p> Note that this registrator is usable for 'this' object
     * only! It will work only for 'this' object.
     */
    public AnnotationListenerRegistrator listenerRegistrator;
    /**
     * Weapon preferences for your bot. See {@link WeaponPrefs} class javadoc.
     * It allows you to define preferences for weapons to be used at given
     * distance (together with their firing modes).
     */
    public WeaponPrefs weaponPrefs;
    /**
     * Shortcut for the {@link UT2004Bot#getWorldView()}.
     */
    public IVisionWorldView world;
    /**
     * Shortcut for the {@link UT2004Bot#getAct()}.
     */
    public IAct act;
    /**
     * Module that is providing various statistics about the bot. You may also
     * used it to output these stats (in CSV format) into some file using {@link AgentStats#startOutput(String)}
     * or {@link AgentStats#startOutput(String, boolean)}.
     */
    public AgentStats stats;
    
    /**
     * Module that provides visibility/cover information for the map. <p><p> May
     * be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Visibility visibility;
    /**
     * Instance of the BlackBoard
     */
    private BlackBoard bb = null;

    public Sensors(UT2004Bot bot) {

       
        this.bot = bot;
        bb = BlackBoard.getInstance();
    }

    public void updateMovement() {
        // mark that another logic iteration has began
        log.info("--- Logic iteration ---");

        if (players.canSeePlayers() || navigation.getCurrentTargetPlayer() != null) 
        {
            // we can see some player / is navigating to some point where we lost the player from sight
            // => navigate to player
            bb.follow_player = true;
            bb.nav_point_navigation = false;
        } 
        else 
        {
            // no player can be seen
            // => navigate to navpoint
            bb.follow_player = false;
            bb.nav_point_navigation = true;
        }
        
        Player player = players.getNearestVisiblePlayer();
        if(player != null)
        {
            bb.player_visible = true;
            bb.player=  player;
            bb.player_distance = info.getLocation().getDistance(player.getLocation());
        }
        else
        {
            bb.player_visible = false;
            bb.player = null;
            bb.player_distance = Double.MAX_VALUE;
        }
       
       
    }

   
}
