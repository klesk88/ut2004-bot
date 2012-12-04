/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.Action;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.*;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.*;

/**
 *
 * @author klesk
 */
public class BlackBoard {

    //The Weapons our bot might use
    public enum WeaponsUsed {ASSAULT_RIFLE_Prim, FLAK_CANNON_Prim,MINIGUN_Prim }
   
    private static BlackBoard instance = null;
    public boolean follow_player = false;
    public boolean nav_point_navigation = false;
    public boolean bot_killed = false;
    public boolean player_visible = false;
    public Player player = null;
    public double player_distance = Double.MAX_VALUE;
    public Location targetPos;
    public Location predictedEnemyPosition;
    public boolean isWallRight45 = false;
    public boolean isWallRight90 = false;
    public boolean isWallLeft45 = false;
    public boolean isWallLeft90 = false;
    public boolean isWallFrontStraight = false;
    public boolean isWallFrontUp = false;
    public boolean isWallFrontDown = false;
    public ItemType[] mostDesiredAmmunition = new ItemType[3];
    // GOAP
    boolean replan = false;
    Stack<Action> currentPlan;
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

    // update the current world state
    public void update() {

        updateAmmoPriorities();

        updateWorldState();
        BotLogic.getInstance().writeToLog_HackCosIMNoob("world state updated");
        
        WorldState.getInstance().setGoalState(WorldState.GoalStates.KillEnemy);
    }

    private void updateWorldState()
    {
        if (player_visible) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, true);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, false);
        }

        if (hasSuppressionAmmo()) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, true);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, false);
        }

        if (player != null) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, true);
        }
    }
    
    private boolean hasSuppressionAmmo() {
        boolean result = false;

        if (BotLogic.getInstance().getWeaponry().getWeaponDescriptor(ItemType.ASSAULT_RIFLE).getPriMaxAmount() > 10) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().getWeaponDescriptor(ItemType.FLAK_CANNON).getPriMaxAmount() > 10) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().getWeaponDescriptor(ItemType.MINIGUN).getPriMaxAmount() > 10) {
            result = true;
        }

        return result;
    }

    /**
     * Attempt to priorize ammo based on current ammo and weaponery
     */
    private void updateAmmoPriorities() {
        Map<Float, ItemType> priorities = new HashMap<Float, ItemType>();

        float priorityRatio_ASSAULT_RIFLE = 1.0f;
        float priorityRatio_FLAK_CANNON = 0.5f;
        float priorityRatio_MINIGUN = 2.0f;

        float priority_ASSAULT_RIFLE = calculatePriorityForWeapon(priorityRatio_ASSAULT_RIFLE, ItemType.ASSAULT_RIFLE);
        priorities.put(priority_ASSAULT_RIFLE, ItemType.ASSAULT_RIFLE);

        float priority_FLAK_CANNON = calculatePriorityForWeapon(priorityRatio_FLAK_CANNON, ItemType.FLAK_CANNON);
        priorities.put(priority_FLAK_CANNON, ItemType.FLAK_CANNON);

        float priority_MINIGUN = calculatePriorityForWeapon(priorityRatio_MINIGUN, ItemType.MINIGUN);
        priorities.put(priority_MINIGUN, ItemType.MINIGUN);

        Map.Entry<Float, ItemType> maxEntry = null;

        for (int i = 0; i < mostDesiredAmmunition.length; i++) {

            for (Map.Entry<Float, ItemType> entry : priorities.entrySet()) {
                if (maxEntry == null || entry.getKey().compareTo(maxEntry.getKey()) > 0) {
                    maxEntry = entry;
                }
            }

            mostDesiredAmmunition[i] = maxEntry.getValue();
            priorities.remove(maxEntry.getKey());
        }
    }

    /**
     * Use only for weapons! Returns (max - current ammo) * priorityRatio
     * @param priorityRatio
     * @param item
     * @return
     */
    private float calculatePriorityForWeapon(float priorityRatio, ItemType item) {
        float calculatedPriority = BotLogic.getInstance().getWeaponry().getWeaponDescriptor(item).getPriMaxAmount();

        if (BotLogic.getInstance().getWeaponry().hasWeapon(item)) {
            calculatedPriority =
                    (BotLogic.getInstance().getWeaponry().getWeaponDescriptor(item).getPriMaxAmount()
                    - BotLogic.getInstance().getWeaponry().getAmmo(item))
                    * priorityRatio;
        }

        BotLogic.getInstance().writeToLog_HackCosIMNoob("priority: " + calculatedPriority + " for " + item.getName()
                + " Max ammo: " + BotLogic.getInstance().getWeaponry().getWeaponDescriptor(item).getPriMaxAmount()
                + " cur ammo: " + BotLogic.getInstance().getWeaponry().getAmmo(item));
        return calculatedPriority;
    }

    public Location predictLocationForWeapon(WeaponsUsed desiredWeapon) {
        //call francescos method here

        return null;
    }
}
