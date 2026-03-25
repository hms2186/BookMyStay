import java.util.*;

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

class BookingRequest {
    private String customerName;
    private RoomType roomType;

    public BookingRequest(String customerName, RoomType roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public RoomType getRoomType() {
        return roomType;
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

    public boolean bookRoom(RoomType type) {
        int available = getAvailability(type);
        if (available > 0) {
            inventory.put(type, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\n--- Current Inventory ---\n");
        for (Map.Entry<RoomType, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

class BookingManager {
    private Queue<BookingRequest> requestQueue;
    private RoomInventory inventory;

    public BookingManager(RoomInventory inventory) {
        this.inventory = inventory;
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
        System.out.println("Request added for " + request.getCustomerName() +
                " (" + request.getRoomType() + ")");
    }

    public void processBookings() {
        System.out.println("\n--- Processing Booking Requests ---\n");

        while (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();

            System.out.println("Processing request for " + request.getCustomerName());

            if (inventory.bookRoom(request.getRoomType())) {
                System.out.println("Booking CONFIRMED for " + request.getCustomerName());
            } else {
                System.out.println("Booking FAILED (No availability) for " + request.getCustomerName());
            }

            System.out.println();
        }
    }
}

public class UseCase5BookingSystem {
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" Book My Stay App ");
        System.out.println(" Version: 5.0 ");
        System.out.println("======================================");

        RoomInventory inventory = new RoomInventory();

        inventory.addRoom(RoomType.SINGLE, 2);
        inventory.addRoom(RoomType.DOUBLE, 1);
        inventory.addRoom(RoomType.SUITE, 1);

        BookingManager manager = new BookingManager(inventory);

        // Simulated booking requests
        manager.addRequest(new BookingRequest("Alice", RoomType.SINGLE));
        manager.addRequest(new BookingRequest("Bob", RoomType.SINGLE));
        manager.addRequest(new BookingRequest("Charlie", RoomType.SINGLE)); // should fail
        manager.addRequest(new BookingRequest("David", RoomType.DOUBLE));
        manager.addRequest(new BookingRequest("Eve", RoomType.DOUBLE)); // should fail

        manager.processBookings();

        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}
