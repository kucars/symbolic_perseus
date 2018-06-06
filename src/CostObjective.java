
import java.io.Serializable;
import java.util.Map;

public class CostObjective implements Serializable{

	private Map<String, DD[]> costObjective = new HashMap();
	
	public void addCostObjective(String action, DD[] co){
		costObjective.put(action, co);
	}
	
	public DD[] getCostObjective(String action){
		
		DD[] co = costObjective.get(action);
		return co;
	}
	
	
	
	public Map getAllCostObjectives(){
		return costObjective;
	}
}
