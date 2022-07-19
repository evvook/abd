package abd.game;

import java.util.List;
import java.util.Map;

public interface GameSetupLoader {

	List<Map<String, String>> getPCharacterLevelInfo(Map<String, String> paramMap) throws Exception;

	List<Map<String, String>> getCompCharacter() throws Exception;

	List<Map<String, String>> getGameSceneInfo() throws Exception;

}
