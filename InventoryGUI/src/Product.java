public class Product {
    private int id;
    private String name;
    private int quantity;
    private int threshold;
    private String category;

    public Product(int id, String name, int quantity, int threshold, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }

    public void updateQuantity(int q) {
        quantity += q;
    }

    public boolean isLowStock() {
        return quantity <= threshold;
    }
}