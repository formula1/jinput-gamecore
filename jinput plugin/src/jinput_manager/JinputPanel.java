package jinput_manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abstracts.Game;

import controller.Player;
import controller.PlayerListener;
import controller.Player_Panel;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;



public class JinputPanel extends Player_Panel implements JinputListener{
	
	
	/*
	 * Load Configuration
	 * Save Configuration
	 * -Save Configuration
	 * 	-Removes the Controller from being used by other players
	 * 	-
	 * 
	 * Player 	-(Combo box)choice of controllers available, (button)-add new player
	 * 			-(Combo box)choice of current players, (button)-edit a player, (button)-remove a player
	 * 	-load configuration, save configuration 
	 * 	-Buttons and their current settings
	 * 
	 * 
	 * 
	 * 
	 * Player is Saved to a Port
	 * -That way I can have keyboard and mouse as something more standardized
	 * -Also is binded to Player one
	 * 	-Multiple Ports being binded to a single player
	 * 
	 * 
	 * 
	 * Ports......
	 * Ports is what players are actually about
	 * -Since controller configuration isn't nealry as big a deal. In fact this needs to be modular...
	 * 
	 * 
	 * 	<doc>
	 * 		<Configuration name="" controller-type="">
	 * 			<button name="" component="" />
	 * 			<button name="" component="" />
	 * 			<button name="" component="" />
	 * 			<button name="" component="" />
	 * 		</configuration>
	 * 		<port>
	 * 			<prefered-config></prefered config>
	 * 		</port>
	 * 	</doc>
	 * 
	 * 
	 */
	
	
	JinputPanel that = this;
	ArrayList<Controller> usedControllers;
	JinputPlayer currentPlayer;

	
	
	JComboBox<JinputPlayer> currentPlayers;
	JButton deletePlayer;
	JButton addPlayer;
	int numplayers=0;
	ArrayList<Thread> players;
	
