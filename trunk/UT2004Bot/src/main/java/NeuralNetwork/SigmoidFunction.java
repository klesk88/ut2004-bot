package NeuralNetwork;

import NeuralNetwork.Neuron;

/*
 * Author Francesco Guerra
 */

public class SigmoidFunction implements java.io.Serializable{


	public float compute(Neuron neuron) {
		
		double l = 0.f;
		for (int i = 0; i < neuron.weights.length; i++){
			
			l += neuron.weights[i] * neuron.inputs[i];
		}
			
		return 1.f / (1.f + (float)Math.pow(Math.E,-l));
	}
}
