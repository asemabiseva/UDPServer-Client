import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.*;

public class UDPServer {

    private HashMap<String, String> storage = new HashMap<>();
    private TreeSet<String> set_keys = new TreeSet<>();


    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.start();
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(2222)) {
            System.out.println("Server started on port " + 2222);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                ByteArrayInputStream byteStream = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream objectInput = new ObjectInputStream(byteStream);
                Object object = objectInput.readObject();
                
                if (object instanceof String[]) {
                    String[] request = (String[]) object;
                    String response = processRequest(request);

                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
                    objectOutput.writeObject(response);
                    byte[] sendData = byteOutput.toByteArray();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            receivePacket.getAddress(), receivePacket.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String processRequest(String[] request) {
        String response = "";

        switch (request[0]) {
            case "GET":
                response = get(request[1]);
                break;
            case "PUT":
                response = put(request[1], request[2]);
                break;
            case "KEYS":
                response = keys();
                break;
            case "DELETE":
                response = delete(request[1]);
                break;
            case "UPDATE": 
                response = update(request[1], request[2]);
                break;
            case "CLEAR": 
                response = clear();
                break;
            case "SEARCH":
                response = search(request[1]);
                break;
            default:
                response = "Invalid command format.";
        }

        return response;
    }

    public synchronized <T> String get(T key) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exist in the store";
        } else {
            return "For key: " + key + " value: " + storage.get(key);
        }
    }

    public synchronized <T> String put(T key, T value) {
        if (storage.containsKey(key)) {
            return "The key [" + key + "] already exists in the store";
        } else {
            storage.put((String)key, (String)value);
            set_keys.add((String)key);
            return "Key-Value pair saved successfully. Key - " + key + ", Value - " + value;
        }
    }

    public synchronized <T> String delete(T key) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exist in the store";
        } else {
            storage.remove(key);
            return "The key [" + key + "] deleted successfully";
        }
    }

    public synchronized <T> String update(T key, T new_value) {
        if (!storage.containsKey(key)) {
            return "The key [" + key + "] does not exists in the store";
        } else {
            storage.replace((String)key, (String)new_value);
            return "Key-Value pair updated successfully. Key - " + key + ", New Value - " + new_value;
        }
    }

    public synchronized <T> String clear() {
        storage.clear();
        return "The storage is cleaned successfully";
    }

    public synchronized String keys() {
        StringBuilder result = new StringBuilder("All KEYS:");
        if (storage.isEmpty()) {
            result.append("\nThe storage is empty");
        } else {
            int i = 1;
            for (String key : storage.keySet()) {
                result.append("\nKey(").append(i).append(") - ").append(key);
                i++;
            }
        }
        return result.toString();
    }

    public synchronized <T> String search(T key)
    {
        List<String> s = new ArrayList<>();
        if (storage.isEmpty()) {
            s.add("The storage is empty");
        } 
        else {

            s.add("SEARCH RESULTS:");
            int i=0;
            for (String k : set_keys) {
                if(k.startsWith((String)key))
                {
                    s.add(k);
                    i++;
                }
            }
            if(i==0)
            {
                s.add("Nothing Found");
            }
        }

        String ans="";
        for(String i : s)
        {
            ans+="\n"+i;
        }
        return ans;
    }
}
