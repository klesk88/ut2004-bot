/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Map;

/**
 * Select goals for goap Select target enemies
 *
 * @author Michele, Tilman
 */
public class TargetManager {
    //SINGLETON START
    
    private WorldState.TruthStates[] previous_world_state = null;

    private static TargetManager instance = null;
    private GOAPPlanner goap = null;
    private BlackBoard bb;
    private WorldState.TruthStates[] previous_goal = null;
    private TargetManager() {
        // Exists only to defeat instantiation.
        bb = BlackBoard.getInstance();
        goap = GOAPPlanner.getInstance();
    }

    public static TargetManager getInstance() {
        if (instance == null) {
            instance = new TargetManager();
        }
        return instance;
    }
    //SINGLETON END

    public void init() {
    }

    
    
    public void update() {
        updateWorldState();
        BotLogic.getInstance().writeToLog_HackCosIMNoob("world state updated");

        //
        
         goalSelection();
       
        
          if(this.previous_goal == null)
         {
             
             this.previous_goal = WorldState.getInstance().getActualGoal();
         }
          
            if(this.previous_world_state == null)
         {
             
             this.previous_world_state = WorldState.getInstance().getWorldState();
         }
         
         boolean goal_changed = false;
         boolean world_state_changed = false;
         WorldState.TruthStates[] temp = WorldState.getInstance().getActualGoal();
         for(int i=0;i<this.previous_goal.length;i++)
         {
             if(this.previous_goal[i] != temp[i])
             {
                
                 goal_changed = true;
                 break;
             }
         }
         
        
          
         if(goal_changed)
         {
              this.previous_goal = temp;
             bb.randomWalk=false;
             bb.follow_player = false;
             GOAPPlanner.getInstance().replan();
         }
         
         WorldState.TruthStates[] temp1 = WorldState.getInstance().getWorldState();
         for(int i=0;i<this.previous_world_state.length;i++)
         {
             if(this.previous_world_state[i] != temp1[i])
             {
                
                 world_state_changed = true;
                 break;
             }
         }
         
         if(world_state_changed)
         {
             this.previous_world_state = temp1;
              bb.randomWalk=false;
             bb.follow_player = false;
             GOAPPlanner.getInstance().replan();
         }
        
         BotLogic.getInstance().writeToLog_HackCosIMNoob(" exit world state updated");
    }
    
    private void goalSelection()
    {
         
        if( bb.player_visible && (BotLogic.getInstance().getBot().getSelf().getHealth() > 50) || bb.is_damaged)
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.KillEnemy);
           
            return;
        }
        
        Map<UnrealId, Item> viable_medpacks = BotLogic.getInstance().getItems().getSpawnedItems(ItemType.Category.HEALTH);
        //@Michele: if the bot has less than 50% of healt and there are medpacks
        if (!(BotLogic.getInstance().getBot().getSelf().getHealth() > 50) && viable_medpacks.size()!=0 && !bb.player_visible)
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.Survive);
            return;
        }
        //@Michele: if the bot has less than 50% of healt and there aren't medpacks
        if(BotLogic.getInstance().getBot().getSelf().getHealth() < 50)
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.KillEnemy);
            return;
       }
        int d = BotLogic.getInstance().getWeaponry().getPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE);
        if((BotLogic.getInstance().getWeaponry().getPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE) <50))
        {
             WorldState.getInstance().setGoalState(WorldState.GoalStates.FindWeapons);
             return;
           
        }
        
        int search_pills = (int)(Math.random()*100);
         Map<UnrealId, Item> viable_pills = BotLogic.getInstance().getItems().getSpawnedItems(ItemType.Category.ADRENALINE);
       if(search_pills<80 && (viable_pills.size() != 0))
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.SearchAdrenaline);
            return;
        }
        WorldState.getInstance().setGoalState(WorldState.GoalStates.SearchRandomly);
        return;
    }

    private void updateWorldState() {
        
        boolean player_was_visible = bb.player_visible;
        
        if (bb.player_visible) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, true);
            
            
            
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, false);
          
        }

        if (hasSuppressionAmmo()) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, true);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, false);
        }

        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.SHOCK_RIFLE_AMMO)
                && BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE))) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.ShockGunAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.ShockGunAmmunition, true);
        }
        
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_AMMO)))
                 {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGunAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGunAmmunition, true);
        }
        
          if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.LINK_GUN)))
                 {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLinkGunAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLinkGunAmmunition, true);
        }
        
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_AMMO)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGrenadeAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGrenadeAmmunition, true);
        }
        
          if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.FLAK_CANNON_AMMO)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasFlakAmmo, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasFlakAmmo, true);
        }
          
             if (!(BotLogic.getInstance().getInfo().isAdrenalineSufficient())
                     ) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasAdrenaline, false);
             //WorldState.getInstance().setWSValue(WorldState.Symbols.PerformAdrenalineAction, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasAdrenaline, true);
             //WorldState.getInstance().setWSValue(WorldState.Symbols.PerformAdrenalineAction, true);
        }
          
          if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.LIGHTNING_GUN_AMMO)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLightiningGunAmmo, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLightiningGunAmmo, true);
        }
          
            if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.MINIGUN_AMMO)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasMachineGunAmmo, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasMachineGunAmmo, true);
        }
            
             if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ROCKET_LAUNCHER_AMMO)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasRocketAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasRocketAmmunition, true);
        }

        if (BotLogic.getInstance().getBot().getSelf().getHealth() > 50) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLowHealth, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLowHealth, true);
        }
       
//        if (bb.player != null) {
//            if (!BotLogic.getInstance().getGame().isPlayerDeathsKnown(bb.player.getId())) {
//                WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, false);
//            } else if (BotLogic.getInstance().getGame().getPlayerDeaths(bb.player.getId())
//                    > bb.lastKnownDeathValue) {
//                int c = BotLogic.getInstance().getGame().getPlayerDeaths(bb.player.getId());
//                WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, true);
//                bb.lastKnownDeathValue++;
//            }
//        }
        
        if(player_was_visible && !bb.player_visible)
        {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, true);
        }
        else
        {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, false);
        }

    }

    private boolean hasSuppressionAmmo() {
        boolean result = false;

        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE)) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.FLAK_CANNON)) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.MINIGUN)) {
            result = true;
        }

        return result;
    }
}
