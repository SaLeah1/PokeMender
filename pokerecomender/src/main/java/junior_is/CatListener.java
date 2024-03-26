package junior_is;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.IntConsumer;

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
