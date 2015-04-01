/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.components.controller;

/**
 *
 * @author miguel
 */
public abstract class ControllerOperator{
    
    abstract void createPreReplica(int id);
    
    abstract void destroyPreReplica(int id);
    
    abstract void createReplica(int id);
    
    abstract void destroyReplica(int id);
    
  
}
