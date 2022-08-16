package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GameEvBranch extends GameEvent {
	
	private String branchCode;
	private String branchMethod;
	
	private String yEventCode;
	private String yEventSeq;
	private String nEventCode;
	private String nEventSeq;
	private String outEventCode;
	private String outEventSeq;

	private GameEventCallback callback;
	
	public GameEvBranch(Map<String, String> eventInfo, GameDataLoader loader, GameScene scene, GameManager manager) throws Exception {
		super(eventInfo, loader, scene, manager.getPlayer());
		// TODO Auto-generated constructor stub
		this.callback = scene.getEventCallBack();
		
		List<Map<String,String>> brList = loader.getBranchOfEvent(eventInfo);
		Map<String,String> brInfo = brList.get(0);
		
		branchCode = brInfo.get("BRANCH_CD");
		branchMethod = brInfo.get("BRANCH_METHOD");
		yEventCode = brInfo.get("Y_EVENT_CD");
		yEventSeq = brInfo.get("Y_EVENT_SEQ");
		nEventCode = brInfo.get("N_EVENT_CD");
		nEventSeq = brInfo.get("N_EVENT_SEQ");
		outEventCode = brInfo.get("OUT_EVENT_CD");
		outEventSeq = brInfo.get("OUT_EVENT_SEQ");
	}
	
	public String getCode() {
		return branchCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> happened() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		String[] methodInfo = branchMethod.split("#");
		String methodName = methodInfo[0];
		
		if(methodName != null && !"".equals(methodName)) {
			Object instance = callback;
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
		
		hasDone();
		
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> happened(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		if(input == null)
			return happened();
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		String[] methodInfo = branchMethod.split("#");
		String methodName = methodInfo[0];
		
		if(methodName != null && !"".equals(methodName)) {
			Object instance = callback;
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
		hasDone();
		
		return resultMap;
	}

	public Map<String, Object> goYEvent() throws Exception {
		// TODO Auto-generated method stub
		return callback.goSpecificEvent(yEventCode, yEventSeq);
	}

	public Map<String, Object> goNEvent() throws Exception {
		// TODO Auto-generated method stub
		return callback.goSpecificEvent(nEventCode, nEventSeq);
	}

	public Map<String, Object> goYEvent(String param) throws Exception {
		// TODO Auto-generated method stub
		return callback.goSpecificEvent(yEventCode, yEventSeq, param);
	}

	public Map<String, Object> goNEvent(String param) throws Exception {
		// TODO Auto-generated method stub
		return callback.goSpecificEvent(nEventCode, nEventSeq, param);
	}

	public void setOutEvent() throws Exception {
		// TODO Auto-generated method stub
		callback.setSpecificEvent(outEventCode, outEventSeq);
	}

}
