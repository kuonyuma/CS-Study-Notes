import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TicTacToeServer {

    private static char[][] board = new char[3][3];
    private static final char PLAYER = 'X';
    private static final char AI = 'O';
    private static final char EMPTY = ' ';

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve static files (HTML, CSS, JS)
        server.createContext("/", new StaticFileHandler());

        // API for game moves
        server.createContext("/api/move", new GameHandler());
        server.createContext("/api/restart", new RestartHandler());

        server.setExecutor(null); // default executor
        System.out.println("Server started on http://localhost:" + port);
        initBoard();
        server.start();
    }

    private static void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String uri = t.getRequestURI().toString();
            if (uri.equals("/"))
                uri = "/index.html";

            // Adjust this path to point to your web folder
            File file = new File("a:/mycode/java/2026/1.1/web" + uri);

            if (file.exists() && !file.isDirectory()) {
                String contentType = "text/html";
                if (uri.endsWith(".css"))
                    contentType = "text/css";
                if (uri.endsWith(".js"))
                    contentType = "application/javascript";

                t.getResponseHeaders().set("Content-Type", contentType);
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RestartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            initBoard();
            String response = "{\"status\":\"OK\"}";
            sendJsonResponse(t, response);
        }
    }

    static class GameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
            int row = Integer.parseInt(params.get("row"));
            int col = Integer.parseInt(params.get("col"));

            // Player Move
            if (board[row][col] == EMPTY) {
                board[row][col] = PLAYER;
            }

            // Check if player won or draw
            if (checkWin(PLAYER)) {
                sendJsonResponse(t, "{\"winner\":\"PLAYER\"}");
                return;
            }
            if (isBoardFull()) {
                sendJsonResponse(t, "{\"winner\":\"DRAW\"}");
                return;
            }

            // AI Move
            int[] aiMove = findBestMove();
            board[aiMove[0]][aiMove[1]] = AI;

            String winner = "NONE";
            if (checkWin(AI))
                winner = "AI";
            else if (isBoardFull())
                winner = "DRAW";

            String json = String.format("{\"aiRow\":%d, \"aiCol\":%d, \"winner\":\"%s\"}", aiMove[0], aiMove[1],
                    winner);
            sendJsonResponse(t, json);
        }
    }

    private static void sendJsonResponse(HttpExchange t, String response) throws IOException {
        t.getResponseHeaders().set("Content-Type", "application/json");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    // Minimax Logic
    private static int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1;
        int moveCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = AI;
                    int score = minimax(0, false);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        moveRow = i;
                        moveCol = j;
                    }
                }
            }
        }
        return new int[] { moveRow, moveCol };
    }

    private static int minimax(int depth, boolean isMaximizing) {
        if (checkWin(AI))
            return 10 - depth;
        if (checkWin(PLAYER))
            return depth - 10;
        if (isBoardFull())
            return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = AI;
                        int score = minimax(depth + 1, false);
                        board[i][j] = EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        int score = minimax(depth + 1, true);
                        board[i][j] = EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private static boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY)
                    return false;
            }
        }
        return true;
    }
}
