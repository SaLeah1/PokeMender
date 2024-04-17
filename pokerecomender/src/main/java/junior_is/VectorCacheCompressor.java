package junior_is;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/*
 * Note: this is not usable without a filled pokeVectorCache folder
 * To generate that, use the PokeAimMDScraper (will not work without the pastes.txt file in the same directory)
 * 
 * Combines all vectors in "pokeVectorCache" into a single file "pokeVectData"
 * I do not recommend attempting to regenerate the file (more info in PokeAimMDScraper.java)
 */

public class VectorCacheCompressor {

    public VectorCacheCompressor() throws IOException{
        File[] s = new File("pokerecomender\\src\\main\\resources\\pokeVectorCache").listFiles();
        FileWriter juniper = new FileWriter("pokerecomender\\src\\main\\resources\\pokeVectData.vect");
        for (int fileNum = 0; fileNum < s.length; fileNum++) { // juniper = writer
            Scanner august = new Scanner(s[fileNum]);          // august  = reader
            while (august.hasNextLine()){
                juniper.write(august.nextLine());
                if(august.hasNextLine()){
                    juniper.write(",");
                }
            }
            juniper.write("\n");
            august.close();
        }
        juniper.close();
    }

    public static void main(String[] args) throws IOException {
        new VectorCacheCompressor();
    }
}
