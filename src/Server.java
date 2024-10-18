import java.net.*;
// for input and output streams
import java.io.*;
// Encrypting and decrypting
import javax.crypto.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class Server {

    // AES Algorithm and key for convenience
    private static final String ALGORITHM = "AES";

    public static void main(String[] args) {

        SecretKey aesKey = null; // Declare the AES key here
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {

            // Constructing the key generator class
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);

            keyGen.init(128); // AES 128-bit key size

            // Dynamically creating the AES key
            aesKey = keyGen.generateKey(); // Generate the AES key


            // Create the Server Socket with the port number 5000
            ServerSocket ss = new ServerSocket(5000);
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
            // Input streams read the contents as a stream of bytes
            FileInputStream fileIn = new FileInputStream(file);

            // Get the output stream to send the file to client
            // This is a nice way to send packets of data over a connection
            OutputStream out = server.getOutputStream();

            // Now to take our output stream and our cipher and encode it
            CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);

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
            // Want to close the cipher now as well
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

