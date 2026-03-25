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

    public void decrement(RoomType type) {
        inventory.put(type, getAvailability(type) - 1);
    }

    public void displayInventory() {
        System.out.println("\n--- Current Inventory ---\n");
        for (Map.Entry<RoomType, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

class RoomAllocationService {
    private Map<RoomType, Set<String>> allocatedRooms;
    private Set<String> allAllocatedRoomIds;

    public RoomAllocationService() {
        allocatedRooms = new HashMap<>();
        allAllocatedRoomIds = new HashSet<>();
    }

    public String allocateRoom(RoomType type) {
        String roomId = generateRoomId(type);

        if (allAllocatedRoomIds.contains(roomId)) {
            return null; // safety check
        }

        allAllocatedRoomIds.add(roomId);

        allocatedRooms.putIfAbsent(type, new HashSet<>());
        allocatedRooms.get(type).add(roomId);

        return roomId;
    }

    private String generateRoomId(RoomType type) {
        return type.name().charAt(0) + "-" + UUID.randomUUID().toString().substring(0, 5);
    }

    public void displayAllocations() {
        System.out.println("\n--- Room Allocations ---\n");

        for (Map.Entry<RoomType, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + " Rooms: " + entry.getValue());
        }
    }
}

class BookingService {
    private Queue<BookingRequest> queue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public BookingService(RoomInventory inventory, RoomAllocationService allocationService) {
        this.queue = new LinkedList<>();
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    public void addRequest(BookingRequest request) {
        queue.offer(request);
        System.out.println("Request added: " + request.getCustomerName() +
                " → " + request.getRoomType());
    }

    public void processRequests() {
        System.out.println("\n--- Processing Requests ---\n");

        while (!queue.isEmpty()) {
            BookingRequest request = queue.poll();

            if (inventory.getAvailability(request.getRoomType()) > 0) {

                String roomId = allocationService.allocateRoom(request.getRoomType());

                if (roomId != null) {
                    inventory.decrement(request.getRoomType());

                    System.out.println("CONFIRMED: " + request.getCustomerName() +
                            " → Room ID: " + roomId);
                } else {
                    System.out.println("FAILED (Duplicate ID): " + request.getCustomerName());
                }

            } else {
                System.out.println("FAILED (No availability): " + request.getCustomerName());
            }
        }
    }
}

public class UseCase6RoomAllocationService {
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" Book My Stay App ");
        System.out.println(" Version: 6.0 ");
        System.out.println("======================================");

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(RoomType.SINGLE, 2);
        inventory.addRoom(RoomType.DOUBLE, 1);

        RoomAllocationService allocationService = new RoomAllocationService();
        BookingService bookingService = new BookingService(inventory, allocationService);

        // Requests
        bookingService.addRequest(new BookingRequest("Alice", RoomType.SINGLE));
        bookingService.addRequest(new BookingRequest("Bob", RoomType.SINGLE));
        bookingService.addRequest(new BookingRequest("Charlie", RoomType.SINGLE)); // fail
        bookingService.addRequest(new BookingRequest("David", RoomType.DOUBLE));
        bookingService.addRequest(new BookingRequest("Eve", RoomType.DOUBLE)); // fail

        bookingService.processRequests();

        inventory.displayInventory();
        allocationService.displayAllocations();

        System.out.println("\nApplication execution completed.");
    }
}
