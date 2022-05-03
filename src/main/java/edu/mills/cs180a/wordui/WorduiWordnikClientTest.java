package edu.mills.cs180a.wordui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import edu.mills.cs180a.wordnik.client.api.*;
import edu.mills.cs180a.wordnik.client.model.*;
import edu.mills.cs180a.wordui.model.*;

class WorduiWordnikClientTest {
    private static WorduiWordnikClient client;
    private static WordApi mockWordApi = mock(WordApi.class);
    private static FrequencySummary mockFrequencySummaryApple = mock(FrequencySummary.class);
    private static FrequencySummary mockFrequencySummaryOrange = mock(FrequencySummary.class);
    // These should be returned by getFrequency() for "apple" and "orange"
    private static final List<Object> APPLE_FREQUENCIES =
            List.of(makeMap(2000, 339), makeMap(2001, 464), makeMap(2020, 0));
    private static final List<Object> ORANGE_FREQUENCIES =
            List.of(makeMap(2000, 774), makeMap(2001, 941));

    private static WordsApi mockWordsApi = mock(WordsApi.class);
    private static WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
    private static FrequencySummary mockFrequencySummaryWotd = mock(FrequencySummary.class);

    private static final String WOTD_DATE = "2022-05-02";
    private static final String WOTD_WORD = "axil";
    private static final String WOTD_DEF_KEY = "text";
    private static final String WOTD_DEF_VALUE =
            "The upper angle between a lateral organ, such as a leaf, and the stem that bears it.";
    private static final Map<String, String> map = Map.of(WOTD_DEF_KEY, WOTD_DEF_VALUE);
    private static final List<Object> defs = List.of(map);

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(WorduiWordnikClient.FREQ_YEAR_KEY, String.valueOf(year),
                WorduiWordnikClient.FREQ_COUNT_KEY, count);
    }

    @BeforeAll
    public static void setup() throws IOException {
        // mockWordApi
        when(mockWordApi.getWordFrequency(eq("apple"), anyString(), anyInt(), anyInt()))
                .thenReturn(mockFrequencySummaryApple);
        when(mockWordApi.getWordFrequency(eq("orange"), anyString(), anyInt(), anyInt()))
                .thenReturn(mockFrequencySummaryOrange);
        when(mockFrequencySummaryApple.getFrequency()).thenReturn(APPLE_FREQUENCIES);
        when(mockFrequencySummaryOrange.getFrequency()).thenReturn(ORANGE_FREQUENCIES);

        // mockWordsApi
        when(mockWordsApi.getWordOfTheDay(anyString())).thenReturn(mockWordOfTheDay);
        when(mockWordOfTheDay.getWord()).thenReturn(WOTD_WORD);
        when(mockWordOfTheDay.getDefinitions()).thenReturn(defs);

        when(mockWordApi.getWordFrequency(eq(WOTD_WORD), anyString(), anyInt(), anyInt()))
                .thenReturn(mockFrequencySummaryWotd);
        when(mockFrequencySummaryWotd.getFrequency()).thenReturn(null);

        client = WorduiWordnikClient.getMockInstance(mockWordApi, mockWordsApi);
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, client.getFrequencyByYear(word, year));
    }

    @Test
    void testGetWordOfTheDay() {
        WordRecord wr = client.getWordOfTheDay(WOTD_DATE);
        assertEquals(WOTD_WORD, wr.getWord());
        assertEquals(WOTD_DEF_VALUE, wr.getDefinition());
    }
}
