package abd.service;

import java.util.List;
import java.util.Map;

public interface CharacterService {
	public List<Map<String,String>> selectCharacter(Map<String,String> paramMap) throws Exception;
}
