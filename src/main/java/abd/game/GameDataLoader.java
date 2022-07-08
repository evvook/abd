package abd.game;

import java.util.List;
import java.util.Map;

public interface GameDataLoader {
	public List<Map<String,String>> getPChacterInfo(Map<String,String> paramMap) throws Exception;

	public List<Map<String, String>> getCharactersInMap(Map<String, String> paramMap) throws Exception;

	public List<Map<String, String>> getNPCharacterInfo(Map<String, String> paramMap) throws Exception;

	public List<Map<String, String>> getCharacterLine(Map<String, String> paramMap) throws Exception;
}
