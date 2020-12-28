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
    private final WordApi mockWordApi = mock(WordApi.class);
    private final WordsApi mockWordsApi = mock(WordsApi.class);
    private static final String WORD_OF_THE_DAY = "yucca";
    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "apple", makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))),
            WORD_OF_THE_DAY, makeFrequencySummary(List.of(makeMap(2020, 37), makeMap(2012, 57))));
    private static final String DEFINITION1 = "Definition #1";
    private static final String DEFINITION2 = "Definition #2";
    private static final String DEFINITION3 = "Definition #3";
    private static final String[] DEFINITION_ARRAY = new String[] {DEFINITION1, DEFINITION2, DEFINITION3};
    private static final List<Object> DEFINITIONS = makeDefinitions(DEFINITION_ARRAY);


    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));

        WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
        when(mockWordsApi.getWordOfTheDay()).thenReturn(mockWordOfTheDay);
        when(mockWordOfTheDay.getWord()).thenReturn(WORD_OF_THE_DAY);
        when(mockWordOfTheDay.getDefinitions()).thenReturn(DEFINITIONS);
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year),
                SampleData.FREQ_COUNT_KEY, count);
    }

    private static Map<Object, Object> makeDefinition(String definition) {
        return Map.of("text", definition);
    }

    private static List<Object> makeDefinitions(String[] definitions) {
        List<Object> definitionList = new ArrayList<Object>();
        for (int i = 0; i < definitions.length; i++) {
            definitionList.add(makeDefinition(definitions[i]));
        }
        return definitionList;
    }

    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0", "yucca,2020,37", "yucca,2012,57"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetWordOfTheDay() {
        WordOfTheDay wordOfTheDay = SampleData.getWordOfTheDay(mockWordsApi);

        Map<String, String> definition1 = (Map<String, String>) wordOfTheDay.getDefinitions().get(0);
        Map<String, String> definition2 = (Map<String, String>) wordOfTheDay.getDefinitions().get(1);
        Map<String, String> definition3 = (Map<String, String>) wordOfTheDay.getDefinitions().get(2);

        assertEquals(WORD_OF_THE_DAY, wordOfTheDay.getWord());
        assertEquals(DEFINITIONS, wordOfTheDay.getDefinitions());
        assertEquals(DEFINITION1, definition1.get("text").toString());
        assertEquals(DEFINITION2, definition2.get("text").toString());
        assertEquals(DEFINITION3, definition3.get("text").toString());
    }

    @Test
    void testAddWordOfTheDay() {
        ObservableList<WordRecord> wordRecordList = FXCollections.observableArrayList();
        assertEquals(0, wordRecordList.size());
        SampleData.addWordOfTheDay(wordRecordList, mockWordsApi, mockWordApi);
        assertEquals(1, wordRecordList.size());

        WordRecord wordRecord = wordRecordList.get(0);
        assertEquals(WORD_OF_THE_DAY, wordRecord.getWord());
        assertEquals(DEFINITION1, wordRecord.getDefinition());
        assertEquals(57, wordRecord.getFrequency());
    }
}
