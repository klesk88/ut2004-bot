/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import NeuralNetwork.NeuralNetwork;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author uer
 */
public class Predictor {
    
    private NeuralNetwork neural;
    private Location predicted_location;
    private Player target = null;
    private boolean prediction_available = false;
    private Calendar cal = Calendar.getInstance();
    private long previous_time = 0;
    private ArrayList<Location> input_list;
    public float normalization_factor = 400;
    
    
    public Predictor(){
        
    }
    
    public void init(){
        
        input_list = new ArrayList<Location>();
        predicted_location = Location.ZERO;
        
        try
        {
           FileInputStream fileIn = new FileInputStream("TestNeuralNetworkTrained_UT.ser");
           ObjectInputStream in = new ObjectInputStream(fileIn);
           neural = (NeuralNetwork) in.readObject();
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
            System.out.println("NN class not found");
            c.printStackTrace();
            return;
        }
          
        
    }
    
    public Location getPredictedLocation(){
        
        return predicted_location;
    }
    
    public boolean isPredictionAvailable(){
        
        return prediction_available;
    }
    
    public void calculatePosition(Player target_player){
        
        
        
        if(target_player != null){             
                        
            Long time_difference = this.cal.getInstance().getTimeInMillis() - previous_time;

            if(time_difference >= 185 ){

                //input_collector.input_collection.add(event.getObject().getLocation());                
                //System.out.println(event.getObject().getLocation().toString() + "  " + "" + time_difference);

                if (!target_player.equals(target)){
                    
                    input_list.clear();
                    target = target_player;
                }
                
                if (target_player.getLocation() != null){
                    input_list.add(target_player.getLocation());
                }

                if (input_list.size() == 4){
                    
                    this.prediction_available = true;
                    float[] temp_calculated_data = this.makeUnique(input_list);
                    neural.calculateOutput(temp_calculated_data);
                    float [] results = neural.getOutput();
                    Location temp_result = this.toLocation(results);
                    Location temp_result2 = temp_result.scale(400);
                    //System.out.println("scaled: " + temp_result.toString());
                    Location prediction = temp_result2.add( target_player.getLocation());
                    predicted_location = prediction;
                    //float [] temp_result = cts.deNormalizeInput(cts.deNormalizeInput(input_list.get(0), input_list.get(3)), results);
                    System.out.println(results[0] + " " +results[1] + " " +results[2] + " ");
                    System.out.println("prediction: " + prediction.toString() +" real:" + target_player.getLocation().toString());
                }
                else{
                    
                    prediction_available = false;
                    if (target_player.getLocation() != null){
                        predicted_location = target_player.getLocation();
                    }
                }

                //System.out.println("ciaooooooooo");


                if(input_list.size() == 4){

                    input_list.remove(0);
                }

                previous_time = this.cal.getInstance().getTimeInMillis();

            }

        }
        
        else{
            
            target = null;
            prediction_available = false;
            
            
        }
        
        
    }
    
    public float[] normalizeInput(Location start_location, Location end_location){
        
        float [] returned_value = new float[3];
        
        returned_value[0] = (float) ((end_location.getX() - start_location.getX())/normalization_factor);
        returned_value[1] = (float) ((end_location.getY() - start_location.getY())/normalization_factor);
        returned_value[2] = (float) ((end_location.getZ() - start_location.getZ())/normalization_factor);
        
        return returned_value;
        
    }
    
    public float[] deNormalizeInput(float[] start_location, float[] end_location){
        
        float [] returned_value = new float[3];
        
        returned_value[0] = (float) ((end_location[0] + start_location[0])*normalization_factor);
        returned_value[1] = (float) ((end_location[1] + start_location[1])*normalization_factor);
        returned_value[2] = (float) ((end_location[2] + start_location[2])*normalization_factor);
        
        return returned_value;
        
    }
    
    
    public Location toLocation(float[] position_values){
        
        
        Location returned_location = new Location(position_values[0],position_values[1],position_values[2]);
        
        return returned_location;
        
    }
    
    public float[] makeUnique(ArrayList<Location> data){
        
        
        float [] temp_input0 = this.normalizeInput(data.get(0), data.get(0));
        float [] temp_input1 = this.normalizeInput(data.get(0), data.get(1));
        float [] temp_input2 = this.normalizeInput(data.get(0), data.get(2));
        float [] temp_input3 = this.normalizeInput(data.get(0), data.get(3));
                 
        float [] returned_data = new float [12];
        
        returned_data[0] = temp_input0[0];
        returned_data[1] = temp_input0[1];
        returned_data[2] = temp_input0[2];
        
        returned_data[3] = temp_input1[0];
        returned_data[4] = temp_input1[1];
        returned_data[5] = temp_input1[2];
        
        returned_data[6] = temp_input2[0];
        returned_data[7] = temp_input2[1];
        returned_data[8] = temp_input2[2];
        
        returned_data[9] = temp_input3[0];
        returned_data[10] = temp_input3[1];
        returned_data[11] = temp_input3[2];
        
        return returned_data;
    }
    
}
