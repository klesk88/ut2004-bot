/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MTC;

/**
 *
 * @author klesk
 */
import com.fmt.UT2004Bot.Action;
import com.fmt.UT2004Bot.ActionManager;
import com.fmt.UT2004Bot.WorldState;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Game;
import java.util.List;




public class MTC{
	
	private float constant;

	private int number_of_simulations;
	private int delay;
        List<Action> actions;
        private static MTC instance = null;
       
        
          private MTC() {
                // Exists only to defeat instantiation.
         }

        public static MTC getInstance() {
            if (instance == null) {
                instance = new MTC();
            }
            return instance;
        }
        
        
	/**
	 * initialize the class Monte Carlo Tree Search
	 * @param constant value of the constant used for the best child search
	 * @param ghost_controller controller for the ghost to use for the simulation initialize here for have only 1 controller in total
	 * @param number_of_simulations number of simulations to perform at most during the simulation
	 * @param delay how much time has ms pac-man to take a decision
	 */
	public void init(float constant,List<Action> actions, int number_of_simulations, int delay)
	{
           
            this.actions =  ActionManager.getInstance().getActionsAvailable();
            this.constant = constant;
            this.number_of_simulations = number_of_simulations;
	    this.delay=delay;
	}
	
	/**
	 
	 */
	public List<Action> MTC(WorldState.TruthStates[] state, WorldState.TruthStates[] goal)
	{
		WorldState.TruthStates[] min_state = state;
                int h_min = 0;
               
                //calculat ethe initial cost
                for(int i=0; i<goal.length; i++)
                {
                    if(state[i] != goal[i])
                    {
                        h_min++;
                    }
                }
              
		float delta = 0;
		//create the root node
		Node root = new Node();	
		root.init(null,number_of_simulations, state,goal);
		//root.setState();
		
		//time when i start to make the calculations
		long start_time = System.currentTimeMillis();
		long temp_time=System.currentTimeMillis();
		
		//while i have time to perform my calculations
		while(temp_time - start_time < delay)
		{
			Node node = treePolicy(root);
			delta = defaultPolicy(node);			
			backup(node,delta);
			
			//update the time
			temp_time=System.currentTimeMillis();
		}
		
                Node best_child = root;
                List<Action> final_list = null;
                
                while(best_child.getChildrenSize() != 0)
                {
                    final_list.add(best_child.getAction());
                    best_child = best_child.getBestChild();
                }
		
              
                return final_list;        
	}
	
	/**
	 * tree policy 
	 * @param actual_node rapresent the actual node take in consideration
	 * @return if the node is not fully expanded expand it, otherwise return the best child
	 */
	private Node treePolicy(Node actual_node)
	{
		
		do
		{
			//if the node isn't all expanded
			if(actual_node.getChildrenSize()<actual_node.getNumberOfPostConditions())
			{
				return expand(actual_node);
			}
			else
			{
				actual_node = bestChild(actual_node, constant);
			}
			
			
		}while(actual_node.getChildrenSize() != 0);
		
		return actual_node;
	}
	
	/**
	 * apply the default policy calling the simulation method inside the node
	 * @param node the node to where start the simulation
	 * @return the delta value
	 */
	private int defaultPolicy(Node node)
	{		
		return node.simulation();	
	}
	
	/**
	 * backup the data from the node to its parents
	 * @param node 
	 * @param delta
	 */
	private void backup(Node node, float delta)
	{
		while(node!=null)
		{
			node.increaseN();
			node.setQ(delta);
                        node.setBestChild(node,node.getQ(),node.getN());
			node = node.getParent();
		}
	}
	
	/**
	 * calculate the best child using the UCT formula
	 * @param node
	 * @param constant to apply inside the UCT forumla
	 * @return best node found
	 */
	private Node bestChild(Node node, float constant)
	{
		int index_best_children = -1;
		double temp_value = Integer.MIN_VALUE;
		double best_child_value = Integer.MIN_VALUE;
		
		int children_size = node.getChildrenSize();
		
		for(int i=0;i<children_size;i++)
		{
			Node children = node.getChildren(i);
			temp_value = (children.getQ()/children.getN()) + constant * (Math.sqrt((2*Math.log(node.getN()))/children.getN()));
			
			//if the value just calculated is bigger than the one i had before i update the index and the best value
			if(temp_value>best_child_value)
			{
				index_best_children = i;
				best_child_value = temp_value;
			}
		}
		
		return node.getChildren(index_best_children);
	}
	
	/**
	 * expand the node creating a child and initializing it
	 * @param node parent node to expand
	 * @return a new initialize child node
	 */
	private Node expand(Node node)
	{
		Node new_child = null;
		WorldState.TruthStates[] state = node.getNodeState();
		Action new_action = node.getNewAction(state);
                
                //if there is no available action in this moment where the pre conditions are satisfied
                if(new_action == null)
                {
                    return null;
                }
                //apply to the state of the parent node the postconditions that the action 
                //selected have
                state = new_action.ApplyPostCondtions(state);
                
		if(new_action != null)
		{
			new_child = new Node();
			new_child.init(new_action,number_of_simulations,state,node.getNodeGoal());
			new_child.setParent(node);
                        new_child.setListOfActions(actions);
			node.setChildren(new_child);
		}
		return new_child;
	}
	
	
        

}
