import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanTree {
    private HuffmanNode root;
    private HashMap<String,String> codeMap;
    private PriorityQueue<HuffmanNode> pq;

    public HuffmanTree(PriorityQueue<HuffmanNode> pq) {
        this.codeMap = new HashMap<>();
        this.pq = pq;
    }

    public void createHuffmanTree(){  //Huffman algorithm
        while (pq.size() > 1) {
            // first min extract.
            HuffmanNode leftNode = pq.poll();

            // second min extract.
            HuffmanNode rightNode = pq.poll();

            // new node z which has frequency equal to sum of frequencies of both extracted min nodes
            HuffmanNode z = new HuffmanNode(leftNode.getFrequency()+rightNode.getFrequency(),null,leftNode,rightNode);

            // add this node to the priority-queue
            pq.add(z);
        }

        this.root = pq.peek();
    }

    public void setCode(HuffmanNode root, String s){  //Builds huffman code hash-map
        if (root.getLeftNode() == null && root.getRightNode() == null && root.getC() != null) {
            codeMap.put(root.getC(),s);
            return;
        }
        // if we go to left then add "0" to the code.
        // if we go to the right add"1" to the code.
        // recursive calls for left and right sub-tree of the generated tree.
        setCode(root.getLeftNode(), s + "0");
        setCode(root.getRightNode(), s + "1");
    }

    public void printCodeMap(){
        for (HashMap.Entry<String, String> entry : codeMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public HuffmanNode getRoot() {
        return root;
    }

    public String getCode(String s){
        return codeMap.get(s);
    }

    public HashMap<String, String> getCodeMap() {
        return codeMap;
    }
}
