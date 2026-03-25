import java.util.*;

// ---------------- ENUM ----------------
enum RoomType {
    SINGLE, DOUBLE, SUITE
}

// ---------------- SERVICE MODEL ----------------
class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (₹" + price + ")";
    }
}

// ---------------- INVENTORY ----------------
class RoomInventory {
    private Map<RoomType, Integer> inventory = new HashMap<>();

    public void addRoom(RoomType type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(RoomType type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(RoomType type) {
        inventory.put(type, getAvailability(type) - 1);
    }
}

// ---------------- ALLOCATION ----------------
class RoomAllocationService {
    private Set<String> allocatedIds = new HashSet<>();

    public String allocate(RoomType type) {
        String id = type.name().charAt(0) + "-" +
                UUID.randomUUID().toString().substring(0, 5);

        if (allocatedIds.contains(id)) return null;

        allocatedIds.add(id);
        return id;
    }
}

// ---------------- ADD-ON SERVICE MANAGER ----------------
class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServices = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        reservationServices.putIfAbsent(reservationId, new ArrayList<>());
        reservationServices.get(reservationId).add(service);
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;

        List<AddOnService> services = reservationServices.get(reservationId);
        if (services != null) {
            for (AddOnService s : services) {
                total += s.getPrice();
            }
        }

        return total;
    }

    public void displayServices(String reservationId) {
        System.out.println("\nServices for Reservation " + reservationId + ":");

        List<AddOnService> services = reservationServices.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// ---------------- MAIN ----------------
public class UseCase7BookingSystem {
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" Book My Stay App ");
        System.out.println(" Version: 7.0 ");
        System.out.println("======================================");

        // Core services (unchanged)
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(RoomType.SINGLE, 2);

        RoomAllocationService allocator = new RoomAllocationService();

        // Allocate room
        String reservationId = allocator.allocate(RoomType.SINGLE);
        inventory.decrement(RoomType.SINGLE);

        System.out.println("\nReservation Confirmed: " + reservationId);

        // Add-on services
        AddOnService wifi = new AddOnService("WiFi", 500);
        AddOnService breakfast = new AddOnService("Breakfast", 800);
        AddOnService spa = new AddOnService("Spa", 1500);

        AddOnServiceManager manager = new AddOnServiceManager();

        // Attach services
        manager.addService(reservationId, wifi);
        manager.addService(reservationId, breakfast);
        manager.addService(reservationId, spa);

        // Display
        manager.displayServices(reservationId);

        System.out.println("\nApplication execution completed.");
    }
}
