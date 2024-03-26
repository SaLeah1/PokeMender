package junior_is;

// Exceptions
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

// Core
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.util.function.IntConsumer;

/*
Full list of imports used anywhere within the project as follows:

// Exceptions
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

// Core
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.*;

// 3rd party
import net.miginfocom.swing.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.IOUtils;

*/
public class Main {

    public SmogonParser parser; // reads in smogon json file || can return Pokemon Names
    public InfoGen pokeGenerator; // PokeAPI access

    public GUI mainWindow; // 我恨恨恨这个东西

    public CatListener[] moveListeners;

    public Main() throws MalformedURLException, IOException{
        pokeGenerator = new InfoGen();
        parser = new SmogonParser("https://www.smogon.com/stats/2024-01/chaos/gen3ou-1760.json");
        Iterator<String> pokemonItt = parser.getPokemon();
        List<String> pokemonList = new ArrayList<>();
        pokemonList.add("                ");
        pokemonItt.forEachRemaining(pokemonList::add);
        String[] pokemonArr = Arrays.copyOf(pokemonList.toArray(), pokemonList.toArray().length, String[].class);
        mainWindow = new GUI(new File("pokerecomender\\src\\main\\resources\\items.txt"));
        int i = 0;
        int j = 0;
        int l = 0;
        
        IntConsumer nameConsumer = value -> { // do not ask me why the int consumer doesnt accept declared throws
            try {                            // I do not know
                nameUpdated(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        IntConsumer moveConsumer = this::moveUpdated;
        IntConsumer abilityConsumer = this::abilityUpdated;
        IntConsumer natureConsumer = this::natureUpdated;
        IntConsumer itemConsumer = this::itemUpdated;
        IntConsumer spreadConsumer = this::spreadUpdated;
        
        for(TeamPanel p : mainWindow.teamPanels){
            // Move listeners
            CatListener mL1 = new CatListener(i, moveConsumer);
            p.moveOneBox.addActionListener(mL1); i++;
            CatListener mL2 = new CatListener(i, moveConsumer);
            p.moveTwoBox.addActionListener(mL2); i++;
            CatListener mL3 = new CatListener(i, moveConsumer);
            p.moveThreeBox.addActionListener(mL3); i++;
            CatListener mL4 = new CatListener(i, moveConsumer);
            p.moveFourBox.addActionListener(mL4); i++;

            // NIAN listeners
            CatListener nL = new CatListener(j,nameConsumer);
            p.pokemonNames.addActionListener(nL);
            CatListener iL = new CatListener(j,itemConsumer);
            p.itemNames.addActionListener(iL);
            CatListener aL = new CatListener(j,abilityConsumer);
            p.abilityNames.addActionListener(aL);
            CatListener naL = new CatListener(j,natureConsumer);
            p.natureNames.addActionListener(naL);

            // Spread listeners
            for (int k = 0; k < 6; k++){
                SpreadListener sL = new SpreadListener(l,spreadConsumer);
                p.spinners[k].addChangeListener(sL);
            }

            mainWindow.updatePokemonList(p, pokemonArr);
        }
    }

    /* 
     * -----------------------------------------------------------------------------------------------------
     * The following methods are linked to listeners                                                       *
     * Move, Item, Ability, and Nature are all CatListeners which implement ActionListener                 *
     * Spread is a SpreadListener which implements ChangeListener                                          *
     * These methods are attached to IntConsumers and then passed into the listeners when they are created *
     * The listener calls the method within the file by using the .accept() function                       *
     * -----------------------------------------------------------------------------------------------------
     */

    public void LaserPointer(int listenerUID){ // test function for consumers
        System.out.println(String.format("%d fired",listenerUID));
    }
    public void nameUpdated(int listenerUID) throws MalformedURLException, IOException { // changing the selected name updates sprite, movepool, and abilities
        String item = (String) mainWindow.teamPanels[listenerUID].pokemonNames.getSelectedItem();
        if(item=="                "){ // if the empty name is selected, reset everything
            mainWindow.setNull(mainWindow.teamPanels[listenerUID]);
        } else {
            Set<String> moveSet = parser.getTAMIData(item,"Moves").keySet();
            String[] moves = new String[moveSet.size()];
            int k = 0; 
            for (String x : moveSet){moves[k++] = x;} // small loops can be written in one line if you hate your readers <3
            moves[0] = "                ";
            Set<String> abilSet = parser.getTAMIData(item,"Abilities").keySet();
            String[] abil = new String[abilSet.size()+1]; // +1 to include the blank
            k = 1;
            for (String ability : abilSet){abil[k++] = ability;}
            abil[0] = "                ";
            mainWindow.updateSprite(mainWindow.teamPanels[listenerUID], pokeGenerator.getSprite(item));
            mainWindow.updateMovesList(mainWindow.teamPanels[listenerUID], moves);
            mainWindow.updateAbilitiesList(mainWindow.teamPanels[listenerUID], abil);
        }
    }
    public void moveUpdated(int listenerUID){
        int panel = (int) Math.floor(listenerUID/4.);
        int slot = (int) Math.floor(listenerUID%4.);
        String item = (String) mainWindow.teamPanels[panel].moveBoxes[slot].getSelectedItem();
        System.out.println(item);
    }
    public void itemUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].itemNames.getSelectedItem();
        System.out.println(item);
    }
    public void abilityUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].abilityNames.getSelectedItem();
        System.out.println(item);
    }
    public void natureUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].natureNames.getSelectedItem();
        System.out.println(item);
    }
    public void spreadUpdated(int listenerUID) {
        int panel = (int) Math.floor(listenerUID/6.);
        int box = (int) Math.floor(listenerUID%6.);
        int item = (int) mainWindow.teamPanels[panel].spinners[box].getValue();
        System.out.println(item);
    }
    public void getInfo(){
        System.out.println("help");
        String[] pokemonNames = new String[6];
        double[] pokemonUsage = new double[6];
        int itt = 0;
        for (TeamPanel x : this.mainWindow.teamPanels){
            String name = (String) x.pokemonNames.getSelectedItem();
            if (name.replaceAll("\\s", "") == ""){
                ;
            } else {
                double usage = parser.getUsage(name);
                pokemonNames[itt] = name;
                pokemonUsage[itt++] = usage;
            }
        }
        for (int qwerty = 0; qwerty<itt; qwerty++){
            System.out.println(pokemonNames[qwerty]+" : "+Double.toString(pokemonUsage[qwerty]));
        }
    }
    public static void main(String[] args) throws MalformedURLException, IOException{
        new Main();
    }
}