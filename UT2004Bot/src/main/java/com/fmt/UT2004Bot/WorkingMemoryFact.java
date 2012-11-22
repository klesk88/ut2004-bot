/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

/**
 *
 * @author Tilman
 */
public class WorkingMemoryFact {
 
    public enum WMFactTypes {Default, AttackLocation, HealthPickup, LowHealth }
    
    public WMFactTypes factType;
    
    // How much confidence we have in this fact
    public float belief;
    
    
    public WorkingMemoryFact()
    {
    
    }
}
