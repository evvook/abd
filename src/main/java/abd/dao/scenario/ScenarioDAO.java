package abd.dao.scenario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("scenarioDAO")
public class ScenarioDAO {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	private String Namespace = "abd.scenario.scenarioMapper";
	
	public List<Map<String,String>> selectStartScene(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectStartScene",paramMap);
	}

	public List<Map<String, String>> selectEvents(Map<String, String> paramMap) throws Exception{
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectEvents",paramMap);
	}

	public List<Map<String, String>> selectScripts(Map<String, String> paramMap) throws Exception{
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectScripts",paramMap);
	}

	public List<Map<String, String>> selectPlay(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectPlay",paramMap);
	}

	public List<Map<String, String>> selectSelection(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectSelection",paramMap);
	}

	public List<Map<String, String>> selectOptions(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectOptions",paramMap);
	}

	public List<Map<String, String>> selectSelectionResult(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectSelectionResult",paramMap);
	}

	public List<Map<String, String>> selectBattle(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectBattle",paramMap);
	}

	public List<Map<String, String>> selectJustHpnOfEvent(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectJustHpnOfEvent",paramMap);
	}

	public List<Map<String, String>> selectInputOfPlay(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectInputOfPlay",paramMap);
	}
}
