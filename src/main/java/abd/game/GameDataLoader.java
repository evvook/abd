package abd.game;

import java.util.List;
import java.util.Map;

public interface GameDataLoader {
	public List<Map<String,String>> getPChacterInfo(Map<String,String> paramMap) throws Exception;

	public List<Map<String, String>> getCharactersInMap(Map<String, String> paramMap) throws Exception;

	public List<Map<String, String>> getNPCharacterInfo(Map<String, String> paramMap) throws Exception;

	public List<Map<String, String>> getCharacterLine(Map<String, String> paramMap) throws Exception;

//	public List<Map<String, String>> getStartSceneInfo(Map<String, String> paramMap) throws Exception;

	public List<Map<String, String>> getEventsOfScene(Map<String, String> map) throws Exception;

	public List<Map<String, String>> getScriptsOfEvent(Map<String, String> eventInfo) throws Exception;

	public List<Map<String, String>> getPlayOfEvent(Map<String, String> eventInfo) throws Exception;

	public List<Map<String, String>> getSelectOfPlay(Map<String, String> playInfo) throws Exception;

	public List<Map<String, String>> getOptionsOfSelect(Map<String, String> selectInfo) throws Exception;

	public List<Map<String, String>> getSeletionResult(Map<String, String> selectedInfo) throws Exception;

	public List<Map<String, String>> getBattleOfPlay(Map<String, String> playInfo) throws Exception;

//	public List<Map<String, String>> getCompCharacter() throws Exception;

	public List<Map<String, String>> getJustHpnOfEvent(Map<String, String> eventInfo) throws Exception;

	public List<Map<String, String>> getInputOfPlay(Map<String, String> playInfo) throws Exception;
}
