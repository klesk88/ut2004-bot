/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author klesk
 */
public class BlackBoard {

    private static BlackBoard instance = null;
    public boolean follow_player = false;
    public boolean nav_point_navigation = false;
    public boolean bot_killed = false;
    public boolean player_visible = false;
    public Player player = null;
    public double player_distance = Double.MAX_VALUE;
    
    public boolean isWallRight45 = false;
    public boolean isWallRight90 = false;
    public boolean isWallLeft45 = false;
    public boolean isWallLeft90 = false;
    public boolean isWallFrontStraight = false;
    public boolean isWallFrontUp = false;
    public boolean isWallFrontDown = false;
    
        /**
     * Whether any of the sensor signalize the collision. (Computed in the
     * doLogic())
     */
    public boolean sensor = false;
    
    private BlackBoard() {
        // Exists only to defeat instantiation.
    }

    public static BlackBoard getInstance() {
        if (instance == null) {
            instance = new BlackBoard();
        }
        return instance;
    }
}
