/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.logging.Level;

/**
 *
 * @author klesk, Tilman
 */
public class MovementLogic {

    private static MovementLogic instance = null;

    public static MovementLogic getInstance() {
        if (instance == null) {
            instance = new MovementLogic();
        }
        return instance;
    }
    
    
    private UT2004PathAutoFixer autoFixer = null;
  
     /*
     * Taboo set is working as "black-list", that is you might add some
     * NavPoints to it for a certain time, marking them as "unavailable".
     *
     *
     */
    public TabooSet<NavPoint> tabooNavPoints;
    /**
     * Shortcut for the {@link UT2004Bot#getWorldView()}.
     */

    /**
     * Current navigation point we're navigating to.
     */
    private NavPoint targetNavPoint;

    
    private boolean moving = false;

    /**
     * How much time should we wait for the rotation to finish (milliseconds).
     */
    private int turnSleep = 250;
    /**
     * How fast should we move? Interval <0, 1>.
     */
    private float moveSpeed = 0.6f;
    /**
     * Small rotation (degrees).
     */
    private int smallTurn = 30;
    /**
     * Big rotation (degrees).
     */
    private int bigTurn = 90;

    /*
     * Raycast part finish
     */

    private BlackBoard bb = null;

    
    private MovementLogic() {
        //this.log.info("Inside contructor");
     
        //this.controller = (UT2004BotModuleController) bot.getController();
        this.bb = BlackBoard.getInstance();

    }

    public void init() {

        /*
         * Navpoints
         */
        // initialize taboo set where we store temporarily unavailable navpoints
    
        tabooNavPoints = new TabooSet<NavPoint>(BotLogic.getInstance().getBot());

        // add stuck detector that watch over the path-following, if it (heuristicly) finds out that the bot has stuck somewhere,
        // it reports an appropriate path event and the path executor will stop following the path which in turn allows 
        // us to issue another follow-path command in the right time
        BotLogic.getInstance().getPathExecutor().addStuckDetector(new UT2004TimeStuckDetector(BotLogic.getInstance().getBot(), 3000, 10000)); // if the bot does not move for 3 seconds, considered that it is stuck
        BotLogic.getInstance().getPathExecutor().addStuckDetector(new UT2004PositionStuckDetector(BotLogic.getInstance().getBot())); // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        BotLogic.getInstance().getPathExecutor().addStuckDetector(new UT2004DistanceStuckDetector(BotLogic.getInstance().getBot())); // watch over distances to target
      
        // auto-removes wrong navigation links between navpoints
        autoFixer = new UT2004PathAutoFixer(BotLogic.getInstance().getBot(),(IUT2004PathExecutor) BotLogic.getInstance().getPathExecutor(),  BotLogic.getInstance().getFwMap(), BotLogic.getInstance().getNavBuilder());



        // IMPORTANT
        // adds a listener to the path executor for its state changes, it will allow you to 
        // react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
         BotLogic.getInstance().getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue.getState());
            }
        });

        // change bot's default speed
         BotLogic.getInstance().getConfig().setSpeedMultiplier(moveSpeed);

        // IMPORTANT:
        // The most important thing is this line that ENABLES AUTO TRACE functionality,
        // without ".setAutoTrace(true)" the AddRay command would be useless as the bot won't get
        // trace-lines feature activated
         BotLogic.getInstance().getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));

        // FINAL NOTE: the ray initialization must be done inside botInitialized method or later on inside
        //             botSpawned method or anytime during doLogic method
         BotLogic.getInstance().getNavigation().getLog().setLevel(Level.INFO);
    }

    public void movementSelection() {
        
        if (bb.bot_killed) {
            BotLogic.getInstance().getNavigation().stopNavigation();
            bb.bot_killed = false;
        }
        BotLogic.getInstance().getLog().info(" after bot kille");
        handleBlackboardNavigation();
                /*
        if (this.bb.follow_player) {
            handlePlayerNavigation();
        } else {
            handleNavPointNavigation();
        }
        * */
        
    }
   
    private void handleBlackboardNavigation()
    {
        
        if(!bb.follow_player && bb.perform_taunt)
        {
             BotLogic.getInstance().getNavigation().stopNavigation();
             BotLogic.getInstance().getBody().getAction().playAnimation("Gesture_Taunt01");
             bb.perform_taunt = false;
             return;
        }
        else
        {
            bb.perform_taunt = false;
        
        }
        
        if(bb.randomWalk)
        {
            
              BotLogic.getInstance().getLog().info(" random walk");
            BotLogic.getInstance().getMove().setSpeed(0.9);
             //getRandomFightingPoint();
           handleNavPointNavigation();
           return;
        }
        
        //if the bot heard a sound and doesn´t see a player, turn to face him
       if(( bb.is_damaged) && !bb.follow_player)
       {
           BotLogic.getInstance().getMove().turnHorizontal(180);
           return;
       }
        
      if(bb.follow_player && bb.player!=null)
      {
          //BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
           
          BotLogic.getInstance().getMove().setSpeed(0.8);
          handlePlayerNavigation();
          return;
      }
      BotLogic.getInstance().getLog().info(" go to target point");
       //BotLogic.getInstance().getPathExecutor().setFocus( bb.targetPos);
      //getRandomFightingPoint();
      BotLogic.getInstance().getMove().setSpeed(0.9);
      BotLogic.getInstance().getNavigation().navigate(bb.targetPos);
    }
    
    
    
      private void handlePlayerNavigation() {
       // BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
         BotLogic.getInstance().getLog().info(" follow player");
         if(bb.is_bumping)
         {
              BotLogic.getInstance().getMove().dodgeBack( BotLogic.getInstance().getInfo().getLocation(), bb.bumping_position);
              return;
         }
         
        if(bb.see_incoming_projectile || bb.is_damaged)
        {
            BotLogic.getInstance().getNavigation().stopNavigation();
            int random_number = (int)(Math.random()*100);
            if(random_number < 30)
                BotLogic.getInstance().getMove().strafeRight(90, bb.player.getLocation());
            else
                if(random_number < 70)
                    BotLogic.getInstance().getMove().strafeLeft(90, bb.player.getLocation());
            else
                 BotLogic.getInstance().getMove().doubleJump();
          
            return;
        }
       // BotLogic.getInstance().getSenses().isBumping();
        
        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another player to navigate to

        //Player player = bb.player;
//        if (player == null) {
//            // NO PLAYERS AT SIGHT
//            // => navigate to random navpoint
//            handleNavPointNavigation();
//            return;
//        }

        // CHECK DISTANCE TO THE PLAYER ...
        if (BotLogic.getInstance().getInfo().getLocation().getDistance(bb.player.getLocation()) < 15) {
             BotLogic.getInstance().getNavigation().stopNavigation();
            int random_number = (int)(Math.random()*100);
            if(random_number < 30)
                BotLogic.getInstance().getMove().strafeRight(90, bb.player.getLocation());
            else
                if(random_number < 70)
                    BotLogic.getInstance().getMove().strafeLeft(90, bb.player.getLocation());
            else
                 BotLogic.getInstance().getMove().doubleJump();
          
            return;
        }
        
          if (BotLogic.getInstance().getNavigation().isNavigating() && BotLogic.getInstance().getNavigation().getCurrentTargetPlayer() != null) {
            // WE'RE NAVIGATING TO SOME PLAYER
            
            return;
        }
        

        BotLogic.getInstance().getNavigation().navigate(bb.player);
        
    }

     
