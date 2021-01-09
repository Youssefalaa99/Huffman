public class HuffmanNode {
    private int frequency;
    private String c;
    private HuffmanNode leftNode;
    private HuffmanNode rightNode;


    public HuffmanNode(int frequency, String c) {
        this.frequency = frequency;
        this.c = c;
        this.leftNode = null;
        this.rightNode = null;
    }

    public HuffmanNode(int frequency, String c, HuffmanNode leftNode, HuffmanNode rightNode) {
        this.frequency = frequency;
        this.c = c;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getC() {
        return c;
    }

    public HuffmanNode getLeftNode() {
        return leftNode;
    }

    public HuffmanNode getRightNode() {
        return rightNode;
    }
}
