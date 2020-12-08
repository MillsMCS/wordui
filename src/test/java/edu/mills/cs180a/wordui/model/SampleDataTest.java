package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private static final Map<Object, Object> PEPPER_DEF = Map.of("text",
            "A perennial climbing vine (Piper nigrum) native to India, widely cultivated for its long slender spikes of small fruit.");
    private static final List<Object> DEFS = List.of(PEPPER_DEF);
    private static final String PEPPER = "pepper";
    private WordOfTheDay wod = makeWOD(PEPPER, DEFS);
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("apple",
            makeFrequencySummary(List.of(makeFreqMap(2000, 339), makeFreqMap(2001, 464))), "orange",
            makeFrequencySummary(List.of(makeFreqMap(2000, 774), makeFreqMap(2001, 941))));

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay()).thenReturn(wod);
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

    private static WordOfTheDay makeWOD(String word, List<Object> defs) {
        WordOfTheDay wod = mock(WordOfTheDay.class);
        when(wod.getWord()).thenReturn(word);
        when(wod.getDefinitions()).thenReturn(defs);
        return wod;
    }

    @Test
    void getWord_True_CorrectWordReturned() {
        assertEquals("pepper", SampleData.getWordOfTheDay(mockWordsApi).getWord());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getDefinitions_True_CorrectDefinitionsReturned() {
        Map<Object, Object> defs = copyMap(PEPPER_DEF);
        assertEquals(defs, (Map<Object, Object>) SampleData.getWordOfTheDay(mockWordsApi)
                .getDefinitions().get(0));
    }

    private Map<Object, Object> copyMap(Map<Object, Object> originalMap) {
        Map<Object, Object> newMap = new HashMap<Object, Object>();
        for (Object elt : originalMap.keySet()) {
            newMap.put(elt, originalMap.get(elt));
        }
        return newMap;
    }
}
