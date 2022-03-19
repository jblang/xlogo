package xlogo.kernel.gui;

import xlogo.gui.GraphFrame;
import xlogo.Logo;
import xlogo.utils.LogoException;

import java.util.HashMap;

public class GuiMap extends HashMap<String, GuiComponent> {
    private static final long serialVersionUID = 1L;
    private final GraphFrame graphFrame;

    public GuiMap(GraphFrame graphFrame) {
        this.graphFrame = graphFrame;
    }

    public void put(GuiComponent gc) throws LogoException {
        if (this.containsKey(gc.getId())) {
            throw new LogoException(graphFrame, Logo.messages.getString("gui_exists") + " " + gc.getId());
        } else this.put(gc.getId(), gc);
    }

    public GuiComponent get(Object key) {
        String k = key.toString().toLowerCase();
        return super.get(k);
    }
	
/*	public void remove(GuiComponent gc) throws LogoException{
		if (this.containsKey(gc.getId())){
			this.remove(gc.getId());
		}
		else{
			throw new LogoException(app,Logo.messages.getString("no_gui")+" "+gc.getId());
		}
	}*/
}
