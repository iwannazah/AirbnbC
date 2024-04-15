import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;



public class Booker {
    private String serverAddress;
    private int serverPort;

    public Booker(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    //checks if the string is a number
    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //checks if a value is 0 or null. If not, it's added to the JSON array
    private void addToFilterArray(JSONArray filterArray, String key, String value) {
        if (!"0".equals(value) && value != null) {
            JSONObject keyValue = new JSONObject();
            keyValue.put(key, value);
            filterArray.add(keyValue);
        }
    }

    public String filterCreation(){
        String input = null ;

        JSONArray filter = new JSONArray();

        String month_fd = "0";
        String month_ld = "0";
        String firstDay = "0";
        String lastDay = "0";
        String stars = "0";
        String persons = "0";
        String price = "0";
        String area = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println("""
                filter options
                press 'D' for dates
                press 'S' for stars
                press 'N' for number of persons
                press 'P' for max daily price
                press 'A' for Area
                press 'C' to continue""");


        while (!input.equals("C"));{
            input = scanner.nextLine().toUpperCase();

            switch (input) {

                case "D":
                    while (Integer.parseInt(month_fd) < 1 || Integer.parseInt(month_fd) > 12) {
                        System.out.println("Month of first day (a number from 1-12):");
                        month_fd = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(month_fd)) {
                            month_fd = "0";
                        }
                    }

                    while (Integer.parseInt(firstDay) < 1 || Integer.parseInt(firstDay) > 31) {
                        System.out.println("First day (a number from 1-31):");
                        firstDay = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(firstDay)) {
                            firstDay = "0";
                        }
                    }

                    while (Integer.parseInt(month_ld) < 1  || Integer.parseInt(month_ld) > 12) {
                        System.out.println("Month of last day (a number from 1-12):");
                        month_ld = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(month_ld)) {
                            month_ld = "0";
                        }
                    }

                    while (Integer.parseInt(lastDay) < 1 || Integer.parseInt(lastDay) > 31) {
                        System.out.println("Lastt day (a number from 1-31):");
                        lastDay = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(lastDay)) {
                            lastDay = "0";
                        }
                    }
                    break;

                case "S":
                    while (Integer.parseInt(stars) < 1 || Integer.parseInt(stars) > 5) {
                        System.out.println("Stars (a number from 1-5):");
                        stars = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(stars)) {
                            stars = "0";
                        }
                    }
                    break;

                case "N":
                    //checks that the number has a logic value
                    while (Integer.parseInt(persons) < 1) {
                        System.out.println("Number of Persons (a number bigger than 0):");
                        persons = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(stars)) {
                            persons = "0";
                        }
                    }
                    break;

                case "P":
                    while (Integer.parseInt(price) < 1) {
                        System.out.println("Number of Persons (a number bigger than 0):");
                        price = scanner.nextLine();

                        //makes sure that the variable isn't a character
                        if (!isNumeric(stars)) {
                            price = "0";
                        }
                    }
                    break;

                case "A":
                    System.out.println("Area:");
                    area = scanner.nextLine();
                    break;
            }

            if(!input.equals("C")){
                System.out.println("Please press C to continue or something from the menu to add more filters");
            }
        }

        addToFilterArray(filter, "month_fd", month_fd);
        addToFilterArray(filter, "month_ld", month_ld);
        addToFilterArray(filter, "firstDay", firstDay);
        addToFilterArray(filter, "lastDay", lastDay);
        addToFilterArray(filter, "stars", stars);
        addToFilterArray(filter, "persons", persons);
        addToFilterArray(filter, "price", price);
        addToFilterArray(filter, "area", area);

        // Creating a JSON object to hold the filter array
        JSONObject filterObject = new JSONObject();
        filterObject.put("filter", filter);

        // Returning the JSON string representation of the filter object
        return filterObject.toJSONString();
    }

    public void search() {
        try {
            // Create a socket connection to the Master
            Socket socket = new Socket(serverAddress, serverPort);

            // Create an ObjectOutputStream to send the file path
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            String filter = filterCreation();

            outputStream.writeObject(filter);
            outputStream.flush();

            // Close the output stream and socket
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void book(String filePath){
        try{
            // Create a socket connection to the Master
            Socket socket = new Socket(serverAddress, serverPort);

            // Create an ObjectOutputStream to send the file path
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(filePath);
            outputStream.flush();

            // Close the output stream and socket
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Set the server address and port
        String serverAddress = "localhost";
        int serverPort = 8080;

        // Create a booker instance
        Booker booker = new Booker(serverAddress, serverPort);

        Scanner scanner = new Scanner(System.in);
        String input;

        do {
            System.out.println("Please Press 'B' for booking or 'F' for filters");
            // Convert input to uppercase to handle case-insensitive comparison
            input = scanner.nextLine().toUpperCase(); // Convert input to uppercase to handle case-insensitive comparison
        } while (!input.equals("B") && !input.equals("F"));

        if (input.equals("F")){

        }
    }
}
