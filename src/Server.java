import java.net.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Scanner;

public class Server {

    // AES Algorithm and key for convenience
    private static final String ALGORITHM = "AES";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("File Transfer Server");
        System.out.print("Enter file path to send: ");
        String filePath = scanner.nextLine();
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            scanner.close();
            return;
        }

        SecretKey aesKey = null;

        try {
            // Constructing the key generator class
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // AES 256-bit key size
            aesKey = keyGen.generateKey(); // Generate the AES key

            // Create the Server Socket with the port number 5000
            ServerSocket ss = new ServerSocket(5000, 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Server waiting for client on port 5000...");
            Socket server = ss.accept();
            System.out.println("Server connected to the Client");

            // Send the AES key to client first
            sendKey(server, aesKey);
            System.out.println("AES key sent to client");

            // Create the cipher object for AES 256 Encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            // turns the file into an InputStream
            FileInputStream fileIn = new FileInputStream(file);

            // Get the output stream to send the file to client
            OutputStream out = server.getOutputStream();

            // Now to take our output stream and our cipher and encode it
            CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);

            // buffer temporarily stores these chunks as it's reading before sending them
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;

            System.out.println("Sending file: " + file.getName() + " (" + file.length() + " bytes)");

            // While loop that only ends when buffer has read all the bytes in the data
            while((bytesRead = fileIn.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead); // Fixed: use cipherOut instead of out
                totalBytes += bytesRead;
            }

            System.out.println("File sent successfully (" + totalBytes + " bytes)");

            // Now to free up resources
            fileIn.close();
            cipherOut.close();
            server.close();
            ss.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // Method to send the AES key to the client
    private static void sendKey(Socket clientSocket, SecretKey aesKey) throws IOException {
        OutputStream keyOut = clientSocket.getOutputStream();
        byte[] keyBytes = aesKey.getEncoded();
        keyOut.write(keyBytes); // Send the key bytes to the client
        keyOut.flush(); // Flush the output stream
    }
}

