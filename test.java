// ...existing code...
public class test {
    // Simple demo CLI for a tiny cafe menu and billing
    public static void main(String[] args) {
        java.util.Scanner in = new java.util.Scanner(System.in);
        java.util.List<MenuItem> menu = new java.util.ArrayList<>();
        // Seed menu
        menu.add(new MenuItem(1, "Espresso", 80.0));
        menu.add(new MenuItem(2, "Cappuccino", 120.0));
        menu.add(new MenuItem(3, "Sandwich", 150.0));

        System.out.println("DataBrew Cafe - Demo");
        boolean running = true;
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1) Show menu");
            System.out.println("2) Add menu item");
            System.out.println("3) Take order");
            System.out.println("4) Exit");
            System.out.print("Option: ");
            String opt = in.nextLine().trim();
            switch (opt) {
                case "1":
                    showMenu(menu);
                    break;
                case "2":
                    addMenuItem(menu, in);
                    break;
                case "3":
                    takeOrder(menu, in);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye.");
        in.close();
    }

    static void showMenu(java.util.List<MenuItem> menu) {
        System.out.println("\n--- Menu ---");
        for (MenuItem m : menu) {
            System.out.printf("%d) %s - ₹%.2f%n", m.id, m.name, m.price);
        }
    }

    static void addMenuItem(java.util.List<MenuItem> menu, java.util.Scanner in) {
        try {
            System.out.print("Name: ");
            String name = in.nextLine().trim();
            System.out.print("Price: ");
            double price = Double.parseDouble(in.nextLine().trim());
            int id = menu.stream().mapToInt(mi -> mi.id).max().orElse(0) + 1;
            menu.add(new MenuItem(id, name, price));
            System.out.println("Added: " + name);
        } catch (Exception e) {
            System.out.println("Invalid input. Cancelled.");
        }
    }

    static void takeOrder(java.util.List<MenuItem> menu, java.util.Scanner in) {
        if (menu.isEmpty()) {
            System.out.println("Menu is empty.");
            return;
        }
        java.util.Map<Integer, Integer> order = new java.util.LinkedHashMap<>();
        showMenu(menu);
        System.out.println("Enter item id and quantity separated by space (empty line to finish):");
        while (true) {
            System.out.print("> ");
            String line = in.nextLine().trim();
            if (line.isEmpty()) break;
            String[] parts = line.split("\\s+");
            try {
                int id = Integer.parseInt(parts[0]);
                int qty = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                if (menu.stream().noneMatch(mi -> mi.id == id)) {
                    System.out.println("Invalid id.");
                    continue;
                }
                order.put(id, order.getOrDefault(id, 0) + qty);
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
        if (order.isEmpty()) {
            System.out.println("No items ordered.");
            return;
        }
        double total = 0.0;
        System.out.println("\n--- Bill ---");
        for (java.util.Map.Entry<Integer, Integer> e : order.entrySet()) {
            MenuItem mi = menu.stream().filter(x -> x.id == e.getKey()).findFirst().get();
            double lineCost = mi.price * e.getValue();
            System.out.printf("%s x%d -> ₹%.2f%n", mi.name, e.getValue(), lineCost);
            total += lineCost;
        }
        System.out.printf("Total: ₹%.2f%n", total);
    }

    static class MenuItem {
        int id;
        String name;
        double price;

        MenuItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
}
// ...existing code...
```// filepath: /home/cse/Desktop/DBMS_LABMID/DataBrew-Cafe-Management-System/assesment/test.java
// ...existing code...
public class test {
    // Simple demo CLI for a tiny cafe menu and billing
    public static void main(String[] args) {
        java.util.Scanner in = new java.util.Scanner(System.in);
        java.util.List<MenuItem> menu = new java.util.ArrayList<>();
        // Seed menu
        menu.add(new MenuItem(1, "Espresso", 80.0));
        menu.add(new MenuItem(2, "Cappuccino", 120.0));
        menu.add(new MenuItem(3, "Sandwich", 150.0));

        System.out.println("DataBrew Cafe - Demo");
        boolean running = true;
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1) Show menu");
            System.out.println("2) Add menu item");
            System.out.println("3) Take order");
            System.out.println("4) Exit");
            System.out.print("Option: ");
            String opt = in.nextLine().trim();
            switch (opt) {
                case "1":
                    showMenu(menu);
                    break;
                case "2":
                    addMenuItem(menu, in);
                    break;
                case "3":
                    takeOrder(menu, in);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye.");
        in.close();
    }

    static void showMenu(java.util.List<MenuItem> menu) {
        System.out.println("\n--- Menu ---");
        for (MenuItem m : menu) {
            System.out.printf("%d) %s - ₹%.2f%n", m.id, m.name, m.price);
        }
    }

    static void addMenuItem(java.util.List<MenuItem> menu, java.util.Scanner in) {
        try {
            System.out.print("Name: ");
            String name = in.nextLine().trim();
            System.out.print("Price: ");
            double price = Double.parseDouble(in.nextLine().trim());
            int id = menu.stream().mapToInt(mi -> mi.id).max().orElse(0) + 1;
            menu.add(new MenuItem(id, name, price));
            System.out.println("Added: " + name);
        } catch (Exception e) {
            System.out.println("Invalid input. Cancelled.");
        }
    }

    static void takeOrder(java.util.List<MenuItem> menu, java.util.Scanner in) {
        if (menu.isEmpty()) {
            System.out.println("Menu is empty.");
            return;
        }
        java.util.Map<Integer, Integer> order = new java.util.LinkedHashMap<>();
        showMenu(menu);
        System.out.println("Enter item id and quantity separated by space (empty line to finish):");
        while (true) {
            System.out.print("> ");
            String line = in.nextLine().trim();
            if (line.isEmpty()) break;
            String[] parts = line.split("\\s+");
            try {
                int id = Integer.parseInt(parts[0]);
                int qty = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                if (menu.stream().noneMatch(mi -> mi.id == id)) {
                    System.out.println("Invalid id.");
                    continue;
                }
                order.put(id, order.getOrDefault(id, 0) + qty);
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
        if (order.isEmpty()) {
            System.out.println("No items ordered.");
            return;
        }
        double total = 0.0;
        System.out.println("\n--- Bill ---");
        for (java.util.Map.Entry<Integer, Integer> e : order.entrySet()) {
            MenuItem mi = menu.stream().filter(x -> x.id == e.getKey()).findFirst().get();
            double lineCost = mi.price * e.getValue();
            System.out.printf("%s x%d -> ₹%.2f%n", mi.name, e.getValue(), lineCost);
            total += lineCost;
        }
        System.out.printf("Total: ₹%.2f%n", total);
    }

    static class MenuItem {
        int id;
        String name;
        double price;

        MenuItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
}
// ...existing code...