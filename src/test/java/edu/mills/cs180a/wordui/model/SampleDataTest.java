package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private final WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "apple", makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))));
    private static final Map<String, String> DEFINITION = Map.of(
    		"source", "my dog",
    		"text", "An animal you take on walks.",
    		"note", "best note ever",
    		"PartOfSpeech", "noun");
    private static final List<Object> DEFINITIONS = makeDefinitions(DEFINITION);
    private static final String WORD = "dog";
    private static final WordOfTheDay WOTD = makeWordOfTheDay(WORD, DEFINITIONS);
        
    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay()).thenReturn(WOTD);
        //when(mockWordOfTheDay.getDefinitions()).thenReturn(DEFINITIONS);
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
    
    private static WordOfTheDay makeWordOfTheDay(String w, List<Object> d) {
    	WordOfTheDay wotd = mock(WordOfTheDay.class);
    	when(wotd.getWord()).thenReturn(w);
    	when(wotd.getDefinitions()).thenReturn(d);
    	return wotd;
    }
    
    private static List<Object> makeDefinitions(Map<String, String> definition) {
    	List<Object> list = new ArrayList<Object>();
    	list.add(definition);
    	return list;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }
    
    @ParameterizedTest
    @CsvSource({"dog"})
    void testGetWordOfTheDay(String w) {
    	//System.out.println(SampleData.getWordOfTheDay(mockWordsApi).getWord());
    	WordOfTheDay wotd = SampleData.getWordOfTheDay(mockWordsApi);
        assertEquals(0, wotd.getWord().compareTo(w));
    }
    
    
    @SuppressWarnings("unchecked")
	@ParameterizedTest
    @CsvSource({
    	"source, my dog", 
    	"text, An animal you take on walks.", 
    	"note, best note ever",
		"PartOfSpeech, noun"})
    void testGetDefinitions(String key, String value) {
    	WordOfTheDay wotd = SampleData.getWordOfTheDay(mockWordsApi);
    	
    	// Debug
    	try {
    		System.out.println("Definition: " + ((Map<String, String>) wotd.getDefinitions().get(0)).get(key));
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	
    	String returnValue = ( (Map<String, String>) wotd.getDefinitions().get(0) ).get(key);
    	assertEquals(0, returnValue.compareTo(value));
    }
    
}
