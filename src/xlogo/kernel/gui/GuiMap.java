package xlogo.kernel.gui;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.kernel.LogoException;

import java.util.HashMap;

public class GuiMap extends HashMap<String, GuiComponent> {
    private static final long serialVersionUID = 1L;
    private final Application app;

    public GuiMap(Application app) {
        this.app = app;
    }

    public void put(GuiComponent gc) throws LogoException {
        if (this.containsKey(gc.getId())) {
            throw new LogoException(app, Logo.messages.getString("gui_exists") + " " + gc.getId());
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
