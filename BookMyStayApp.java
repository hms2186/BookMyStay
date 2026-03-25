import java.util.HashMap;
import java.util.Map;

abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price per night: ₹" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }
}

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
        }
    }

    public void displayInventory() {
        System.out.println("\n--- Current Inventory ---\n");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

public class UseCase3InventorySetup {
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" Book My Stay App ");
        System.out.println(" Version: 3.0 ");
        System.out.println("======================================");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        inventory.addRoom(single.getType(), 5);
        inventory.addRoom(doubleRoom.getType(), 3);
        inventory.addRoom(suite.getType(), 2);

        System.out.println("\n--- Room Details ---\n");
        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getType()) + "\n");

        doubleRoom.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getType()) + "\n");

        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getType()) + "\n");

        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}
