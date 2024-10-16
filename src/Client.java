import java.net.*;
import java.io.*;

public class Client {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 5000);
            System.out.println("Client is connected");

            // Get the input stream to receive the file from server
            InputStream in = client.getInputStream();
            FileOutputStream fileOut = new FileOutputStream("some_other_path_name\\received_file.pdf");

            // Buffer for reading the data in chunks
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Read data from server and write to a file
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            fileOut.close();
            client.close();
            System.out.println("File received and saved.");

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
