package junior_is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math3.linear.*;

public class TeamComparator {
    public Map<double[],String> vectors;
    public TeamComparator() throws FileNotFoundException{
        getDataFromFile();
    }

    public Map<String,Double> compareToData(double[] teamVector){
        Map<String,Double> similarityList = new HashMap<String,Double>();
        for (Entry<double[],String> entrySet : this.vectors.entrySet()) {
            double[] monVector = entrySet.getKey();
            String fileName = entrySet.getValue();
            Double coSim = cosSim(teamVector, monVector);
            similarityList.put(fileName,coSim);
        }
        similarityList = sortHashMap(similarityList);
        int count = 0 ;
        for (Entry<String,Double> entrySet : similarityList.entrySet()) {
            System.out.println(entrySet.getKey()+" "+entrySet.getValue());
            //count++;
            if(count>10){break;}
        }
        return similarityList;
    }

    public void getDataFromFile() throws FileNotFoundException{ // preprocessing
        Scanner fileReader = new Scanner(new File("pokerecomender\\src\\main\\resources\\pokeVectData.vect"));
        Map<double[],String> vectors = new HashMap<double[],String>();
        File[] files = new File("pokerecomender\\src\\main\\resources\\pokeSheetCache").listFiles();
        List<String> fileNames = new ArrayList<String>();
        for (File file : files) {
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
    public Double cosSim(double[] Vect1, double[] Vect2){
        /* returns a double between -1 and 1
         * 1 is exact match, -1 is exact mismatch
         */
        RealVector Matrx1 = MatrixUtils.createRealVector(Vect1);
        RealVector Matrx2 = MatrixUtils.createRealVector(Vect2);
        return Matrx1.cosine(Matrx2);
    }

    public Map<String,Double> sortHashMap(Map<String,Double> map){
        List<Map.Entry<String, Double> > listedMap =
               new LinkedList<Map.Entry<String, Double> >(map.entrySet());
        // Sort the list
        Collections.sort(listedMap, new Comparator<Map.Entry<String, Double>>() { // sort using the compare function defined internally
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){
                return (o1.getValue()).compareTo(o2.getValue());
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
        String[] t1 = new String[]{"zapdos","damp-rock","pressure","hurricane","volt-switch","heat-wave","roost"};
        String[] t2 = new String[]{"articuno","damp-rock","pressure","roost","freeze-dry","u-turn","brave-bird"};
        double[] compressedTeam = c.CompressTeam(new String[][]{t1,t2});
        comparator.compareToData(compressedTeam);
    }
}