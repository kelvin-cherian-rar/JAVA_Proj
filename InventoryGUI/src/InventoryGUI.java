import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.FileWriter;

public class InventoryGUI {
    private Inventory inventory = new Inventory();
    private DefaultTableModel model;
    private JLabel totalItemsLabel;
    private JLabel totalQtyLabel;
    private JLabel lowStockLabel;

    public InventoryGUI() {

        if (!Login.authenticate()) {
            System.exit(0);
        }

        JFrame frame = new JFrame("Inventory Management System");
        frame.setSize(1100, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Inventory Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        frame.add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.add(mainPanel);

        // ===== LEFT PANEL =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(300,0));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField thField = new JTextField();
        JTextField searchField = new JTextField();

        JComboBox<String> categoryBox = new JComboBox<>(new String[]{
                "Electronics","Groceries","Accessories"
        });

        JPanel form = createCard("Product Details");
        form.setLayout(new GridLayout(12,1,5,5));

        form.add(new JLabel("ID")); form.add(idField);
        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Quantity")); form.add(qtyField);
        form.add(new JLabel("Threshold")); form.add(thField);
        form.add(new JLabel("Category")); form.add(categoryBox);
        form.add(new JLabel("Search")); form.add(searchField);

        JPanel summary = createCard("Summary");
        summary.setLayout(new GridLayout(3,1));

        totalItemsLabel = new JLabel("Total Products: 0");
        totalQtyLabel = new JLabel("Total Quantity: 0");
        lowStockLabel = new JLabel("Low Stock: 0");

        // 🎨 COLORS
        totalItemsLabel.setForeground(new Color(41,128,185));
        totalQtyLabel.setForeground(new Color(39,174,96));
        lowStockLabel.setForeground(new Color(192,57,43));

        summary.add(totalItemsLabel);
        summary.add(totalQtyLabel);
        summary.add(lowStockLabel);

        left.add(form);
        left.add(Box.createVerticalStrut(10));
        left.add(summary);

        mainPanel.add(left, BorderLayout.WEST);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"ID","Name","Qty","Category","Status"},0);
        JTable table = new JTable(model);

        table.setRowHeight(28);

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table,Object value,
                                                           boolean isSelected,boolean hasFocus,
                                                           int row,int col){

                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,col);

                String status = table.getValueAt(row,4).toString();

                if(status.contains("LOW")){
                    c.setBackground(new Color(255,200,200)); // red highlight
                } else {
                    c.setBackground(Color.WHITE);
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btn = new JPanel();

        JButton add = createButton("➕ Add", new Color(46,204,113));
        JButton update = createButton("✏ Update", new Color(52,152,219));
        JButton delete = createButton("🗑 Delete", new Color(231,76,60));
        JButton bill = createButton("🧾 Bill", new Color(155,89,182));
        JButton export = createButton("⬇ Export", new Color(243,156,18));
        JButton clear = createButton("🔄 Clear", new Color(149,165,166));

        btn.add(add); btn.add(update); btn.add(delete);
        btn.add(export); btn.add(bill); btn.add(clear);

        frame.add(btn, BorderLayout.SOUTH);

        // ===== ACTIONS =====

        add.addActionListener(e -> {
            try {
                inventory.add(new Product(
                        Integer.parseInt(idField.getText()),
                        nameField.getText(),
                        Integer.parseInt(qtyField.getText()),
                        Integer.parseInt(thField.getText()),
                        categoryBox.getSelectedItem().toString()
                ));
                refresh();
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null,"Invalid input");
            }
        });

        update.addActionListener(e -> {
            inventory.update(Integer.parseInt(idField.getText()),
                    Integer.parseInt(qtyField.getText()));
            refresh();
        });

        delete.addActionListener(e -> {
            inventory.remove(Integer.parseInt(idField.getText()));
            refresh();
        });

        clear.addActionListener(e -> {
            idField.setText(""); nameField.setText("");
            qtyField.setText(""); thField.setText("");
        });

        export.addActionListener(e -> {
            try {
                FileWriter fw = new FileWriter("inventory.txt");
                for(Product p:inventory.getAll()){
                    fw.write(p.getId()+","+p.getName()+","+p.getQuantity()+"\n");
                }
                fw.close();
                JOptionPane.showMessageDialog(null,"Exported!");
            } catch(Exception ex){}
        });

        bill.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Product ID"));
            int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity"));

            Product p = inventory.getById(id);

            if(p == null){
                JOptionPane.showMessageDialog(null,"Product not found");
                return;
            }

            double price = 100;
            double total = qty * price;

            inventory.update(id,-qty);

            JOptionPane.showMessageDialog(null,
                    "Bill:\nProduct: "+p.getName()+
                            "\nQty: "+qty+
                            "\nTotal: ₹"+total);

            refresh();
        });

        searchField.addKeyListener(new java.awt.event.KeyAdapter(){
            public void keyReleased(java.awt.event.KeyEvent e){
                model.setRowCount(0);
                for(Product p:inventory.searchByName(searchField.getText())){
                    model.addRow(new Object[]{
                            p.getId(),p.getName(),p.getQuantity(),
                            p.getCategory(),
                            p.isLowStock()?"🔴 LOW":"🟢 OK"
                    });
                }
            }
        });

        frame.setVisible(true);
    }

    // ===== UI HELPERS =====

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    private JPanel createCard(String title){
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.setBackground(Color.WHITE);
        return p;
    }

    private void refresh(){
        model.setRowCount(0);

        int totalQty=0, totalItems=0, low=0;

        for(Product p:inventory.getAll()){
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getQuantity(),
                    p.getCategory(),
                    p.isLowStock()?"🔴 LOW":"🟢 OK"
            });

            totalQty+=p.getQuantity();
            totalItems++;
            if(p.isLowStock()) low++;
        }

        totalItemsLabel.setText("Total Products: "+totalItems);
        totalQtyLabel.setText("Total Quantity: "+totalQty);
        lowStockLabel.setText("Low Stock: "+low);
    }

    public static void main(String[] args){
        new InventoryGUI();
    }
}