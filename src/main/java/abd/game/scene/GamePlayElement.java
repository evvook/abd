package abd.game.scene;

import java.util.Map;

public interface GamePlayElement {
	public Map<String,Object> getElContext();

	public String getCode();

	public void play(Map<String, Object> commandInfo) throws Exception;
}
