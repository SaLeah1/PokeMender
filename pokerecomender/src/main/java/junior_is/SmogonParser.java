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
