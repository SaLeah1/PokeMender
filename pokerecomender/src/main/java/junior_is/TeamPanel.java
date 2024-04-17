package junior_is;

// exceptions
import java.io.FileNotFoundException;
import java.io.IOException;

// core
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;

// 3rd party
import net.miginfocom.swing.*;

/*
 * GUI ELEMENT:
 * Used by GUI.java to easily create multiple identical sections within the GUI. 
 * Uses the thirdparty layout manager "MigLayout"
 * All functions present are used for building its self and should not be called after set up
 * Modifying the components of the various elements, is done directly via interacting with public variables
 */

public class TeamPanel extends JPanel{

    public JPanel spritePanel;
    public JPanel movePanel;
    public JPanel infoPanel;
    public JPanel spreadPanel;
    public JPanel iNatPanel; // iNat = Item / Nature. This pannel contains both combo boxes

    public JLabel spriteLabel;

    public JComboBox<String> pokemonNames;
    public JComboBox<String> abilityNames;
    public JComboBox<String> itemNames;
    public JComboBox<String> natureNames;
    public JComboBox<String> moveOneBox;
    public JComboBox<String> moveTwoBox;
    public JComboBox<String> moveThreeBox;
    public JComboBox<String> moveFourBox;

    @SuppressWarnings("rawtypes")
    public JComboBox[] moveBoxes;
    public JSpinner[] spinners;

    public JSpinner hp;
    public JSpinner atk;
    public JSpinner def;
    public JSpinner spatk;
    public JSpinner spdef;
    public JSpinner spe;

