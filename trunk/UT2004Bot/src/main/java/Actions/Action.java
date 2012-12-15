/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.WorldState;
import java.util.logging.Level;

/**
 *
 * @author Tilman, Klesk
 */
public interface Action 
{
    public enum ActionResult {Success, Failed, Running}
    
    public enum TypeOfAction{FlackCannonAmmo, FindHealt, FindLighningGunAmmo, FindMinigunGunAmmo,
    FindRocketGunAmmo, FindShockgunGunAmmo, FlackCannonGun, GrenadeThrowing,
    LightningGun, MiniGun, RandomWak, RetreatWithSuppressionFIre, RocketLuncher,
    SearchAdrenaline, ShockGunNuke, UseAdrenaline
    }
  
    //pre - post conditionss
    
  
   
    /**
     * Apply post-conditions to the (copy of the) world state
     */
    public WorldState.TruthStates[] GetPostCondtionsArray();
    
    //public void UndoPostConditions();
    
  
    
    /**
     * Apply the pre conditions to the goal state of the world for add them in the search
     * @param goal_state previous goal_state of the world
     * @return new goal_state of the world
     */
    // public WorldState.TruthStates[] applyPreConditions(WorldState.TruthStates[] goal_state);
    
    public WorldState.TruthStates[] getPreConditionArray();
    
    public ActionResult executeAction();
    
    public float getConfidence();
    
    public void setConfidence(float confidence_value);
    
    public TypeOfAction getTypeOfAction();
            
    
}
