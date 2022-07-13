package abd.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import abd.dao.character.CharacterDAO;
import abd.dao.scenario.ScenarioDAO;
import abd.game.Game;
import abd.game.GameDataLoader;

@Service("playService")
public class PlayServiceImpl implements PlayService, GameDataLoader{

	@Resource(name="characterDAO")
	private CharacterDAO characterDAO;
	
	@Resource(name="scenarioDAO")
	private ScenarioDAO scenarioDAO;
	
	private Game game;
	
	//Service 구현 메서드
	@Override
	public void setGame(Game game) {
		// TODO Auto-generated method stub
		this.game = game;
		this.game.setGameDataLoader(this);
	}
	
	@Override
	public Map<String, Object> play(Map<String, Object> paramMap) throws Exception {
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

	@Override
	public List<Map<String, String>> getStartSceneInfo(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectStartScene(paramMap);
	}

	@Override
	public List<Map<String, String>> getEventsOfScene(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectEvents(paramMap);
	}

	@Override
	public List<Map<String, String>> getScriptsOfEvent(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectScripts(paramMap);
	}

	@Override
	public List<Map<String, String>> getPlayOfEvent(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectPlay(paramMap);
	}

	@Override
	public List<Map<String, String>> getSelectOfPlay(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectSelection(paramMap);
	}

	@Override
	public List<Map<String, String>> getOptionsOfSelect(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectOptions(paramMap);
	}

	@Override
	public List<Map<String, String>> getSeletionResult(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectSelectionResult(paramMap);
	}

	@Override
	public List<Map<String, String>> getBattleOfPlay(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectBattle(paramMap);
	}

}
