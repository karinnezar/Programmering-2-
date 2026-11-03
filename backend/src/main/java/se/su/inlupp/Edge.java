
// Karin Nezar Mustafa 

package se.su.inlupp;

public class Edge<T> {

  private final T destination;
    private final String name;
    private int weight;

    
    public Edge(T destination, String name, int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Weight can not be negative");
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Weight can not be negative");
        this.weight = weight;
    }

    public T getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }

  @Override
  public String toString() {
    return "till " + destination + " med " + name + " tar " + weight;
  }
}
