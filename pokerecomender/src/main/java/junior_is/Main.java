package junior_is;

// Exceptions
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

// Core
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.util.function.IntConsumer;

/*
 *
 *
 */

public class Main {

    public SmogonParser parser; // reads in smogon json file || can return Pokemon Names
    public InfoGen pokeGenerator; // PokeAPI access
    public TypeBot typeChecker;

    public GUI mainWindow; // 我恨恨恨这个东西

    public CatListener[] moveListeners;

    public Main() throws MalformedURLException, IOException{
        pokeGenerator = new InfoGen();
        typeChecker = new TypeBot();
        parser = new SmogonParser("https://www.smogon.com/stats/2024-01/chaos/gen3ou-1760.json");
        Iterator<String> pokemonItt = parser.getPokemon();
        List<String> pokemonList = new ArrayList<>();
        pokemonList.add("                ");
        //pokemonList.add("Bisharp"); // for testing the type gen. This is supposed to break the gui display dw
        pokemonItt.forEachRemaining(pokemonList::add);
        String[] pokemonArr = Arrays.copyOf(pokemonList.toArray(), pokemonList.toArray().length, String[].class);

        Scanner iStream = new Scanner(new File("pokerecomender\\src\\main\\resources\\items.txt"));
        String itemStr = "                ,";
        while(iStream.hasNext()){
            String item = iStream.nextLine();
            itemStr += item+",";
        } iStream.close();
        try{
            itemStr = itemStr.substring(0,itemStr.length()-1);
        } catch(IndexOutOfBoundsException e){
            System.out.println("No Item CSV btw"); // theres a what to do on the teampanel for empty itemsets dw
        } String[] itemArr = itemStr.split(",");
        mainWindow = new GUI(itemArr);

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
            j++;

            // Spread listeners
            for (int k = 0; k < 6; k++){
                SpreadListener sL = new SpreadListener(l,spreadConsumer);
                p.spinners[k].addChangeListener(sL);
                l++;
            }
            mainWindow.updatePokemonList(p, pokemonArr);
        }
        //this.getDefTypal();
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
        System.out.println(listenerUID);
        int panel = (int) Math.floor(listenerUID/6.);
        int box = (int) Math.floor(listenerUID%6.);
        int item = (int) mainWindow.teamPanels[panel].spinners[box].getValue();
        System.out.println(item);
    }
    public void getDefTypal() throws IOException{
        String[] names = new String[]{
            "normal","fire","water","electric","grass","ice","fighting","poison","ground",
            "flying","psychic","bug","rock","ghost","dragon","dark","steel"};
        double[] typeSums = new double[18];
        Arrays.fill(typeSums, 0);
        for (int i=0;i<6;i++){
            String item = (String) mainWindow.teamPanels[i].pokemonNames.getSelectedItem();
            String[] types = pokeGenerator.getTypes(item);
            for (int j = 0; j < names.length; j++) {
                double c = typeChecker.typeMatch(names[j], types[0],types[1]);
                typeSums[j] += c;
            }
        }
        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i]+": "+typeSums[i]);
        }
    }
    public static void main(String[] args) throws MalformedURLException, IOException{
        new Main();
    }
}