package abd.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import abd.character.dao.CharacterDAO;
import abd.game.Game;
import abd.game.GameDataLoader;

@Service("playService")
public class PlayServiceImpl implements PlayService, GameDataLoader{

	@Resource(name="characterDAO")
	private CharacterDAO characterDAO;
	private Game game;
	
	//Service 구현 메서드
	@Override
	public void setGame(Game game) {
		// TODO Auto-generated method stub
		this.game = game;
		this.game.setGameDataLoader(this);
	}
	
	@Override
	public Map<String, Object> play(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return game.run(paramMap);
	}
	
	//Loader 구현 메서드들
	@Override
	public List<Map<String,String>> getPChacterInfo(Map<String,String> paramMap) throws Exception{
		return characterDAO.selecPCharacter(paramMap);
	}

	@Override
	public List<Map<String, String>> getCharactersInMap(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selectCharacterInMap(paramMap);
	}

	@Override
	public List<Map<String, String>> getNPCharacterInfo(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selecNPCharacter(paramMap);
	}

	@Override
	public List<Map<String, String>> getCharacterLine(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selecCharacterLine(paramMap);
	}

}
