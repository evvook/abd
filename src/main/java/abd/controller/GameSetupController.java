package abd.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import abd.game.Game;
import abd.game.GameContainer;
import abd.game.GameManager;
import abd.service.SetupService;


@Controller
public class GameSetupController {

	@Resource
	private SetupService service;
	
	private static final Logger logger = LoggerFactory.getLogger(GameSetupController.class);
	
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/gameSetup", method= {RequestMethod.POST,RequestMethod.OPTIONS})
	public Map<String,Object> play(@RequestBody Map<String,Object> param, HttpServletRequest request) throws Exception{
		logger.info("setup");
		@SuppressWarnings("unchecked")
		Map<String,Object> paramMap = (Map<String,Object>)param.get("inputs");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		//세션별로 구분하기위해 세션id를 받아서 게임 정보와 함께 컨테이너에 보관한다.
		HttpSession session = request.getSession();
		String key = session.getId();

		Game game = null;
		if("clear".equals(paramMap.get("status"))) {
			//초기화시 컨테이너에서 게임 제거
			if(GameContainer.isExists(key)) {
				game = GameContainer.getGame(key);
				GameContainer.remove(game);
			}
		}else {
			if(GameContainer.isExists(key)) {
				//게임이 이미 존재하면 삭제함
				game = GameContainer.getGame(key);
				GameContainer.remove(game);
			}
			//setup에서는 항상 새로운 게임 설정
			game = new Game(new GameManager());
			GameContainer.putGame(key, game);
			
			service.setGame(game);
			resultMap = service.setup(paramMap);
		}
		
		return resultMap;
	}
}
