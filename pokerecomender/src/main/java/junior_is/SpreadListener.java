package junior_is;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.function.IntConsumer;

public class SpreadListener implements ChangeListener{

    int uID;
    IntConsumer actionMethod;

    public SpreadListener(int uID, IntConsumer actionMethod){
        super();
        this.uID = uID;
        this.actionMethod = actionMethod;
    }
    public void stateChanged(ChangeEvent e) {
        actionMethod.accept(uID);
    }
}