package NeuralNetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;
import java.util.*;

/*
 * Author Francesco Guerra
 */

public class NeuralNetwork implements java.io.Serializable{
	
	//Input data_set and expected outputs
	protected float[] inputs;
	protected float[] desiredOutput;
	
	//Neurons of the ann, categorized in layers and positions
	public Neuron[][] neurons;
	
	protected int[] train_parameter_layer;
	protected int train_parameter_number_of_inputs;
	
	/*
	 * The constructor creates the topology of the neural network
	 */
	public NeuralNetwork(int layers[], int number_of_inputs) {
		
		train_parameter_layer = layers;
		train_parameter_number_of_inputs = number_of_inputs;
		
			
		this.neurons = new Neuron[layers.length][];
		
		for (int layer = 0; layer < layers.length; layer++)
			this.neurons[layer] = new Neuron[layers[layer]];
			
		
		for (int layer = 0; layer < layers.length; layer++)
			for (int p = 0; p < this.neurons[layer].length; p++) {
				
				int temp_dimension ;
				
				if (layer>0)
					temp_dimension = neurons[layer-1].length;
				else
					temp_dimension = number_of_inputs;
				
				 this.neurons[layer][p] = new Neuron(temp_dimension, this, layer, p);
			}
	}
        
      
	

	protected Neuron[] previousLayer(int layer) {
		if (layer <= 0) 
			throw new InvalidParameterException("No prev layer for layer 0.");
		
		return this.neurons[layer-1];
	}
	

	protected Neuron[] nextLayer(int layer) {	
		if (layer >= this.neurons.length)
			throw new InvalidParameterException("Last layer passed as arg.");
	
		return this.neurons[layer+1];
	}
	

    public void setInputs(float inputs[], float[] desiredOutput) {
		this.inputs = inputs;
		this.desiredOutput = desiredOutput;
    }
    

    public float[] getOutput() {
		float[] result = new float[neurons[neurons.length-1].length];
		
		for (int p = 0; p < neurons[neurons.length-1].length; p++)
			result[p] = neurons[neurons.length-1][p].output;
			
		return result;
    }


    /*
     * Compute the outputs of the ann using forward mode
     */
    public void activate() {
		

		//assign the initial input values for the first perceptron's layer
		for (int p = 0; p < this.neurons[0].length; p++){
			float [] temp_input = new float[this.inputs.length+1];
			for(int i = 0; i<this.inputs.length;i++){
				
				temp_input[i] = this.inputs[i];
				
			}
			temp_input[temp_input.length-1] = 1;
			this.neurons[0][p].inputs = /*temp_input;*/ this.inputs;
		}
	
			
		//assign the desired output for the last layer
		for (int p = 0; p < this.neurons[this.neurons.length-1].length; p++)
			this.neurons[this.neurons.length-1][p].desiredOutput = this.desiredOutput[p];
			
		//compute the output for all the neurons
		for (int i = 0; i< this.neurons.length;i++){
			for (int n = 0; n< this.neurons[i].length; n++){
				
				this.neurons[i][n].updateOutput();
				
			}
		}
		
	}
	
	/*
	 * Apply the back prop to the ann
	 */
    public void applyBackpropagation() {
		 
	
		//compute error gradients
		for (int layer = this.neurons.length-1; layer >= 0; layer--)
			for (int n = 0; n < this.neurons[layer].length; n++)
		
				//output layer
				if (layer == this.neurons.length-1) 
					this.neurons[layer][n].computeGradient(true);
				
				//hidden layers
				else this.neurons[layer][n].computeGradient(false);
				
		//delta updates
		for (Neuron[] layer : this.neurons)
			for (Neuron n : layer) n.updateWeights();
	}
	
	/*
	 * This method compute the outputs of the ann using forward mode
	 * It's used after the perceptron has been trained
	 * The only difference with the method activate is the update of desired output
	 */

    public void calculateOutput(float[] input){
	

		//assign the initial input values for the first perceptron's layer
		for (int p = 0; p < this.neurons[0].length; p++){
			float [] temp_input = new float[this.inputs.length+1];

			for(int i = 0; i<this.inputs.length;i++){
				
				temp_input[i] = this.inputs[i];
				
			}
			temp_input[temp_input.length-1] = 1;
			this.neurons[0][p].inputs = /*temp_input;*/ input;
		}
		
			
		for (int i = 0; i< this.neurons.length;i++){
			for (int n = 0; n< this.neurons[i].length; n++){
					
				this.neurons[i][n].updateOutput();
					
			}
		}
	
    }
    
    /*
     * It automatically train the neural network and save it into a file
     */
    /*public void train(String data_set,String output_file, int epochs){
    	
    	NeuralNetwork ann = new NeuralNetwork(train_parameter_layer, train_parameter_number_of_inputs);
    	DataTrainingSet dts = null;
    	
    	//loading data set
    	try
	       {
	          FileInputStream fileIn = new FileInputStream(data_set);
	          ObjectInputStream in = new ObjectInputStream(fileIn);
	          dts = (DataTrainingSet) in.readObject();
	          in.close();
	          fileIn.close();
	      }
    	catch(IOException i)
	      {
	          i.printStackTrace();
	          return;
	      }
    	catch(ClassNotFoundException c)
	      {
	          System.out.println("DataTrainingSet class not found");
	          c.printStackTrace();
	          return;
	      }
    	
    	
    	//train the neural network
    	int g = 0;
    	while (g<epochs){
			
    		g++;
		
    		for (int i = 0; i<dts.data_input.size();i++){
			
    			ann.setInputs(dts.data_input.get(i), dts.data_output.get(i));
    			ann.activate();
    			ann.applyBackpropagation();
			
    			//print the error for debug
    			if (i == 0 || i == dts.data_input.size()-1){
    				for (int n = 0; n<3;n++){
    					float error1 = dts.data_output.get(i)[n] - ann.getOutput()[n];
    					System.out.print(" error = " + error1);
			
    				}		
    				System.out.println("");
    			}
    		}
    	}
    	
    	
    	//save the neural network into a file
    	
    	try
	      
		{
	         
			FileOutputStream fileOut = new FileOutputStream(output_file);
			ObjectOutputStream out =  new ObjectOutputStream(fileOut); 
			out.writeObject(ann);
			out.close();
			fileOut.close();
	      
		}
		
		catch(IOException i)
	      
		{
	          i.printStackTrace();
	      
		}
    	
    	
    }*/
	
		
	
}
