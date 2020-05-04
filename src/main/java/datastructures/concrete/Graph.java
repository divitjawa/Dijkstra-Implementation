package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.NoPathExistsException;

import static misc.Sorter.topKSort;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */

    private int totalVertices = 0;
    private int totalEdges = 0;
    private IDictionary<V, IList<E>> adjacencyList;
    private IList<V> listOfVertices;
    private IList<E> listOfEdges;
    private IDisjointSet<V> makeVerticesForMST;

    public Graph(IList<V> vertices, IList<E> edges) {
        totalEdges = edges.size();
        totalVertices = vertices.size();
        adjacencyList = new ChainedHashDictionary<>();
        listOfVertices = vertices;
        listOfEdges = edges;
        makeVerticesForMST = new ArrayDisjointSet<>();

        for (V vertex : vertices) {
            adjacencyList.put(vertex, new DoubleLinkedList<>());
        }

        for (E currentEdge : edges) {
            if (currentEdge == null || currentEdge.getWeight() < 0) {
                throw new IllegalArgumentException("illegal edge");
            }
            V linkedVertex1 = currentEdge.getVertex1();
            V linkedVertex2 = currentEdge.getVertex2();
            if (!vertices.contains(linkedVertex1) || !vertices.contains(linkedVertex2)) {
                throw new IllegalArgumentException("vertex not contained in list");
            }
            adjacencyList.get(linkedVertex1).add(currentEdge);
            adjacencyList.get(linkedVertex2).add(currentEdge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return totalVertices;
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return totalEdges;
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> finalEdges = new ChainedHashSet<>();
        for (V individualVertex : listOfVertices) {
            makeVerticesForMST.makeSet(individualVertex);
        }
        //IList<E> edgesAfterSorting = topKSort(totalEdges, listOfEdges);
        for (E edge: topKSort(totalEdges, listOfEdges)) {
           V sourceVertex = edge.getVertex1();
           V otherVertex = edge.getVertex2();
           if (makeVerticesForMST.findSet(sourceVertex) != makeVerticesForMST.findSet(otherVertex)) {
               makeVerticesForMST.union(sourceVertex, otherVertex);
               finalEdges.add(edge);
           }
        }
        return finalEdges;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     *
     * Dijkstra(Graph G, Vertex source)
     * for (Vertex v : G.getVertices()) { v.dist = INFINITY; }
     * G.getVertex(source).dist = 0;
     * initialize MPQ as a Min Priority Queue, add source
     * while(MPQ is not empty){
     * u = MPQ.removeMin();
     * for (Edge e : u.getEdges(u)){
     * oldDist = v.dist; newDist = u.dist+weight(u,v)
     * if(newDist < oldDist){
     * v.dist = newDist
     * v.predecessor = u
     * if(oldDist == INFINITY) { MPQ.insert(v) }
     * else { MPQ.updatePriority(v, newDist) }
     * }
     * }
     */

    public IList<E> findShortestPathBetween(V start, V end) {
        IList<E> finalEdges = new DoubleLinkedList<>();
        IDictionary<V, VerticesCompare> mapOfVertices = new ChainedHashDictionary<>();
        IPriorityQueue<VerticesCompare> minQueue = new ArrayHeap<>();
        Double oldDist = 0.0;
        Double newDist = 0.0;

        if (start.equals(end)) {
            return finalEdges;
        }
        for (V currentVertex : listOfVertices) {
            VerticesCompare v1 = new VerticesCompare(Double.POSITIVE_INFINITY, currentVertex);
            mapOfVertices.put(currentVertex, v1);
            if (currentVertex.equals(start)) {
                v1.setDist(0.0);
            }
        }
        minQueue.insert(mapOfVertices.get(start));
        while (!minQueue.isEmpty()) {
            VerticesCompare u = minQueue.removeMin();
            if (u.localVertex.equals(end)) {
                VerticesCompare temp = u;
                while (!start.equals(temp.localVertex)) {
                    E nextEdge = temp.edge;
                    finalEdges.insert(0, nextEdge);
                    temp = mapOfVertices.get(nextEdge.getOtherVertex(temp.localVertex));
                }
                return finalEdges;
            }
            if (!(u.processed)) {
                for (E edge : adjacencyList.get(u.localVertex)) {
                    V vLocal = edge.getOtherVertex(u.localVertex);
                    VerticesCompare v = mapOfVertices.get(vLocal);
                    oldDist = v.dist;
                    newDist = u.dist + edge.getWeight();
                    if (newDist < oldDist) {
                        v.dist = newDist;
                        v.predecessor = u.localVertex;
                        v.edge = edge;
                            minQueue.insert(v);
                    }
                }
                u.processed = true;
            }
        }
        throw new NoPathExistsException("no path found!");
    }

private class VerticesCompare implements Comparable<VerticesCompare> {
    public Double dist;
    public V localVertex;
    public V predecessor;
    public E edge;
    public boolean processed;

    public VerticesCompare(Double distance, V vertex) {
        dist = distance;
        localVertex = vertex;
        processed = false;
    }

    public void setDist(Double distance) {
        dist = distance;
    }

    // Compares the distance of the 2 vertices and
    public int compareTo(VerticesCompare other) {
        return (int) (dist - other.dist);
    }
}
}
