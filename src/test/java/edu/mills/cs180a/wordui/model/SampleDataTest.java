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
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.Definition;

class SampleDataTest {
    private final WordApi mockWordApi = mock(WordApi.class);
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "apple", makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))));
    
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private final WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
    
    private static final String WORD = "other";
    private static final WordOfTheDay WOTD = makeWordOfTheDay(WORD);
        
    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));        
        when(mockWordsApi.getWordOfTheDay()).thenReturn(WOTD);
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year),
                SampleData.FREQ_COUNT_KEY, count);
    }

    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }
    
    private static WordsApi makeWordsApi(WordOfTheDay w) {
    	WordsApi wapi = mock(WordsApi.class);
    	when(wapi.getWordOfTheDay()).thenReturn(w);
    	return wapi;
    }
    
    private static WordOfTheDay makeWordOfTheDay(String w) {
    	WordOfTheDay wotd = mock(WordOfTheDay.class);
    	when(wotd.getWord()).thenReturn(w);
    	return wotd;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }
    
    @ParameterizedTest
    @CsvSource({"other"})
    void testGetWordOfTheDay(String w) {
    	WordOfTheDay s = SampleData.getWordOfTheDay(mockWordsApi);
    	System.out.println(s.getWord());
        assertEquals(0, SampleData.getWordOfTheDay(mockWordsApi).getWord().compareTo(w));
    }
}
