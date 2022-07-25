package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GameEvJustHpn extends GameEvent {

	private String jhCode;
	private String resultTxt;
	private String resultOccurred;
	
	private GameManager manager;

	public GameEvJustHpn(Map<String, String> eventInfo, GameDataLoader loader, GameScene scene, GameManager manager) throws Exception {
		super(eventInfo, loader, scene, manager.getPlayer());
		// TODO Auto-generated constructor stub
		this.manager = manager;
		
		List<Map<String,String>> jhList = loader.getJustHpnOfEvent(eventInfo);
		Map<String,String> jhInfo = jhList.get(0);
		
		jhCode = jhInfo.get("JH_CD");
		resultTxt = jhInfo.get("RESULT_TXT");
		resultOccurred = jhInfo.get("RESULT_OCCURRED");
	}

	public String getCode() {
		return jhCode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> happened(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		if(input == null) {
			return happened();
		}
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//이벤트 JH인 걸 명시해준다.
		resultMap.put("event", "justHappened");
		
		String[] methodInfo = resultOccurred.split("#");
		String methodName = methodInfo[0];
		
		if(methodName != null && !"".equals(methodName)) {
			Object instance = manager;
			Class<?> clazz = instance.getClass();

			Method method = null;
			Map<String,Object> resultContext = null;
			
			String param = (String)input.get("param");
			if(methodInfo.length > 1) {
				if(methodInfo.length == 5) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					String methodParam3 = methodInfo[3];
					String methodParam4 = methodInfo[4];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class,String.class,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2,methodParam3,methodParam4,param);
				}else if(methodInfo.length == 7) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					String methodParam3 = methodInfo[3];
					String methodParam4 = methodInfo[4];
					String methodParam5 = methodInfo[5];
					String methodParam6 = methodInfo[6];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class,String.class,String.class,String.class,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2,methodParam3,methodParam4,methodParam5,methodParam6,param);
				}
			}else {
				method = clazz.getDeclaredMethod(methodName,String.class);
				resultContext = (Map<String,Object>)method.invoke(instance,param);
				}
			
			
			if(resultContext != null)
				resultMap.putAll(resultContext);
		}
		
		//코드 설정
		//esultMap.put("jhCode", jhCode);
		//resultTxt 설정
		String playerName = getPlayer().getName();
		String playerJob = getPlayer().getJob();
		
		if(resultMap.get("script") != null) {
			resultTxt = (String)resultMap.get("script");
		}
		if(playerName != null) {
			resultTxt = resultTxt.replace("%name%", playerName);
		}
		if(playerJob != null) {
			resultTxt = resultTxt.replace("%job%", playerJob);
		}
		resultMap.put("script", resultTxt);
		
		hasDone();
		
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> happened() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//이벤트 JH인 걸 명시해준다.
		resultMap.put("event", "justHappened");
		
		String[] methodInfo = resultOccurred.split("#");
		String methodName = methodInfo[0];
		
		if(methodName != null && !"".equals(methodName)) {
			Object instance = manager;
			Class<?> clazz = instance.getClass();

			Method method = null;
			Map<String,Object> resultContext = null;
			
				
			if(methodInfo.length > 1) {
				if(methodInfo.length == 2) {
					String methodParam = methodInfo[1];
					method = clazz.getDeclaredMethod(methodName,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam);
				}else if(methodInfo.length == 3) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2);
				}else if(methodInfo.length == 4) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					String methodParam3 = methodInfo[3];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2,methodParam3);
				}else if(methodInfo.length == 5) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					String methodParam3 = methodInfo[3];
					String methodParam4 = methodInfo[4];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2,methodParam3,methodParam4);
				}
				else if(methodInfo.length == 6) {
					String methodParam1 = methodInfo[1];
					String methodParam2 = methodInfo[2];
					String methodParam3 = methodInfo[3];
					String methodParam4 = methodInfo[4];
					String methodParam5 = methodInfo[5];
					method = clazz.getDeclaredMethod(methodName,String.class,String.class,String.class,String.class,String.class);
					resultContext = (Map<String,Object>)method.invoke(instance,methodParam1,methodParam2,methodParam3,methodParam4,methodParam5);
				}			
			}else {
				method = clazz.getDeclaredMethod(methodName);
				resultContext = (Map<String,Object>)method.invoke(instance);
			}
			if(resultContext != null)
				resultMap.putAll(resultContext);
		}
		
		//코드 설정
		//resultMap.put("jhCode", jhCode);
		//resultTxt 설정
		String playerName = getPlayer().getName();
		String playerJob = getPlayer().getJob();
		
		if(resultMap.get("script") != null) {
			resultTxt = (String)resultMap.get("script");
		}
		if(playerName != null) {
			resultTxt = resultTxt.replace("%name%", playerName);
		}
		if(playerJob != null) {
			resultTxt = resultTxt.replace("%job%", playerJob);
		}
		resultMap.put("script", resultTxt);
		
		hasDone();
		
		return resultMap;
	}
}
