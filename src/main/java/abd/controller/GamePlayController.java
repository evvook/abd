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
import abd.service.PlayService;


@Controller
public class GamePlayController {

	@Resource
	private PlayService service;
	
	private static final Logger logger = LoggerFactory.getLogger(GamePlayController.class);
	
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/gamePlay", method= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
	public Map<String,Object> play(@RequestBody Map<String,Object> param, HttpServletRequest request) throws Exception{
		logger.info("play");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		//세션별로 구분하기위해 세션id를 받아서 게임 정보와 함께 컨테이너에 보관한다.
		String key = (String)param.get("gameToken");
		HttpSession session = request.getSession();
		if(key == null) {
			key = session.getId();
		}

		Game game = null;
		if("clear".equals(param.get("status"))) {
			//초기화시 컨테이너에서 게임 제거
			if(GameContainer.isExists(key)) {
				game = GameContainer.getGame(key);
				GameContainer.remove(game);
			}
		}else {
			//그 외의 경우 게임 생성 또는 가져와서 진행
			if(GameContainer.isExists(key)) {
				game = GameContainer.getGame(key);
			}else {
				//setup에서 생성했어야 하는데 존재하지 않음
				throw new Exception("게임이 존재하지 않습니다.");
			}
			service.setGame(game);
			resultMap = service.play(param);
		}
		
		return resultMap;
	}
}
