package junior_is;

// Core
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// 3rd Party
import org.apache.commons.math3.linear.*;

/*
 * Main Class for determining recommendations
 * Takes in a compressed team (see Compressor.java), 
 * Inverts the first 36 dimensions of the vector,
 * runs cosine similarity on the modified vector against all vectors in pokeVectDec
 * returns a LinkedHashMap(abstracted to generic map) containing cosSim values and associated pokemonSheet file names
 * retuned Map is sorted largest to smallest
 * 
 * run 'compareToData()' to generate recommendations (returns the recommendation score for each vector in PokeVectData)
 */

public class TeamComparator {
    public Map<double[],String> vectors;
    public TeamComparator() throws FileNotFoundException{
        getDataFromFile();
    }

    public Map<String,Double> compareToData(double[] teamVector, List<String> teammates) throws FileNotFoundException{  // Compares all pokeVectors to the team vector and returns a map of (pokesheet:similarity)
        Map<String,Double> similarityList = new HashMap<String,Double>();
        for (int i = 0; i < teamVector.length-2; i++) { // invert def and off totals to find things that complement as opposed to things that match
            teamVector[i] *= -1;
        }
        for (Entry<double[],String> entrySet : this.vectors.entrySet()) {
            double[] monVector = entrySet.getKey();
            String fileName = entrySet.getValue();
            Double coSim = cosSim(teamVector, monVector);
            similarityList.put(fileName,coSim);
        }
        similarityList = sortHashMap(similarityList);
        int count = 0 ;
        List<String> recommendedMons = new ArrayList<String>();   // add mons already on the team to the blacklist so that they arent recommended again
        for (String mon : teammates) {
            recommendedMons.add(mon);
        }
        for (Entry<String,Double> entrySet : similarityList.entrySet()) {
            String name = entrySet.getKey();
            Matcher matcher = Pattern.compile("\\d+").matcher(name);    // pull the name out of the sheet name (sheet names are in the form nameXYZ) where XYZ is a number
            matcher.find();
            String nums = matcher.group();
            name = name.substring(0,name.indexOf(nums));
            if (!recommendedMons.contains(name)){
                //System.out.println(name+": "+entrySet.getKey()+" "+entrySet.getValue());
                Scanner s = new Scanner(new File(
                    String.format("pokerecomender\\src\\main\\resources\\pokeSheetCache\\%s.txt",entrySet.getKey())));
                System.out.println(s.nextLine().replaceAll(",",", "));  // convert the read in pokesheet to a string 
                s.close();
                recommendedMons.add(name);
                count++;
            }
            if(count>3){break;}  // only recommend three mons
        }
        return similarityList;
    }

    public void getDataFromFile() throws FileNotFoundException{ // preprocessing to read in the pokeVectData file
        Scanner fileReader = new Scanner(new File("pokerecomender\\src\\main\\resources\\pokeVectData.vect"));
        Map<double[],String> vectors = new HashMap<double[],String>();
        File[] files = new File("pokerecomender\\src\\main\\resources\\pokeSheetCache").listFiles();
        List<String> fileNames = new ArrayList<String>();
        for (File file : files) {                               // generate the corresponding list of pokeSheets (row number in pokeVectData = location in file list)
            String filePath = file.toString();
            filePath = filePath.substring(filePath.indexOf("Cache")+6,filePath.indexOf(".txt"));
            fileNames.add(filePath);
        }
        int counter = 0;
        while (fileReader.hasNextLine()){
            String vectString = fileReader.nextLine();
            String[] vectArrStr = vectString.split(",");
            //System.out.println(vectString);
            double[] vectArr = new double[vectArrStr.length]; // what does a pirate call a vector? a vectARRRRrrrr
            for (int i = 0; i<vectArrStr.length; i++){
                vectArr[i] = Double.parseDouble(vectArrStr[i]);
            }
            vectors.put(vectArr,fileNames.get(counter));
            counter++;
        }
        fileReader.close();
        this.vectors = vectors;
        return;
    }
    public Double cosSim(double[] Vect1, double[] Vect2){           // run cosine similarity on two vectors
        /* returns a double between -1 and 1
         * 1 is exact match, -1 is exact mismatch
         */
        RealVector Matrx1 = MatrixUtils.createRealVector(Vect1);
        RealVector Matrx2 = MatrixUtils.createRealVector(Vect2);
        return Matrx1.cosine(Matrx2);
    }

