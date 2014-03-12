package jinput_manager;

import net.java.games.input.Component;
import controller.PlayerListener;

public interface JinputListener extends PlayerListener {

	public void push(int playernumber, Component o);

}
