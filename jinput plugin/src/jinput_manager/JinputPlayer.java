package jinput_manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import controller.Player;
import controller.PlayerListener;


import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class JinputPlayer extends Player{
	HashMap<String,String> bindings; //<Component Name, Action name>
	HashMap<Float,String> POV;
	
	Controller controller = null;
	EventQueue eq;

	public JinputPlayer(Controller controller, int pn){
		super(pn);
		prep();
		this.controller = controller;
		this.debug = false;
	}
	
	public JinputPlayer(boolean debug, PlayerListener pl, int pn){
		super(pl, pn);
		prep();
		this.debug = debug;
	}
	
	public JinputPlayer(boolean debug, PlayerListener pl, Controller controller, int pn){
		super(pl, pn);
		prep();
		this.debug = debug;
		this.controller = controller;
	}
	
	private void prep(){
		bindings = new HashMap<String,String>();
		POV = new HashMap<Float,String>();
		POV.put(Component.POV.OFF, "o");
		POV.put(Component.POV.LEFT, "l");
		POV.put(Component.POV.DOWN_LEFT, "dl");
		POV.put(Component.POV.DOWN, "d");
		POV.put(Component.POV.DOWN_RIGHT, "dr");
		POV.put(Component.POV.RIGHT, "r");
		POV.put(Component.POV.UP_RIGHT, "ur");
		POV.put(Component.POV.UP, "u");
		POV.put(Component.POV.UP_LEFT, "ul");
	}
	
	
	
	
	
	public void setController(Controller newcontrol){
		bindings = new HashMap<String, String>();
		controller = newcontrol;
		System.out.println(controller.getType());

	}
	
	public void bind(Component c,boolean posneg,String s){
		unbind(s);
		if(c.getIdentifier() == Component.Identifier.Axis.POV){
			String pov = POV.get(c.getPollData());
			if(pov.length()>1) return;
			bindings.put(c.getName()+"|"+pov, s);
			
		}else{
			if(posneg)
				bindings.put(c.getName(),s);
			else
				bindings.put(c.getName()+"-",s);
		}
	}
	public void unbind(String s){
	    Iterator it = bindings.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Component, String> pairs = (Map.Entry)it.next();
	    	if(s.equals(pairs.getValue())){
	    		bindings.remove(pairs.getKey());
	    		break;
	    	}
	    }
	}
	
	
	public HashMap<String, Float> getButtonEvents() {
		// TODO Auto-generated method stub
		Event ed = new Event();
		String temp2;
		boolean hasevent = false;
		
		HashMap<String,Float> ret = new HashMap<String,Float>();
		
		if(controller != null){
			controller.poll();
			eq = controller.getEventQueue();
			while(eq.getNextEvent(ed)){
				Component temp = ed.getComponent();
				
				if(temp.getIdentifier() == Component.Identifier.Axis.POV){
					String pov = POV.get(temp.getPollData());
					ArrayList<Float> lastvalue = new ArrayList<Float>();
					byte[] dirs = {'u', 'l', 'd', 'r'};
					if(pov == "o"){
						for(byte i =0; i<dirs.length;i++)
							if(	(temp2 = bindings.get(temp.getName()+"|"+dirs[i])) != null
							&&	buttons.get(temp2) == 1f
							)	ret.put(temp2, 0f);
					}else{
						for(byte i =0; i<pov.length();i++)
							if(	(temp2 = bindings.get(temp.getName()+"|"+pov.charAt(i))) != null
							&&	buttons.get(temp2) != 1f
							){
								ret.put(temp2, 1f);
								for(byte j =0; j<dirs.length;j++)
									if(dirs[j] == pov.charAt(i)){
										if((temp2 = bindings.get(temp.getName()+"|"+dirs[j])) != null
										&&	buttons.get(temp2) == 1f
										)	ret.put(temp2, 0f);
									}
							}
						
					}
				}else if(temp.getPollData() < 0){
					if((temp2 = bindings.get(temp.getName()+"-")) != null)
						ret.put(temp2, -1*temp.getPollData());
				}else{
					if((temp2 = bindings.get(temp.getName())) != null){
						ret.put(temp2, temp.getPollData());
					}
				}
				if(debug){
					for(int i=0;i<listeners.size();i++){
						
						((JinputListener)listeners.get(i)).push(playernumber, temp);
					}
					System.out.println("debug");
				}
			}
		} else
			try {
				throw new Exception("controller doens't exsist");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return ret;
	}
	

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}//End Class
