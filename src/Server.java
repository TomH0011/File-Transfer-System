import java.net.*;
import java.io.*;


public class Server {
    public static void main(String[] args) {
        try {
            // Create the Server Socket with the port number 5000
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Waiting for client");
            Socket server = ss.accept();
            System.out.println("Sever connected to the Client");

            // Initialise the file we want to send
            File file = new File("file_path_name");
            // turns the file into an InputStream
            // Input streams read the contents as a stream of bytes
            FileInputStream fileIn = new FileInputStream(file);

            // Get the output stream to send the file to client
            // This is a nice way to send packets of data over a connection
            OutputStream out = server.getOutputStream();


            // This is to read the file in chunks of size 4096 bytes to be more efficient
            // buffer temporarily stores these chunks as it's reading before sending them
            byte[] buffer = new byte[4096];

            int bytesRead;

            // While loop that only ends when buffer has read all the bytes in the data
            while((bytesRead = fileIn.read(buffer)) != -1) {
                // Sending all the data
                // Total bytes to send is buffer
                // Starting from 0 bytes
                // Up to bytesRead amount of bytes
                // This way it will only send read data
                out.write(buffer, 0, bytesRead);
            }

            // Now to free up resources
            fileIn.close();
            server.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
