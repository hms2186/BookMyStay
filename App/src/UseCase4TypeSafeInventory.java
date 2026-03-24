import java.util.LinkedHashMap;
import java.util.Map;

enum RoomType {
    SINGLE,
    DOUBLE,
    SUITE;

    @Override
    public String toString() {
        switch (this) {
            case SINGLE: return "Single Room";
            case DOUBLE: return "Double Room";
            case SUITE: return "Suite Room";
            default: return super.toString();
        }
    }
}

abstract class Room {
    private RoomType type;
    private int beds;
    private double price;

    public Room(RoomType type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public RoomType getType() {
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
        super(RoomType.SINGLE, 1, 2000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(RoomType.DOUBLE, 2, 3500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(RoomType.SUITE, 3, 6000);
    }
}

class RoomInventory {
    private Map<RoomType, Integer> inventory;

    public RoomInventory() {
        inventory = new LinkedHashMap<>();
    }

    public void addRoom(RoomType type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(RoomType type) {
        return inventory.getOrDefault(type, 0);
    }

    public void updateAvailability(RoomType type, int newCount) {
        if (inventory.containsKey(type)) {
            inventory.put(type, newCount);
        }
    }

    public void displayInventory() {
        System.out.println("\n--- Current Inventory ---\n");
        for (Map.Entry<RoomType, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

public class UseCase4TypeSafeInventory {
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" Book My Stay App ");
        System.out.println(" Version: 4.1 ");
        System.out.println("======================================");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        inventory.addRoom(RoomType.SINGLE, 5);
        inventory.addRoom(RoomType.DOUBLE, 3);
        inventory.addRoom(RoomType.SUITE, 2);

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