package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.Definition;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;

class SampleDataTest {
    private final WordApi mockWordApi = mock(WordApi.class);
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "apple", makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))));
    
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private static final WordOfTheDay WORD_TODAY = makeWordOfTheDay();

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay())
        		.thenReturn(WORD_TODAY);
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
    
    private static WordOfTheDay makeWordOfTheDay() {
    	WordOfTheDay wotd = mock(WordOfTheDay.class);
    	List<Object> bananaDef = new LinkedList<>();
    	bananaDef.add("An elongated curved fruit, which grows in bunches, and has a sweet creamy flesh and a smooth yellow skin.");
    	
    	when(wotd.getWord()).thenReturn("banana");
    	when(wotd.getDefinitions()).thenReturn(bananaDef);
    	return wotd;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }
    
    void getWordOfTheDay_True_mockedWord() {
    	assertEquals("banana", SampleData.getWordOfTheDay(mockWordsApi));
    }
    
    void getDefinition_True_mockedDefinition() {
    	assertEquals("An elongated curved fruit, which grows in bunches, and has a sweet creamy flesh and a smooth yellow skin.", 
    	        SampleData.getWordOfTheDay(mockWordsApi).getDefinitions());
    }
}
