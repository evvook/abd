package abd.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import abd.character.dao.CharacterDAO;

@Service("characterService")
public class CharacterServiceImpl implements CharacterService {

	@Resource(name="characterDAO")
	private CharacterDAO characterDAO;

	@Override
	public List<Map<String, String>> selectCharacter(Map<String,String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selecCharacter(paramMap);
	}
}
