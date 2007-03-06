package de.fhaugsburg.rundfunker.rundspieler.view;

import de.fhaugsburg.rundfunker.rundspieler.Controller;

public interface Screen {

    public void turnDDS(int value);

    public void pressDDS();
    
    public void render();
    
    public void setController(Controller controller);
    
    public void setDisplay(Display display);

}
