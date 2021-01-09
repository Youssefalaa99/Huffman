import java.util.Comparator;

public class NodeComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y){
        return x.getFrequency() - y.getFrequency();
    }
}
