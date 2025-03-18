package comlisanshuxue.lisan.huff;

import lombok.Data;

@Data
class HuffmanNode implements Comparable<HuffmanNode> {
    String data;
    Double frequency;
    HuffmanNode left, right;

    public HuffmanNode(String data, Double frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return (int) (this.frequency*1e9 - node.frequency*1e9);
    }

    @Override
    public String toString() {
        return "[" + data + ":" + frequency + "]";
    }
}
