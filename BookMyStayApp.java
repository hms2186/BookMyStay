import java.util.*;
import java.io.*;

// -------------------- ADD-ON SERVICE --------------------
class AddOnService implements Serializable {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    public void display() {
        System.out.println(name + " (₹" + price + ")");
    }
}

// -------------------- ADD-ON SERVICE MANAGER --------------------
class AddOnServiceManager implements Serializable {
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
        System.out.println(service.getName() + " added to Reservation " + reservationId);
    }

    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getPrice();
        }
        return total;
    }

    public void displayServices(String reservationId) {
        System.out.println("\n=== Add-On Services for " + reservationId + " ===");
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService s : services) {
            s.display();
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// -------------------- BOOKING RECORD --------------------
class BookingRecord implements Serializable {
    private String reservationId;
    private String guestName;
    private String roomType;

    public BookingRecord(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType);
    }
}

// -------------------- BOOKING HISTORY --------------------
class BookingHistory implements Serializable {
    private List<BookingRecord> history = new ArrayList<>();

    public synchronized void addBooking(BookingRecord record) {
        history.add(record);
        System.out.println("Booking added: " + record.getReservationId());
    }

    public synchronized BookingRecord findBooking(String reservationId) {
        for (BookingRecord record : history) {
            if (record.getReservationId().equals(reservationId)) {
                return record;
            }
        }
        return null;
    }

    public synchronized void removeBooking(String reservationId) {
        history.removeIf(record -> record.getReservationId().equals(reservationId));
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        for (BookingRecord record : history) {
            record.display();
        }
    }

    public List<BookingRecord> getAllBookings() {
        return history;
    }
}

// -------------------- BOOKING REPORT SERVICE --------------------
class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n=== Booking Report ===");
        System.out.println("Total Confirmed Bookings: " + history.getAllBookings().size());
    }
}

// -------------------- VALIDATION --------------------
class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

class BookingValidator {
    public static void validate(String reservationId, String guestName, String roomType)
            throws ValidationException {

        if (reservationId == null || reservationId.isEmpty())
            throw new ValidationException("Invalid Reservation ID");

        if (guestName.length() < 3)
            throw new ValidationException("Guest name too short");

        if (!(roomType.equals("Single") || roomType.equals("Double") || roomType.equals("Deluxe")))
            throw new ValidationException("Invalid Room Type");
    }
}

// -------------------- INVENTORY --------------------
class Inventory implements Serializable {
    private Map<String, Integer> rooms = new HashMap<>();

    public Inventory() {
        rooms.put("Single", 2);
        rooms.put("Double", 2);
        rooms.put("Deluxe", 1);
    }

    public synchronized boolean allocateRoom(String roomType) {
        if (rooms.get(roomType) > 0) {
            rooms.put(roomType, rooms.get(roomType) - 1);
            return true;
        }
        return false;
    }

    public synchronized void increaseRoom(String roomType) {
        rooms.put(roomType, rooms.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("\n=== Inventory ===");
        for (String type : rooms.keySet()) {
            System.out.println(type + ": " + rooms.get(type));
        }
    }
}

// -------------------- CANCELLATION SERVICE --------------------
class CancellationService {
    public void cancelBooking(String reservationId, BookingHistory history, Inventory inventory) {
        BookingRecord record = history.findBooking(reservationId);

        if (record == null) {
            System.out.println("Cancellation failed: Not found");
            return;
        }

        inventory.increaseRoom(record.getRoomType());
        history.removeBooking(reservationId);

        System.out.println("Booking " + reservationId + " cancelled.");
    }
}

// -------------------- CONCURRENT BOOKING --------------------
class BookingRequest {
    String reservationId;
    String guestName;
    String roomType;

    public BookingRequest(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

class ConcurrentBookingProcessor extends Thread {
    private BookingQueue queue;
    private Inventory inventory;
    private BookingHistory history;

    public ConcurrentBookingProcessor(BookingQueue queue, Inventory inventory, BookingHistory history) {
        this.queue = queue;
        this.inventory = inventory;
        this.history = history;
    }

    public void run() {
        while (true) {
            BookingRequest req = queue.getRequest();
            if (req == null) break;

            synchronized (inventory) {
                if (inventory.allocateRoom(req.roomType)) {
                    BookingRecord record = new BookingRecord(req.reservationId, req.guestName, req.roomType);
                    history.addBooking(record);
                    System.out.println(Thread.currentThread().getName() +
                            " booked room for " + req.guestName);
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " booking failed for " + req.guestName + " (No rooms)");
                }
            }
        }
    }
}

// -------------------- PERSISTENCE SERVICE (UC12) --------------------
class PersistenceService {

    public static void saveSystem(BookingHistory history, Inventory inventory) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("system_data.ser"));
            out.writeObject(history);
            out.writeObject(inventory);
            out.close();
            System.out.println("System state saved to file.");
        } catch (Exception e) {
            System.out.println("Error saving system state.");
        }
    }

    public static Object[] loadSystem() {
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("system_data.ser"));
            BookingHistory history = (BookingHistory) in.readObject();
            Inventory inventory = (Inventory) in.readObject();
            in.close();
            System.out.println("System state loaded from file.");
            return new Object[]{history, inventory};
        } catch (Exception e) {
            System.out.println("No previous saved data found.");
            return null;
        }
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        Inventory inventory = new Inventory();

        // UC12 - Load previous state
        Object[] data = PersistenceService.loadSystem();
        if (data != null) {
            history = (BookingHistory) data[0];
            inventory = (Inventory) data[1];
            System.out.println("Previous system state restored.");
        }

        AddOnServiceManager manager = new AddOnServiceManager();
        BookingReportService report = new BookingReportService();
        CancellationService cancelService = new CancellationService();

        // UC7 Add-ons
        manager.addService("S101", new AddOnService("Breakfast", 300));
        manager.displayServices("S101");

        // UC8 + UC9 Booking
        try {
            BookingValidator.validate("S101", "Arun", "Single");
            if (inventory.allocateRoom("Single")) {
                history.addBooking(new BookingRecord("S101", "Arun", "Single"));
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        history.displayHistory();
        report.generateReport(history);

        // UC10 Cancellation
        cancelService.cancelBooking("S101", history, inventory);
        history.displayHistory();

        // UC11 Concurrent Booking
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new BookingRequest("B1", "John", "Single"));
        queue.addRequest(new BookingRequest("B2", "Mary", "Single"));
        queue.addRequest(new BookingRequest("B3", "David", "Single"));

        ConcurrentBookingProcessor t1 =
                new ConcurrentBookingProcessor(queue, inventory, history);
        ConcurrentBookingProcessor t2 =
                new ConcurrentBookingProcessor(queue, inventory, history);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {}

        history.displayHistory();
        inventory.displayInventory();

        // UC12 - Save system state before shutdown
        PersistenceService.saveSystem(history, inventory);

        System.out.println("\nSystem finished execution.");
    }
}
