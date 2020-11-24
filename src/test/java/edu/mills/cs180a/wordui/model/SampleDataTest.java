package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;

class SampleDataTest {
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private final WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
    private final WordApi mockWordApi = mock(WordApi.class);
    private static final Map<String, FrequencySummary> FREQ_MAP = Map.of("apple",
            makeFrequencySummary(List.of(
                    makeMap(2000, 339),
                    makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(
                    makeMap(2000, 774),
                    makeMap(2001, 941))));

    private static final List<Object> DEFINITIONS = List.of(
            "In pharmacy, a medicine composed of powders or other ingredients, incorporated with "
                    + "some conserve, honey, or syrup, originally made in a form to be licked by the patient.",
                    "Any preparation of a medicine mixed with honey or similar in order to make it more "
                            + "palatable to swallow.\r\n");

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
        .thenAnswer(invocation -> FREQ_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay()).thenReturn(mockWordOfTheDay);
        when(mockWordOfTheDay.getWord()).thenReturn("electuary");
        when(mockWordOfTheDay.getDefinitions()).thenReturn(DEFINITIONS);
    }


    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year), SampleData.FREQ_COUNT_KEY,
                count);
    }

    @ParameterizedTest
    @CsvSource({"apple,2000, 339", "apple,2001,464", "apple,2002,0", "orange,2000,774",
        "orange,2001,941", "orange,2002,0"})
    void testGetFreqByYear(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }

    @Test
    void testGetWordOfTheDay() {
        WordOfTheDay word = SampleData.getWordOfTheDay(mockWordsApi);
        assertEquals(mockWordOfTheDay, word);
        assertEquals("electuary", word.getWord());
        assertEquals(2, word.getDefinitions().size());
    }
}
