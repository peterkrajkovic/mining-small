/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.distance;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public interface DistanceBased {

    public void setDistance(Distance distance);

    public <T extends Distance> T getDistance();

}
