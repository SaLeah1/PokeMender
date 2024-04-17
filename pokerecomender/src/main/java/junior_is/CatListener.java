package junior_is;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.IntConsumer;

/*
 * Generic listener class for comboBoxes within the GUI
 * Uses an integer consumer to pass in a function from the Main class
 * When actionPerformed is triggered, the IntegerConsumer will call the accept method, 
 *   causing its corresponding method in Main to be called using uID as its input
 * 
 * Shout out to my brother's cat Ghoul.
 */

public class CatListener implements ActionListener{

    IntConsumer actionMethod;
    int uID;

    public CatListener (int uID, IntConsumer actionMethod){
        super();
        this.actionMethod = actionMethod;
        this.uID = uID;
    }
    public void actionPerformed(ActionEvent e) {
        actionMethod.accept(uID);
    }
}
