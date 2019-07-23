import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// C:\Users\rajaz\IdeaProjects\project-gutenberg\src\textresources\odyssey.txt

public class TextAnalyzer {
    HashMap<String, Integer> wordsMap;
    HashMap<Integer, HashMap<String, Integer>> chaptersMap;
    int totalNumWords;

    private String filePath;

    public TextAnalyzer(String fileName) {
        this.filePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\" + fileName;
        System.out.println(filePath);
        this.wordsMap = new HashMap<>();
        this.chaptersMap = new HashMap<>();
        this.totalNumWords = 0;

        populateMaps();
    }

    //Uses totalNumWords that gets set when reading the file the first time. Avoids multiple passes
    public int getTotalNumWords () {
        return this.totalNumWords;
    }
    //uses map that is populated when reading file the first time. Avoids multiple passes
    public int getTotalUniqueWords() {
        return this.wordsMap.size();
    }

    public List<List<String>> get20MostFrequentWords() {
        List<String> words = new ArrayList<>(wordsMap.keySet());
        //sort in descending order
        Collections.sort(words, (a,b)->wordsMap.get(b)-wordsMap.get(a));
        List<List<String>> wordCount = new ArrayList<>();
        for(int i = 0; i < 20; i ++) {
            List<String> wCount = Arrays.asList(
                    new String []{words.get(i), String.valueOf(this.wordsMap.get(words.get(i)))});
            wordCount.add(wCount);
        }
        return wordCount;
    }

    public List<List<String>> get20InterestingMostFrequentWords() {
        List<String> words = new ArrayList<>(wordsMap.keySet());
        //sort in descending order
        Collections.sort(words, (a,b)->wordsMap.get(b)-wordsMap.get(a));
        List<List<String>> wordCount = new ArrayList<>();
        HashSet<String> mostFreqWords = findMostFreqWords(100);

        int count = 0;
        int idx = 0;
        while(count < 20) {
            if(! mostFreqWords.contains(words.get(idx))) {
                List<String> wCount = Arrays.asList(
                        new String []{words.get(idx), String.valueOf(this.wordsMap.get(words.get(idx)))});
                wordCount.add(wCount);
                count ++;
            }
            idx ++;
        }
        return wordCount;
    }

    public List<Integer> getFreqeuncyOfWord(String word) {
        List<Integer> frequency = new ArrayList<>();
        int numChapters = this.chaptersMap.size();
        for(int i = 0; i < numChapters; i ++) {
            int freq = this.chaptersMap.get(i + 1).containsKey(word) ? this.chaptersMap.get(i + 1).get(word) : 0;
            frequency.add(freq);
        }
        return frequency;
    }

    /*
     * The following method works as follows:
     * An important fact to consider that the number of "s in a properly formatted book is even,
     * unless "s are used for things other than quotations. So if the number of "s is even, splitting
     * the entire text based off of "s gives an array with an odd number of elements. Then the quotations
     * would simply just be the even indexed elements in the array.
     *
     * Needs more work. Quotes are left open when there is a line break.
     * A temporary fix is to just consider everything between "s. It is guaranteed that at least
     * one of two is a valid quote
     */
    public int getChapterQuoteAppears(String quote) throws Exception {
        //first split book into chapters
        String strFile = FileUtils.readFileToString(new File(this.filePath));
        String [] chapters = strFile.split(".*BOOK.*");

        //ignore element before chapter 1
        for(int i = 1; i < chapters.length; i ++) {
            String chapter = chapters[i];

            String [] quotes = chapter.split("\"");
            for(int j = 0; j < quotes.length; j ++) {
                System.out.println(quotes[j] + "\n");
                if(quotes[j].replaceAll("\\s", "").
                        contains(quote.replaceAll("\\s", "")))
                    return i;
            }
        }

        return -1;
    }

    private HashSet<String> findMostFreqWords(int size) {
        HashSet<String> mostFreqWords = new HashSet<>();
        String rootPath = new File("").getAbsolutePath();
        try {
            BufferedReader bReader = new BufferedReader
                    (new FileReader(rootPath + "\\src\\textresources\\mostcommonwords.txt"));

            int count = 0;
            String line;
            while((line = bReader.readLine())!= null) {
                mostFreqWords.add(line.replaceAll("\\s", ""));
                count ++;

                if(count  == size) break;
            }
        }
        catch(Exception e) {
            System.out.println("Cannot open mostcommonwords.txt\n" + "e.getMessage()" );
        }

       return mostFreqWords;
    }

    private void populateMaps() {
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(this.filePath));
            String line;
            int chapter = 0;
            HashMap<String, Integer> countsInChap = new HashMap<>();

            while((line = bReader.readLine()) != null) {
                //beginning of a new chapter. Update chaptermap accordingly
                if(line.contains("BOOK")) {
                    chapter ++;
                    countsInChap = new HashMap<>();
                    this.chaptersMap.put(chapter, countsInChap);
                }
                else {
                    String [] words = line.split(" ");
                    Pattern pattern = Pattern.compile("[^\\w\\d-]");

                    for(String rawWord : words) {
                        Matcher matcher = pattern.matcher(rawWord);
                        String newWord = matcher.replaceAll("");

                        //Handle the case for potential hyphenated single words( eg:- 'well-known')
                        if(newWord.contains("-") && ! isValidHyphenWord(newWord)) continue;

                        if(newWord.length() == 0) continue;

                        newWord = newWord.toLowerCase();

                        if(! this.wordsMap.containsKey(newWord)) this.wordsMap.put(newWord, 0);

                        if(! countsInChap.containsKey(newWord)) countsInChap.put(newWord, 0);

                        this.wordsMap.put(newWord, this.wordsMap.get(newWord) + 1);
                        
                        countsInChap.put(newWord, countsInChap.get(newWord) + 1);

                        this.totalNumWords ++;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Cannot open file\n" + "e.getMessage()");
        }
    }

    private boolean isValidHyphenWord(String word) {
        Pattern pattern = Pattern.compile("\\w+-\\w+");
        Matcher matcher = pattern.matcher(word);
        return matcher.matches();
    }


}
