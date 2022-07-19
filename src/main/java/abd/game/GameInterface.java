package abd.game;

import java.util.Map;

public interface GameInterface {
	public void createPlayerCharacter(GameSetupLoader loader, String name) throws Exception;
	
	public Map<String, Object> getGameContext();
	
	public void setupScenesInfo(GameSetupLoader loader) throws Exception;
	
	public void startSceneLoad(GameDataLoader loader) throws Exception;

	void goEvent() throws Exception;

	void goEvent(Map<String, Object> input) throws Exception;

	public Map<String, Object> getCharStat();
}
