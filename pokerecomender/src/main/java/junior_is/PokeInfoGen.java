package junior_is;


// Exceptions
import java.io.IOException;
import java.io.FileNotFoundException;

// Core
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// 3rd party
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.IOUtils;

/*
 * Proxy for PokeAPI server, methods are for accessing pokemon information
 * Caches files to pokerecomender\src\main\java\junior_is\caches\moveCache
 * getJSON checks if a file is cached, if it is, it returns that file as a JSONObject, else it queies PokeAPI and saves the result to the cache
 * Use 'getSprite()' to get a string which is the url of a sprite
 * Use 'getStats()' to get an array of integers containing the pokemon's base stats (Unused)
 * Use 'getMoves()' 'getAbilities()' or 'getTypes()' to get an array of strings containing all moves, abilities, or types respectivley
 */

public class PokeInfoGen {

    List<String> cachedMons = new ArrayList<String>();

    public PokeInfoGen() throws FileNotFoundException{ // grab the list of cached files
        Scanner iStream = new Scanner(new File("pokerecomender/src/main/java/junior_is/caches/pokeCache/.pokemon"));
        while(iStream.hasNext()){
            String item = iStream.nextLine();
            cachedMons.add(item);
        } iStream.close();
    }

    public JSONObject getJSON(String pokemonName) throws IOException{  // main proxy method, checks to see if cached and if not downloads and adds to cache
        pokemonName = pokemonName.toLowerCase();
        if (cachedMons.contains(pokemonName)){
            Path cache = Path.of(String.format("pokerecomender/src/main/java/junior_is/caches/pokeCache/%s.json",pokemonName));
            String jsonString = Files.readString(cache);
            return new JSONObject(jsonString);
        } else {
            return download(pokemonName);
        }
    }

    public JSONObject download(String pokemonName) throws IOException{
        pokemonName = pokemonName.toLowerCase();
        String urlString = String.format("https://pokeapi.co/api/v2/pokemon/%s",pokemonName);
        URL APICall = new URL(urlString);
        String json = IOUtils.toString(APICall, Charset.forName("UTF-8"));
        FileWriter JSONfile = new FileWriter(String.format("pokerecomender/src/main/java/junior_is/caches/pokeCache/%s.json",pokemonName));
        JSONfile.write(json);
        JSONfile.close();
        FileWriter cacheFile = new FileWriter("pokerecomender/src/main/java/junior_is/caches/pokeCache/.pokemon",true);
        cacheFile.write("\n"+pokemonName);
        cacheFile.close();
        cachedMons.add(pokemonName);
        return new JSONObject(json);
    }

    public String getSprite(String pokemonName) throws IOException{
        JSONObject fullJSON = getJSON(pokemonName);
        return fullJSON.getJSONObject("sprites").getJSONObject("other").getJSONObject("official-artwork").getString("front_default");
    }

    public int[] getStats(String pokemonName) throws IOException{
        int[] returner = new int[6];
        JSONObject fullJSON = getJSON(pokemonName);
        JSONArray stats = fullJSON.getJSONArray("stats");
        for(int i = 0; i<stats.length(); i++) {
            JSONObject statsInf = (JSONObject) stats.get(i);
            int value = statsInf.getInt("base_stat");
            returner[i] = value;
        }
        return returner;

    }
    public String[] getTypes(String pokemonName) throws IOException{
        String[] returner = new String[2];
        JSONObject fullJSON = getJSON(pokemonName);
        JSONArray types = fullJSON.getJSONArray("types");
        for(int i = 0; i<types.length(); i++) {
            JSONObject typeInf = (JSONObject) types.get(i);
            String type = typeInf.getJSONObject("type").getString("name");
            returner[i] = type;
        } if (returner[1] == null){returner[1] = "";}
        return returner;
    }
    public String[] getMoves(String pokemonName) throws IOException{
        JSONObject fullJSON = getJSON(pokemonName);
        JSONArray moves = fullJSON.getJSONArray("moves");
        String[] ret = new String[moves.length()+1];
        ret[0] = "                   ";
        for (int i = 0; i < moves.length(); i++) {
            JSONObject moveInfo = (JSONObject) moves.get(i);
            String moveName = moveInfo.getJSONObject("move").getString("name");
            ret[i+1] = moveName;
        }
        return ret;
    }
    public String[] getAbilities(String pokemonName) throws IOException{
        JSONObject fullJSON = getJSON(pokemonName);
        JSONArray abilities = fullJSON.getJSONArray("abilities");
        String[] ret = new String[abilities.length()+1];
        ret[0] = "                   ";
        for (int i = 0; i < abilities.length(); i++) {
            JSONObject abilityInfo = (JSONObject) abilities.get(i);
            String abilityName = abilityInfo.getJSONObject("ability").getString("name");
            ret[i+1] = abilityName;
        }
        return ret;
    }
    public static void main(String[] args) throws IOException {
        PokeInfoGen c = new PokeInfoGen();
        c.getAbilities("pichu");
    }
}