    public TeamPanel() throws IOException{
        setUIFont (new javax.swing.plaf.FontUIResource("Courier",Font.BOLD,10));
        MigLayout mig = new MigLayout(
            "",
            "0[120!, center]0[120!, center]0[190!, center]0",
            "0[100!, center]0[100!, center]0[100!, center]0"
        );
        this.setLayout(mig);
        spritePanel = makeSpritePanel();
        movePanel = makeMovePanel();
        infoPanel = makeInfoPanel();
        iNatPanel = makeiNatPanel();
        moveBoxes = new JComboBox[]{moveOneBox,moveTwoBox,moveThreeBox,moveFourBox};
        spinners = new JSpinner[]{hp,atk,def,spatk,spdef,spe};
        // There is nothing in the coding world I hate more than Java GUIs
        this.add(spritePanel,"cell 0 0 2 2, grow");
        this.add(infoPanel, "cell 2 0 1 1, grow");
        this.add(iNatPanel,"cell 2 1 1 1, grow");
        this.add(movePanel, "cell 0 2 3 1, grow");
    }
    public JPanel makeSpritePanel() throws IOException{
        JPanel spritePanel = new JPanel();
        BufferedImage bImg = ImageIO.read(new File("pokerecomender\\src\\main\\resources\\EMPTY.jpg"));
        Image img = bImg.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon ico = new ImageIcon(img);
        spriteLabel = new JLabel(ico);
        spritePanel.add(spriteLabel);
        spritePanel.setBackground(new Color(255,255,255));
        return spritePanel;
    }
    public JPanel makeMovePanel(){
        JPanel movePanel = new JPanel(new MigLayout(
            "",
            "5[120!, center]25[120!, center]25[100!, center]10",
            "20[20!, center]20[20!, center]20"));
        String[] tempMoves = new String[]{"                   ","Select a Pokemon"};
        JPanel spreadPanel = new JPanel(new MigLayout(
            "wrap 4",
            "0[right]0[30!]1[right]0[30!]0",
            "0[]2[]2[]0"
        ));
        spreadPanel.setBackground(new Color(218, 228, 242));

        int base = 0;
        int min = 0;
        int max = 252;
        int step = 1;
        SpinnerNumberModel modHP = new SpinnerNumberModel(base, min, max, step);
        SpinnerNumberModel modATK = new SpinnerNumberModel(base, min, max, step);
        SpinnerNumberModel modDEF = new SpinnerNumberModel(base, min, max, step);
        SpinnerNumberModel modSPATK = new SpinnerNumberModel(base, min, max, step);
        SpinnerNumberModel modSPDEF = new SpinnerNumberModel(base, min, max, step);
        SpinnerNumberModel modSPE = new SpinnerNumberModel(base, min, max, step);

        this.hp = new JSpinner(modHP);
        hideSpinnerArrow(this.hp);
        JLabel hpLabel = new JLabel("HP:");
        this.atk = new JSpinner(modATK);
        hideSpinnerArrow(this.atk);
        JLabel atkLabel = new JLabel("ATK:");
        this.def = new JSpinner(modDEF);
        hideSpinnerArrow(this.def);
        JLabel defLabel = new JLabel("DEF:");
        this.spatk = new JSpinner(modSPATK);
        hideSpinnerArrow(this.spatk);
        JLabel spatkLabel = new JLabel("SpAtk:");
        this.spdef = new JSpinner(modSPDEF);
        hideSpinnerArrow(this.spdef);
        JLabel spdefLabel = new JLabel("SpDef:");
        this.spe = new JSpinner(modSPE);
        hideSpinnerArrow(this.spe);
        JLabel speLabel = new JLabel("SPE:");
    
        spreadPanel.add(hpLabel);
        spreadPanel.add(this.hp);
        spreadPanel.add(spatkLabel);
        spreadPanel.add(this.spatk);
        spreadPanel.add(atkLabel);
        spreadPanel.add(this.atk);
        spreadPanel.add(spdefLabel);
        spreadPanel.add(this.spdef);
        spreadPanel.add(defLabel);
        spreadPanel.add(this.def);
        spreadPanel.add(speLabel);
        spreadPanel.add(this.spe);

        this.moveOneBox = new JComboBox<String>(tempMoves);
        this.moveTwoBox = new JComboBox<String>(tempMoves);
        this.moveThreeBox = new JComboBox<String>(tempMoves);
        this.moveFourBox = new JComboBox<String>(tempMoves);
        AutoCompletion.enable(this.moveOneBox);
        AutoCompletion.enable(this.moveTwoBox);
        AutoCompletion.enable(this.moveThreeBox);
        AutoCompletion.enable(this.moveFourBox);
        movePanel.add(this.moveOneBox, "cell 0 0");
        movePanel.add(this.moveTwoBox,"cell 1 0");
        movePanel.add(this.moveThreeBox, "cell 0 1");
        movePanel.add(this.moveFourBox, "cell 1 1");
        movePanel.add(spreadPanel, "cell 2 0 1 2");
        movePanel.setBackground(new Color(218, 228, 242));
        return movePanel;
    }
    public JPanel makeInfoPanel(){
        JPanel infoPanel = new JPanel(new MigLayout(
            "",
            "10[170!, center]10",
            "30[20!, center]30"
        ));
        String[] tempNames = new String[]{"                ","No JSON LOADED"};
        this.pokemonNames = new JComboBox<String>(tempNames);
        AutoCompletion.enable(this.pokemonNames);
        infoPanel.add(this.pokemonNames,"grow");
        infoPanel.setBackground(new Color(218, 228, 242));
        return infoPanel;
    }
    public JPanel makeiNatPanel() throws FileNotFoundException{

        JPanel holdPanel = new JPanel(new MigLayout(
            "wrap 1",
            "10[170!, center]10",
            "0[20!, center]10[20!, center]10[20!, center]10"
        ));
        String[] tempAbilities = new String[]{"                   ","Select a Pokemon"};
        String[] tempItems = new String[]{"                   ","No Item CSV Loaded"};
        String[] natures = new String[]{
            "                   ",
            "Adamant/ATK/SPA",
            "Bashful/Neutral",
            "Bold/DEF/ATK",
            "Brave/ATK/SPE",
            "Calm/SPD/ATK",
            "Careful/SPD/SPA",
            "Docile/Neutral",
            "Gentle/SPD/DEF",
            "Hardy/Neutral",
            "Hasty/SPD/DEF",
            "Impish/DEF/SPA",
            "Jolly/SPE/SPA",
            "Lax/DEF/SPD",
            "Lonely/ATK/DEF",
            "Mild/SPA/DEF",
            "Modest/SPA/ATK",
            "Naive/SPE/SPD",
            "Naughty/ATK/SPD",
            "Quiet/SPA/SPD",
            "Quirky/Neutral",
            "Rash/SPA/SPD",
            "Relaxed/DEF/SPE",
            "Sassy/SPD/SPE",
            "Serious/Neutral",
            "Timid/SPE/ATK"
        };
        this.abilityNames = new JComboBox<String>(tempAbilities);
        this.itemNames = new JComboBox<String>(tempItems);
        this.natureNames = new JComboBox<String>(natures);
        AutoCompletion.enable(this.abilityNames);
        AutoCompletion.enable(this.itemNames);
        AutoCompletion.enable(this.natureNames);
        holdPanel.add(this.abilityNames);
        holdPanel.add(this.itemNames);
        holdPanel.add(this.natureNames);
        holdPanel.setBackground(new Color(218, 228, 242));
        return holdPanel;
    }

    @SuppressWarnings("rawtypes")
    public static void setUIFont (javax.swing.plaf.FontUIResource f){   // method to set the font of everything using a ui manager
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value instanceof javax.swing.plaf.FontUIResource)         // loop through all of the things in my UI, if any of them are a FontUIResource, apply the font to them
            UIManager.put (key, f);
          }
        } 
    
    public void hideSpinnerArrow(JSpinner spinner) {                   // hide the spinner arrows of a spinner and increase its space to compensate
    Dimension d = spinner.getPreferredSize();
    d.width = 30;
    spinner.setUI(new BasicSpinnerUI() {
        protected Component createNextButton() {
            return null;
        }

        protected Component createPreviousButton() {
            return null;
        }
    });
    spinner.setPreferredSize(d);
}
}