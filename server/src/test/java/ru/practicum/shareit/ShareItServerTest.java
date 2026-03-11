package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShareItServerTest {

    @Test
    void mainTest() {
        ShareItServer server = new ShareItServer();
        assertNotNull(server);
    }
}