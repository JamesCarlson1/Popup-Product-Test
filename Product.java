public class Product {
    private int quantity;
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String newName) {
        name = newName;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }
    
    public void receive (int n) {
        quantity += n;
    }
    public void sell (int n) {
        quantity -= n;
    }
}