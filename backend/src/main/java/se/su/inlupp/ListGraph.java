
// Karin Nezar Mustafa 

package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
  private Map<T, Set<Edge<T>>> graph = new HashMap<>();

  @Override
  public void add(T node) {
    graph.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if(weight < 0)
        throw new IllegalArgumentException(" Weight can not be negative ");

    if(!graph.containsKey(node1) || !graph.containsKey(node2))
        throw new NoSuchElementException("Missing node in the graph ");

    for(Edge<T> edge : graph.get(node1)){
      if(edge.getDestination().equals(node2)){
        throw new IllegalStateException("Edge already exists");
      }
    }
    
    graph.get(node1).add(new Edge<>(node2, name, weight));
    graph.get(node2).add(new Edge<>(node1, name, weight));
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (weight < 0)
        throw new IllegalArgumentException();

    if (!graph.containsKey(node1) || !graph.containsKey(node2))
        throw new NoSuchElementException();

    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);

    if (edge1 == null || edge2 == null)
        throw new NoSuchElementException();

    edge1.setWeight(weight);
    edge2.setWeight(weight);
  }



  @Override
  public Set<T> getNodes() {
    return new HashSet<>(graph.keySet());
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if(!graph.containsKey(node))
      throw new NoSuchElementException();
    return new HashSet<>(graph.get(node));
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
        throw new NoSuchElementException();

    for (Edge<T> edge : graph.get(node1)) {
        if (edge.getDestination().equals(node2)) {
            return edge;
        }
    }
    return null;
  }


  @Override
  public void disconnect(T node1, T node2) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
        throw new NoSuchElementException();

    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);

    if (edge1 == null || edge2 == null)
        throw new IllegalStateException(" no corner between both nodes");

    graph.get(node1).remove(edge1);
    graph.get(node2).remove(edge2);
  }


  @Override
  public void remove(T node) {
    if (!graph.containsKey(node))
        throw new NoSuchElementException();

    for (Edge<T> edge : graph.get(node)) {
        T destination = edge.getDestination();
        graph.get(destination).removeIf(e -> e.getDestination().equals(node));
    }
    graph.remove(node);
  }

  @Override
  public void clear() {
    graph.clear();
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ListGraph:\n");
    for (T node : graph.keySet()) {
        sb.append(node).append(" -> ").append(graph.get(node)).append("\n");
    }
    return sb.toString();
  }



  @Override
  public boolean pathExists(T from, T to) {
    if(!graph.containsKey(from) || !graph.containsKey(to))
      return false;

    Set<T> visited = new HashSet<>();
    return dfs(from, to , visited);
  }

  private boolean dfs(T current, T target, Set<T> visited){
    if (current.equals(target)) return true;
    visited.add(current);
    for (Edge<T> edge : graph.get(current)) {
        T next = edge.getDestination();
        if (!visited.contains(next)) {
            if (dfs(next, target, visited)) return true;
        }
    }
    return false;
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    if (!graph.containsKey(from) || !graph.containsKey(to))
        return null;

    Map<T, T> cameFrom = new HashMap<>();
    cameFrom.put(from, null);
    Queue<T> queue = new LinkedList<>();
    queue.add(from);

    while (!queue.isEmpty()) {
        T current = queue.poll();
        if (current.equals(to)) break;

        for (Edge<T> edge : graph.get(current)) {
            T neighbor = edge.getDestination();
            if (!cameFrom.containsKey(neighbor)) {
                cameFrom.put(neighbor, current);
                queue.add(neighbor);
            }
        }
    }

    if (!cameFrom.containsKey(to)) return null;

    List<Edge<T>> path = new LinkedList<>();
    T current = to;
    while (!current.equals(from)) {
        T prev = cameFrom.get(current);
        Edge<T> edge = getEdgeBetween(prev, current);
        path.add(0, edge);
        current = prev;
    }
    return path;
  }

}
