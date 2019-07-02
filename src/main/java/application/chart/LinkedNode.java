package application.chart;

public interface LinkedNode {

    LinkedNode getPrevNode();

    void setPrevNode(LinkedNode node);

    LinkedNode getNextNode();

    void setNextNode(LinkedNode node);
}
