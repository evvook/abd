package abd.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GameStatusVisualNovel implements GameStatus{
	private GameManager manager;
	
	public GameStatusVisualNovel(GameManager gameManager) {
		// TODO Auto-generated constructor stub
		this.manager = gameManager;
	}
	
	@Override
	public void go(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		if("B".equals(manager.getCurrentEventChildType())) {
			manager.switchCurrentStatus();
			manager.playCurrentStatus(input);
			return;
		}
		
		//이벤트 실행
		Map<String,Object> resultContext = manager.currnetEventHappened(input);
		manager.setEventContext(resultContext);
		
		//연속 실행할 이벤트가 있는지 탐색 및 이벤트 실행
		if(!"P".equals(manager.getCurrentEventType())) {

			//직전 이벤트가 플레이고 셀렉트일때
			if("select".equals(manager.getPlayInEventContext())) {
				if("afterSelect".equals(manager.getStatusInEventContext())) {
					//플레이 정보는 삭제하고
					List<String> removeKeys = new ArrayList<String>(Arrays.asList("play","status"));
					manager.removeEventContext(removeKeys);
					//스크립트 담아주고
					manager.setScriptEventContext();
					manager.goEvent();
					
				}
			//스크립트가 있다면 담아주고 이벤트 호출
			}else if(manager.getScriptInEventContext() != null) {
				manager.setScriptEventContext();
				manager.goEvent();
			}
			
		}//플레이 타입이 아닌 경우 이벤트 연쇄 발생
		//플레이 타입이면
		//이벤트 연쇄(재귀)인 경우 몇몇 케이스를 제외하고 이벤트 연쇄(재귀)를 멈춘다
		else if("P".equals(manager.getCurrentEventType())) {
			
			//케이스 #1
			//인풋이고 에프터인풋이면 리절트 텍스트 담아주고 이벤트 호출
			if("input".equals(manager.getPlayInEventContext())) {
				@SuppressWarnings("unchecked")
				Map<String,String> rInput = (Map<String,String>)manager.getInputInEventContext();
				if("afterInput".equals(rInput.get("status"))) {
					List<String> removeKeys = new ArrayList<String>(Arrays.asList("input"));
					manager.removeEventContext(removeKeys);
					manager.setScript(rInput.get("resultTxt"));
					manager.goEvent();
					
				}
			}
			//케이스 #2
			//셀렉트이고 애프터셀렉트 이면 스크립트 담아주고 플레이 정보 제거
			else if("select".equals(manager.getPlayInEventContext())) {
				if("afterSelect".equals(manager.getStatusInEventContext())) {
					List<String> removeKeys = new ArrayList<String>(Arrays.asList("play","status"));
					manager.removeEventContext(removeKeys);
					manager.setScriptEventContext();
					manager.goEvent();
					
				}
			}
			//케이스 #3
			//스크립트가 있다면 담아주고 이벤트 호출	
			else if(manager.getScriptInEventContext() != null){
				manager.setScriptEventContext();
				manager.goEvent();
			}
			
			//스크립트 담아주고 이벤트 더이상 호출하지 않음
			List<String> scripts = new ArrayList<String>();
			scripts.addAll(manager.getEventScripts());
			manager.putEventContext("scripts", scripts);
				
		}//플레이 타입이 아니거나 플레이 타입임
	}
}
