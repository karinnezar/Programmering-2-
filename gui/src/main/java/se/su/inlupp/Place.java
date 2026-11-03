
// Karin Nezar Mustafa 

package se.su.inlupp;

import javafx.scene.shape.Circle;

public class Place extends Circle{
    private String name;
   
    public Place(String name, double x, double y) {
      this.name = name;
      setCenterX(x);
      setCenterY(y);
    }

    public String getName() {
      return name;
    }
   
    @Override
    public String toString() {
      return name;
    }
}
