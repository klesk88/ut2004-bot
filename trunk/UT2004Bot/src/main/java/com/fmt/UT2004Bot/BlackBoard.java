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
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.*;

/**
 *
 * @author klesk
 */
public class BlackBoard {

    //The Weapons our bot might use
    public enum WeaponsUsed {

        ASSAULT_RIFLE_Prim, FLAK_CANNON_Prim, MINIGUN_Prim
    }
    private static BlackBoard instance = null;
    public boolean follow_player = false;
    public boolean nav_point_navigation = false;
    public boolean bot_killed = false;
    public boolean player_visible = false;
    // this is a linked map to get the health packs in order of value when iterating the map
    private Map<UnrealId, Item> itemMapHealth = new LinkedHashMap<UnrealId, Item>();
    // TODO: player shoudl be private, and only be set by a target selection!!!
    public Player player = null;
    int lastKnownDeathValue = 0;
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
    private Predictor predictor = null;
    
    // GOAP
    boolean replan = false;
    Stack<Action> currentPlan;
    /**
     * Whether any of the sensor signalize the collision. (Computed in the
     * doLogic())
     */
    public boolean sensor = false;

    private BlackBoard() {
        
        predictor = new Predictor();
        predictor.init();
        
    }

    public static BlackBoard getInstance() {
        if (instance == null) {
            instance = new BlackBoard();
        }
        return instance;
    }

    // update the current world state
    public void update() {

        if (BotLogic.getInstance().getSenses().hasDied()) {
            cleanUpBlackBoardAfterDeath();
        }
        
        updateAmmoPriorities();
        
        predictor.calculatePosition(player);
        //System.out.println("noooooooooooooo");
        
    }

    private void targetManaging()
    {
    
    }
    
    private void cleanUpBlackBoardAfterDeath() {
        targetPos = null;
        predictedEnemyPosition = null;
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
     *
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

    /**
     * Find best health pack location. 
     * 
     * Preference with 1 being most important:
     * (1) Prefers spawned over not spawned
     * (2) Prefers visible over invisible
     * (3) Prefers strong over weak
     *
     * @return location of a health pack. null if there is no health pack on the map
     */
    public Location getBestHealthPackLocation() {

        Location bestLocation = null;

        itemMapHealth.clear();
        itemMapHealth.putAll(BotLogic.getInstance().getItems().getSpawnedItems(ItemType.SUPER_HEALTH_PACK));
        itemMapHealth.putAll(BotLogic.getInstance().getItems().getSpawnedItems(ItemType.HEALTH_PACK));
        itemMapHealth.putAll(BotLogic.getInstance().getItems().getSpawnedItems(ItemType.MINI_HEALTH_PACK));

        // if no health pack was spawned simply look up all potential health packs
        if (itemMapHealth.isEmpty()) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("No Health Pack in black board health item map");
            itemMapHealth.putAll(BotLogic.getInstance().getItems().getAllItems(ItemType.SUPER_HEALTH_PACK));
            itemMapHealth.putAll(BotLogic.getInstance().getItems().getAllItems(ItemType.HEALTH_PACK));
            itemMapHealth.putAll(BotLogic.getInstance().getItems().getAllItems(ItemType.MINI_HEALTH_PACK));
            if (itemMapHealth.isEmpty()) {
                BotLogic.getInstance().writeToLog_HackCosIMNoob("No Health Pack in map?");
                return null;
            }
        }

        Iterator it = itemMapHealth.keySet().iterator();

        while (it.hasNext()) {
            UnrealId value = (UnrealId) it.next();
            if (itemMapHealth.get(value).isVisible()) {
                return itemMapHealth.get(value).getLocation();
            }
        }
        // if no health pack is visible look for another one
        it = itemMapHealth.keySet().iterator();
        while (it.hasNext()) {
            UnrealId value = (UnrealId) it.next();
            return itemMapHealth.get(value).getLocation();

        }

        // this will be null if no health pack has been dropped
        return bestLocation;
    }

    public Location predictLocationForWeapon(WeaponsUsed desiredWeapon) {
        //call francescos method here
    
        
        return predictor.getPredictedLocation();
    }
    
    public Location lerp(Location first_location, Location second_location, float weight){
        
        Location returend_location = new Location(0,0,0);
        
        returend_location.setX(first_location.getX() + weight*(second_location.getX() - first_location.getX()));
        returend_location.setY(first_location.getY() + weight*(second_location.getY() - first_location.getY()));
        returend_location.setZ(first_location.getZ() + weight*(second_location.getZ() - first_location.getZ()));
        
        
        return returend_location;
    }
    
}
