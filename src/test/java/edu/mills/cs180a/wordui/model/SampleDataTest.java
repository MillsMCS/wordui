package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;

class SampleDataTest {
    private static final WordApi mockWordApi = mock(WordApi.class);
    private static final WordsApi mockWordsApi = mock(WordsApi.class);

    private static final WordOfTheDay WORD =
            makeWordOfTheDay("cromulent",
                    List.of(
                            Map.of("text", "a valid word",
                                    "partOfSpeech", "adjective",
                                    "source", "wiktionary",
                                    "note", "null"),
                            Map.of("text", "appropriate for use",
                                    "partOfSpeech", "adjective",
                                    "source", "wiktionary",
                                    "note", "null")));

    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of(
            "cromulent", makeFrequencySummary(List.of(makeMap(2000, 10), makeMap(2001, 20))),
            "apple", makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))),
            "orange", makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))));

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));

        when(mockWordsApi.getWordOfTheDay())
                .thenAnswer(invocation -> WORD);
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

    private static WordOfTheDay makeWordOfTheDay(String words,
            List<Object> definitions) {
        WordOfTheDay wrd = mock(WordOfTheDay.class);
        when(wrd.getWord()).thenReturn(words);
        when(wrd.getDefinitions()).thenReturn(definitions);
        return wrd;
    }

    @Test
    void testGetWordOfTheDayGetWord() {
        assertEquals("cromulent", SampleData.getWordOfTheDay(mockWordsApi).getWord());
    }

    @Test
    void testGetWordOfTheDayGetDefinitionInstanceofMap() {
        assertTrue(SampleData.getWordOfTheDay(mockWordsApi).getDefinitions().get(0) instanceof Map);
    }

    @Test
    void testGetWordOfTheDayGetDefinition() {
        @SuppressWarnings("unchecked")
        Map<Object, Object> definitions = (Map<Object, Object>) SampleData
                .getWordOfTheDay(mockWordsApi).getDefinitions().get(0);
        assertEquals("a valid word", definitions.get("text"));
    }

    @Test
    void testAddWordOfTheDay() throws IOException {
        ObservableList<WordRecord> list = FXCollections.observableArrayList(
                new WordRecord("schoolbus", 23736, "A bus to take children to school."));
        assertEquals("schoolbus", list.get(0).getWord());
        SampleData.addWordOfTheDay(mockWordsApi, mockWordApi, list);
        assertEquals(2, list.size());
        assertEquals("cromulent", list.get(1).getWord());
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void testGetFrequencyFromSummary(String word, int year, int count) {
        assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
    }
}
