/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MTC;

/**
 *
 * @author klesk
 */
import Actions.Action;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Node {
	
	private Node parent;
        //variable used for reetrieve tyhe best childs after the MTC have finished
        private Node best_child;
        private double best_child_score;
	private Vector<Node> children;
	private float q;
	private int n;	
        private int number_of_possible_actions;
        private LinkedList<Action> possible_actions;
	private WorldState.TruthStates[] node_state;
        private int number_of_post_conditions;
	private int number_of_simulations;	
	private boolean final_condition_reached;
	private Action action_choose;
        private List<Action> list_of_actions;
        private WorldState.TruthStates[] node_goal;
        
	/**
	 * initialize the class
	 * 

	 */
	protected void init(Action action,int number_of_simulations, WorldState.TruthStates[] initial_state,WorldState.TruthStates[] original_goal)
	{
		q = 0;
		n = 0;
                possible_actions = new LinkedList<Action>();
		parent = null;
		node_state = initial_state;
		this.number_of_simulations = number_of_simulations;
		final_condition_reached = false;
                list_of_actions = new LinkedList<Action>();
		children = new Vector<Node>();	
                this.number_of_post_conditions = -1;
                this.action_choose = action;
                //update the goal of the node applying the action
                
                WorldState.TruthStates[] goal = new WorldState.TruthStates[original_goal.length];
                for(int i=0; i<original_goal.length;i++)
                {
                    goal[i] = original_goal[i];
                }
               this.node_goal  = updateGoalState(goal);
               this.best_child = null;
               this.best_child_score = 0;
	}
	
	
	/**set the parent of the node
	 * 
	 * @param parent node
	*/	 
	protected void setParent(Node parent)
	{
		
		this.parent = parent;
	}
	
	
	
	/**
	 * increase the number of how much time the node wasx visit
	 */
	protected void increaseN()
	{
		this.n++;
	}
	
	/**
	 * modify the total rewarding value
	 * @param delta 
	 */
	protected void setQ(float delta)
	{
		this.q += delta;
	}
	
	/**
	 * 
	 * @return the parent of this node
	 */
	protected Node getParent()
	{
		return parent;
	}
	
	/**
	 * 
	 * @return the total rewarding of the node
	 */
	protected float getQ()
	{
		return q;
	}
	
	/**
	 * 
	 * @return the number of times the node was visit
	 */
	protected int getN()
	{
		return n;
	}
	
	/**
	 * add the children node to the list of childrens of the father
	 * @param children children node
	 */
	protected void setChildren(Node children)
	{
		this.children.add(children);
	}
	
	/**
	 * return how many childrens this parent have
	 * @return
	 */
	protected int getChildrenSize()
	{
		return children.size();
	}
	
	
	/**
	 * return the children pointed by the index of this node
	 * @index index of the node to exztract
	 * @return
	 */
	protected Node getChildren(int index)
	{
		return children.get(index);
	}
	
	/**
	 * set the number of moves that are possible for ms-pacman 
	 */
	private void setNumberOfMovePossible(int number)
	{		
            this.number_of_post_conditions = number;
	}
	
	/**
	 * get the number of post conditions that have to be made from this node
	 * @return
	 */
	protected int getNumberOfPostConditions()
	{
            return number_of_post_conditions;
	}
	
	
        public void setListOfActions(List<Action> action)
        {
            for(int i=0;i<action.size();i++)
            {
                this.list_of_actions.add(action.get(i));
            }
            
            setNumberOfPossibleActions();
        }
	
        /**
         * set the best child
         * @param node 
         */
        protected void setBestChild(Node node, float q, int n)
        {
            double score = (q/n) + 0 * (Math.sqrt((2*Math.log(n))/n));
            
            //if the score of the actual node is bigger i update it. The second condition
            //is check for when there are the nodes that have q = 0.
            if(score > this.best_child_score || this.best_child == null)
            {
                 this.best_child = node;
            }
        }
        
        /**
         * return the best child from this node
         * @return 
         */
        protected Node getBestChild()
        {
            return this.best_child;
        }
        
	/**
         * Get the goal of this node
         * @return 
         */
	protected WorldState.TruthStates[] getNodeGoal()
        {
            return this.node_goal;
        }
	/**
	 * return the action of this node
	 * @return
	 */
	protected Action getAction()
	{
           return action_choose;
	}
        
        private void setNumberOfPossibleActions()
        {
          
            
            //check which action satisfied the passed goal state
            for(int i=0; i<list_of_actions.size();i++)
            {
                WorldState.TruthStates[] worldStateSim =
                        WorldState.getInstance().applyPostConditionOfAction(this.node_state, list_of_actions.get(i).GetPostCondtionsArray());
                   
                if(WorldState.getInstance().IsWorldStateAGoal(worldStateSim, node_goal))
                {
                    possible_actions.add(list_of_actions.get(i));
                    //index = i;
                   // break;
                }
            }
            
            number_of_possible_actions = possible_actions.size();                  
        }
        
        protected int getNumberOfPossibleActions()
        {
            return number_of_possible_actions;
        }
        
        /**
         * Return the new action that have to be applied to the actual world state
         * @param state world state of the parent node
         * @return new action that the child performs
         */
        protected Action getNewAction(WorldState.TruthStates[] state)
	{
         
            /*
            //check which action satisfied the passed goal state
            for(int i=0; i<list_of_actions.size();i++)
            {
                WorldState.TruthStates[] worldStateSim =
                        WorldState.getInstance().applyPostConditionOfAction(state, list_of_actions.get(i).GetPostCondtionsArray());
                   
                if(WorldState.getInstance().IsWorldStateAGoal(worldStateSim, node_goal))
                {
                    index = i;
                    break;
                }
            }
            
            //if there is no valid action i return null
            if(index == -1)
            {
                return null;
            }
            
            Action temp = list_of_actions.get(index);
            
            //remove the action take in consideration from the list
            list_of_actions.remove(index);
            //return the first action available
            return temp;
            * 
            */
            Action temp;
            if(possible_actions.size()!=0)
            {
                temp = possible_actions.getFirst();
                possible_actions.removeFirst();
                return temp;
            }
         
            return null;
	}
        
        /**
         * update the goal state of the node when created it
         * @param previous_goal the goal of the father of this node
         * @return the new goal that this child have to satisfied
         */
        private WorldState.TruthStates[] updateGoalState(WorldState.TruthStates[] previous_goal)
        {
           
            
            //if there is no action used for the root node i return the previous goal
            if(action_choose == null)
            {
                return previous_goal;
            }
            
            WorldState.TruthStates[] pre_consitions_applied = WorldState.getInstance().applyPreConditionOfAction(previous_goal, this.action_choose.getPreConditionArray());
           
            boolean preconsditions_met = true;
            for(int i=0; i<pre_consitions_applied.length;i++)
            {
                if(previous_goal[i] == WorldState.TruthStates.True)
                    if(pre_consitions_applied[i] == WorldState.TruthStates.False)
                    {
                        preconsditions_met = false;
                        break;
                    }
                
                if(previous_goal[i] == WorldState.TruthStates.False)
                    if(pre_consitions_applied[i] == WorldState.TruthStates.True)
                    {
                        preconsditions_met = false;
                        break;
                    }
            }
            
                    
            return pre_consitions_applied;
        }
	/**
	 * simluate the game and give the delta value to the node
	 * @return the delta value
	 */
	protected int simulation()
	{
		int temp = 0;
		int score = 0;
		
                Action simulate_action = null;
                WorldState.TruthStates[] simulate_goal = this.node_goal;
                WorldState.TruthStates[] simulate_state = this.node_state;
                // BotLogic.getInstance().getLog().info(" simulation");
		//continue until i don't reach  a final condition or the number of simulations decided previously
		while(!final_condition_reached && temp<number_of_simulations)
		{
                                   
                    //get the action to simulate from the actual workd state of this node
                    simulate_action = getNewAction(simulate_state);
                    if(simulate_action == null)
                    {
                        return score;
                    }
                    simulate_state = WorldState.getInstance().applyPostConditionOfAction(simulate_state, simulate_action.GetPostCondtionsArray());
                    simulate_goal = updateGoalState(simulate_goal);
                    
                    //if the final condition is reached, subtract 1 to the toal amount of score
                    int number_of_preconditions_not_met = finalConditions(simulate_state, simulate_goal);
                    if(number_of_preconditions_not_met == 0)
                    {
			score += 1;
                        break;
		    }
		    //or add to the score the number of unsutisfied conditions
		    else
		    {
			score += 1/number_of_preconditions_not_met;
		    }
			
		    temp++;
		}		
		
               
            return (score);
		
	}	
	
	/**
	 * Check if the world state and the goal state are equal so if we have a plan teh solve
         * the problem
	 * @param simulate_state state of the world
         * @param simulate_goal  goal 
	 */
	private int finalConditions(WorldState.TruthStates[] simulate_state, WorldState.TruthStates[] simulate_goal)
	{
            int number_of_preconditions_not_met = 0;
            for(int i=0; i<simulate_state.length; i++)
            {
              
                if(simulate_state[i] != simulate_goal[i])
                {
                    number_of_preconditions_not_met++;
                }
                                
            }
		
            return number_of_preconditions_not_met;
	}
	
	/**
	 * get the instance of the game inside this node
	 * @return the instance of the game inside this node
	 */
	protected WorldState.TruthStates[] getNodeState()
	{
		return node_state;
	}
	
	
	
}
