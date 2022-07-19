package abd.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import abd.dao.character.CharacterDAO;
import abd.dao.scenario.ScenarioDAO;
import abd.game.Game;
import abd.game.GameSetupLoader;

@Service("setupSerivce")
public class SetupServiceImpl implements SetupService, GameSetupLoader {

	@Resource(name="characterDAO")
	private CharacterDAO characterDAO;
	
	@Resource(name="scenarioDAO")
	private ScenarioDAO scenarioDAO;
	
	private Game game;
	
	@Override
	public void setGame(Game game) {
		// TODO Auto-generated method stub
		this.game = game;
	}

	@Override
	public Map<String, Object> setup(Map<String, Object> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return game.setup(this, paramMap);
	}

	@Override
	public List<Map<String, String>> getPCharacterLevelInfo(Map<String, String> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selecPCharacter(paramMap);
	}

	@Override
	public List<Map<String, String>> getCompCharacter() throws Exception {
		// TODO Auto-generated method stub
		return characterDAO.selectCompCharacters();
	}

	@Override
	public List<Map<String, String>> getGameSceneInfo() throws Exception {
		// TODO Auto-generated method stub
		return scenarioDAO.selectScenes();
	}
}
