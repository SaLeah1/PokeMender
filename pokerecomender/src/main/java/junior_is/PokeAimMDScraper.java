package junior_is;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


/*
 * This program does *not* use an api, it is just scraping the source code of a PokeAimMD page looking for pokePaste links
 * It then does the same for each pokePaste site, grabbing the pokemon name, item, ability, and moves based off of common patterns in the HTML
 * 
 * The program takes the input of name, item, ability, and moves to create two files: a pokeSheet and a pokeVector stored in resources
 * **TWO FILES ARE MADE FOR EVERY POKEMON IN EVERY POKEPASTE, THIS WILL CREATE ROUGHLY 2000 FILES**
 * 
 * Often the name used by pokepaste is not the same name as what is used by the pokeAPI, there is a collection of if statements trying to fix this
 *     If a name gets through all of those and still is not in the API, that pokemon is skipped (sorry tinkaton)
 * 
 * Please get permission from the pokepaste team before using this
 */

public class PokeAimMDScraper {
    public  String genString(URL website) throws IOException{ // input URL should be "https://www.pokeaimmd.com/overused" unless you are using a different gen / format
        String ret = "";                                      // this reads in the website's source code and returns it as a string
        BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    website.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null){
            ret+=inputLine+"\n";
        }
        in.close();
        return ret;
    }
    public  Object[] processSource(String code) throws IOException{  // accepting the source code string from genString()
        int indx = code.indexOf("<div class=\"sqs-gallery\">");  // this writes a file containing all pokepaste links found
        int end = code.indexOf("<div id=\"footerWrapper\">");    // this file is used by pokePasteScrape()
        code = code.substring(indx,end);
        List<String> pokemonNames = new ArrayList<String>();
        List<String> pasteLinks = new ArrayList<String>();
        while(code.contains("href=\"")){
            indx = code.indexOf("href=\"");
            //System.out.println(code.substring(indx+6, indx+42));
            pasteLinks.add(code.substring(indx+6,indx+42));
            int nameIndx = code.indexOf("\" alt=\"");
            int nameEnd = code.indexOf(" - ");
            //System.out.println(nameEnd);
            //System.out.println(code.substring(nameIndx+7, nameEnd));
            FileWriter nameout = new FileWriter("pokerecomender\\src\\main\\java\\junior_is\\names.txt",true);
            FileWriter pasteout = new FileWriter("pokerecomender\\src\\main\\java\\junior_is\\pastes.txt",true);
            nameout.write(code.substring(nameIndx+7,nameEnd)+"\n");
            pasteout.write(code.substring(indx+6,indx+42)+"\n");
            nameout.close();
            pasteout.close();
            pokemonNames.add(code.substring(nameIndx+7,nameEnd));
            code = code.substring(indx+6);
        }
        return new Object[]{pokemonNames,};
    }
    public  void pokePasteScrape() throws IOException{              // reads the file at the location below, iterates through it to access each pokePaste
        List<String> pasteLinks = new ArrayList<String>();
        Scanner iStream = new Scanner(new File("pokerecomender\\src\\main\\java\\junior_is\\pastes.txt"));
        while(iStream.hasNext()){
            String item = iStream.nextLine();
            pasteLinks.add(item);
        } iStream.close();
        int counter = 1;
        Iterator<String> itt = pasteLinks.iterator();
        MoveInfoGen berry = new MoveInfoGen();
        TypeBot jerry = new TypeBot();
        PokeInfoGen larry = new PokeInfoGen();
        while(itt.hasNext()){
            System.out.println(counter);                    // as of 4/17/2024 the pokeaimMD page had ~200 pokepaste links
            counter++;
            URL pasteLink = new URL(itt.next());
            String code = genString(pasteLink);
            String[] modCode = takeBite(code);              // takeBite() finds the first instance of a pokemon in the code then returns that pokemon's info and the code with that 'mon removed
            code = modCode[0];                              // this is called once outside of the loop because the first pokemon is slightly different in code
            String info = modCode[1];
            genVector(info, berry, larry, jerry, counter);  // convert the info found in takeBite() to a 38x1 vector, see below for more info
            code = code.substring(code.indexOf("</pre>"),code.indexOf("<aside>")-23);       // trimming the header of the file
            while(code.contains("</pre>")){               // after the file has been trimmed, every pokemon info section directly follows a "</pre>"
                if (code.substring(0,code.indexOf("<pre>")+4).contains("<img class=\"img-pokemon\" src=\"/img/pokemon/0-0.png\">")){    // if the pokemon is blank, skip it
                    code = code.substring(code.indexOf("<pre>")+5);
                }
                modCode = takeBite(code);
                code = modCode[0];
                info = modCode[1];     
                genVector(info, berry, larry, jerry, counter);
            }
        }

    }
    public  String[] takeBite(String code){
        code = code.substring(code.indexOf("<pre>")+5);                     // remove header from info block
        int end = code.indexOf("@"); // end of name             
        String name = code.substring(0, end).toLowerCase();
        if (name.contains("span class=\"type-")){                             // the vast majority of pokemon in a paste have a type id however for the ones that dont, this prevents breakage
            name = name.substring(name.indexOf(">")+1, name.indexOf("</span>"));
        } if (name.contains("(<span class=\"gender")){                        // some pokemon have gender markers (pretty much just indeedee and nidoran)
            name = name.substring(0, name.indexOf("(")-1);       // this removes them
        } 
        name = name.replaceAll(" ", "-");                     // this replaces all spaces with "-" so that the the API can read them
        if (name.substring(name.length()-1).equals("-")){              // Sometimes there are spaces at the end of the name so they get kerned
            name = name.substring(0,name.length()-1);
        } // for names that arent represented correctly
        if ((                                                                   // the incarnate form genies need their ending added
            name.contains("tornadus") || 
            name.contains("thundurus") ||
            name.contains("landorus") ||
            name.contains("enamorus")
            ) &&
            !name.contains("-therian")){ // if its a genie, add the correct ending
            name = name+"-incarnate";
        } 
        if (name.contains("meloetta")){name += "-aria";}                      // Meloetta needs her ending added
        if (name.equals("igant-hisui")){name = "lilligant-hisui";}     // pokepaste refers to lilligant-hisui as just igant for some reason
        if (name.equals("arunt")){name = "pecharunt";}                 // pecharunt is shortened to arunt for some reason
        if (name.equals("-boulder")){name = "iron-boulder";}           // iron-boulder and iron-crown get reduced to just crown and boulder but only sometimes
        if (name.equals("-crown")){name = "iron-crown";}
        if (name.equals("indeedee")){name = "indeedee-female";}        // when I remove the gender info, I lose whether this is indeedee-f or indeedee-m. no one uses indeedee-m so I am assuming here
        if (name.equals("minior")){name = "minior-blue";}              // all miniors are exactly the same but the api needs a color specification
        String item = code.substring(end+2,code.indexOf("\n")-2).toLowerCase();
        item = item.replaceAll(" ", "-");
        code = code.substring(code.indexOf("\n")+1);                        // navigate to the next line in the HTML (contains ability info)
        String ability = code.substring(code.indexOf("Ability: </span>")+16,code.indexOf("\n")-2).toLowerCase();
        ability = ability.replaceAll(" ", "-");
        code = code.substring(code.indexOf("Nature")+9);                    // skips 2 or three lines depending on the context
        if (code.substring(0,code.indexOf("\n")).contains("IVs: ")){
            code = code.substring(code.indexOf("\n")+1);                    // if IVs ar specified, skip them, otherwise do nothing
        }
        String move1 = "";                      // The following code block is the same thing four times (once for each move slot) 
        String move2 = "";                      // check to see if the move is blank, if it is, skip it.
        String move3 = "";                      // If not, get the text and replace spaces with "-"
        String move4 = "";                      //
        try {
            if (code.substring(0, code.indexOf("\n")).contains("- ")){ // if contains unmarked move
                move1 = code.substring(code.indexOf("- ")+2, (code.indexOf("\n")-2)).toLowerCase();
            } else {move1 = code.substring(code.indexOf(">-</span> ")+10, (code.indexOf("\n")-2)).toLowerCase();}
            move1 = move1.replaceAll(" ", "-");
            code = code.substring(code.indexOf("\n")+1);
            if (code.substring(0, code.indexOf("\n")).contains("- ")){ // if contains unmarked move
                move2 = code.substring(code.indexOf("- ")+2, (code.indexOf("\n")-2)).toLowerCase();
            } else {move2 = code.substring(code.indexOf(">-</span> ")+10, (code.indexOf("\n")-2)).toLowerCase();}
            move2 = move2.replaceAll(" ", "-");
            code = code.substring(code.indexOf("\n")+1);
            if (code.substring(0, code.indexOf("\n")).contains("- ")){ // if contains unmarked move
                move3 = code.substring(code.indexOf("- ")+2, (code.indexOf("\n")-2)).toLowerCase();
            } else {move3 = code.substring(code.indexOf(">-</span> ")+10, (code.indexOf("\n")-2)).toLowerCase();}
            move3 = move3.replaceAll(" ", "-");
            code = code.substring(code.indexOf("\n")+1);
            if (code.substring(0, code.indexOf("\n")).contains("- ")){ // if contains unmarked move
                move4 = code.substring(code.indexOf("- ")+2, (code.indexOf("\n")-2)).toLowerCase();
            } else {move4 = code.substring(code.indexOf(">-</span> ")+10, (code.indexOf("\n")-2)).toLowerCase();}
            move4 = move4.replaceAll(" ", "-");
            code = code.substring(code.indexOf("\n")+1);
        } catch (Exception e) { // something has gone horribly wrong (the file has ended) so we must purge the file 
            code = code.substring(code.length()-4);
        }
        //System.out.println      (name+","+ability+","+item+","+move1+","+move2+","+move3+","+move4);
        return new String[]{code,name+","+ability+","+item+","+move1+","+move2+","+move3+","+move4};
    }
    public  void genVector(String info, MoveInfoGen moveGenerator, PokeInfoGen pokeGenerator, TypeBot typeChecker, int counter) throws IOException{
        String[] parts = info.split(",");                                               // split the returned string into its components
        String[] names = new String[]{                                                        // generate the offensive and defensive vectors of the pokemon
            "normal","fire","water","electric","grass","ice","fighting","poison","ground",    
            "flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy"};
        double[] typeSums = new double[18];
        double defC = 0.;
        double offC = 0.;
        Arrays.fill(typeSums,0);
        for (int x=3;x<7;x++){                                                                 // for each move the pokemon has
            String item = parts[x];                                         
            String[] dTypes = moveGenerator.getMoveType(item);                                 // get the move's type
            for (int j = 0; j < names.length; j++) {
                if (!dTypes[1].equals("status")){                                     // if the move isnt a status, compare it against each type in "names"
                    offC += 1;
                    double c = typeChecker.typeMatch(dTypes[0], names[j]);                     // (typeMatch is a typeBot)
                    typeSums[j] += c;                                                          // add the result of the type match to the corresponding part of the vector
                } else {typeSums[j]+=1;}
            }
        }
        double[] defSums = new double[18];
        Arrays.fill(defSums,0);
        String item = parts[0];
        String[] types = new String[]{" "," "};
        try {
            types = pokeGenerator.getTypes(item);
        } catch (Exception e) {
            return;
        }
        
        for (int j = 0; j < names.length; j++) {                                                // do the same for the defensive vector except comparing each type as offensive against the pokemon's type(s)
                double c = typeChecker.typeMatch(names[j],types[0],types[1]);
                defSums[j] += c;
            } 
        FileWriter vectorWriter = new FileWriter(
            String.format("pokerecomender\\src\\main\\resources\\pokeVectorCache\\%s%d.vect",parts[0],counter)
            );
        FileWriter sheetWriter = new FileWriter(
            String.format("pokerecomender\\src\\main\\resources\\pokeSheetCache\\%s%d.txt",parts[0],counter)
            );
        sheetWriter.write(info+"\n");
        sheetWriter.close();
        for (Double val : typeSums){
            if (offC == 0.){
                vectorWriter.write(0.+"\n");
            } else {vectorWriter.write(val/offC+"\n");}
        }
        for (Double val : defSums){
            vectorWriter.write(val+"\n");
        }
        vectorWriter.write((offC/4.)+"\n");
        vectorWriter.write((defC/4.)+"");
        vectorWriter.close();
    }
    public static void main(String[] args) throws IOException {
        PokeAimMDScraper p = new PokeAimMDScraper();
        //PokeAimMDScraper.processSource(PokeAimMDScraper.genString(new URL("https://www.pokeaimmd.com/overused")));
        p.pokePasteScrape();
    }
}
