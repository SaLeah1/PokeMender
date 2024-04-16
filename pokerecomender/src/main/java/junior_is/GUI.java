package junior_is;

// exceptions
import java.io.IOException;
import java.net.MalformedURLException;

// core
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;

// 3rd party
import net.miginfocom.swing.*;

public class GUI extends JFrame{

    public JPanel contentPane;
    public JPanel sendPane;
    public TeamPanel one;
    public TeamPanel two;
    public TeamPanel three;
    public TeamPanel four;
    public TeamPanel five;
    public TeamPanel six;
    public TeamPanel[] teamPanels;

    public JFrame fire;
    public JButton fireButton;

    public JFrame recommendationPane;
    public JPanel recPanel;
    public TeamPanel[] recTeamPanels;

    public GUI() throws MalformedURLException, IOException{
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1340,665); 
        this.setResizable(false);
        contentPane = new JPanel();
        contentPane.setLayout(new MigLayout(
            "wrap 3",
            "10[]10[]10[]10",
            "10[]10[]10"
            ));
        this.add(contentPane);
        one = new TeamPanel();
        contentPane.add(one);
        two = new TeamPanel();
        contentPane.add(two);
        three = new TeamPanel();
        contentPane.add(three);
        four = new TeamPanel();
        contentPane.add(four);
        five = new TeamPanel();
        contentPane.add(five);
        six = new TeamPanel();
        contentPane.add(six);
        teamPanels = new TeamPanel[]{one,two,three,four,five,six};
        this.setVisible(true);
        setupFire();
    }

    public void updateAllItemsList(String[] itemList){
        for (TeamPanel panel : teamPanels) {
            panel.itemNames.setModel(new DefaultComboBoxModel<String>(itemList));
        }
    }
    public void updatePokemonList(TeamPanel p, String[] pokemonList){
        p.pokemonNames.setModel(new DefaultComboBoxModel<String>(pokemonList));
    }
    public void updateMovesList(TeamPanel p, String[] movesList){
        p.moveOneBox.setModel(new DefaultComboBoxModel<String>(movesList));
        p.moveTwoBox.setModel(new DefaultComboBoxModel<String>(movesList));
        p.moveThreeBox.setModel(new DefaultComboBoxModel<String>(movesList));
        p.moveFourBox.setModel(new DefaultComboBoxModel<String>(movesList));
    }
    public void updateAbilitiesList(TeamPanel p, String[] abilityList){
        p.abilityNames.setModel(new DefaultComboBoxModel<String>(abilityList));
    }
    public void updateSprite(TeamPanel p, String spriteLink) throws MalformedURLException, IOException{
        p.spritePanel.remove(p.spriteLabel);
        BufferedImage bImg = ImageIO.read(new URL(spriteLink));
        Image img = bImg.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon ico = new ImageIcon(img);
        p.spriteLabel = new JLabel(ico);
        p.spritePanel.add(p.spriteLabel);
        p.revalidate();
        p.repaint();
    }
    public void setNull(TeamPanel p) throws MalformedURLException, IOException{
        String[] tempAbilities = new String[]{"                   ","Select a Pokemon"};
        String[] tempMoves = new String[]{"                   ","Select a Pokemon"};
        updateMovesList(p, tempMoves);
        updateAbilitiesList(p, tempAbilities);
        p.spritePanel.remove(p.spriteLabel);
        ImageIcon ico = new ImageIcon(ImageIO.read(new File("pokerecomender\\src\\main\\resources\\EMPTY.jpg")).getScaledInstance(200,200,Image.SCALE_SMOOTH));
        p.spriteLabel = new JLabel(ico);
        p.spritePanel.add(p.spriteLabel);
        p.revalidate();
        p.repaint();
    }
    public void setupFire(){
        this.fire = new JFrame();
        this.fire.setSize(200, 100);
        this.fireButton = new JButton("Generate Recomendations");
        this.fire.add(this.fireButton);
        this.fire.setVisible(true);
        this.fire.setAlwaysOnTop(true);
    }
    @SuppressWarnings("unchecked")
    public void createRecomendationPane(Map<String, Double> recommendations, PokeInfoGen pokeGen) throws MalformedURLException, IOException{
        this.recommendationPane = new JFrame();
        this.recommendationPane.setSize(1340,355);
        this.recPanel = new JPanel();
        this.recPanel.setLayout(new MigLayout("",
            "10[433!]10[433!]10[433!]10"
            ));
        recommendationPane.add(recPanel);
        int count = 0;
        List<String> recommendedMons = new ArrayList<String>();
        this.recTeamPanels = new TeamPanel[3];
        for (Entry<String,Double> entrySet : recommendations.entrySet()) {
            String name = entrySet.getKey();
            Matcher matcher = Pattern.compile("\\d+").matcher(name);
            matcher.find();
            String nums = matcher.group();
            name = name.substring(0,name.indexOf(nums));
            if (!recommendedMons.contains(name)){
                Scanner s = new Scanner(new File(
                    String.format("pokerecomender\\src\\main\\resources\\pokeSheetCache\\%s.txt",entrySet.getKey())));
                String[] infoArray = s.nextLine().split(",");
                String spriteLink = pokeGen.getSprite(infoArray[0]);
                System.out.println(infoArray[0]);
                recommendedMons.add(infoArray[0]);
                TeamPanel newMon = new TeamPanel();
                this.recPanel.add(newMon);
                this.recTeamPanels[count] = newMon;
                newMon.pokemonNames.setModel(new DefaultComboBoxModel<String>(new String[]{infoArray[0],"                   "}));
                newMon.pokemonNames.setEditable(false);
                updateSprite(newMon, spriteLink);
                newMon.abilityNames.setModel(new DefaultComboBoxModel<String>(new String[]{infoArray[1],"                   "}));
                newMon.abilityNames.setEditable(false);
                newMon.itemNames.setModel(new DefaultComboBoxModel<String>(new String[]{infoArray[2],"                   "}));
                newMon.itemNames.setEditable(false);
                int i = 3;
                for (JComboBox<String> move : newMon.moveBoxes) {
                    move.setModel(new DefaultComboBoxModel<String>(new String[]{infoArray[i++],"                   "}));
                    move.setEditable(false);
                }
                newMon.natureNames.setEditable(false);
                for (JSpinner ev : newMon.spinners) {
                    ev.setEnabled(false);
                }
                s.close();
                count++;
            }
            if(count>=3){System.out.println("finished adding");break;}
        }
        recommendationPane.setVisible(true);
    }
}
