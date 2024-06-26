package junior_is;

// Core
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Converts each individual pokemon into a 'pokeVector' and then reduces them into the returned 'teamVector'
 * PokeVectors are length 38 vectors. 
 * 
 * The first 18 are the offensive effectivness of a pokemon for each type 
 *   generated by running each of their offensive moves against each type in TypeBot.
 * The next 18 are the defensive effectivness of a pokemon against each type
 *   generated by running the pokemon's type(s) against each type in TypeBot.
 * The last two dimensions are the aggression and stall scores of a pokemon
 *   Agression score is equal to the number of offensive moves / 4
 *   Stall score is equal to the number of status moves / 4 (or 1 - Agression Score)
 * 
 * The pokeVectors are then combined into a single teamVector 
 *   each dimension is the average of that dimension in each of the pokeVectors
 */

public class Compressor {
    
    public double[] CompressTeam(List<String[]> team) throws IOException{
        MoveInfoGen moveGen = new MoveInfoGen(); //
        PokeInfoGen pokeGen = new PokeInfoGen(); // used for gathering information about the selected pokemon 
        TypeBot typeChecker = new TypeBot();     //

        double[] compressedTeam = new double[38];
        for (String[] pokemon : team) {
            double[] compressedPokemon = compressPokemon(pokemon,moveGen,pokeGen,typeChecker);
            for (int i = 0; i<compressedPokemon.length; i++){
                compressedTeam[i] += compressedPokemon[i];
            }
        }
      for (int itt = 0; itt < compressedTeam.length; itt++) { // normalize
        compressedTeam[itt] /= team.size();
      }
        return compressedTeam;
    }
    public double[] compressPokemon (String[] values, MoveInfoGen moveGenerator, PokeInfoGen pokeGenerator, TypeBot typeChecker) throws IOException{
        // submit values as [name,item,ability,move1,move2,move3,move4]
        double[] returnVect = new double[38];
        String[] names = new String[]{
            "normal","fire","water","electric","grass","ice","fighting","poison","ground",
            "flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy"};
        double offC = 0.;
        double defC = 0.;
        for (int x=3;x<7;x++){                                                  // for each move the pokemon has
            String item = values[x];
            String[] dTypes = moveGenerator.getMoveType(item);                  // get the move's type
            if (!dTypes[1].equals("status")){
                offC += 1;
                for (int j = 0; j < names.length; j++) {                        // check the move's type against each type (stored in names)
                        double c = typeChecker.typeMatch(dTypes[0], names[j]);  // each result from the typChecker (a typeBot) is added to the corresponding section in returnVect
                        returnVect[j] += c;
                    } 
            } else {
                defC += 1;
            }
        }
        String name = values[0];
        String[] types = new String[]{" "," "};
        types = pokeGenerator.getTypes(name);
        for (int j = 0; j < names.length; j++) {                                // for each type, call typeBot against the pokemon's types
                double c = typeChecker.typeMatch(names[j],types[0],types[1]);
                returnVect[j+18] += c; // +18 to avoid overwriting offSums
            } 
        returnVect[36] = (offC);                                             // generate Agression score
        returnVect[37] = (defC);                                             // generate Stall score
        return returnVect;
    }

    public static void main(String[] args) throws IOException {
        Compressor c = new Compressor();
        List<String[]> teamList = new ArrayList<String[]>();
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
        double[] compressedTeam = c.CompressTeam(teamList);
        for (int itemIndx = 0; itemIndx < compressedTeam.length; itemIndx++) {
            System.out.print(compressedTeam[itemIndx]+",");
        }
    }
}
