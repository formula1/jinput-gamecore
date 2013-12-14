package jinput_manager;

import controller.PlayerListener;

public interface JinputListener extends PlayerListener {

	public void push(int playernumber, Object o);

}
