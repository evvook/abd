package abd.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStatusRpgBattle implements GameStatus{
	private GameManager manager;
	
	public GameStatusRpgBattle(GameManager gameManager) {
		// TODO Auto-generated constructor stub
		this.manager = gameManager;
	}

	@Override
	public void go(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		if(!"B".equals(manager.getCurrentEventChildType())) {
			manager.switchCurrentStatus();
			manager.playCurrentStatus(input);
			return;
		}
		
		Map<String,Object> resultContext = manager.currnetEventHappened(input);
		manager.setEventContext(resultContext);
		
		//전투 중 발생한 스크립트 담아줌. 이벤트 호출하지 않음
		if("battle".equals(manager.getPlayInEventContext())) {
			@SuppressWarnings("unchecked")
			Map<String,Object> battleContext = (Map<String,Object>)manager.getBattleInEventContext();
			if(battleContext.get("script") != null) {
				manager.setScript(battleContext);
			}
		}
		List<String> scripts = new ArrayList<String>();
		scripts.addAll(manager.getEventScripts());
		manager.putEventContext("scripts", scripts);

		//전투 끝날 경우 이벤트 실행
		if("endBattle".equals(manager.getPlayInEventContext())) {
			@SuppressWarnings("unchecked")
			Map<String,Object> battleContext = (Map<String,Object>)manager.getBattleInEventContext();
			if(battleContext.get("script") != null) {
				manager.setScript(battleContext);
			}
			
			//패배한 경우 인트로로 돌아감
			if("defeat".equals(battleContext.get("battleResult"))) {
				manager.goBackIntro();
				return;
			}
			//승리한 경우 전투후 이벤트 실행
			else {
				manager.goBattleNextEvent();
				manager.setScriptEventContext();
				manager.goEvent();
			}
		}
		
	}

}
