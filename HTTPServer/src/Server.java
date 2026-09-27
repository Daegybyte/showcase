import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void runServerPlease() throws IOException {
        int serverPort = 8080;
        ServerSocket myServerSocket = new ServerSocket(serverPort);
        System.out.println("Listening on Port " + serverPort);

        while (true) {
            try {
                Socket socket = myServerSocket.accept();

                //hand this connection off to its own thread so we can go accept the next one right away
                Thread clientThread = new Thread(() -> handleClient(socket));
                clientThread.start();
            }
            catch(Exception e) {
                System.out.println("Server broken");
            }
        }
    }

    //handles one client's request/response, runs on its own thread
    private static void handleClient(Socket socket) {
        try {
            HTTPRequest request = new HTTPRequest();
            request.getHTTPRequest(socket);

            HTTPResponse httpResponse = new HTTPResponse();
            httpResponse.populateHttpResponse(request);
            httpResponse.sendToUser(httpResponse, socket);
        } catch (IOException e) {
            //client probably disconnected or sent something weird, not the end of the world
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        runServerPlease();
    }
}
