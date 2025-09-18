import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileTransferUI extends JFrame implements DropTargetListener {
    
    private static final String ALGORITHM = "AES";
    private JPanel mainPanel;
    private JTextArea logArea;
    private JTextField serverIPField;
    private JTextField portField;
    private JButton selectFileButton;
    private JButton sendButton;
    private JButton receiveButton;
    private JLabel statusLabel;
    private JLabel dropZoneLabel;
    private File selectedFile;
    private ExecutorService executor;
    
    public FileTransferUI() {
        initializeUI();
        executor = Executors.newCachedThreadPool();
    }
    
    private void initializeUI() {
        setTitle("File Transfer System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel for controls
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Center panel for file drop zone
        JPanel dropPanel = createDropPanel();
        mainPanel.add(dropPanel, BorderLayout.CENTER);
        
        // Bottom panel for log
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createTitledBorder("Connection Settings"));
        
        // Server IP
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        serverIPField = new JTextField("localhost", 15);
        panel.add(serverIPField, gbc);
        
        // Port
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        portField = new JTextField("5000", 8);
        panel.add(portField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(e -> selectFile());
        panel.add(selectFileButton, gbc);
        
        gbc.gridx = 1;
        sendButton = new JButton("Send File");
        sendButton.addActionListener(e -> sendFile());
        sendButton.setEnabled(false);
        panel.add(sendButton, gbc);
        
        gbc.gridx = 2;
        receiveButton = new JButton("Receive File");
        receiveButton.addActionListener(e -> receiveFile());
        panel.add(receiveButton, gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(statusLabel, gbc);
        
        return panel;
    }
    
    private JPanel createDropPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("File Drop Zone"));
        panel.setBackground(new Color(240, 240, 240));
        
        dropZoneLabel = new JLabel("<html><center>Drag and drop a file here<br/>or use the 'Select File' button</center></html>", 
                                  JLabel.CENTER);
        dropZoneLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        dropZoneLabel.setForeground(Color.GRAY);
        panel.add(dropZoneLabel, BorderLayout.CENTER);
        
        // Enable drag and drop
        new DropTarget(panel, this);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        
        logArea = new JTextArea(8, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            updateFileSelection();
        }
    }
    
    private void updateFileSelection() {
        if (selectedFile != null) {
            dropZoneLabel.setText("<html><center>Selected: " + selectedFile.getName() + 
                                 "<br/>Size: " + formatFileSize(selectedFile.length()) + 
                                 "<br/><br/>Click 'Send File' to transfer</center></html>");
            dropZoneLabel.setForeground(Color.BLUE);
            sendButton.setEnabled(true);
            log("File selected: " + selectedFile.getAbsolutePath());
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    private void sendFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        executor.submit(() -> {
            try {
                sendFileToServer();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("Error sending file: " + e.getMessage());
                    statusLabel.setText("Error: " + e.getMessage());
                });
            }
        });
    }
    
    private void sendFileToServer() throws Exception {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Connecting to server...");
            log("Connecting to " + serverIPField.getText() + ":" + portField.getText());
        });
        
        String serverIP = serverIPField.getText().trim();
        if (serverIP.isEmpty()) serverIP = "localhost";
        int port = Integer.parseInt(portField.getText().trim());
        
        try (Socket socket = new Socket(serverIP, port)) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Connected. Sending file...");
                log("Connected to server");
            });
            
            // Generate AES key
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey aesKey = keyGen.generateKey();
            
            // Send key first
            OutputStream keyOut = socket.getOutputStream();
            keyOut.write(aesKey.getEncoded());
            keyOut.flush();
            
            SwingUtilities.invokeLater(() -> log("AES key sent to server"));
            
            // Create cipher for encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            
            // Send file
            try (FileInputStream fileIn = new FileInputStream(selectedFile);
                 CipherOutputStream cipherOut = new CipherOutputStream(socket.getOutputStream(), cipher)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                final long[] totalBytes = {0};
                
                SwingUtilities.invokeLater(() -> log("Sending file: " + selectedFile.getName()));
                
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    cipherOut.write(buffer, 0, bytesRead);
                    totalBytes[0] += bytesRead;
                }
                
                final long finalTotalBytes = totalBytes[0];
                SwingUtilities.invokeLater(() -> {
                    log("File sent successfully (" + formatFileSize(finalTotalBytes) + ")");
                    statusLabel.setText("File sent successfully");
                });
            }
        }
    }
    
    private void receiveFile() {
        executor.submit(() -> {
            try {
                receiveFileFromServer();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("Error receiving file: " + e.getMessage());
                    statusLabel.setText("Error: " + e.getMessage());
                });
            }
        });
    }
    
    private void receiveFileFromServer() throws Exception {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Starting server...");
            log("Starting file transfer server on port " + portField.getText());
        });
        
        int port = Integer.parseInt(portField.getText().trim());
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Waiting for client connection...");
                log("Server listening on port " + port);
            });
            
            try (Socket clientSocket = serverSocket.accept()) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Client connected. Receiving file...");
                    log("Client connected from: " + clientSocket.getInetAddress());
                });
                
                // Receive AES key
                InputStream keyIn = clientSocket.getInputStream();
                byte[] keyBytes = new byte[32];
                int bytesRead = keyIn.read(keyBytes);
                if (bytesRead < 32) {
                    throw new IOException("Key received is too short");
                }
                
                SecretKey aesKey = new SecretKeySpec(keyBytes, ALGORITHM);
                SwingUtilities.invokeLater(() -> log("AES key received from client"));
                
                // Create cipher for decryption
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                
                // Receive file
                try (CipherInputStream cipherIn = new CipherInputStream(clientSocket.getInputStream(), cipher)) {
                    
                    // Choose save location
                    JFileChooser saveChooser = new JFileChooser();
                    saveChooser.setDialogTitle("Save received file as...");
                    saveChooser.setSelectedFile(new File("received_file_" + System.currentTimeMillis()));
                    
                    SwingUtilities.invokeLater(() -> {
                        int result = saveChooser.showSaveDialog(this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            // Continue with file saving in a separate thread
                            executor.submit(() -> {
                                try {
                                    saveReceivedFile(cipherIn, saveChooser.getSelectedFile());
                                } catch (Exception e) {
                                    SwingUtilities.invokeLater(() -> {
                                        log("Error saving file: " + e.getMessage());
                                        statusLabel.setText("Error saving file");
                                    });
                                }
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                log("File save cancelled");
                                statusLabel.setText("File save cancelled");
                            });
                        }
                    });
                }
            }
        }
    }
    
    private void saveReceivedFile(InputStream inputStream, File saveFile) throws Exception {
        try (FileOutputStream fileOut = new FileOutputStream(saveFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            final long[] totalBytes = {0};
            
            SwingUtilities.invokeLater(() -> log("Receiving file..."));
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalBytes[0] += bytesRead;
            }
            
            final long finalTotalBytes = totalBytes[0];
            SwingUtilities.invokeLater(() -> {
                log("File received and saved: " + saveFile.getName() + " (" + formatFileSize(finalTotalBytes) + ")");
                statusLabel.setText("File received successfully");
            });
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    // DropTargetListener implementation
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // Do nothing
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // Do nothing
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
        // Do nothing
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                
                if (!files.isEmpty()) {
                    selectedFile = files.get(0);
                    updateFileSelection();
                }
                
                dtde.dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (UnsupportedFlavorException | IOException e) {
            dtde.rejectDrop();
            log("Error handling dropped file: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileTransferUI().setVisible(true);
        });
    }
}
