import java.util.*;

// -------------------- ADD-ON SERVICE --------------------

class AddOnService {
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

class AddOnServiceManager {
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

class BookingRecord {
    private String reservationId;
    private String guestName;
    private String roomType;

    public BookingRecord(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType);
    }
}

// -------------------- BOOKING HISTORY --------------------

class BookingHistory {
    private List<BookingRecord> history = new ArrayList<>();

    public void addBooking(BookingRecord record) {
        history.add(record);
        System.out.println("Booking added to history: " + record.getReservationId());
    }

    public List<BookingRecord> getAllBookings() {
        return history;
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        if (history.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (BookingRecord record : history) {
            record.display();
        }
    }
}

// -------------------- BOOKING REPORT SERVICE --------------------

class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n=== Booking Report ===");

        List<BookingRecord> bookings = history.getAllBookings();
        System.out.println("Total Confirmed Bookings: " + bookings.size());

        for (BookingRecord record : bookings) {
            record.display();
        }
    }
}

// -------------------- VALIDATION EXCEPTION --------------------

class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

// -------------------- BOOKING VALIDATOR --------------------

class BookingValidator {

    public static void validate(String reservationId, String guestName, String roomType)
            throws ValidationException {

        if (reservationId == null || reservationId.isEmpty()) {
            throw new ValidationException("Reservation ID cannot be empty.");
        }

        if (guestName == null || guestName.length() < 3) {
            throw new ValidationException("Guest name must be at least 3 characters.");
        }

        if (!(roomType.equalsIgnoreCase("Single") ||
              roomType.equalsIgnoreCase("Double") ||
              roomType.equalsIgnoreCase("Deluxe"))) {
            throw new ValidationException("Invalid room type selected.");
        }
    }
}

// -------------------- MAIN DEMO --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        String res1 = "S101";
        String res2 = "D205";

        AddOnServiceManager manager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService wifi = new AddOnService("Premium WiFi", 200);
        AddOnService spa = new AddOnService("Spa Access", 1000);

        manager.addService(res1, breakfast);
        manager.addService(res1, wifi);
        manager.addService(res2, spa);

        manager.displayServices(res1);
        manager.displayServices(res2);

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        try {
            // Valid booking
            BookingValidator.validate(res1, "Arun", "Single");
            BookingRecord b1 = new BookingRecord(res1, "Arun", "Single");
            history.addBooking(b1);

            // Invalid booking example
            BookingValidator.validate(res2, "Me", "Luxury"); // Will cause error
            BookingRecord b2 = new BookingRecord(res2, "Me", "Luxury");
            history.addBooking(b2);

        } catch (ValidationException e) {
            System.out.println("Booking Error: " + e.getMessage());
        }

        // Admin views history & report
        history.displayHistory();
        reportService.generateReport(history);

        System.out.println("\nSystem continues running safely after handling errors ✅");
    }
}
