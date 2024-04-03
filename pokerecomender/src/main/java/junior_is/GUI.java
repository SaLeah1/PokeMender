package junior_is;

// exceptions
import java.io.IOException;
import java.net.MalformedURLException;

// core
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
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
    public JButton fire;

    public GUI(String[] itemArr) throws MalformedURLException, IOException{
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
        one = new TeamPanel(itemArr);
        contentPane.add(one);
        two = new TeamPanel(itemArr);
        contentPane.add(two);
        three = new TeamPanel(itemArr);
        contentPane.add(three);
        four = new TeamPanel(itemArr);
        contentPane.add(four);
        five = new TeamPanel(itemArr);
        contentPane.add(five);
        six = new TeamPanel(itemArr);
        contentPane.add(six);
        teamPanels = new TeamPanel[]{one,two,three,four,five,six};
        this.setVisible(true);
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
}