	JComboBox<Controller> availableControllers;
	JButton openButton;
	JButton saveButton;
	HashMap <String, ButContain> sets;
	
	
	
	
	public JinputPanel(){
		super();
		players = new ArrayList<Thread>();
		usedControllers = new ArrayList<Controller>();
		this.setLayout(new GridBagLayout());
		GridBagConstraints pis = new GridBagConstraints();
		
		pis.fill = GridBagConstraints.HORIZONTAL;
		pis.ipadx = 2;
		pis.ipady = 2;
		pis.weightx = 0;
		pis.weighty = 0;
		pis.anchor = GridBagConstraints.NORTH;
		pis.insets = new Insets(2,0,0,0);  //top padding
		pis.gridx = 0;
		pis.gridy = 0;
		pis.gridwidth = GridBagConstraints.REMAINDER;
		pis.gridheight = 1;

		
		JPanel playerEdit = new JPanel();
		currentPlayers = new JComboBox<JinputPlayer>();
		currentPlayers.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if(e.getStateChange() == ItemEvent.SELECTED)
					switchPlayer((JinputPlayer)e.getItem());
			}
		});
		addPlayer = new JButton("Add Player");
		addPlayer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){addPlayer();}
		});
		deletePlayer = new JButton("Delete Player");
		deletePlayer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){removePlayer();}
		});

		playerEdit.add(currentPlayers);
		playerEdit.add(deletePlayer);
		playerEdit.add(addPlayer);
		deletePlayer.setEnabled(false);

		this.add(playerEdit, pis);

		JPanel controllerEdit = new JPanel();
		controllerEdit.setLayout(new GridBagLayout());

		pis.fill = GridBagConstraints.HORIZONTAL;
		pis.anchor = GridBagConstraints.NORTH;
		pis.gridx = 0;
		pis.gridy = 1;
		pis.gridwidth = GridBagConstraints.REMAINDER;
		pis.gridheight = GridBagConstraints.REMAINDER;

		this.add(controllerEdit, pis);
		
		availableControllers = new JComboBox<Controller>(ControllerEnvironment.getDefaultEnvironment().getControllers());
		availableControllers.addItemListener(new ItemListener(){
			
			public void itemStateChanged(ItemEvent e){
				if(e.getStateChange() == ItemEvent.SELECTED) 
					changeController();
				}
		});

		pis.fill = GridBagConstraints.NONE;
		pis.anchor = GridBagConstraints.PAGE_START;
		pis.gridx = 0;
		pis.gridy = 0;
		pis.gridwidth = 1;
		pis.gridheight = 1;

		controllerEdit.add(availableControllers, pis);


		openButton = new JButton("Open a Configuration...");
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				load();
			}
		});
		
		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		saveButton = new JButton("Save a Configuration...");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				save();
			}
		});

		pis.gridx = 1;
		controllerEdit.add(openButton, pis);
		pis.gridx = 2;
		controllerEdit.add(saveButton, pis);

		
		pis.fill = GridBagConstraints.NONE;
		pis.anchor = GridBagConstraints.PAGE_START;
		pis.gridx = 0;
		pis.gridy = 1;
		pis.gridwidth = 2;
		pis.gridheight = 1;

		
		sets = new HashMap<String, ButContain>();
		for(int i=0;i<Player.butnames.length;i++){
			sets.put(Player.butnames[i], new ButContain(Player.butnames[i]));
			pis.gridy = i%(Player.butnames.length/2)+1;
			pis.gridx = (int)Math.floor(i*2/Player.butnames.length);
			controllerEdit.add(sets.get(Player.butnames[i]), pis);
		}

		
	    addPlayer();	    
	}
		
	public void switchPlayer(JinputPlayer player){
		HashMap<String,String> bgs = player.bindings;
		if(currentPlayer != null){
			System.out.println("FOUND A PLAYER");
			usedControllers.add(currentPlayer.controller);
		}
		currentPlayer = player;
		if(player.controller != null) usedControllers.remove(player.controller);
		availableControllers.removeAllItems();

		Boolean boo;
		Controller[] cn = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int i = 0;i<cn.length;i++){
			boo = true;
			System.out.println(usedControllers.size());
			for(int j=0;j<usedControllers.size();j++)
				if(usedControllers.get(j).equals(cn[i])) boo = false;
			if(boo) availableControllers.addItem((Controller)cn[i]);
		}
		if(player.controller != null){
			availableControllers.setSelectedItem(player.controller);
			Iterator i = bgs.entrySet().iterator();
			while(i.hasNext()){
				Map.Entry<String, String> pairs = (Map.Entry)i.next();
				sets.get(pairs.getValue()).setComponent(pairs.getKey());
			}
		}else if(availableControllers.getItemCount() > 0){
			availableControllers.setSelectedIndex(0);

		}
	}
	
	public void refreshControllers(){
		
	}
	
	public void addPlayer(){
		JinputPlayer jp = new JinputPlayer(true,this, numplayers++);
		players.add(new Thread(jp));
		players.get(players.size()-1).start();
		currentPlayers.addItem(jp);
		deletePlayer.setEnabled(true);
		currentPlayers.setSelectedIndex(currentPlayers.getItemCount()-1);
	}
	
	public void end() throws InterruptedException{
		
		for(int i=0;i<currentPlayers.getItemCount();i++){
			currentPlayers.getItemAt(i).end();
			currentPlayers.getItemAt(i).setDebug(false);
			currentPlayers.getItemAt(i).playernumber = i;
		}

		
		while(players.size() > 0){
			players.get(0).join();
			players.remove(0);
		}
		
	}

	public void removePlayer(){
				JinputPlayer jp = (JinputPlayer) currentPlayers.getSelectedItem();
				jp.end();
				jp.removeListener(this);
				if(currentPlayers.getItemCount() == 2) 		deletePlayer.setEnabled(false);				
				currentPlayers.setSelectedIndex(currentPlayers.getSelectedIndex() - 1);
				currentPlayers.removeItem(jp);
	}
	
	public void changeController(){
		currentPlayer.setController((Controller) availableControllers.getSelectedItem());
		for(int i=0;i<Player.butnames.length;i++){
			sets.get(Player.butnames[i]).unsetComponent();
		}
	}
	
	private Long waiting = 0L;
	private String butear= null;
	public void prepareBinding(JButton o){
		waiting = System.currentTimeMillis();
		String set = o.getText().substring(4);
		butear = set;
		unbind(set);

	}
	
	public void push(int playernumber, Object o) {
		if(playernumber == currentPlayer.playernumber){
			Component temp = (Component)o;
			if(waiting != 0L && System.currentTimeMillis() - waiting < 5000 && currentPlayer.bindings.get(temp) == null){
				System.out.println("Comp Ident="+temp.getIdentifier().getClass().toString());
				if(temp.getIdentifier() != Component.Identifier.Axis.POV && temp.getPollData() > 0.5f){
					bind(temp, true, butear);
					butear = null;
					waiting = 0L;
				}else if(temp.getIdentifier() != Component.Identifier.Axis.POV && temp.getPollData() < -0.5f){
					bind(temp, false, butear);
					butear = null;
					waiting = 0L;
				}else if(temp.getIdentifier() == Component.Identifier.Axis.POV){
					bind(temp, false, butear);
					butear = null;
					waiting = 0L;
				}

				if(temp.getIdentifier().getClass() == Component.Identifier.Axis.class){
					System.out.println(temp.getName()+"=>"+temp.getPollData());
					System.out.println(temp.getIdentifier().getName()+"=>"+temp.getIdentifier().toString());
				}
			}else waiting = 0L;
		}
	}

	
	
	public Player[] getPlayers(){
		Player[] cp = new Player[currentPlayers.getItemCount()];
		for(int i=0;i<currentPlayers.getItemCount();i++)
			cp[i] = currentPlayers.getItemAt(i);
		return cp;
	}

	public void bind(Component com,boolean posneg, String setting){
		currentPlayer.bind(com,posneg, setting);
		String butconname = com.getName();
		if(com.getIdentifier() == Component.Identifier.Axis.POV){
			butconname += ":"+com.getPollData();
		}
		if(posneg){
			butconname += "-";
		}
		
		sets.get(setting)
		.setComponent(butconname);
	}
	public void unbind(String setting){
		if(sets.get(setting).isSet()){
			String un = sets.get(setting).unsetComponent();
			currentPlayer.unbind(setting);
		}
	}
	

	
	public void save(){
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would save the file.
            String loc = file.getAbsolutePath();
    		String f;
    		Iterator i = currentPlayer.bindings.entrySet().iterator();
    		f = "!"+currentPlayer.controller.getName()+",";
    		while (i.hasNext()) {
    			Map.Entry<Component, String> pairs = (Map.Entry)i.next();
    			f += pairs.getKey().getName()+":"+pairs.getValue()+",";
    		}
    		try{
    			BufferedWriter outputStream = new BufferedWriter(new FileWriter(loc));
    			outputStream.write(f);
    			outputStream.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}

        } else {
        	throw new Error("cannot save");
        }

	}

	
	public void load(){
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File loc = fc.getSelectedFile();
            //This is where a real application would open the file.
    		String c="";
    		String m;
    		try{
    			BufferedReader inputStream = new BufferedReader(new FileReader(loc));
    			while ((m = inputStream.readLine()) != null){
    				c += m;
    			}
    			inputStream.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		String[] buts = c.split(",");
    		Controller cur = null;
    		Component[] cos = new Component[0];
    		for(int i=0;i<buts.length;i++){
    			if(buts[i].startsWith("!")){
    				for(int x=0;x<availableControllers.getItemCount();x++)
    					if(availableControllers.getItemAt(x).getName().equals(buts[i].substring(1))){
    						cur = availableControllers.getItemAt(x);
    						cos = cur.getComponents();
    						break;
    					}
    				if(cur==null)throw new Error("controller doesn't exsist");
    			}else if(cur != null){
    				String[] comp_butt = buts[i].split(":");
    				for(int j=0;j<cos.length;j++){
    					if(comp_butt[0].equals(cos[j].getName())){
    						bind(cos[j], true, comp_butt[1]);
    						comp_butt[0] = "GGGG";
    						break;
    					}else if(comp_butt[0].equals(cos[j].getName()+"-")){
    						bind(cos[j], false, comp_butt[1]);
    						comp_butt[0] = "GGGG";
    						break;
    					}
    				}
    				if(comp_butt[0] != "GGGG") throw new Error("nonexsistant button");
    			}
    		}
    		if(cur != null) availableControllers.setSelectedItem(cur);;

        } else {
        	throw new Error("cannot open");
        }

	}
		
	

	
	
	//-----------------------------------------------
	
	private class ButContain extends JPanel{
		String name;
		JButton but;//Turn on the listener
		JLabel compo;//The ocmponent that is either on or off
		boolean isSet;
		
		
		public boolean isSet(){
			return isSet;
		}
		public String getName(){
			return name;
		}
		
		public ButContain(String name){
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.name = name;
			compo = new JLabel("none set");
			compo.setForeground(Color.white);
			compo.setBackground(Color.black);
			but = new JButton("Set "+name);
			but.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					prepareBinding((JButton)e.getSource());
				}
			});
			this.add(but);
			this.add(compo);
		}
		
		public void setComponent(String str){
			toggle(true);
			compo.setText(str);
			isSet = true;
		}
		
		public String unsetComponent(){
			String ret = compo.getText();
			compo.setText("none set");
			compo.setForeground(Color.white);
			compo.setBackground(Color.black);
			isSet = false;
			return ret;
			
		}
		
		public void toggle(boolean boo){
			if(boo){
				compo.setForeground(Color.blue);
				compo.setBackground(Color.orange);
			}else{
				compo.setForeground(Color.orange);
				compo.setBackground(Color.blue);
			}
		}
		
	}//end ButContain





	@Override
	public void playerEvent(int player_number, String input, Float value) {
		if(player_number == currentPlayer.playernumber)
			if(sets.get(input).isSet()){
				if(value > 0.5f) sets.get(input).toggle(true);
				else  sets.get(input).toggle(false);
			}
	}

	@Override
	public void applyPlayers(Player[] players) {
		// TODO Auto-generated method stub
	}

	@Override
	public void playerEnter(int playernumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerLeave(int playernumber) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}//end class
