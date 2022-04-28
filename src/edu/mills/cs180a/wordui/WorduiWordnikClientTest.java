package edu.mills.cs180a.wordui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class WorduiWordnikClientTest {
    private static WorduiWordnikClient client;

    @BeforeAll
    public static void setup() throws IOException {
        client = WorduiWordnikClient.getMockInstance();
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, client.getFrequencyByYear(word, year));
    }
}
