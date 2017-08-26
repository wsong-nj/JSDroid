package tool.Analy.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class LocalValue{	
	Map<Unit, List> unitToLocalsAfter;
    Map<Unit, List> unitToLocalsBefore;
	UnitGraph graph ;
	SootMethod sm ; 
	public LocalValue(SootMethod sm) {
		this.sm = sm;
		graph = new ExceptionalUnitGraph(sm.retrieveActiveBody());
		LiveLocalsValueAnalysis analysis = new LiveLocalsValueAnalysis(graph);{
            unitToLocalsAfter = new HashMap<Unit, List>(graph.size() * 2 + 1, 0.7f);
            unitToLocalsBefore = new HashMap<Unit, List>(graph.size() * 2 + 1, 0.7f);

            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext()){
            	Unit s = (Unit) unitIt.next();
                FlowSet set = (FlowSet) analysis.getFlowBefore(s);
                unitToLocalsBefore.put(s, Collections.unmodifiableList(set.toList()));                
                FlowSet set2 = (FlowSet) analysis.getFlowAfter(s);
                unitToLocalsAfter.put(s, Collections.unmodifiableList(set2.toList()));    
            }            
        }
	}
	
	public SootMethod getMethod(){
		return sm;
	}
	
	public List getLiveLocalsAfter(Unit s)
    {
        return unitToLocalsAfter.get(s);
    }
    
    public List getLiveLocalsBefore(Unit s)
    {
        return unitToLocalsBefore.get(s);
    }
    
    
    /**
     * 局部变量v在语句u处的值。
     * */
    public DefinitionStmt getValueTillCurUnit(Unit currentUnit,Value v){ 
    	
    	List<Unit> l = unitToLocalsBefore.get(currentUnit);
    	if(l==null){
    	//	System.out.println(getClass()+" "+currentUnit+" "+v+" "+sm);
    		return null;
    	}
    	Iterator<Unit> it = l.iterator();
    	while(it.hasNext()){
    		Unit unit = it.next();
    		if(unit.getDefBoxes().get(0).getValue().equals(v)){
    			if(unit instanceof AssignStmt){
    				AssignStmt as = (AssignStmt)unit;
    				if((as.getRightOp() instanceof CastExpr)&&((CastExpr)(as.getRightOp())).getType().toString().contains("android")){
						CastExpr ce = (CastExpr)(as.getRightOp());
						return getValueTillCurUnit(unit, ce.getOp());
					}
					else if(as.getRightOp() instanceof JimpleLocal){						
						return getValueTillCurUnit(unit, as.getRightOp());
					}
					else {
						return ( DefinitionStmt)unit;
					}
    			}
    			else if(unit instanceof IdentityStmt){
    				IdentityStmt as = (IdentityStmt)unit;
    				if(as.getLeftOp().equals(v)){
    					if(as.getRightOp() instanceof JimpleLocal){
    						return getValueTillCurUnit(unit, as.getRightOp());
    					}
    					else{
    						return ( DefinitionStmt)unit;
    					}		
    				}
    			}
    		}
    	}
    	return null;
    }
}

class LiveLocalsValueAnalysis extends ForwardFlowAnalysis{

	 FlowSet emptySet;
	 Map<Unit, FlowSet> unitToGenerateSet;
	 Map<Unit, FlowSet> unitToKillSet;
	 
	public LiveLocalsValueAnalysis(UnitGraph graph) {
		super(graph);
		emptySet = new ArraySparseSet();

        // Create kill sets.
        {
            unitToKillSet = new HashMap<Unit, FlowSet>(graph.size() * 2 + 1, 0.7f);

            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet killSet = emptySet.clone();
                
                Iterator boxIt = s.getDefBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        killSet.add(s, killSet);
                }

                    unitToKillSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            unitToGenerateSet = new HashMap<Unit, FlowSet>(graph.size() * 2 + 1, 0.7f);

            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet genSet = emptySet.clone();

                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        genSet.add(s, genSet);
                }

                unitToGenerateSet.put(s, genSet);
            }
        }   
        doAnalysis();         
	}

	@Override
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

	@Override
	protected Object entryInitialFlow() {
		return emptySet.clone();
	}

	@Override
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inSet1 = (FlowSet)in1,
				inSet2 = (FlowSet)in2,
				outSet = (FlowSet)out;
		inSet1.intersection(inSet2, outSet);
	}

	@Override
	protected void copy(Object source, Object dest) {
		FlowSet srcSet = (FlowSet)source,
				destSet = (FlowSet)dest;
		srcSet.copy(destSet);
	}

	@Override
	protected void flowThrough(Object in, Object d, Object out) {
		FlowSet inSet = (FlowSet)in,
				outSet = (FlowSet)out;
		Unit u = (Unit)d;
		// out <- (in - expr containing locals defined in d) union out 
		kill(inSet, u, outSet);
		// out <- out union expr used in d
		gen(outSet, u);
	}
	
	private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
		FlowSet kills = (FlowSet)emptySet.clone();
		Iterator defIt = u.getDefBoxes().iterator();
		while (defIt.hasNext()) {
			ValueBox defBox = (ValueBox)defIt.next();

			if (defBox.getValue() instanceof Local) {
				Iterator inIt = inSet.iterator();
				while (inIt.hasNext()) {
					Unit unit = (Unit)inIt.next();
					Iterator eIt = unit.getDefBoxes().iterator();
					while(eIt.hasNext()){
						ValueBox dBox = (ValueBox)eIt.next();
						if (dBox.getValue() instanceof Local &&
								dBox.getValue().equivTo(defBox.getValue())){
							kills.add(unit);
							
						}
					}
				}
			}
		}			
		inSet.difference(kills, outSet);
	}
	
	/**
	 * Performs gens by iterating over the units use-boxes.
	 * If the value of a use-box is a binopExp then we add
	 * it to the outSet.
	 * @param outSet the set flowing out of the unit
	 * @param u the unit being flown through
	 */
	private void gen(FlowSet outSet, Unit u) {
		Iterator useIt = u.getDefBoxes().iterator();
		while (useIt.hasNext()) {
			ValueBox defBox = (ValueBox)useIt.next();
			
			if (defBox.getValue() instanceof Local)
				outSet.add(u);
		}
	}
	
}
