package junior_is;

import java.io.IOException;

public class Compressor {
    
    public double[] CompressTeam(String[][] team) throws IOException{
        /*
         * Team is reduced to 38 dimension vector
         * Team is originally a N x 38 vector where 0 < N <5 
         */
        MoveInfoGen moveGen = new MoveInfoGen();
        PokeInfoGen pokeGen = new PokeInfoGen();
        TypeBot typeChecker = new TypeBot();

        double[] compressedTeam = new double[38];
        for (String[] pokemon : team) {
            double[] compressedPokemon = compressPokemon(pokemon,moveGen,pokeGen,typeChecker);
            for (int i = 0; i<compressedPokemon.length; i++){
                compressedTeam[i] += compressedPokemon[i];
            }
        }
      for (int itt = 0; itt < compressedTeam.length; itt++) { // normalize
        compressedTeam[itt] /= team.length;
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
        for (int x=3;x<7;x++){
            String item = values[x];
            String[] dTypes = moveGenerator.getMoveType(item);
            if (!dTypes[1].equals("status")){
                offC += 1;
                for (int j = 0; j < names.length; j++) {
                        double c = typeChecker.typeMatch(dTypes[0], names[j]);
                        returnVect[j] += c;
                    } 
            } else {
                defC += 1;
            }
        }
        for(int x = 0; x<18; x++){ // normalize offensive powers
            returnVect[x] /= offC;
        }
        String name = values[0];
        String[] types = new String[]{" "," "};
        types = pokeGenerator.getTypes(name);
        for (int j = 0; j < names.length; j++) {
                double c = typeChecker.typeMatch(names[j],types[0],types[1]);
                returnVect[j+18] += c; // +18 to avoid overwriting offSums
            } 
        returnVect[36] = (offC/4.);
        returnVect[37] = (defC/4.);
        return returnVect;
    }

    public static void main(String[] args) throws IOException {
        Compressor c = new Compressor();
        String[] t1 = new String[]{"zapdos","damp-rock","pressure","tackle","tackle","tackle","earthquake"};
        String[] t2 = new String[]{"articuno","damp-rock","pressure","tackle","tackle","tackle","earthquake"};
        double[] compressedTeam = c.CompressTeam(new String[][]{t1,t2});
        for (int itemIndx = 0; itemIndx < compressedTeam.length; itemIndx++) {
            System.out.print(compressedTeam[itemIndx]+",");
        }
    }
}