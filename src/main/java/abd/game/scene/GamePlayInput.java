package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GamePlayInput implements GamePlayElement {
	private String inputCode;
	private String inputPlaceholer;
	private String resultTxt;
	private String resultOccurred;
	
	public GamePlayInput(Map<String, String> playInfo, GameDataLoader loader) throws Exception {
		// TODO Auto-generated constructor stub
		List<Map<String,String>> inputList = loader.getInputOfPlay(playInfo);
		Map<String,String> inputInfo = inputList.get(0);
		inputCode = inputInfo.get("INPUT_CD");
		inputPlaceholer = inputInfo.get("INPUT_PLACEHOLDER");
		resultTxt = inputInfo.get("RESULT_TXT");
		resultOccurred = inputInfo.get("RESULT_OCCURRED");
	}
	
	@Override
	public Map<String, Object> getElContext() throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("status", "beforeInput");
		context.put("inputPlaceholer", inputPlaceholer);
		return context;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return inputCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> play(Map<String, Object> input, GameManager manager) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", "afterInput");
		
		String userInput =(String)input.get("userInput");
		
		String methodName = resultOccurred;
		
		if(methodName != null && !"".equals(methodName)) {
			Object instance = manager;
			Class<?> clazz = instance.getClass();

			Method method = null;
			Map<String,Object> resultContext = null;
			
			method = clazz.getDeclaredMethod(methodName,String.class,String.class);
			resultContext = (Map<String,Object>)method.invoke(instance,userInput,this.resultTxt);
			
			if(resultContext != null)
				resultMap.putAll(resultContext);
		}
		return resultMap;
	}

}
