/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BotLogic;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author klesk
 */
public class ActionManager {
    
    private List<Action> actions_available;
    private static ActionManager instance = null;
   
 
    private ActionManager(){
        actions_available = new LinkedList<Action>();

    }
    
    
    public static ActionManager getInstance() {
        if (instance == null) {
            instance = new ActionManager();
        }
        return instance;
    }
    
    public void init()
    {
       
         
       new Action_RandomWalk();
        new Action_ShockGunNuke();
        new Action_FindShockGunAmmo();
         new Action_FindHealth();
        new Action_RetreatWithSuppressionFire();
        new Action_GrenadeThrowing(); 
        new Action_RocketLuncher();
        new Action_FindRocketAmmunition();
        new Action_FindFlackCannonAmmo();
        new Action_FindLightiningGunAmmo();
        new Action_FindMachineGunAmmo();
        new Action_FlackCannonGun();
        new Action_LightiningGun();
        new Action_MachineGun();
        new Action_SearchAdrenaline();
        new Action_UseAdrenaline();
    }
    
    public void addAction(Action action_to_add)
    {
        actions_available.add(action_to_add);
    }
    
    public List<Action> getActionsAvailable()
    {
        return actions_available;
    }
}
