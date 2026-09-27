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
                // Hand the connection off to a new thread instead of
                // handling it right here. This lets the server accept
                // the next connection immediately instead of waiting.
                Thread clientThread = new Thread(() -> handleClient(socket));
                clientThread.start();
            }
            catch(Exception e) {
                System.out.println("Server broken");
            }
        }
    }

    private static void handleClient(Socket socket) {
        try {
            HTTPRequest request = new HTTPRequest();
            request.getHTTPRequest(socket);

            HTTPResponse httpResponse = new HTTPResponse();
            httpResponse.populateHttpResponse(request);
            httpResponse.sendToUser(httpResponse, socket);
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        runServerPlease();
    }
}
