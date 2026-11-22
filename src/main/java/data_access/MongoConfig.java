package data_access;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

/**
 * Opens the connection to the MongoDB database.
 *
 * <p>The connection string and database name come from environment variables, so that
 * passwords are never written into the code or committed to Git:</p>
 * <ul>
 *     <li>{@code MONGODB_URI}: for example {@code mongodb+srv://user:password@cluster0.xxxxx.mongodb.net};
 *         defaults to a MongoDB server running on this computer</li>
 *     <li>{@code MONGODB_DATABASE}: defaults to {@value #DEFAULT_DATABASE}</li>
 * </ul>
 */
public final class MongoConfig {

    public static final String URI_VARIABLE = "MONGODB_URI";
    public static final String DATABASE_VARIABLE = "MONGODB_DATABASE";
    public static final String DEFAULT_URI = "mongodb://localhost:27017";
    public static final String DEFAULT_DATABASE = "learning_tracker";

    /** How long to wait for the server before giving up, so a wrong URI fails quickly. */
    private static final int TIMEOUT_SECONDS = 5;

    private MongoConfig() {
    }

    /**
     * Connects to the database named by the environment variables, or to the defaults.
     * @return the connected database
     * @throws IllegalStateException if the server cannot be reached
     */
    public static MongoDatabase connectFromEnvironment() {
        return connect(valueOrDefault(System.getenv(URI_VARIABLE), DEFAULT_URI),
                valueOrDefault(System.getenv(DATABASE_VARIABLE), DEFAULT_DATABASE));
    }

    /**
     * Connects to the given database and checks that the server answers.
     * @param uri the MongoDB connection string
     * @param databaseName the name of the database to use
     * @return the connected database
     * @throws IllegalStateException if the server cannot be reached
     */
    public static MongoDatabase connect(String uri, String databaseName) {
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .applyToClusterSettings(builder ->
                        builder.serverSelectionTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS))
                .build();
        final MongoClient client = MongoClients.create(settings);
        final MongoDatabase database = client.getDatabase(databaseName);
        try {
            database.runCommand(new Document("ping", 1));
        }
        catch (MongoException ex) {
            client.close();
            // The URI is not included in the message because it may contain a password.
            throw new IllegalStateException("Could not connect to MongoDB. Check that the server is running "
                    + "and that " + URI_VARIABLE + " is set correctly.", ex);
        }
        return database;
    }

    private static String valueOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}
