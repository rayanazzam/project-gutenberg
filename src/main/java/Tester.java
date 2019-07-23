import java.util.HashMap;

public class Tester {

    public static void main(String [] args) throws Exception{
        TextAnalyzer tAnalyzer = new TextAnalyzer("odyssey.txt");

        //System.out.println(tAnalyzer.getTotalUniqueWords());
        //System.out.println(tAnalyzer.get20InterestingMostFrequentWords());
        //System.out.println(tAnalyzer.get20MostFrequentWords());
        //for(String key : tAnalyzer.wordsMap.keySet()) System.out.println(key);
        //System.out.println(tAnalyzer.getFreqeuncyOfWord("ulysses"));
        System.out.println(tAnalyzer.getChapterQuoteAppears("Ev'n nature starts, and what ye ask denies.\n" +
                "Thus, shall I thus repay a mother's cares,\n" +
                "Who gave me life, and nursed my infant years!\n" +
                "While sad on foreign shores Ulysses treads.\n" +
                "Or glides a ghost with unapparent shades;\n"));
        //System.out.println(tAnalyzer.chaptersMap.size());
    }
}