    public Map<String,Double> sortHashMap(Map<String,Double> map){              // Sort the hash map into a linked hash map and then abstract back to a standard map
        List<Map.Entry<String, Double> > listedMap =
               new LinkedList<Map.Entry<String, Double> >(map.entrySet());
        // Sort the list
        Collections.sort(listedMap, new Comparator<Map.Entry<String, Double>>() { // sort using the compare function defined internally
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        // listedMap is now a collection of bootleg doubles sorted by the value (2nd item)
        // convert the list of doubles into a sorted linked hashmap (which is declassed into a generic map)
        HashMap<String, Double> newMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : listedMap) {
            newMap.put(aa.getKey(), aa.getValue());
        }
        return newMap;
    }

    public static void main(String[] args) throws IOException {
        TeamComparator comparator = new TeamComparator();
        Compressor c = new Compressor();
        List<String[]> teamList = new ArrayList<String[]>();
        /*
        String[] t1 = new String[]{"darkrai","expert-belt","bad-dreams","ice-beam","sludge-bomb","dark-pulse","focus-blast"};
        String[] t2 = new String[]{"roaring-moon","booster-energy","protosynthesis","dragon-dance","acrobatics","knock-off","taunt"};
        String[] t3 = new String[]{"glimmora","red-card","toxic-debris","stealth-rock","mortal-spin","earth-power","power-gem"};
        String[] t4 = new String[]{"kingambit","air-balloon","supreme-overlord","swords-dance","sucker-punch","kowtow-cleave","iron-head"};
        String[] t5 = new String[]{"zamazenta","leftovers","dauntless-shield","iron-defense","body-press","heavy-slam","roar"};
        //String[] t5 = new String[]{"enamorus-incarnate","choice-scarf","contrary","moonblast","earth-power","mystical-fire","healing-wish"};
        teamList.add(t1);
        teamList.add(t2);
        teamList.add(t3);
        teamList.add(t4);
        teamList.add(t5);
        List<String> teamNames = new ArrayList<String>();
        teamNames.add("darkrai");
        teamNames.add("roaring-moon");
        teamNames.add("glimmora");
        teamNames.add("kingambit");
        teamNames.add("zamazenta");
        double[] compressedTeam = c.CompressTeam(teamList);
        */
        String[] t1 = new String[]{"heracross","flame-orb","guts","earthquake","megahorn","close-combat","facade"};
        String[] t2 = new String[]{"porygon-Z","choice-specs","adaptability","ice-beam","tri-attack","shadow-ball","charge-beam"};
        String[] t3 = new String[]{"kingambit","black-glasses","supreme-overlord","kowtow-cleave","iron-head","swords-dance","x-scissor"};
        String[] t4 = new String[]{"dragonite","lum-berry","multiscale","dragon-dance","extreme-speed","outrage","earthquake"};
        String[] t5 = new String[]{"volcarona","leftovers","swarm","quiver-dance","flamethrower","bug-buzz","tera-blast"};
        //String[] t2 = new String[]{"articuno","damp-rock","pressure","tackle","tackle","tackle","earthquake"};
        teamList.add(t1);
        teamList.add(t2);
        teamList.add(t3);
        teamList.add(t4);
        teamList.add(t5);
        List<String> teamNames = new ArrayList<String>();
        teamNames.add("heracross");
        teamNames.add("porygon-Z");
        teamNames.add("kingambit");
        teamNames.add("dragonite");
        teamNames.add("volcarona");
        double[] compressedTeam = c.CompressTeam(teamList);
        comparator.compareToData(compressedTeam, teamNames);
    }
}