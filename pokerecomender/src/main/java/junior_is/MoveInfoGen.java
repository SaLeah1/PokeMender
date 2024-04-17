package junior_is;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
// Exceptions
import java.io.IOException;

// Core
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// 3rd party
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

/*
 * Proxy for PokeAPI server, methods are for accessing move information
 * Caches files to pokerecomender\src\main\java\junior_is\caches\moveCache
 * getJSON checks if a file is cached, if it is, it returns that file as a JSONObject, else it queies PokeAPI and saves the result to the cache
 * Use 'getMoveType()' to get an array containing the move class (phsyical, special, or status) and its type
 */


public class MoveInfoGen { 

    List<String> cachedMoves = new ArrayList<String>();

    public MoveInfoGen() throws FileNotFoundException{ // grab the list of cached files
        Scanner iStream = new Scanner(new File("pokerecomender/src/main/java/junior_is/caches/moveCache/.moves"));
        while(iStream.hasNext()){
            String item = iStream.nextLine();
            cachedMoves.add(item);
        } iStream.close();
    }

    public JSONObject getJSON(String moveName) throws IOException{
        moveName = moveName.toLowerCase();
        if (cachedMoves.contains(moveName)){
            //System.out.println("got");
            Path cache = Path.of(String.format("pokerecomender/src/main/java/junior_is/caches/moveCache/%s.json",moveName));
            String jsonString = Files.readString(cache,Charset.forName("UTF-8"));
            return new JSONObject(jsonString);
        } else {
            return download(moveName);
        }
    }
    public JSONObject download(String moveName) throws IOException{
        //System.out.println("Downloaded");
        moveName = moveName.toLowerCase();
        String urlString = String.format("https://pokeapi.co/api/v2/move/%s",moveName);
        URL APICall = new URL(urlString);
        String json = IOUtils.toString(APICall, Charset.forName("UTF-8"));
        JSONObject jason = new JSONObject(json);
        jason.remove("effect_entries");
        jason.remove("flavor_text_entries");
        jason.remove("learned_by_pokemon");
        jason.remove("names");
        json = jason.toString();
        FileWriter JSONfile = new FileWriter(String.format("pokerecomender/src/main/java/junior_is/caches/moveCache/%s.json",moveName));
        JSONfile.write(json);
        JSONfile.close();
        FileWriter cacheFile = new FileWriter("pokerecomender/src/main/java/junior_is/caches/moveCache/.moves",true);
        cacheFile.write("\n"+moveName);
        cacheFile.close();
        cachedMoves.add(moveName);
        return new JSONObject(json);
    }
    public String[] getMoveType(String moveName) throws IOException{
        String[] ret = new String[2];
        if (moveName == "                   "){
            ret[0] = "status";
            ret[1] = "";
            return ret;
        }
        JSONObject moveJSON = getJSON(moveName);
        String dClass = moveJSON.getJSONObject("damage_class").getString("name");
        ret[1] = dClass;
        String type = moveJSON.getJSONObject("type").getString("name");
        ret[0] = type;
        return ret;
    }
    public static void main(String[] args) throws IOException {
        MoveInfoGen c = new MoveInfoGen();
        c.getMoveType("splash");
    }
}
