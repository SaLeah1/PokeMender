package junior_is;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


// NOTE: Because iron moth just wasnt labled on PokeAIMMD the bot gets a little 
// bit screwy and replaces where its name should be with a block of code

public class PasteEater {
    public static String genString(URL website) throws IOException{
        String ret = "";
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
    public static Object[] huffGlue(String code) throws IOException{
        int indx = code.indexOf("<div class=\"sqs-gallery\">");
        int end = code.indexOf("<div id=\"footerWrapper\">");
        code = code.substring(indx,end);
        List<String> pokemonNames = new ArrayList<String>();
        List<String> pasteLinks = new ArrayList<String>();
        while(code.contains("href=\"")){
            indx = code.indexOf("href=\"");
            System.out.println(code.substring(indx+6, indx+42));
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
    public static void munchMarker() throws IOException{
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
        while(itt.hasNext()){
            System.out.println(counter);
            counter++;
            URL pasteLink = new URL(itt.next());
            String code = genString(pasteLink);
            String[] sateth = takeBite(code);
            code = sateth[0];
            String info = sateth[1];
            genVector(info, berry, jerry, counter);
            code = code.substring(code.indexOf("</pre>"),code.indexOf("<aside>")-23);
            while(code.contains("</pre>")){
                if (code.substring(0,code.indexOf("<pre>")+4).contains("<img class=\"img-pokemon\" src=\"/img/pokemon/0-0.png\">")){
                    code = code.substring(code.indexOf("<pre>")+5);
                }
                sateth = takeBite(code);
                code = sateth[0];
                info = sateth[1];     
                genVector(info, berry, jerry, counter);
            }
        }

    }
    public static String[] takeBite(String code){
        code = code.substring(code.indexOf("<pre>")+5);
        int end = code.indexOf("@"); // end of name
        String name = code.substring(0, end).toLowerCase();
        if (name.contains("span class=\"type-")){
            name = name.substring(name.indexOf(">")+1, name.indexOf("</span>"));
        }
        if (name.substring(name.length())==" "){
            name = name.substring(0,name.length()-1);
        }
        if (name.contains("(<span class=\"gender")){
            name = name.substring(0, name.indexOf("(")-1);
        }
        name = name.replaceAll(" ", "-");
        String item = code.substring(end+2,code.indexOf("\n")-2).toLowerCase();
        item = item.replaceAll(" ", "-");
        code = code.substring(code.indexOf("\n")+1);
        String ability = code.substring(code.indexOf("Ability: </span>")+16,code.indexOf("\n")-2).toLowerCase();
        ability = ability.replaceAll(" ", "-");
        code = code.substring(code.indexOf("Nature")+9);
        if (code.substring(0,code.indexOf("\n")).contains("IVs: ")){
            code = code.substring(code.indexOf("\n")+1);
        }
        String move1 = "";
        String move2 = "";
        String move3 = "";
        String move4 = "";
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
        } catch (Exception e) { // something has gone horribly wrong (the file is ended) so we must purge the file 
            code = code.substring(code.length()-4);
        }
        System.out.println      (name+","+ability+","+item+","+move1+","+move2+","+move3+","+move4);
        return new String[]{code,name+","+ability+","+item+","+move1+","+move2+","+move3+","+move4};
    }
    public static void genVector(String info, MoveInfoGen moveGenerator, TypeBot typeChecker, int counter) throws IOException{
        String[] parts = info.split(",");
        String[] names = new String[]{
            "normal","fire","water","electric","grass","ice","fighting","poison","ground",
            "flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy"};
        double[] typeSums = new double[18];
        double defC = 0.;
        double offC = 0.;
        Arrays.fill(typeSums,0);
        for (int x=3;x<7;x++){
            String item = parts[x];
            if (item != "                   "){
                String[] dTypes = moveGenerator.getMoveType(item);
                if (!dTypes[1].equals("status")){
                    offC += 1;
                    for (int j = 0; j < names.length; j++) {
                            double c = typeChecker.typeMatch(dTypes[0], names[j]);
                            typeSums[j] += c;
                        } 
                } else {
                    defC += 1;
                }
            }
        }
        FileWriter samantha = new FileWriter(
            String.format("pokerecomender\\src\\main\\java\\junior_is\\caches\\pokeVectorCache\\%s%d.vect",parts[0],counter)
            );
        FileWriter sasha = new FileWriter(
            String.format("pokerecomender\\src\\main\\java\\junior_is\\caches\\pokeSheetCache\\%s%d.txt",parts[0],counter)
            );
      sasha.write(info+"\n");
      sasha.close();
        for (Double val : typeSums){
            samantha.write(val+"\n");
        }
        samantha.write((offC/4.)+"\n");
        samantha.write((defC/4.)+"");
        samantha.close();
    }
    public static void main(String[] args) throws MalformedURLException, IOException {
        //PasteEater.huffGlue(PasteEater.genString(new URL("https://www.pokeaimmd.com/overused")));
        munchMarker();

    }
}
