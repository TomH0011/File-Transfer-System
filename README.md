# File Transfer System

A secure file transfer system with both command-line and GUI interfaces, featuring AES-256 encryption for secure file transfers between client and server.

## Features

- **AES-256 Encryption**: Secure file transfer with strong encryption
- **GUI Interface**: User-friendly Java Swing interface with drag-and-drop support
- **Command-line Interface**: Traditional server/client command-line tools
- **Cross-platform**: Works on Windows, macOS, and Linux I believe
- **File Selection**: Manual file selection or drag-and-drop functionality
- **Real-time Logging**: Activity logs for monitoring transfers

## Quick Start

### Option 1: GUI Application (Recommended)

1. **Build the project:**
   ```bash
   # Windows
   build.bat
   
   # macOS and Linux too I believe
   ./build.sh
   ```

2. **Run the GUI:**
   ```bash
   java -cp build FileTransferUI
   ```

3. **Using the GUI:**
   - **To Send a File**: Select a file (drag-and-drop or click "Select File"), enter server IP, and click "Send File"
   - **To Receive a File**: Click "Receive File" to start the server and wait for a client connection

### Option 2: Command-line Interface

1. **Build the project** (same as above)

2. **Run the Server:**
   ```bash
   java -cp build Server
   ```
   - Enter the file path when prompted
   - Server will wait for client connection on port 5000

3. **Run the Client:**
   ```bash
   java -cp build Client
   ```
   - Enter server IP (or press Enter for localhost)
   - Enter save directory (or press Enter for Downloads folder)
   - File will be received and saved

## How It Works

1. **Key Exchange**: Server generates an AES-256 key and sends it to the client
2. **Encryption**: File is encrypted using AES-256 before transmission
3. **Transfer**: Encrypted data is sent over the network
4. **Decryption**: Client receives and decrypts the file using the shared key

## Security Features

- **AES-256 Encryption**: Industry-standard encryption algorithm
- **Dynamic Key Generation**: New encryption key for each transfer
- **Secure Key Exchange**: Key is transmitted securely before file transfer

## System Requirements

- Java 8 or higher
- Network connectivity between client and server

## Troubleshooting

- **Connection Issues**: Ensure firewall allows connections on port 5000
- **File Not Found**: Check file paths and permissions
- **Build Errors**: Ensure Java JDK is installed and in PATH

## File Structure

```
File-Transfer-System/
├── src/
│   ├── Server.java          # Command-line server
│   ├── Client.java          # Command-line client
│   └── FileTransferUI.java  # GUI application
├── build.bat                # Windows build script
├── build.sh                 # Linux/macOS build script
└── README.md               # This file
```
