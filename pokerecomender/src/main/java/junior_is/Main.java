package junior_is;

// Exceptions
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

// Core
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.util.function.IntConsumer;

/*
 * TODO: Add naming exceptions to PokeInfoGen. Found issues are as follows:
 * Ogerpon-x should be reduced to just ogerpon
 * Escue should be expanded to escue-ice-head or whatever its called
 */

public class Main {

    public SmogonParser parser; // reads in smogon json file || can return Pokemon Names
    public PokeInfoGen pokeGenerator; // PokeAPI access for pokemon info
    public MoveInfoGen moveGenerator;  // PokeAPI access for move info

    public TypeBot typeChecker; // creates vector of type match up between an offenseive and 1/2 defensive types

    public Compressor compressor;
    public TeamComparator teamComparator;

    public GUI mainWindow; // 我恨恨恨这个东西

    public CatListener[] moveListeners;

    public Main() throws MalformedURLException, IOException{
        pokeGenerator = new PokeInfoGen();
        moveGenerator = new MoveInfoGen();
        typeChecker = new TypeBot();
        compressor = new Compressor();
        teamComparator = new TeamComparator();
        parser = new SmogonParser("https://www.smogon.com/stats/2024-03/chaos/gen9ou-0.json");
        Iterator<String> pokemonItt = parser.getPokemon();
        List<String> pokemonList = new ArrayList<>();
        pokemonList.add("                   ");
        //pokemonList.add("Bisharp"); // for testing the type gen. This is supposed to break the gui display dw
        pokemonItt.forEachRemaining(pokemonList::add);
        String[] pokemonArr = Arrays.copyOf(pokemonList.toArray(), pokemonList.toArray().length, String[].class);

        Scanner iStream = new Scanner(new File("pokerecomender\\src\\main\\resources\\items.txt"));
        String itemStr = "                   ,";
        while(iStream.hasNext()){
            String item = iStream.nextLine();
            itemStr += item+",";
        } iStream.close();
        try{
            itemStr = itemStr.substring(0,itemStr.length()-1);
        } catch(IndexOutOfBoundsException e){
            System.out.println("No Item CSV btw"); // theres a what to do on the teampanel for empty itemsets dw
        } String[] itemArr = itemStr.split(",");
        mainWindow = new GUI();
        mainWindow.updateAllItemsList(itemArr);

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
        IntConsumer recomamendtionConsumer = value -> {
            try {
                generateInfoArray(value);
            } catch (IOException e) {
                System.err.println("If this occured, something has gone horribly wrong");
                e.printStackTrace();
            }
        };
        
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
            j++;

            // Spread listeners
            for (int k = 0; k < 6; k++){
                SpreadListener sL = new SpreadListener(l,spreadConsumer);
                p.spinners[k].addChangeListener(sL);
                l++;
            } 
            mainWindow.updatePokemonList(p, pokemonArr);
        } mainWindow.fireButton.addActionListener(new CatListener(100, recomamendtionConsumer));
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

    public String LaserPointer(int listenerUID){ // test function for consumers
        System.out.println(String.format("%d fired",listenerUID));
        return "Hi";
    }
    public void nameUpdated(int listenerUID) throws MalformedURLException, IOException { // changing the selected name updates sprite, movepool, and abilities
        String item = (String) mainWindow.teamPanels[listenerUID].pokemonNames.getSelectedItem();
        if(item=="                   "){ // if the empty name is selected, reset everything
            mainWindow.setNull(mainWindow.teamPanels[listenerUID]);
        } else {
            String[] moves = pokeGenerator.getMoves(item);
            String[] abil = pokeGenerator.getAbilities(item);
            mainWindow.updateSprite(mainWindow.teamPanels[listenerUID], pokeGenerator.getSprite(item));
            mainWindow.updateMovesList(mainWindow.teamPanels[listenerUID], moves);
            mainWindow.updateAbilitiesList(mainWindow.teamPanels[listenerUID], abil);
        }
    }
    public void moveUpdated(int listenerUID){
        int panel = (int) Math.floor(listenerUID/4.);
        int slot = (int) Math.floor(listenerUID%4.);
        String item = (String) mainWindow.teamPanels[panel].moveBoxes[slot].getSelectedItem();
        //System.out.println(item);
    }
    public void itemUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].itemNames.getSelectedItem();
        //System.out.println(item);
    }
    public void abilityUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].abilityNames.getSelectedItem();
        //System.out.println(item);
    }
    public void natureUpdated(int listenerUID){
        String item = (String) mainWindow.teamPanels[listenerUID].natureNames.getSelectedItem();
        //System.out.println(item);
    }
    public void spreadUpdated(int listenerUID) {
        System.out.println(listenerUID);
        int panel = (int) Math.floor(listenerUID/6.);
        int box = (int) Math.floor(listenerUID%6.);
        int item = (int) mainWindow.teamPanels[panel].spinners[box].getValue();
        //System.out.println(item);
    }
    public void generateInfoArray(int unused) throws IOException{
        List<String[]> teamInfo = new ArrayList<String[]>();
        for (int i=0; i<5; i++){
            if (!mainWindow.teamPanels[i].pokemonNames.getSelectedItem().equals("                   ")){
                System.out.println(i);
                String[] monInfo = new String[7];
                monInfo[0] = (String) mainWindow.teamPanels[i].pokemonNames.getSelectedItem();
                monInfo[1] = (String) mainWindow.teamPanels[i].itemNames.getSelectedItem();
                monInfo[2] = (String) mainWindow.teamPanels[i].abilityNames.getSelectedItem();
                monInfo[3] = (String) mainWindow.teamPanels[i].moveOneBox.getSelectedItem();
                monInfo[4] = (String) mainWindow.teamPanels[i].moveTwoBox.getSelectedItem();
                monInfo[5] = (String) mainWindow.teamPanels[i].moveThreeBox.getSelectedItem();
                monInfo[6] = (String) mainWindow.teamPanels[i].moveFourBox.getSelectedItem();
                teamInfo.add(monInfo);
            }
        }
        List<String> names = new ArrayList<String>();
        for(String[] mon : teamInfo){
            names.add(mon[0]);
        }
        double[] teamVector = compressor.CompressTeam(teamInfo);
        Map<String, Double> recommendations = teamComparator.compareToData(teamVector, names);
        mainWindow.createRecomendationPane(recommendations, pokeGenerator);
    }

    public static void main(String[] args) throws MalformedURLException, IOException{
        new Main();
    }
}