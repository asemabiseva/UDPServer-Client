import java.io.*;
import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        System.out.println("Client started");
        while (true) {
            try {
                System.out.println("\nPlease Input Command in either of the following forms:\nGET <key> \nPUT <key> <val> \nDELETE <key> \nSEARCH <key> \nUPDATE <key> <new val> \nKEYS \nCLEAR \nQUIT \nEnter Command:");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String userInput = reader.readLine();
                String[] words = inputHandling(userInput);

                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
                objectOutput.writeObject(words);
                byte[] sendData = byteOutput.toByteArray();

                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(1000);
                InetAddress serverAddress = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 2222);
                socket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                ByteArrayInputStream byteInput = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream objectInput = new ObjectInputStream(byteInput);
                String response = (String) objectInput.readObject();
                System.out.println(response);

                socket.close();
            } catch (SocketTimeoutException e) {
                System.out.println("Exception: [Unknown IO Error. Command Not Successful]");
            } catch (IOException | ClassNotFoundException | WrongFormatException | LongKeyException e) {
                System.out.println("Exception: [" + e.getMessage() + "]");
            }

        }
    }

    public static String[] inputHandling(String s) throws WrongFormatException, LongKeyException {
        String[] words = s.split("\\s+");
        switch (words[0]) {
            case "GET":
                if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "PUT":
                if (words.length != 3) {
                    throw new WrongFormatException("Invalid command format.");
                }
                if (words[1].length() > 10) {
                    throw new LongKeyException("Key length should not exceed 10 characters.");
                }
                break;
            case "KEYS":
                if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "QUIT":
                if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                } else {
                    System.exit(0);
                }
                break;
            case "DELETE":
                if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "UPDATE": 
                if (words.length != 3) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "CLEAR": 
                if (words.length != 1) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            case "SEARCH": 
                if (words.length != 2) {
                    throw new WrongFormatException("Invalid command format.");
                }
                break;
            default:
                throw new WrongFormatException("Invalid command format.");
        }
        return words;
    }
}
