/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.ActionManager;

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
    
    
    
    public void updateWeaponConfidence()
    {
        int size = ActionManager.getInstance().getActionsAvailable().size();
    
        for(int i=0; i<size ; i++)
        {
            switch(ActionManager.getInstance().getActionsAvailable().get(i).getTypeOfAction())
            {
                case FlackCannonAmmo:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
                case FindHealt:
                     
                    break;
                case FindLighningGunAmmo:
                        
                    break;
                case FindMinigunGunAmmo:
                   //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                   break;
                case FindRocketGunAmmo:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
               case FindShockgunGunAmmo:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
                case FlackCannonGun:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
                 case GrenadeThrowing:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
                 case LightningGun:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;    
                case MiniGun:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;     
                case RandomWak:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;     
                case RetreatWithSuppressionFIre:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;    
               case RocketLuncher:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;  
               case SearchAdrenaline:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;    
               case ShockGunNuke:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;
               case UseAdrenaline:
                    //ActionManager.getInstance().getActionsAvailable().get(i).setConfidence(1);
                    break;    
            }
        }
    }
}
