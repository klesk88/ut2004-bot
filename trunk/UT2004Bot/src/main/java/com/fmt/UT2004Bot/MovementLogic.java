/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.*;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.logging.Level;

/**
 *
 * @author klesk
 */
public class MovementLogic {

    private UT2004PathAutoFixer autoFixer = null;
    private UT2004Bot bot = null;
    /**
     * Support for creating rays used for raycasting (see {@link AutoTraceRay}
     * that is being utilized). <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public Raycasting raycasting;
    /**
     * Executor is used for following a path in the environment. <p><p> May be
     * used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     * <p><p>
     * {@link UT2004PathExecutor#addStuckDetector(cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector)}
     * is initialized with default stuck detectors:
     * {@link UT2004TimeStuckDetector}, {@link UT2004PositionStuckDetector}, {@link UT2004DistanceStuckDetector}.
     * <p><p> If one of stuck detectors (heuristicly) finds out that the bot has
     * stuck somewhere, it reports it back to {@link UT2004PathExecutor} and the
     * path executor will stop following the path switching itself to {@link PathExecutorState#STUCK},
     * which in turn allows us to issue another follow-path command in the right
     * time.
     */
    public IUT2004PathExecutor<ILocated> pathExecutor = null;
    /**
     * Navigation helper that is able to get your bot back to the nearest
     * navigation graph so you can use {@link UT2004BotModuleController#navigation}
     * without fear of catastrophe. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     */
    public IUT2004GetBackToNavGraph getBackToNavGraph;
    /**
     * Navigation helper that can run-straight to some point with stuck
     * detectors. <p><p> May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     */
    public IUT2004RunStraight runStraight;
    /**
     * Shortcut for <i>body.getAdvancedLocomotion()</i> that allows you to
     * manually steer the movement through the environment. <p><p> Note:
     * navigation is done via {@link UT2004BotModuleControllerNew#pathExecutor}
     * that needs {@link PathHandle} from the {@link UT2004BotModuleControllerNew#pathPlanner}.
     * <p><p> May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    public AdvancedLocomotion move;
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
    /**
     * Taboo set is working as "black-list", that is you might add some
     * NavPoints to it for a certain time, marking them as "unavailable".
     *
     *
     */
    private TabooSet<NavPoint> tabooNavPoints;
    /**
     * Shortcut for the {@link UT2004Bot#getWorldView()}.
     */
    public IVisionWorldView world;
    
     /**
     * Planner used to compute the path (consisting of navigation points) inside
     * the map. <p><p> May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called. <p><p> Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     */
    public IPathPlanner<ILocated> pathPlanner = null;
    
    /**
     * Path-planner ({@link IPathPlanner} using {@link NavPoint}s), you may use
     * it to find paths inside the environment wihtout waiting for round-trip of {@link GetPath}
     * command and {@link PathList}s response from UT2004. It is much faster
     * than
     * {@link UT2004BotModuleController#pathPlanner} but you need to pass {@link NavPoint}
     * instances to planner instead of
     * {@link ILocated} ... to find the nearest {@link NavPoint} instance, {@link DistanceUtils}
     * is a handy, check especially
     * {@link DistanceUtils#getNearest(java.util.Collection, ILocated)}.
     */
    public FloydWarshallMap fwMap;
        
    public LogCategory log = null;
    
     /**
     * Current navigation point we're navigating to.
     */
    private NavPoint targetNavPoint;
    private UT2004BotModuleController controller;
  
    private BlackBoard bb = null;

    public MovementLogic(UT2004Bot bot) {
        //this.log.info("Inside contructor");
        this.bot = bot;
        this.controller = (UT2004BotModuleController) bot.getController();
        this.bb = BlackBoard.getInstance();
       
    }
    
