import java.net.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class Server {

    // AES Algorithm and key for convenience
    private static final String ALGORITHM = "AES";

    public static void main(String[] args) {

        SecretKey aesKey = null;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {

            // Constructing the key generator class
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);

            keyGen.init(256); // AES 128-bit key size

            // Dynamically creating the AES key
            aesKey = keyGen.generateKey(); // Generate the AES key


            // Create the Server Socket with the port number 5000
            // Server for a local host
            // ServerSocket ss = new ServerSocket(5000);
            // Server for
            ServerSocket ss = new ServerSocket(5000, 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Waiting for client");
            Socket server = ss.accept();
            System.out.println("Sever connected to the Client");

            // Initialise the file we want to send
            File file = new File("Some File Path");

            // Create the cipher object for AES 128 Encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // This initializes the Cipher object to be ready for encryption
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

            // While loop that only ends when buffer has read all the bytes in the data
            while((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // Now to free up resources
            fileIn.close();
            cipherOut.close();
            server.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Method to send the AES key to the client
    private static void sendKey(Socket clientSocket, SecretKey aesKey) throws IOException {
        OutputStream keyOut = clientSocket.getOutputStream();
        keyOut.write(aesKey.getEncoded()); // Send the key bytes to the client
        keyOut.flush(); // Flush the output stream
    }
}

