import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;


public class Master {
    //Define port numbers for client and worker communication
    private static int CLIENT_PORT;
    private static int WORKER_PORT;
    private static int numWorkers; //Number of worker nodes

    private static List<Socket> workerSockets; //List to store results
    private ServerSocket clientSS; //Server socket for client connection
    private static ServerSocket  workerSS; //Server socket for worker connection
    private Socket workerSocket;


    //Master constructor
    public Master(Socket workerSocket) {
        //this.workerSockets = new ArrayList<>();
        this.workerSocket = workerSocket;
        //initializeWorkers(numWorkers);
        //this.results = new ArrayList<>();
    }

    /*
    //method to initialize worker connections
    private void initializeWorkers(int numWorkers) {
        try {
            for (int i = 0; i < numWorkers; i++) {
                Socket workerSocket = workerSS.accept(); // accept worker connections
                System.out.println("Worker connected: " + workerSocket);
                //add worker socket to the list
                workerSockets.add(workerSocket);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    

    public void start() {
        try {
            //Start the client server socket
            clientSS = new ServerSocket(CLIENT_PORT);
            System.out.println("Client server started on port" + CLIENT_PORT);

            //Start the worker server socket
            //workerSS = new ServerSocket(WORKER_PORT);
            //System.out.println("Worker server started on port" + WORKER_PORT);

            while (true) {
                //accepting a client connection
                Socket clientSocket = clientSS.accept();
                System.out.println("Client connected: " + clientSocket);

                //create a new client handler thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Private class to handle client connections
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        //constructor to initialize client socket
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        //handle client requests
        public void run() {
            try {
                //Receive the client request type
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                String requestType = (String) inputStream.readObject(); //string type

                //Handling client requests
                switch (requestType) {
                    case "F": //handle accomondation filtering request
                        handleFilterRequest(inputStream);
                        break;
                    case "B":
                        handleBookRequest(inputStream);
                        break;
                    default:
                        System.out.println("Unknown request type received.");

                }
                //clientSocket.close();
                //System.out.println("Client connection closed");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFilterRequest(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        //Read filter parameters from client
        //Assuming : List of filters
        List<String> filters = (List<String>) inputStream.readObject();

        //send filter parameters to all workers for processing
        for (Socket workerSocket : workerSockets) {
            ObjectOutputStream outputStream = new ObjectOutputStream(workerSocket.getOutputStream());
            outputStream.writeObject(filters);
            outputStream.flush();
        }

        //receive filtered results from workers and send them back to the client
        //SEND FILTERED RESULTS BACK TO THE CLIENT
    }

    private void handleBookRequest(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        //Read booking details from the client
        //Assuming : Booking object
        Booking booking = (Booking) inputStream.readObject();
        //Send the booking details to the appropriate worker for processing
        //DETERMINE THE WORKER BASED ON THE ACCOMMONDATION
        Socket workerSocket = workerSockets.get(0); ////thelei diorthosi
        ObjectOutputStream outputStream = new ObjectOutputStream(workerSocket.getOutputStream());
        outputStream.writeObject(booking);
        outputStream.flush();

        //receive confirmation from the worker and send it back to the client
        //RECEIVING INFO AND SEND IT BACK
    }

    public static void main(String [] args) {

        //Extra. Take info from config file.
        String dir  = System.getProperty("user.dir");
        try{
            String configFilePath = dir+"/config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties props = new Properties();
            props.load(propsInput);
            numWorkers = Integer.parseInt(props.getProperty("WORKER_NUMBER"));
            CLIENT_PORT = Integer.parseInt(props.getProperty("CLIENT_PORT"));
            WORKER_PORT = Integer.parseInt(props.getProperty("WORKER_PORT"));
            

        }catch(Exception E){
            E.printStackTrace(System.out);
        } 

        //Extra.  Create Server Socket for Worker
        try {
			workerSS = new ServerSocket( WORKER_PORT);
            
            /* Accept the connection for each worker */
            for(int i=0;i<numWorkers;i++){
                Socket workerSocket = workerSS.accept();
                System.out.println("Worker server started on port" + WORKER_PORT);
                workerSockets.add(workerSocket);
                Master master = new Master(workerSocket);
                master.start();
            }
			
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
		}
        //Master master = new Master(numWorkers);
        //master.start();
    }

}
