package data_access;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoConfigTest {

    @Test
    void unreachableServerFailsWithClearMessage() {
        final IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> MongoConfig.connect("mongodb://user:hunter2@localhost:1", "learning_tracker_test"));
        assertTrue(ex.getMessage().contains(MongoConfig.URI_VARIABLE));
        // The message must not leak the password from the connection string.
        assertFalse(ex.getMessage().contains("hunter2"));
    }
}
