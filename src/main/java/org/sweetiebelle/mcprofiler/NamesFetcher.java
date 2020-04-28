package org.sweetiebelle.mcprofiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Gets the previous usernames from Mojang's API Adapted from https://github.com/LogBlock/LogBlock/blob/master/src/main/java/de/diddiz/util/UUIDFetcher.java
 */
public class NamesFetcher {

    /**
     * The response class used to handle the data gotten.
     *
     */
    public static class Response {

        /**
         * The time they changed to at. This is 0 if it is an original name.
         */
        public long changedToAt;

        /**
         * The name
         */
        public String name;

        public Response() {
        }
        public Response(String string, long i) {
            name = string;
            changedToAt = i;
        }
    }
    private static final Gson gson = new Gson();

    /**
     * The API url
     */
    private static final String PROFILE_URL = "https://api.mojang.com/user/profiles/%s/names";

    /**
     * Gets previous names from the UUID
     *
     * @param uuid
     * @return An Array of {@link Response}s
     * @throws IOException
     *             if an error occurs
     * @throws JsonSyntaxException
     *             if an error occurs
     * @throws JsonIOException
     *             if an error occurs
     */
    public static Response[] getPreviousNames(UUID uuid) throws IOException, JsonSyntaxException, JsonIOException {
        return gson.fromJson(new InputStreamReader(createConnection(uuid).getInputStream()), Response[].class);
    }

    /**
     * Gets an {@link HttpURLConnection} from a UUID.
     *
     * @param uuid
     *            the UUID
     * @return the connection
     * @throws IOException
     *             if an error occurs.
     */
    private static HttpURLConnection createConnection(UUID uuid) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(PROFILE_URL, uuid.toString().replaceAll("-", ""))).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
}