import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * A customized edge that overrides how weighted edge string representation on graph looks like.
 */
public class LSEdge extends DefaultWeightedEdge {
    @Override
    public String toString() {
        return String.valueOf(getWeight());
    }
}