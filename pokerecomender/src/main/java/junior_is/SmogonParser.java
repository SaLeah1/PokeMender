package junior_is;

// exceptions
import java.io.IOException;
import java.net.MalformedURLException;

// core
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// 3rd party
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

/*
 * Note, very few pieces of smogon data are implemented into the recommender. Sorry 👍
 * 
 * Smogon parser acts as a pseudo-api for a smogon chaos file
 * Smogon stores data from their pokemon simulator in JSON files located at https://www.smogon.com/stats/2024-01/chaos/
 * This class reads in a JSON file, processes it to find the data section and the info section
 * Data contains all usage data about pokemon.
 * Info contains specifics about the simulator such as number of games played and average playerweight
 * 
 * After the data and info have been sorted, the following methods allow for querying the Data
 *      getPokemon() returns a list of all avalible pokemon
 *      getUsage() takes in a pokemon name and returns how often that pokemon has been used in recorded games
 *      getTAMIData() takes in a pokemon name and a specifier (teammates, abilities, moves, items) and returns a map containing the different types of the specifier and their corresponding usage rates
 */

public class SmogonParser {
    public JSONObject info;
    public JSONObject data;

    public SmogonParser(String urlString) throws MalformedURLException, IOException{
        parseJson(new URL(urlString));
    }

    public void parseJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        JSONObject fullJSON = new JSONObject(json);
        info = fullJSON.getJSONObject("info");
        data = fullJSON.getJSONObject("data");
    }

    public Iterator<String> getPokemon(){
        Iterator<String> keys = this.data.keys();
        return keys;
    }

    public double getUsage(String PokemonName){
        return this.data.optJSONObject(PokemonName).getDouble("Usage");
    }

    public Map<String,Double> getTAMIData(String PokemonName, String DataType){ // returns {TAMI names : TAMI parUsage}
        String[] DataTypes = {"Moves","Teammates","Abilities","Items"};
        Map<String,Double> returner = new HashMap<String,Double>();
        JSONObject pokemonData = this.data.optJSONObject(PokemonName);
        // if pokemon doesnt exist (or just isnt on the generation's JSON), return an empty returner
        // also return empty if given a wrong datatype (see DataTypes[])
        if(pokemonData == null || Arrays.asList(DataTypes).contains(DataType) == false){
            System.out.println(PokemonName + " " + DataType);
            return returner;
        } else {
            JSONObject TAMIData = pokemonData.getJSONObject(DataType);
            Iterator<String> TAMIs = TAMIData.keys();
            while(TAMIs.hasNext()){
                String TAMI = TAMIs.next();
                returner.put(TAMI, TAMIData.getDouble(TAMI)); // Hell hath frozen over
            }
        }
        return returner;
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        SmogonParser a = new SmogonParser("https://www.smogon.com/stats/2024-01/chaos/gen1ou-1760.json");
        System.out.println("start");
        System.out.println(a.getTAMIData("Zapdos","Moves").keySet());
        System.out.println(a.getTAMIData("Vaporeon","Abilities").keySet());
        System.out.println("end");
    }
}
