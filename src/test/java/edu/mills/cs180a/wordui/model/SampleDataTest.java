package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class SampleDataTest {

    @Mock
    private final WordApi mockWordApi = mock(WordApi.class);
    @Mock
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private static final String SAMPLE_WORD_DEF =
            "A perennial climbing vine (Piper nigrum) native to India, widely cultivated for its long slender spikes of small fruit.";
    private static final Map<Object, Object> SAMPLE_WORD_DEF_MAP = Map.of("text", SAMPLE_WORD_DEF);
    private static final List<Object> SAMPLE_WORD_DEF_LIST = List.of(SAMPLE_WORD_DEF_MAP);
    private static final String SAMPLE_WORD_STRING = "pepper";
    private static final WordOfTheDay SAMPLE_WORD_OF_THE_DAY =
            makeWOD(SAMPLE_WORD_STRING, SAMPLE_WORD_DEF_LIST);
    private static final int SAMPLE_WORD_FREQ = 1;
    private static final int SAMPLE_WORD_YEAR = 2012;
    private static final String SAMPLE_WORD_CSV = SAMPLE_WORD_STRING + "," + SAMPLE_WORD_YEAR
            + "," + SAMPLE_WORD_FREQ;
    private static final FrequencySummary SAMPLE_WORD_FREQ_SUMMARY =
            makeFrequencySummary(List.of(makeFreqMap(SAMPLE_WORD_YEAR, SAMPLE_WORD_FREQ)));
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("apple",
            makeFrequencySummary(List.of(makeFreqMap(2000, 339), makeFreqMap(2001, 464))), "orange",
            makeFrequencySummary(List.of(makeFreqMap(2000, 774), makeFreqMap(2001, 941))));

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordApi.getWordFrequency(eq(SAMPLE_WORD_STRING), anyString(), anyInt(), anyInt()))
                .thenReturn(SAMPLE_WORD_FREQ_SUMMARY);
        when(mockWordsApi.getWordOfTheDay()).thenReturn(SAMPLE_WORD_OF_THE_DAY);
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
            "orange,2001,941", "orange,2050,0", SAMPLE_WORD_CSV})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }

    private static WordOfTheDay makeWOD(String word, List<Object> defs) {
        WordOfTheDay wod = mock(WordOfTheDay.class);
        when(wod.getWord()).thenReturn(word);
        when(wod.getDefinitions()).thenReturn(defs);
        return wod;
    }

    @SuppressWarnings("unchecked")
    @Test
    void getWordOfTheDay_True_CorrectWordReturned() {
        WordOfTheDay word = SampleData.getWordOfTheDay(mockWordsApi);
        assertEquals(SAMPLE_WORD_STRING, word.getWord());
        assertEquals(1, word.getDefinitions().size());
        assertEquals(SAMPLE_WORD_DEF,
                ((Map<Object, Object>) word.getDefinitions().get(0)).get("text").toString());
    }

    @Test
    void addWordOfTheDay_True_WordAddedToBackingList() throws IOException {
        ObservableList<WordRecord> backingList = FXCollections.observableArrayList();
        assertEquals(0, backingList.size());
        SampleData.addWordOfTheDay(mockWordApi, mockWordsApi, backingList);
        assertEquals(1, backingList.size());
        WordRecord wordRecordFromList = backingList.get(0);
        assertEquals(SAMPLE_WORD_STRING, wordRecordFromList.getWord());
        assertEquals(SAMPLE_WORD_DEF, wordRecordFromList.getDefinition());
        assertEquals(SAMPLE_WORD_FREQ, wordRecordFromList.getFrequency());
    }
}
