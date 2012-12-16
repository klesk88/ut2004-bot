/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.ActionManager;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 *
 * @author klesk
 */
public class WeaponConfidence {

    private static WeaponConfidence instance = null;

    private WeaponConfidence() {
    }

    public static WeaponConfidence getInstance() {
        if (instance == null) {
            instance = new WeaponConfidence();
        }
        return instance;
    }

    public void updateWeaponConfidence() {
        int size = ActionManager.getInstance().getActionsAvailable().size();

        for (int i = 0; i < size; i++) {
            switch (ActionManager.getInstance().getActionsAvailable().get(i).getTypeOfAction()) {

                case FindHealt:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(0.1f);
                    break;
                case FindLinkGunAmmo:
                     ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.LINK_GUN, ItemType.LINK_GUN_AMMO, 0.055f));
                    break;
                case FlackCannonAmmo:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.FLAK_CANNON, ItemType.FLAK_CANNON_AMMO, 0.06f));
                    break;
                case FindLighningGunAmmo:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.LIGHTNING_GUN, ItemType.LIGHTNING_GUN_AMMO, 0.04f));
                    break;
                case FindMinigunGunAmmo:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.MINIGUN, ItemType.MINIGUN_AMMO, 0.05f));
                    break;
                case FindRocketGunAmmo:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.ROCKET_LAUNCHER, ItemType.ROCKET_LAUNCHER_AMMO, 0.07f));
                    break;
                case FindShockgunGunAmmo:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleAmmoValue(ItemType.SHOCK_RIFLE, ItemType.SHOCK_RIFLE_AMMO, 0.09f));
                    break;
                case FlackCannonGun:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.FLAK_CANNON, ItemType.FLAK_CANNON_AMMO, 0.6f));
                    break;
                case GrenadeThrowing:

                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.ASSAULT_RIFLE, ItemType.ASSAULT_RIFLE_AMMO, 0.2f));
                    break;
                case LightningGun:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.LIGHTNING_GUN, ItemType.LIGHTNING_GUN_AMMO, 0.4f));
                    break;
                case MiniGun:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.MINIGUN, ItemType.MINIGUN_AMMO, 0.5f));
                    break;
                case RocketLuncher:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.ROCKET_LAUNCHER, ItemType.ROCKET_LAUNCHER_AMMO, 0.7f));
                    break;
                    
                case ShockGunNuke:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.SHOCK_RIFLE, ItemType.SHOCK_RIFLE_AMMO, 0.9f));
                    break;
                case LinkGun:
                      ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(
                            updateSingleWeaponValue(ItemType.LINK_GUN, ItemType.LINK_GUN_AMMO, 0.55f));
                    break;
                case RandomWak:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(0.1f);
                    break;
                case RetreatWithSuppressionFIre:
                   ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(0.1f);
                    break;
              
                case SearchAdrenaline:
                   ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(0.1f);
                    break;
               
                case UseAdrenaline:
                    ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(0.1f);
                    break;
                
                
            }
        }
    }

    private float updateSingleAmmoValue(ItemType weapon, ItemType ammo, float value) {
        if (BotLogic.getInstance().getWeaponry().hasAmmo(ammo)) {
             value = value - (value / 3);
        }
        if (BotLogic.getInstance().getWeaponry().hasWeapon(weapon)) {
             value = value - (value / 2);
        }
        return value;
    }

    private float updateSingleWeaponValue(ItemType weapon, ItemType ammo, float value) {
        if (!BotLogic.getInstance().getWeaponry().hasWeapon(weapon)) {
            return 0.0f;
        }
        if (!BotLogic.getInstance().getWeaponry().hasAmmo(ammo)) {
            return 0.01f;
        }
        return value;
    }
}
