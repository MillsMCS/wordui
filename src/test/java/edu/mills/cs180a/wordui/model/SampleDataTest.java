package edu.mills.cs180a.wordui.model;

import static edu.mills.cs180a.wordui.model.SampleData.FREQ_COUNT_KEY;
import static edu.mills.cs180a.wordui.model.SampleData.FREQ_YEAR_KEY;
import static edu.mills.cs180a.wordui.model.SampleData.getFrequencyByYear;
import static edu.mills.cs180a.wordui.model.SampleData.getWordOfTheDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;

class SampleDataTest {
    private final WordApi mockWordApi = mock(WordApi.class);
    private final WordsApi mockWordsApi = mock(WordsApi.class);

    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("apple",
            makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))), "orange",
            makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))), "blueberry",
            makeFrequencySummary(List.of(makeMap(2000, 10), makeMap(2001, 58))), "airplane",
            makeFrequencySummary(List.of(makeMap(2000, 791), makeMap(2001, 1300))));

    // 結局 makeFrequencySummaryをやると
    // Map.of("apple",FrequencySummary）の形になる

    private static final WordOfTheDay MOCK_WORD = makeWordOfTheDay("airplane",
            List.of("Any of various winged vehicles capable of flight, "
                    + "generally heavier than air and driven by jet engines or propellers."));

    // private static final List<Object> MOCK_DIFINITION =
    // List.of("Any of various plants of the genus");

    private static final WordRecord MOCK_WORDRECORD =
            makeWordRecord("banana", 2000, List.of("Any of several treelike plants of the genus"));


    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay()).thenReturn(MOCK_WORD);

        when(mockWordsApi.getWordOfTheDay().getWord()).thenReturn("blueberry");
        when(mockWordsApi.getWordOfTheDay().getDefinitions())
                .thenReturn(List.of("Any of various plants of the genus"));
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(FREQ_YEAR_KEY, String.valueOf(year), FREQ_COUNT_KEY, count);
    }

    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }

    // """""""""""""""""""""""""""""""""""" OK
    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0"})
    void getFrequencyByYear_Equal_correctValue(String word, int year, int count) {
        assertEquals(count, getFrequencyByYear(mockWordApi, word, year));
    }
    // """""""""""""""""""""""""""""""""""" OK


    private static WordOfTheDay makeWordOfTheDay(String word, List<Object> defin) {
        WordOfTheDay wd = mock(WordOfTheDay.class);
        when(wd.getWord()).thenReturn(word);
        when(wd.getDefinitions()).thenReturn(defin);
        return wd;
    }

    private static List<Object> makeDifinition(List<Object> Defin) {
        WordOfTheDay wd = mock(WordOfTheDay.class);
        when(wd.getDefinitions()).thenReturn(Defin);
        return Defin;
    }

    // WordRecord(String word, int frequency, String definition
    private static WordRecord makeWordRecord(String word, int frequency, List<Object> definition) {
        WordRecord wr = mock(WordRecord.class);
        when(wr.getWord()).thenReturn(word);
        when(wr.getFrequency()).thenReturn(frequency);
        when(wr.getDefinition()).thenReturn(definition.get(0).toString());
        return wr;
    }

    // """""""""""""""""""""""""""""""""""" OK
    @Test
    void getWord_Equal_correctValue() {
        assertEquals("blueberry", getWordOfTheDay(mockWordsApi).getWord());
    }

    @Test
    void getDefinitions_Equal_correctValue() {
        assertEquals("Any of various plants of the genus",
                getWordOfTheDay(mockWordsApi).getDefinitions().get(0));
    }
    // """""""""""""""""""""""""""""""""""" OK



    @Before
    void setup2() {
        // doReturn(MOCK_WORDRECORD).when(モックインスタンス).メソッド(任意の引数);
        // when(SampleData.addWordOfTheDay()).thenReturn(MOCK_WORDRECORD);

    }

    @Test
    void addWordOfTheDay_Equal_correctValue() {
        SampleData mockSampleData = mock(SampleData.class);

        WordRecord wr = MOCK_WORDRECORD;
        // doReturn(MOCK_WORDRECORD).when(mockSampleData).addWordOfTheDay(mockWordsApi);
        // when(SampleData.addWordOfTheDay(mockWordsApi)).thenReturn();
        assertEquals(wr.getDefinition(), SampleData.addWordOfTheDay(mockWordsApi).getDefinition());

        // assertEquals(wr.getDefinition(), MOCK_WORDRECORD.getDefinition());
        // assertEquals(wr.getDefinition(), addWordOfTheDay(mockWordsApi).getDefinition());
    }

    private WordRecord doReturn(WordRecord mockWordrecord) {

        return MOCK_WORDRECORD;
    }
}
