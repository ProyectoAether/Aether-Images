package dao;

public class ItemKnapSack {
    private int position;
    private int profit;
    private int weight;

    public ItemKnapSack(){

    }
    public ItemKnapSack(int position, int profit, int weight) {
        this.position = position;
        this.profit = profit;
        this.weight = weight;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
