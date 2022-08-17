package abd.game;

import java.util.Map;

public interface GameInterface {
	public void createPlayerCharacter(GameSetupLoader loader, String name) throws Exception;
	
	public Map<String, Object> getGameContext();
	
	public void setupScenesInfo(GameSetupLoader loader) throws Exception;
	
	public void startSceneLoad(GameDataLoader loader) throws Exception;

	public void goEvent() throws Exception;

	public void goEvent(Map<String, Object> input) throws Exception;

	public Map<String, Object> getCharStat();
	
	public void goToIntro() throws Exception;
}
