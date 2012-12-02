/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;


import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;



/**
 *
 * @author klesk, tilman :)
 */
public class Sensors{

    public Sensor_RayCastSystem ray_cast_system;
    
  
    private BlackBoard bb = null;


    
    public Sensors() {

       
       
        bb = BlackBoard.getInstance();

        
        ray_cast_system = new Sensor_RayCastSystem();
        ray_cast_system.init();
    }

    public void update()
    {
        ray_cast_system.update();
    }
    
    
    //TODO: this should not be here!!!
    public void updateMovement() {
        // mark that another logic iteration has began
        //log.info("--- Logic iteration ---");

        if ( BotLogic.getInstance().getPlayers().canSeePlayers() ||  BotLogic.getInstance().getNavigation().getCurrentTargetPlayer() != null) 
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
        
        Player player =  BotLogic.getInstance().getPlayers().getNearestVisiblePlayer();
        if(player != null)
        {
            bb.player_visible = true;
            bb.player=  player;
            bb.player_distance =  BotLogic.getInstance().getInfo().getLocation().getDistance(player.getLocation());
        }
        else
        {
            bb.player_visible = false;
            bb.player = null;
            bb.player_distance = Double.MAX_VALUE;
        }
       
       
    }

   
}