    public void init()
    {
         // initialize taboo set where we store temporarily unavailable navpoints
        tabooNavPoints = new TabooSet<NavPoint>(bot);

        // add stuck detector that watch over the path-following, if it (heuristicly) finds out that the bot has stuck somewhere,
        // it reports an appropriate path event and the path executor will stop following the path which in turn allows 
        // us to issue another follow-path command in the right time
        pathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000)); // if the bot does not move for 3 seconds, considered that it is stuck
        pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot)); // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        pathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot)); // watch over distances to target


        //autoFixer = new UT2004PathAutoFixer(bot, pathExecutor, fwMap, navBuilder); // auto-removes wrong navigation links between navpoints
        // auto-removes wrong navigation links between navpoints
        // autoFixer = new UT2004PathAutoFixer(bot,  pathExecutor,  (controller.getFwMap()),  (controller.getNavBuilder()));

        // IMPORTANT
        // adds a listener to the path executor for its state changes, it will allow you to 
        // react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
        pathExecutor.getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue.getState());
            }
        });

        navigation.getLog().setLevel(Level.INFO);
    }
    
    public void movementSelection() {
        if (bb.bot_killed) {
            navigation.stopNavigation();
            bb.bot_killed = false;
        }
        if (this.bb.follow_player) {
            handlePlayerNavigation();
        } else {
            handleNavPointNavigation();
        }
    }

    private void handlePlayerNavigation() {
        if (navigation.isNavigating() && navigation.getCurrentTargetPlayer() != null) {
            // WE'RE NAVIGATING TO SOME PLAYER
            logNavigation();
            return;
        }



        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another player to navigate to

       
        if (bb.player_visible == false) {
            // NO PLAYERS AT SIGHT
            // => navigate to random navpoint
            handleNavPointNavigation();
            return;
        }

        // CHECK DISTANCE TO THE PLAYER ...
        if (bb.player_distance < UT2004Navigation.AT_PLAYER) {
            // PLAYER IS NEXT TO US... 
            // => talk to player			

            return;
        }

        navigation.navigate(bb.player);
        logNavigation();
    }

    private void handleNavPointNavigation() {
        if (navigation.isNavigating()) {
            // IS TARGET CLOSE & NEXT TARGET NOT SPECIFIED?
            while (navigation.getContinueTo() == null && navigation.getRemainingDistance() < 400) {
                // YES, THERE IS NO "next-target" SET AND WE'RE ABOUT TO REACH OUR TARGET!
                navigation.setContinueTo(getRandomNavPoint());
                // note that it is WHILE because navigation may immediately eat up "next target" and next target may be actually still too close!
            }

            // WE'RE NAVIGATING TO SOME NAVPOINT
            logNavigation();
            return;
        }



        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another navpoint to navigate to
        // => possibly follow some players ...

        targetNavPoint = getRandomNavPoint();
        if (targetNavPoint == null) {
            log.severe("COULD NOT CHOOSE ANY NAVIGATION POINT TO RUN TO!!!");
            if (world.getAll(NavPoint.class).size() == 0) {
                log.severe("world.getAll(NavPoint.class).size() == 0, there are no navigation ponits to choose from! Is exporting of nav points enabled in GameBots2004.ini inside UT2004?");
            }

            return;
        }



        navigation.navigate(targetNavPoint);
        logNavigation();
    }

    private void logNavigation() {
        // log how many navpoints & items the bot knows about and which is visible    	
        if (navigation.getCurrentTargetPlayer() != null) {
            log.info("Pursuing player:    " + navigation.getCurrentTargetPlayer());
        } else {
            log.info("Navigating to:      " + navigation.getCurrentTarget());
        }
        int pathLeftSize = pathExecutor.getPath() == null ? 0 : pathExecutor.getPath().size() - pathExecutor.getPathElementIndex();
        log.info("Path points left:   " + pathLeftSize);
        log.info("Remaining distance: " + navigation.getRemainingDistance());
        log.info("Visible navpoints:  " + world.getAllVisible(NavPoint.class).size() + " / " + world.getAll(NavPoint.class).size());
       // log.info("Visible items:      " + (this.controller.getItems()).getVisibleItems().values() + " / " + world.getAll(Item.class).size());
       // log.info("Visible players:    " + players.getVisiblePlayers().size());
    }

    /**
     * Path executor has changed its state (note that {@link UT2004BotModuleController#getPathExecutor()}
     * is internally used by
     * {@link UT2004BotModuleController#getNavigation()} as well!).
     *
     * @param state
     */
    private void pathExecutorStateChange(PathExecutorState state) {
        switch (state) {
            case PATH_COMPUTATION_FAILED:
                // if path computation fails to whatever reason, just try another navpoint
                // taboo bad navpoint for 3 minutes
                tabooNavPoints.add(targetNavPoint, 180);
                break;

            case TARGET_REACHED:
                // taboo reached navpoint for 3 minutes
                tabooNavPoints.add(targetNavPoint, 180);
                break;

            case STUCK:
                // the bot has stuck! ... target nav point is unavailable currently
                tabooNavPoints.add(targetNavPoint, 60);
                break;

            case STOPPED:
                // path execution has stopped
                targetNavPoint = null;
                break;
        }
    }

    /**
     * Randomly picks some navigation point to head to.
     *
     * @return randomly choosed navpoint
     */
    private NavPoint getRandomNavPoint() {
        log.info("Picking new target navpoint.");

        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
        NavPoint chosen = MyCollections.getRandomFiltered(this.world.getAll(NavPoint.class).values(), tabooNavPoints);

        if (chosen != null) {
            return chosen;
        }

        log.warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

        // ok, all navpoints have been visited probably, try to pick one at random
        return MyCollections.getRandom(this.world.getAll(NavPoint.class).values());
    }
}