//     private void getRandomFightingPoint() {
//        //BotLogic.getInstance().getLog().info("Picking new target navpoint.");
//
//        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
//        //NavPoint chosen = MyCollections.getRandomFiltered(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values(), tabooNavPoints);
//          if(BotLogic.getInstance().getNavigation().isNavigating() && BotLogic.getInstance().getNavigation().getRemainingDistance() >10 && !BotLogic.getInstance().getSenses().isBumping())
//          {
//                return ;
//          }
//          
//          int random_action = (int)Math.random()*2;
//          Location random_position;
//          if(random_action>1)
//          {
//            //BotLogic.getInstance().getMove().strafeLeft(30, UnrealId.NONE);
//                random_position = new Location( this.initial_location.x +Math.random()*50 , 0 , this.initial_location.z + Math.random() * 50 );
//          }
//          else
//          {
////            BotLogic.getInstance().getMove().strafeRight(30, UnrealId.NONE);
//                random_position = new Location( this.initial_location.x - Math.random()*50 , 0 ,  this.initial_location.z - Math.random() * 50 );
//          }
//         //Location random_position = new Location(Math.random()*50 + this.initial_location.x, 0 , Math.random() * 50 + this.initial_location.z);
//         BotLogic.getInstance().getNavigation().
//         IPathFuture future_path = BotLogic.getInstance().getPathPlanner().computePath( BotLogic.getInstance().getBot().getLocation(), (random_position));
//         BotLogic.getInstance().getNavigation().navigate(future_path);
//         return ;
//        //BotLogic.getInstance().getLog().warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");
//
//        // ok, all navpoints have been visited probably, try to pick one at random
//        //return MyCollections.getRandom(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values());
//    
//     }

    private void handleNavPointNavigation() {
        
       
      
        if (BotLogic.getInstance().getNavigation().isNavigating()) {
            // IS TARGET CLOSE & NEXT TARGET NOT SPECIFIED?
            while (BotLogic.getInstance().getNavigation().getContinueTo() == null && BotLogic.getInstance().getNavigation().getRemainingDistance() < 400) {
                // YES, THERE IS NO "next-target" SET AND WE'RE ABOUT TO REACH OUR TARGET!
                BotLogic.getInstance().getNavigation().setContinueTo(getRandomNavPoint());
                // note that it is WHILE because navigation may immediately eat up "next target" and next target may be actually still too close!
            }

            // WE'RE NAVIGATING TO SOME NAVPOINT
            //logNavigation();
            return;
        }



        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another navpoint to navigate to
        // => possibly follow some players ...

        targetNavPoint = getRandomNavPoint();
        if (targetNavPoint == null) {
             BotLogic.getInstance().getLog().info("COULD NOT CHOOSE ANY NAVIGATION POINT TO RUN TO!!!");
            if (BotLogic.getInstance().getWorld().getAll(NavPoint.class).size() == 0) {
               BotLogic.getInstance().getLog().info("world.getAll(NavPoint.class).size() == 0, there are no navigation ponits to choose from! Is exporting of nav points enabled in GameBots2004.ini inside UT2004?");
            }

            return;
        }

 
      //  BotLogic.getInstance().getPathExecutor().setFocus((targetNavPoint.getLocation()));
       BotLogic.getInstance().getNavigation().navigate(targetNavPoint);
        //logNavigation();
    }

    private void logNavigation() {
        // log how many navpoints & items the bot knows about and which is visible    	
        if (BotLogic.getInstance().getNavigation().getCurrentTargetPlayer() != null) {
            BotLogic.getInstance().getLog().info("Pursuing player:    " + BotLogic.getInstance().getNavigation().getCurrentTargetPlayer());
        } else {
            BotLogic.getInstance().getLog().info("Navigating to:      " + BotLogic.getInstance().getNavigation().getCurrentTarget());
        }
        int pathLeftSize = BotLogic.getInstance().getPathExecutor().getPath() == null ? 0 : BotLogic.getInstance().getPathExecutor().getPath().size() - BotLogic.getInstance().getPathExecutor().getPathElementIndex();
        BotLogic.getInstance().getLog().info("Path points left:   " + pathLeftSize);
        if (pathLeftSize != 0) {
            BotLogic.getInstance().getLog().info("Remaining distance: " + BotLogic.getInstance().getNavigation().getRemainingDistance());
        }
        BotLogic.getInstance().getLog().info("Visible navpoints:  " + BotLogic.getInstance().getWorld().getAllVisible(NavPoint.class).size() + " / " + BotLogic.getInstance().getWorld().getAll(NavPoint.class).size());

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
                tabooNavPoints.add(targetNavPoint,8);
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
       // BotLogic.getInstance().getLog().info("Picking new target navpoint.");

        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
        NavPoint chosen = MyCollections.getRandomFiltered(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values(), tabooNavPoints);

        if (chosen != null) {
            return chosen;
        }

        //BotLogic.getInstance().getLog().warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

        // ok, all navpoints have been visited probably, try to pick one at random
        return MyCollections.getRandom(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values());
    }

    public void raycast() {
        
        
       
        if (!bb.sensor) {
            // no sensor are signalizes - just proceed with forward movement
           // BotLogic.getInstance().getLog().info("MOVEMENT");
            movementSelection();
            return;
        }
        movementSelection();
        return;
//           moving = true;
//        // some sensor/s is/are signaling
//
//        // if we're moving
//        if (moving) {
//            // stop it, we have to turn probably
//            BotLogic.getInstance().getMove().stopMovement();
//            moving = false;
//        }
//        
//        BotLogic.getInstance().getLog().info("Inside Raycast ");
//       
//        // according to the signals, take action...
//        // 8 cases that might happen follow
//        if (bb.isWallFrontStraight) {
//            if (bb.isWallLeft45) {
//                if (bb.isWallRight45) {
//                    // LEFT45, RIGHT45, FRONT are signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(bigTurn);
//                } else {
//                    // LEFT45, FRONT45 are signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(smallTurn);
//                }
//            } else {
//                if (bb.isWallRight45) {
//                    // RIGHT45, FRONT are signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(-smallTurn);
//                } else {
//                    // FRONT is signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(180);
//                }
//            }
//        } else {
//            if (bb.isWallLeft45) {
//                if (bb.isWallRight45) {
//                    // LEFT45, RIGHT45 are signaling
//                    movementSelection();
//                     getRandomNavPoint();
//                } else {
//                    // LEFT45 is signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(smallTurn);
//                }
//            } else {
//                if (bb.isWallRight45) {
//                    // RIGHT45 is signaling
//                    BotLogic.getInstance().getMove().turnHorizontal(-smallTurn);
//                } else {
//                    // no sensor is signaling
//                    movementSelection();
//                     getRandomNavPoint();
//                }
//            }
//        }
      
    }


}
