package abd.game.scene;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GamePlaySelect implements GamePlayElement{
	private String selectCode;
	private String selectType;
	private Integer numOfSelect;
	private String selectHead;
	private List<Map<String,String>> options;
	private Map<Map<String,String>,String> optionsDetail;
	
	public GamePlaySelect(Map<String, String> playInfo, GameDataLoader loader) throws Exception {
		// TODO Auto-generated constructor stub
		List<Map<String,String>> selectList = loader.getSelectOfPlay(playInfo);
		Map<String,String> selectInfo = selectList.get(0);
		
		selectCode = selectInfo.get("SELECT_CD");
		selectType = selectInfo.get("S_TYPE");
		numOfSelect = Integer.valueOf(selectInfo.get("NUM_OF_SELECT"));
		selectHead = selectInfo.get("SELECT_HEAD");
		
		options = new ArrayList<Map<String,String>>();
		optionsDetail = new HashMap<Map<String,String>, String>();
		
		List<Map<String,String>> optionList = selectList;//loader.getOptionsOfSelect(selectInfo);
		Iterator<Map<String,String>> mi = optionList.iterator();
		while(mi.hasNext()) {
			Map<String,String> optionInfo = mi.next();
			Map<String,String> optionMap = new HashMap<String, String>();
			//optionMap.put(optionInfo.get("OPTION_SEQ"), optionInfo.get("OPTION_TXT"));
			optionMap.put("value", optionInfo.get("OPTION_SEQ"));
			optionMap.put("name", optionInfo.get("OPTION_TXT"));
			options.add(optionMap);
			Map<String,String> key = new HashMap<String, String>();
			key.put(selectCode, optionInfo.get("OPTION_SEQ"));
			optionsDetail.put(key, optionInfo.get("RESULT_OCCURRED"));
		}
	}

	@Override
	public Map<String, Object> getElContext() {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("selectHead",selectHead);
		resultMap.put("numOfSelect",numOfSelect);
		resultMap.put("selectOptions",options);
		resultMap.put("status", "beforeSelect");
		
		return resultMap;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return selectCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> play(Map<String, Object> input, GameManager manager) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", "afterSelect");
		
		Map<String,Object> selectInfo =(Map<String,Object>)input.get("selected");
		
		//{selected={SELECT_CD=SL001, OPTION_SEQ=1}}
		Map<String,String> key = new HashMap<String, String>();
		if(selectCode.equals(selectInfo.get("SELECT_CD"))) {
			String optionSeq = String.valueOf(selectInfo.get("OPTION_SEQ"));
			key.put(selectCode, optionSeq);
		}
		
		String resultOccurred = optionsDetail.get(key);
		String[] methodInfo = resultOccurred.split("#");
		String methodName = resultOccurred.split("#")[0];
		
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
				}
			}else {
				method = clazz.getDeclaredMethod(methodName);
				resultContext = (Map<String,Object>)method.invoke(instance);
			}
			
			if(resultContext != null)
				resultMap.putAll(resultContext);
		}
		
		//selectType이 F(flexible)인 경우 선택한 후에 제거해준다.
		if("F".equals(selectType)) {
			for(int i=0; i<options.size();i++) {
				Map<String,String> option = options.get(i);
				String optionSeq = String.valueOf(selectInfo.get("OPTION_SEQ"));
				if(optionSeq.equals(option.get("value"))) {
					options.remove(i);
					break;
				}
			}
		}
		
		return resultMap;
	}
}
