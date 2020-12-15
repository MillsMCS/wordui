package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


class SampleDataTest {
    private final FrequencySummary mockFS = mock(FrequencySummary.class);
    private final WordApi mockWordApi = mock(WordApi.class);
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private static final WordOfTheDay TODAYSWORD = mock(WordOfTheDay.class);
    private static final String aWord = "jingle";
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "apple",
            makeFreqSummary(List.of(
                    makeMap(2000, 339),
                    makeMap(2001, 464))),
            "orange",
            makeFreqSummary(List.of(
                    makeMap(2000, 774),
                    makeMap(2001, 941))),
            aWord,
            makeFreqSummary(List.of(
                    makeMap(2020, 187))));
    private static final List<Object> WORD_DEFS = List.of(
            "def for jingle");
    private static final List<Object> DEF_LIST_MAP = List.of(Map.of(
            "text", WORD_DEFS.get(0)));



    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay())
                .thenReturn(TODAYSWORD);
        when(TODAYSWORD.getWord())
                .thenReturn(aWord);
        when(TODAYSWORD.getDefinitions())
                .thenReturn(DEF_LIST_MAP);
    }

    @Test
    void addWordOfTheDay_EqualsWordRecord_MockWordsObject() {
        List<WordRecord> testList = new ArrayList();
        ObservableList<WordRecord> testListRecord = FXCollections.observableList(testList);
        SampleData.addWordOfTheDay(testListRecord, mockWordsApi, mockWordApi);

        WordOfTheDay wordToday = SampleData.getWordOfTheDay(mockWordsApi);
        assertEquals(wordToday.getWord(), "jingle");
        List<Object> getDefList = List.of(Map.of("text", WORD_DEFS.get(0)));
        assertEquals(wordToday.getDefinitions(), getDefList);

        assertEquals(1, testListRecord.size());
    }

    private static FrequencySummary makeFreqSummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency())
                .thenReturn(freqs);
        return fs;
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year),
                SampleData.FREQ_COUNT_KEY, count);
    }

    @Test
    void getWordOfTheDay_EqualsWordRecord_MockWordsObject() {
        WordOfTheDay wordToday = SampleData.getWordOfTheDay(mockWordsApi);
        assertEquals(wordToday.getWord(), "jingle");
        List<Object> getDefList = List.of(Map.of("text", WORD_DEFS.get(0)));
        assertEquals(wordToday.getDefinitions(), getDefList);
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0",
            "orange,2000,774", "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }

    // @ParameterizedTest
    // @CsvSource({})
    // void testGetWordOfTheDay(String word) {
    // assertTrue(getWordOfTheDay(mockWordsApi));
    // }
}
