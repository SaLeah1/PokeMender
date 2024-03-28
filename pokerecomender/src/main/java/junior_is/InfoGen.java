package junior_is;

// Exceptions
import java.io.IOException;

// Core
import java.net.URL;
import java.nio.charset.Charset;

// 3rd party
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.IOUtils;

public class InfoGen {

    public JSONObject getJSON(String PokemonName) throws IOException{
        PokemonName = PokemonName.toLowerCase();
        String urlString = String.format("https://pokeapi.co/api/v2/pokemon/%s",PokemonName);
        URL APICall = new URL(urlString);
        String json = IOUtils.toString(APICall, Charset.forName("UTF-8"));
        return new JSONObject(json);
    }

    public Object[] getInfo(String PokemonName) throws IOException{
        Object[] returner = new Object[3];
        JSONObject fullJSON = getJSON(PokemonName);
        // Type
        String[] typesArr = new String[2];
        JSONArray types = fullJSON.getJSONArray("types");
        for(int i = 0; i<types.length(); i++) {
            JSONObject typeInf = (JSONObject) types.get(i);
            String type = typeInf.getJSONObject("type").getString("name");
            typesArr[i] = type;
        }
        returner[0] = typesArr;
        // Stats
        int[] statsArr = new int[6];
        JSONArray stats = fullJSON.getJSONArray("stats");
        for(int i = 0; i<stats.length(); i++) {
            JSONObject statsInf = (JSONObject) stats.get(i);
            int value = statsInf.getInt("base_stat");
            statsArr[i] = value;
        }
        // Sprite
        returner[2] = fullJSON.getJSONObject("sprites").getJSONObject("other").getJSONObject("official-artwork").getString("front_default");
        return returner;
    }

    public String getSprite(String PokemonName) throws IOException{
        JSONObject fullJSON = getJSON(PokemonName);
        return fullJSON.getJSONObject("sprites").getJSONObject("other").getJSONObject("official-artwork").getString("front_default");
    }

    public int[] getStats(String PokemonName) throws IOException{
        int[] returner = new int[6];
        JSONObject fullJSON = getJSON(PokemonName);
        JSONArray stats = fullJSON.getJSONArray("stats");
        for(int i = 0; i<stats.length(); i++) {
            JSONObject statsInf = (JSONObject) stats.get(i);
            int value = statsInf.getInt("base_stat");
            returner[i] = value;
        }
        return returner;

    }
    public String[] getTypes(String PokemonName) throws IOException{
        String[] returner = new String[2];
        JSONObject fullJSON = getJSON(PokemonName);
        JSONArray types = fullJSON.getJSONArray("types");
        for(int i = 0; i<types.length(); i++) {
            JSONObject typeInf = (JSONObject) types.get(i);
            String type = typeInf.getJSONObject("type").getString("name");
            returner[i] = type;
        } if (returner[1] == null){returner[1] = "";}
        return returner;
    }
    public static void main(String[] args) throws IOException {
        InfoGen c = new InfoGen();
        String[] q = c.getTypes("pichu");
        for (String d : q){
            System.out.println(d);
        }

    }
}
