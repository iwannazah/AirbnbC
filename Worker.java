//import org.json.simple.JSONObject;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.net.Socket;
public class Worker extends Thread {
    private int workerId;
    private HashMap<Integer, Accommodation> accommodations;// apothikeusi katalimaton se domi
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public class Accommodation {
        private int id;
        private String name;
        private String location;
        private double price;
        private int stars;
        private int rating;
        public Accommodation(int id, String name, String location, double price, int stars) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.price = price;
            this.stars = stars;
        }
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getStars() {
            return stars;
        }

        public void setStars(int stars) {
            this.stars = stars;
        }
        public void setRating(int stars) {
            if (stars >= 1 && stars <= 5) {
                this.rating = stars;
            } else {
                System.out.println("Invalid rating. Rating should be between 1 and 5.");
            }
        }
    }
    public Worker(int id,Socket socket) {
        this.workerId = id;
        this.accommodations = new HashMap<>();
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public synchronized void addAccommodation(int accommodationId, Accommodation accommodation) {
        accommodations.put(accommodationId, accommodation);
    }
    public synchronized void serveRequest(String requestType, Object requestData,Object additionalData, ObjectOutputStream out) {
        try {
            switch (requestType) {
                case "reservation":
                    int accommodationId = (int) requestData;
                    boolean success = reserveAccommodation(accommodationId);
                    if (success) {
                        out.writeObject("Accommodation reserved successfully");
                    } else {
                        out.writeObject("Failed to reserve accommodation");
                    }
                    break;
                case "rating":
                    int accommodationIdToRate = (int) requestData;
                    int stars=(int) additionalData;
                    Accommodation accommodationToRate = accommodations.get(accommodationIdToRate);
                    if (accommodationToRate != null) {

                        accommodationToRate.setRating(stars);
                        out.writeObject("Rating submitted successfully");
                    } else {
                        out.writeObject("Accommodation not found");
                    }
                    break;
                case "filter":
                    //dimiourgia case gia tin efarmogi filtron
                    out.writeObject("Filtered accommodations");
                    break;
                default:
                    out.writeObject("Invalid request type");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private HashMap<Integer, Boolean> reservations = new HashMap<>();
    private boolean reserveAccommodation(int accommodationId) {
        if (reservations.containsKey(accommodationId)) {
            boolean isAvailable = reservations.get(accommodationId);
            if (isAvailable) {
                reservations.put(accommodationId, false);
                return true;
            } else {
                return false;
            }
        } else {
            return false; // Επιστρέφουμε true για επιτυχή κράτηση
        }
    }
    public void run() {
        try {
            while (true) {
                String requestType = (String) in.readObject();
                Object requestData = in.readObject();
                Object additionalData = in.readObject();
                serveRequest(requestType, requestData,additionalData, out);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
