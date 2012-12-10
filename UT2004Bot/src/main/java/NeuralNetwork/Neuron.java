package NeuralNetwork;

import java.util.*;


import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;
import NeuralNetwork.SigmoidFunction;

/*
 * Author Francesco Guerra
 */

public class Neuron implements java.io.Serializable{
	 
	
	
	//The activation function for this Neuron
	private SigmoidFunction activationFunction = new SigmoidFunction();
	
	//neuron's learning rate
	protected float learningRate = 1.f;
	
	//inputs
	protected float[] inputs;
	
	//weights
	public float[] weights;
	
	//delta weights
	protected float[] deltaWeights;
	
	//desired output for the current inputs
	protected float desiredOutput = 0.f;
	
	//the actual output
	protected float output = 0.f;
	
	//the error for the current epoch
	protected float error;
	
	//These variables identify the layer and the position of this neuron
	private int layer = 0;
	private int position = 0;
	
	
	// The ann which neuron belongs to
	private NeuralNetwork network;
	
	
	public Neuron (int input_dimension, NeuralNetwork network, int layer, int pos) {	
		this.resetWeights(input_dimension);
		this.network = network;
		this.layer = layer;
		this.position = pos;
	}


	private void resetWeights(int input_dimension) {
		this.weights      = new float[input_dimension+1];
		this.deltaWeights = new float[input_dimension+1];
		
		//Initialize the weight vector with random values
		Random r = new Random();
		for (int i = 0; i < this.weights.length; i++)
			this.weights[i] = r.nextFloat();			
	}
	

	public void updateWeights() {
		for (int i = 0; i < this.weights.length; i++)
			this.weights[i] -= this.learningRate * this.deltaWeights[i];
	}
	

	public void computeGradient(boolean isOutputLayer) {
		
		//output layer derivatives and error
		if (isOutputLayer) {
			
			//error
			this.error = - (this.desiredOutput - this.output);
			this.error *= this.output * (1 - output);
			
			//gradient
			for (int w = 0; w < this.weights.length; w++)
				this.deltaWeights[w] = this.error * this.inputs[w];
		
		//hidden layer derivatives and error
		} else {
			
			//get the neurons in the last layer
			Neuron[] nextLayer = network.nextLayer(this.layer);
			
			float lc = 0.f;
			
			//linear combination of the error from the next layer 
			for (Neuron n : nextLayer)
				lc += n.weights[this.position] * n.error;
				
			//gradient
			for (int w = 0; w < this.weights.length; w++)
				this.deltaWeights[w] = this.output * (1 - this.output) *
				                       this.inputs[w] * lc;
			
			//error
			this.error = this.deltaWeights[0] / this.inputs[0];
		}	
	}
	
	

	public void updateOutput() {
		
		//if the layer is not the first one, the ann has to get the inputs from the previous layer 
		if (this.layer > 0) {
			
			Neuron[] prevLayer = network.previousLayer(this.layer);
			
			//update the inputs
			this.inputs = new float[prevLayer.length + 1];
			
			//retrieve the inputs 
			for (int p = 0; p < prevLayer.length; p++) 
				this.inputs[p] = prevLayer[p].output;
				
			//bias neuron
			this.inputs[inputs.length-1] = 1.0f;
		}
		
		//compute the output using the neuron's activation function
		this.output = this.activationFunction.compute(this);
		
	}
}


