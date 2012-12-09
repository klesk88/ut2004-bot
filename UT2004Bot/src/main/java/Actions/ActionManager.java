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
    
    public void addAction(Action action_to_add)
    {
        actions_available.add(action_to_add);
    }
    
    public List<Action> getActionsAvailable()
    {
        return actions_available;
    }
}
