package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;

class SampleDataTest {

    @Mock
    private final WordApi mockWordApi = mock(WordApi.class);
    @Mock
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("apple",
            makeFrequencySummary(List.of(makeFreqMap(2000, 339), makeFreqMap(2001, 464))), "orange",
            makeFrequencySummary(List.of(makeFreqMap(2000, 774), makeFreqMap(2001, 941))));

    private static final Map<String, WordOfTheDay> WOD_MAP =
            Map.of("2020-10-31", makeWODMap(List.of("2020-10-31", "spoopy")));

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay(anyString())).thenAnswer(null);
    }

    private static Map<Object, Object> makeFreqMap(int year, int count) {
        return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year), SampleData.FREQ_COUNT_KEY,
                count);
    }

    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }

    private static Map<Object, Object> makeWODMap(String date, String word) {
        return Map.of(SampleData.WOD_PUBLISHDATE_KEY, date, SampleData.WOD_WORD_KEY, word);
    }

    private static WordOfTheDay makeWOD(List<String> wods) {
        WordOfTheDay wod = mock(WordOfTheDay.class);
        when(wod.getWord()).thenReturn(wods);
        return wod;
    }
}
