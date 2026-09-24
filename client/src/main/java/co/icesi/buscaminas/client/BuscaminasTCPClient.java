package co.icesi.buscaminas.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import co.icesi.buscaminas.client.dtos.Request;
import co.icesi.buscaminas.client.dtos.Response;
import co.icesi.buscaminas.client.model.Cell;

public class BuscaminasTCPClient {

    private final Gson gson = new GsonBuilder().create();

    private final String host;
    private final int port;

    public BuscaminasTCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Response sendRequest(String host, int port, Request request) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String jsonOut = gson.toJson(request);
            writer.write(jsonOut);
            writer.newLine();
            writer.flush();

            String jsonIn = reader.readLine();
            if (jsonIn == null) {
                throw new IOException("el servidor cerro la conexion sin enviar respuesta");
            }
            try {
                return gson.fromJson(jsonIn, Response.class);
            } catch (JsonSyntaxException e) {
                throw new IOException("respuesta JSON malformada: " + jsonIn, e);
            }
        }
    }

    public Response initGame(int n, int m, int mines) throws IOException {
        return send(new Request("INIT_GAME").with("n", n).with("m", m).with("minas", mines));
    }

    public Response selectCell(int i, int j) throws IOException {
        return send(new Request("SELECT_CELL").with("i", i).with("j", j));
    }

    public Response markCell(int i, int j) throws IOException {
        return send(new Request("MARK_CELL").with("i", i).with("j", j));
    }

    public Response getBoard() throws IOException {
        return send(new Request("GET_BOARD"));
    }

    public Response showAll() throws IOException {
        return send(new Request("SOW_ALL"));
    }

    public Cell[][] boardOf(Response response) {
        Object raw = response == null || response.data == null ? null : response.data.get("board");
        return raw == null ? null : gson.fromJson(gson.toJsonTree(raw), Cell[][].class);
    }

    public boolean flagOf(Response response, String key) {
        Object raw = response == null || response.data == null ? null : response.data.get(key);
        return Boolean.TRUE.equals(raw);
    }

    public String messageOf(Response response) {
        Object raw = response == null || response.data == null ? null : response.data.get("message");
        return raw == null ? null : String.valueOf(raw);
    }

    public boolean isError(Response response) {
        return response == null || "ERROR".equals(response.status);
    }

    private Response send(Request request) throws IOException {
        return sendRequest(host, port, request);
    }
}
