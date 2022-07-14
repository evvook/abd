package abd.game.scene;

import java.util.Map;

import abd.game.GameManager;

public interface GamePlayElement {
	public Map<String,Object> getElContext() throws Exception;

	public String getCode();

	Map<String, Object> playSelect(Map<String, String> selectedInfo, GameManager manager) throws Exception;

	//public void play(Map<String, Object> commandInfo) throws Exception;
}
