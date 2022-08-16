package abd.game.scene;

import java.util.Map;

public interface GamePlayElement {
	public Map<String,Object> getElContext() throws Exception;

	public String getCode();

	Map<String, Object> play(Map<String, Object> input, GameEventCallback callback) throws Exception;

	//public void play(Map<String, Object> commandInfo) throws Exception;
}
