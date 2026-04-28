import java.util.*;

public class Inventory {
    private Map<Integer, Product> map = new HashMap<>();

    public void add(Product p) {
        map.put(p.getId(), p);
    }

    public void update(int id, int qty) {
        if (map.containsKey(id)) {
            map.get(id).updateQuantity(qty);
        }
    }

    public void remove(int id) {
        map.remove(id);
    }

    public Collection<Product> getAll() {
        return map.values();
    }

    public Product getById(int id) {
        return map.get(id);
    }

    public List<Product> searchByName(String keyword) {
        List<Product> result = new ArrayList<>();
        for (Product p : map.values()) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }
}