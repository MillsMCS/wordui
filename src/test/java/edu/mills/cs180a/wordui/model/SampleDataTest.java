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
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;

class SampleDataTest {
    private final WordApi mockWordApi = mock(WordApi.class);
    private static final Map<String, FrequencySummary> FREQ_MAP = Map.of("apple",
            makeFrequencySummary(List.of(
                    makeMap(2000, 339),
                    makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(
                    makeMap(2000, 774),
                    makeMap(2001, 941))));

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
        .thenAnswer(invocation -> FREQ_MAP.get(invocation.getArgument(0)));
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

    // @ParameterizedTest
    // @CsvSource({"2000, 339", "2001,464","2002,0"})
    // void testGetFreqSummary(int year, int count) {
    // assertEquals(count, SampleData.getFrequencyFromSummary(mockFS, year));
    // }

    @ParameterizedTest
    @CsvSource({"apple,2000, 339", "apple,2001,464", "apple,2002,0", "orange,2000,774",
        "orange,2001,941", "orange,2002,0"})
    void testGetFreqByYear(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }
}
