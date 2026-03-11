package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShareItServerTest {

    @Test
    void contextLoads() {
        ShareItServer server = new ShareItServer();
        assertNotNull(server);
    }
}