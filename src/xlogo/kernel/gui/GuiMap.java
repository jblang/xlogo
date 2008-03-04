package xlogo.kernel.gui;
import xlogo.utils.myException;
import java.util.HashMap;
import xlogo.Application;
import xlogo.Logo;
public class GuiMap extends HashMap<String,GuiComponent> {
	private static final long serialVersionUID = 1L;
	private Application app;
	public GuiMap(Application app){
		this.app=app;
	}
	public void put(GuiComponent gc) throws myException{
		if (this.containsKey(gc.getId())){
			throw new myException(app,Logo.messages.getString("gui_exists")+" "+gc.getId());
		}
		else this.put(gc.getId(), gc);
	}
	public GuiComponent get(Object key){
		String k=key.toString().toLowerCase();
		return super.get(k);
	}
	
/*	public void remove(GuiComponent gc) throws myException{
		if (this.containsKey(gc.getId())){
			this.remove(gc.getId());
		}
		else{
			throw new myException(app,Logo.messages.getString("no_gui")+" "+gc.getId());
		}
	}*/
}
