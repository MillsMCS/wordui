package edu.mills.cs180a.wordui;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class WorduiWordnikClientTest {
    private static WorduiWordnikClient client;

    @BeforeAll
    public static void setup() throws IOException {
        client = new WorduiWordnikClient();
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,3845", "apple,2001,3883", "apple,2020,0", "orange,2000,3845",
            "orange,2001,3883"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, client.getFrequencyByYear(word, year));
    }
}
