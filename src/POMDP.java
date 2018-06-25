import java.io.*;
import java.util.*;


public class POMDP implements Serializable {
	
	
	//TODO: these values for only testing later we have to find way to get them from the spudd problem file 
	int nObj = 2;
	int nStates = 2;
	//double [] weights = {1,0};
	DD w1dd = DD.zero;
	DD w2dd = DD.zero;
	DD w3dd = DD.zero;
	static DD[] ddweights = new DD[1];
	
    public  DD [][]alphaVectors_2D; //---------> new variable 
	public  ArrayList<DD[][]> listArrayAlphaMatrix_2x3; //-----------> list that has DD[][] as alphaMatrix
	public  ArrayList<DD[][]> original_listArrayAlphaMatrix_3x2; //-----------> list that has DD[][] as alphaMatrix
	public  ArrayList<DD[][]> listArrayAlphaMatrix_3x2;
	
    public int nStateVars;
    public int nObsVars;
    public int nVars;
    public int nActions;
    public int nObservations;
    
    public DD costObjectivesDD = DD.zero; 
    public DD [] costObjectivesDDarray;// to be used in dpbackup (we save the not scalarized rewFn 

    public int maxAlphaSetSize;

    public boolean debug;

    public boolean ignoremore;
    public boolean addbeldiff;

    public StateVar [] stateVars;
    public StateVar [] obsVars;
    public Action [] actions;
    public int [] varDomSize;
    public int [] primeVarIndices;
    public int [] varIndices;
    public int [] obsIndices;
    public int [] primeObsIndices;
    public int [] obsVarsArity;
    public String [] varName;
    public double discFact,tolerance,maxRewVal;
    public DD initialBelState;
    public DD ddDiscFact;
    public String [] adjunctNames;
    public int nAdjuncts;
    public DD [] adjuncts;  // some additional DDs that can be used 
    public DD [] initialBelState_f;
    public DD [] qFn;
    public int [] qPolicy;
    public DD [][] belRegion;
    // these three should really be combined into AlphaVector class
    public int [] policy;
    public boolean [] uniquePolicy;
    public double [] policyvalue;
    public   DD [] alphaVectors;
    public DD [] origAlphaVectors;

    public DD [] alphaMatrix;
    
    public double [][] currentPointBasedValues, newPointBasedValues;
    public AlphaVector [] newAlphaMatrix;
    public int numnewAlphaMatrix;
    public double bestImprovement, worstDecline;
    Writer writer;
    Writer writer1;
    Writer writer2;
    
    Writer writerb_1; 
    Writer writerb_2;
    Writer writerd_1;
    Writer writerd_2;
    
    public CostObjective costObjective;
    
    public List<Alpha_Matrix> finalAlphaVectors;
    public String fileName="";
    
    public static DD [] concatenateArray(DD a, DD [] b, DD c) 
    {
	DD [] d = new DD[b.length+2];
	d[0] = a;
	System.arraycopy(b, 0, d, 1, b.length);
	d[b.length+1]=c;
	
	return d;
    }

    // assumes they're the same size along the first dimension
    public static int [][] concatenateArray(int [][] a, int [][] b) {
	int [][] d = new int[a.length][a[0].length+b[0].length];
	for (int i=0; i<a.length; i++) 
	    d[i] = concatenateArray(a[i],b[i]);
	return d;
    }
    public static int [] concatenateArray(int [] a, int [] b) {
	int [] d = new int[a.length+b.length];
	int k=0;
	for (int i=0; i<a.length; i++) 
	    d[k++]=a[i];
	for (int i=0; i<b.length; i++) 
	    d[k++]=b[i];
	return d;
    }

    public static DD [] concatenateArray(DD a, DD b) {
	DD [] d = new DD[2];
	d[0] = a;
	d[1] = b;
	return d;
    }
    public static DD [] concatenateArray(DD a, DD b, DD c) {
	DD [] d = new DD[3];
	d[0] = a;
	d[1] = b;
	d[2] = c;
	return d;
    }
    public static DD [] concatenateArray(DD [] a, DD [] b) {
	DD [] d = new DD[b.length+a.length];
	System.arraycopy(a, 0, d, 0, a.length);
	System.arraycopy(b, 0, d, a.length, b.length);
	return d;
    }
    public static DD [] concatenateArray(DD [] a, DD [] b, DD c) {
	DD [] d = new DD[b.length+a.length+1];
	System.arraycopy(a, 0, d, 0, a.length);
	System.arraycopy(b, 0, d, a.length, b.length);
	d[b.length+a.length]=c;
	return d;
    }
    public static DD [] concatenateArray(DD a, DD [] b, DD [] c) {
	DD [] d = new DD[b.length+c.length+1];
	d[0] = a;
	System.arraycopy(b, 0, d, 1, b.length);
	System.arraycopy(c, 0, d, 1+b.length, c.length);
	return d;
    }
    public static DD [] concatenateArray(DD [] a, DD [] b, DD [] c) {
	DD [] d = new DD[b.length+a.length+c.length];
	System.arraycopy(a, 0, d, 0, a.length);
	System.arraycopy(b, 0, d, a.length, b.length);
	System.arraycopy(c, 0, d, a.length+b.length, c.length);
	return d;
    }
    public static double [] concatenateArray(double [] a, double [] b) {
	double [] d = new double[b.length+a.length];
	System.arraycopy(a, 0, d, 0, a.length);
	System.arraycopy(b, 0, d, a.length, b.length);
	return d;
    }
    public static int [][] stackArray(int [] a, int [] b) {
	int [][] d = new int[2][a.length];
	System.arraycopy(a, 0, d[0], 0, a.length);
	System.arraycopy(b, 0, d[1], 0, b.length);
	return d;
    }

    public class Action implements Serializable {
	public String name;
	public DD[] transFn;
	public DD[] obsFn;
	public DD rewFn;
	public DD costObj;
	public DD[] rewTransFn;
	public DD[] costObjTransFn;
	public DD initialBelState;
	public Action(String n) {
	    name = n;
	}
	public void addTransFn(DD [] tf) {
	    transFn = new DD[tf.length];
	    for ( int idx = 0; idx < tf.length; ++idx ) {
		transFn[idx] = tf[idx];
	    }
	}
	public void addObsFn(DD [] of) {
	    obsFn = new DD[of.length];
	    for ( int idx = 0; idx < of.length; ++idx ) {
		obsFn[idx] = of[idx];
	    }
	}
	public void buildRewTranFn() {
	    rewTransFn = new DD[transFn.length+1];
	    int k=0;
	    rewTransFn[k++] = rewFn;
	    for ( int idx = 0; idx < transFn.length; ++idx ) {
		rewTransFn[k++] = transFn[idx];
	    }
	}
	
	public void buildcostObjTranFn() {
	    costObjTransFn = new DD[transFn.length+1];
	    int k=0;
	    costObjTransFn[k++] = costObj;
	    for ( int idx = 0; idx < transFn.length; ++idx ) {
	    	costObjTransFn[k++] = transFn[idx];
	    }
	}
    }

    public class StateVar implements Serializable {
	public int arity;
	public String name;
	public int id;
	public String [] valNames;
	public StateVar(int a, String n, int i) {
	    arity = a;
	    name = n;
	    id = i;
	    valNames = new String[arity];
	}
	public void addValName(int i, String vname) {
	    valNames[i]=vname;
	}

    }
    public double [] getRewFnTabular(int actId) {
	return OP.convert2array(actions[actId].rewFn,concatenateArray(varIndices,primeVarIndices));
    }
    public double [] getInitBelStateTabular() {
	return OP.convert2array(initialBelState,varIndices);
	
    }
    public double [] getTransFnTabular(int actId) { 
	// first, blow up the transition function
	// WARNING: this may cause bad things to happen memory wise
	DD fullTF = actions[actId].transFn[0];
	for (int i=1; i<nStateVars; i++) {
	    fullTF = OP.mult(actions[actId].transFn[i],fullTF);
	}
	
	return OP.convert2array(fullTF,concatenateArray(varIndices,primeVarIndices));
    }
    public double [] getObsFnTabular(int actId, int obsId) { 
	int [] tmpidarray = new int[1];
	tmpidarray[0] = primeObsIndices[obsId];
	return OP.convert2array(actions[actId].obsFn[obsId],concatenateArray(primeVarIndices,tmpidarray));
    }


    public void solveQMDP() {
	solveQMDP(50);
    }
    // solves the POMDP as an MDP 
    public void solveQMDP(int count) {
	System.out.println("Computing qMDP policy");
	double bellmanErr = 2*tolerance;
	DD valFn = DD.zero;
	DD prevValFn;
	DD [] cdArray;
	int actId1, actId2, actId;
	double [] zerovalarray = new double[1];
	DD [] tempQFn = new DD[nActions];
	zerovalarray[0]=0;
	int iter=0;
	while (bellmanErr > tolerance && iter < count) {
	    System.out.println("iteration "+iter++);
	    prevValFn = valFn; 
	    valFn = OP.primeVars(valFn,nVars);
	    for (actId=0; actId<nActions; actId++) {
		cdArray = concatenateArray(ddDiscFact,actions[actId].transFn,valFn);
		tempQFn[actId] = OP.addMultVarElim(cdArray,primeVarIndices);
		tempQFn[actId] = OP.add(actions[actId].rewFn,tempQFn[actId]);
		tempQFn[actId] = OP.approximate(tempQFn[actId],bellmanErr*(1-discFact)/2.0,zerovalarray);
	    }
	    valFn = OP.maxN(tempQFn);
	    bellmanErr = OP.maxAll(OP.abs(OP.sub(valFn,prevValFn)));
	    System.out.println("Bellman error: "+bellmanErr);
	    Global.newHashtables();
	}
	// remove dominated alphaVectors
	boolean dominated;
	boolean [] notDominated = new boolean[nActions];
	for (actId1=0; actId1<nActions; actId1++) 
	    notDominated[actId1]=false;
	for (actId1=0; actId1<nActions; actId1++) {
	    dominated=false;
	    actId2=0;
	    while (!dominated && actId2 < nActions) {
		if (notDominated[actId2] && OP.maxAll(OP.sub(tempQFn[actId1],tempQFn[actId2])) < tolerance)
		    dominated=true;
		actId2++;
	    }
	    if (!dominated) 
		notDominated[actId1]=true;
	}
	int numleft = 0;
	for (actId1=0; actId1<nActions; actId1++) {
	    if (notDominated[actId1])
		numleft++;
	}
	qFn = new DD[numleft];
	qPolicy = new int[numleft];
	numleft=0;
	for (actId1=0; actId1<nActions; actId1++) {
	    if (notDominated[actId1]) {
		qFn[numleft] = tempQFn[actId1];
		qPolicy[numleft]=actId1;
		numleft++;
	    }
	}
    }
    public void displayQFn() {
	for (int i=0; i<qFn.length; i++) {
	    System.out.println("--------------------------------------------------- dd "+i);
	    qFn[i].display();
	}
    }
    public POMDP(String fileName, double [] w) {
	readFromFile(fileName,false,w);
    }
    public POMDP(String fileName,POMDP oldpomdp, double [] w) {
	readFromFile(fileName,false,w);
	setAlphaVectors(oldpomdp.alphaVectors,oldpomdp.policy);
	setBelRegion(oldpomdp.belRegion);
    }
    public POMDP(String fileName, boolean debb, boolean ig, boolean abd, double [] w) {
	readFromFile(fileName,debb,w);
	ignoremore=ig;
	addbeldiff=abd;
    }

    public POMDP(String fileName, boolean debb, boolean ig, double []w) {
	readFromFile(fileName,debb,w);
	ignoremore=ig;
    }

    public POMDP(String fileName, boolean debb, double [] w) {
	readFromFile(fileName,debb, w);
    }
    

	public void readFromFile(String fileName, double [] w) {
	readFromFile(fileName,false,w);
    }
    public void setIgnoreMore(boolean ig) {
	ignoremore = ig;
    }
    
  //-----------------convertDoubleAtoDD-----------------------------------------------------------
    private DD[] convertDoubleAtoDD(double[] w){
		DD[] dd = new DD[1];
		DD[] children = new DD[w.length];
		for(int i=0;i<w.length;i++)
		{
			//System.out.println("w[i]:"+w[i]);
			children[i] = DDleaf.myNew(w[i]);
			//children[i].display();
		}
		dd[0] = DDnode.myNew(1, children);
		int [] n = dd[0].getVarSet(); 
		return dd;
	}
		//DDNode
			//DDLeaf
			//DDLeaf
    //---------------scalarize---------------------------------------------------------------------------------
    
    public DD scalarize ( int a)// a node of action will be sent
    {
    	//System.out.println("---number of leaves--: "+ dds.getNumLeaves());
    	//multiply rewards for first objective with w1  
    	//System.out.println("================printing dds[0]============");
    	//dds[0].display();
    	//DD multiply1 = OP.mult(w1dd, dds[0]);
    	
    	//System.out.println("=====================multiply by 1* DD ");
    	//actionRewards.display();
    	//multiply1.display();
    			
    	//DD multiply2 = OP.mult(w2dd, dds[1]);
    	//System.out.println("=====================multiply by 0* DD ");
    	//multiply2.display();
    	//System.out.println("================printing dds[0]============");
    	//dds[1].display();
    	
    	//DD resultAdd = OP.add(multiply1, multiply2);
    	//System.out.println("=====================result of adding ");
    	//resultAdd.display();
    	
    	DD multiply1 = DD.one;
    	DD multiply2 = DD.one;
    	//---------------------------get multiObjectives manually and multiply by weights  ------------------------
    	/*if (a ==0)
    	{
    		double [] robj1 = {1,1};
    		double [] robj2 = {1,1};
    		
    		DD[] ddrewardsObj1 = convertDoubleAtoDD(robj1);
    		DD dobj1= ddrewardsObj1[0];//it is only one node
    		
    		DD[] ddrewardsObj2 = convertDoubleAtoDD(robj2);
    		DD dobj2= ddrewardsObj1[0];//it is only one node

    		
    		 multiply1 = OP.mult(w1dd, dobj1);
    		 multiply2 = OP.mult(w2dd, dobj2);
    		
    	}else if (a==1)
    	{
    		double [] robj1 = {100,-10};
    		double [] robj2 = {0,0};
    		
    		DD[] ddrewardsObj1 = convertDoubleAtoDD(robj1);
    		DD dobj1= ddrewardsObj1[0];//it is only one node
    		
    		DD[] ddrewardsObj2 = convertDoubleAtoDD(robj2);
    		DD dobj2= ddrewardsObj1[0];//it is only one node

    		
    		 multiply1 = OP.mult(w1dd, dobj1);
    		 multiply2 = OP.mult(w2dd, dobj2);
    	}else if (a==2)
    	{
    		double [] robj1 = {-10,100};
    		double [] robj2 = {0,0};
    		
    		DD[] ddrewardsObj1 = convertDoubleAtoDD(robj1);
    		DD dobj1= ddrewardsObj1[0];//it is only one node
    		
    		DD[] ddrewardsObj2 = convertDoubleAtoDD(robj2);
    		DD dobj2= ddrewardsObj1[0];//it is only one node

    		
    		 multiply1 = OP.mult(w1dd, dobj1);
    		 multiply2 = OP.mult(w2dd, dobj2);
    	}
    	*/
    	//-------------by parsing multiOBjectives and multiply by weights -----------------------------
  
    	DD obj1DD = DD.zero;
		DD obj2DD= DD.zero;
    	DD[] children = costObjectivesDD.getChildren();
    	if(children!=null)
    	{
    		DD [][] childrenMatrix = new DD [nObj][nStates];
    		if (children.length == nObj)
    		{
    			//DD [][] childrenMatrix = new DD [costObjectivesDD.getChildren().length][nStates];
    			for (int i = 0; i < childrenMatrix.length; i++) 
    			{
    				for (int s = 0; s < childrenMatrix[i].length; s++) 
    				{
				
    					if(children[i].getChildren()!=null)
    					{
    						//System.out.println("++" + children[i].getChildren()[s].getVal());
									
    						childrenMatrix[i][s]= DDleaf.myNew(children[i].getChildren()[s].getVal());
					
    					}else
    					{
    						//System.out.println("^" + children[i].getVal());
				
    						childrenMatrix[i][s]= DDleaf.myNew(children[i].getVal());
    									
    					}
    				}
		
    			}

    		}else if (children.length == nStates)// in case of 3 nodes (special case) if reward was = {100,100} {10,10} {0,0}
    		{
    			//DD [][] childrenMatrix = new DD [nObj][costObjectivesDD.getChildren().length];
    			for (int i = 0; i < childrenMatrix.length; i++) 
    			{
    				for (int s = 0; s < childrenMatrix[i].length; s++) 
    				{
				
    					if(children[i].getChildren()!=null)
    					{
    						//System.out.println("++" + children[i].getChildren()[s].getVal());
									
    						childrenMatrix[i][s]= DDleaf.myNew(children[i].getChildren()[s].getVal());
					
    					}else
    					{
    						//System.out.println("^" + children[s].getVal());
				
    						childrenMatrix[i][s]= DDleaf.myNew(children[s].getVal());// this works bcz of nStates is same nNodes
    									
    					}
    				}
		
    			}

    			
    		}

			//TODO: still these obj1DD should be generalized 
			obj1DD = DDnode.myNew(1, childrenMatrix[0]);
        	obj2DD = DDnode.myNew(1, childrenMatrix[1]);
        	
        	int [] n1 = obj1DD.getVarSet(); 
        	int [] n2 = obj2DD.getVarSet();
    	}else// this for only one leave cost(1)
    	{
    		obj1DD = DDleaf.myNew(costObjectivesDD.getVal());
    		obj2DD = obj1DD;
    	}

    	
    	//---------------multiply with weights-------------------------------
    	//System.out.println("=== print obj1DD: ");
    	//obj1DD.display(); 
    	//System.out.println("=== print obj2DD: ");
    	//obj2DD.display(); 
    	//System.out.println("=== print w1dd: ");
    	//w1dd.display(); 
    	//System.out.println("=== print w2dd: ");
    	//w2dd.display(); 
		multiply1 = OP.mult(w1dd, obj1DD);
		multiply2 = OP.mult(w2dd, obj2DD);    	
    	
    	/*DD[] children = costObjectivesDD.getChildren();// number of objectives will be 2, 2 nodes 
    	DD obj1DD = DD.one;
		DD obj2DD= DD.one;
		
		DD[] obj1DDarray = new DD[nStates];
		DD[] obj2DDarray = new DD[nStates];
		int indexObj1 =0; 
		int indexObj2 =0;
		
		int index =0; 
		boolean isExist = false;
		if (children == null || children[0] == null || children[0].getChildren() == null || children.length < 1) {
			isExist = false;
			
		} else {				
			isExist = true;
		}
		if(!isExist&&children != null&&children.length>0)// 
		{
			for(int l=0;l<children.length;l++)
			{
				if(children[l].getChildren()!=null)
				{
					for(int y=0; y<children[l].getChildren().length;y++)
					{
						System.out.println("**" + children[l].getChildren()[y].getVal());
						index++;
						
						obj1DDarray[indexObj1]= DDleaf.myNew(children[l].getChildren()[y].getVal());
						indexObj1++;
					}
				}
				else
				{
					System.out.println("@@" + children[l].getVal());
					index++;
					
					obj2DDarray[indexObj2]= DDleaf.myNew(children[l].getVal());
					indexObj2++;
				}
				
				//costsArray[index]=list[l].getVal();
				index++;
			}
		}
		if(!isExist&&children == null)
		{
			System.out.println("%%" +costObjectivesDD.getVal());
			obj1DDarray[index]= DDleaf.myNew(costObjectivesDD.getVal());
			obj2DDarray[index]=obj1DDarray[index];
		}
		
		if (isExist) 
		{
			
			String[][] mat = new String[children.length][children[0].getChildren().length];

		
			for (int i = 0; i < mat.length; i++) 
			{
				for (int s = 0; s < mat[i].length; s++) 
				{
					
					if(children[i].getChildren()!=null)
					{
						System.out.println("++" + children[i].getChildren()[s].getVal());
						index++;
						
						obj1DDarray[indexObj1]= DDleaf.myNew(children[i].getChildren()[s].getVal());
						indexObj1++;
						
					}else{
						System.out.println("^" + children[i].getVal());
						index++;
						
						obj2DDarray[indexObj2]= DDleaf.myNew(children[i].getVal());
						indexObj2++;
					}
				}
			}
			
		}*/
		
		/*if (obj1DDarray==null)
		{
			obj1DD = DDnode.myNew(1, obj1DDarray);
			
			
		}else
		{
			obj1DD = DDleaf.myNew(obj1DDarray[0].getVal());
		}
    	
		if (obj2DDarray==null)
		{
			obj2DD = DDnode.myNew(1, obj2DDarray);
			
			
		}else
		{
			obj2DD = DDleaf.myNew(obj2DDarray[0].getVal());
		}*/
    	
		//obj2DD = DDnode.myNew(1, obj2DDarray);
		
    	
    	//-------------finally add ------------------------------
    	
    	DD resultAdd = OP.add(multiply1, multiply2);
    	/*System.out.println("--------action: "+a+"---------------");
    	System.out.println("=====================result of multipy1 ");
    	multiply1.display();
    	System.out.println("=====================result of multipy2 ");
    	multiply2.display();
    	System.out.println("=====================result of adding ");
    	resultAdd.display();*/
    	
    	return resultAdd;
    			
    	
    }
    //-----------costObjective (2 objs) -----------------------------------------------------------------------------------------
    public DD[] manuallycostObjectives(){
		DD[] dd1 = new DD[3];//level 1 we have 3 actions 
		DD[] dd2 = new DD [2];//level 2 we have 2 objectives 
		
		DD[] children = new DD[2];// 2 number of states for dd2
		for (int d1=0; d1<dd1.length;d1++)
		{
			for (int d2=0; d2<dd2.length;d2++)
			{
				double [] r = new double [2];
				//rewards 
				if(d1==0 && d2==0)// action 1 obj 1 
					{ r[0]=1; r[1]=1;}
				else if (d1==0 && d2==1)//action 1 obj 2
					{ r[0]=1; r[1]=1;}
				else if (d1==1 && d2==0) //action 2 obj 1 
					{ r [0] =100; r[1]=-10;}
				else if (d1==1 && d2==1)//action 2 obj2 
					{ r [0] =0; r[1]=0;}
				else if (d1==2 && d2==0)//action 3 obj1
					{ r [0] =-10; r[1]=100;}
				else if (d1==2 && d2==1)//action 3 obj 2
					{ r [0] =0; r[1]=0;}
				
				for(int i=0;i<r.length;i++)// 2 is number of leafs
				{
					
					children[i] = DDleaf.myNew(r[i]);
				}
				dd2[d2] = DDnode.myNew(2, children);
				int [] n1= dd2[d2].getVarSet();
				//System.out.println("-----------d2: "+d2+"---------------");
				//dd2[d2].display(); 
			}
			dd1[d1]=DDnode.myNew(1, dd2);
			int [] n2= dd1[d1].getVarSet();
			//System.out.println("-------------d1----------------------");
			//dd1[d1].display();
		}
		
		
		return dd1;
	}
    //ddcost[a].getChildren()[0]
  //DDNode
    	//DDNode---------------action0
    		//DDNode-------------obj0
				//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1
			//DDNode-------------obj1
				//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1
    	//DDNode---------------action1
			//DDNode-------------obj0
    			//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1
			//DDNode-------------obj1
				//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1
    	//DDNode--------------action2
			//DDNode-------------obj0
				//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1
			//DDNode-------------obj1
				//DDLeaf------------reward for s0
				//DDLeaf------------reward for s1    	
    //-------------readFromFile------------------------------------------------------------------------------
    public void readFromFile(String fileName, boolean debb, double [] w) {
	
    // temp weights to check if the scalarized alphamatrix (alphavectors) used in calculating the currentPointBasedValues
    	
    w[0]=1;
    w[1]=0;
    //w[2]=0;
    
    // i need to try other weights to test the scalarization function 
    //w[0]= 0;
    //w[1]=1;
   
    //w[0]=0.5;
    //w[1]=0.5;
    	
    ParseSPUDD rawpomdp = new ParseSPUDD(fileName);
    rawpomdp.parsePOMDP(false);

    costObjective= rawpomdp.costObjective;// i don't think this variable is being used 
    	
	debug=debb;
	
	ignoremore = false;
	addbeldiff = false;
	nStateVars = rawpomdp.nStateVars;
	nObsVars = rawpomdp.nObsVars;
	nVars = nStateVars+nObsVars;
	stateVars = new StateVar[nStateVars];
	obsVars = new StateVar[nObsVars];

	varDomSize = new int[2*(nStateVars+nObsVars)];
	varName = new String[2*(nStateVars+nObsVars)];
	varIndices = new int[nStateVars];
	primeVarIndices = new int[nStateVars];
	obsIndices = new int[nObsVars];
	primeObsIndices = new int[nObsVars];
	

	// set up state variables
	int k=0;
	for (int i=0; i<nStateVars; i++) 
	{
	    stateVars[i] = new StateVar(rawpomdp.valNames.get(i).size(),rawpomdp.varNames.get(i), i);
	    for (int j=0; j<stateVars[i].arity; j++) 
	    {
	    	stateVars[i].addValName(j,((String) rawpomdp.valNames.get(i).get(j)));
	    }
	    // must be indices as in Matlab!
	    varIndices[i] = i+1;
	    primeVarIndices[i] = i+nVars+1;
	    varDomSize[k] = stateVars[i].arity;
	    varName[k++] = stateVars[i].name;
	}

	// set up observation variables
	nObservations = 1;
	obsVarsArity = new int[nObsVars];
	for (int i=0; i<nObsVars; i++) {
	    obsVars[i] = new StateVar(rawpomdp.valNames.get(i+nStateVars).size(),
					    rawpomdp.varNames.get(i+nStateVars),
					    i+nStateVars);
	    for (int j=0; j<obsVars[i].arity; j++) {
		obsVars[i].addValName(j,((String) rawpomdp.valNames.get(nStateVars+i).get(j)));
	    }
	    obsVarsArity[i] = obsVars[i].arity;
	    nObservations = nObservations*obsVars[i].arity;
	    obsIndices[i] = i+nStateVars+1;
	    primeObsIndices[i] = i+nVars+nStateVars+1;
	    varDomSize[k] = obsVars[i].arity;
	    varName[k++] = obsVars[i].name;
	}
	for (int i=0; i<nStateVars; i++) {
	    varDomSize[k] = stateVars[i].arity;
	    varName[k++] = stateVars[i].name+"_P";
	}
	for (int i=0; i<nObsVars; i++) {
	    varDomSize[k] = obsVars[i].arity;
	    varName[k++] = obsVars[i].name+"_P";
	}

	// set up Globals
	Global.setVarDomSize(varDomSize);
	Global.setVarNames(varName);

	for (int i=0; i<nStateVars; i++) {
	    Global.setValNames(i+1,stateVars[i].valNames);
	}
	for (int i=0; i<nObsVars; i++) {
	    Global.setValNames(nStateVars+i+1,obsVars[i].valNames);
	}
	for (int i=0; i<nStateVars; i++) {
	    Global.setValNames(nVars+i+1,stateVars[i].valNames);
	}
	for (int i=0; i<nObsVars; i++) {
	    Global.setValNames(nVars+nStateVars+i+1,obsVars[i].valNames);
	}

	// set up dynamics
	nActions = rawpomdp.actTransitions.size();
	actions = new Action[nActions];
	uniquePolicy = new boolean[nActions];

	qFn = new DD[nActions];
	//-----------Step 1: get the rewards manually for multiple objectives --------------
	//DD[] ddcost=manuallycostObjectives();
	//----------Step 2: have the weights as DDs ----------------------------------------
	ddweights = convertDoubleAtoDD(w);
	DD d= ddweights[0];//it is only one node
	//d.display();// the one node with two leaves 
	//System.out.println("=========display w1dd and w2dd===========");
	if(d.getChildren()==null)
	{
		w1dd=d;
		w2dd=d;
		//w3dd=d;
	}else
	{
	 w1dd = d.getChildren()[0]; //w1dd.display();//DD of w1
	 w2dd = d.getChildren()[1]; //w2dd.display();//DD of w2
	 //w3dd = d.getChildren()[2];
	}
	 //System.exit(200);
	//----------Step 3: scalarize from multiple objective into one objective------------
	DD []childs = new DD[2];
	childs[0]=DDleaf.myNew(1);
	childs[1]=DDleaf.myNew(0);
	//childs[2]=DDleaf.myNew(-5);
	//rawpomdp.reward =DDnode.myNew(1, childs);
	//System.out.println("size rewardObjectives:" +rawpomdp.rewardObjectives.size());
	//rawpomdp.rewardObjectives.firstElement().display();
	//System.exit(200);
	 //multiply and add 
	costObjectivesDDarray = new DD [nActions];
	for (int a=0; a<nActions; a++) {
		System.out.println("************action:"+a+"**************");
		costObjectivesDD=rawpomdp.CostObjectives.get(a);
		costObjectivesDDarray[a]= rawpomdp.CostObjectives.get(a);// this to save the multi-objective rewads (not scalarized)
		
	    actions[a] = new Action(rawpomdp.actNames.get(a));
	    actions[a].addTransFn(rawpomdp.actTransitions.get(a));
	    actions[a].addObsFn(rawpomdp.actObserve.get(a));
	    actions[a].rewFn = OP.sub(rawpomdp.reward,rawpomdp.actCosts.get(a));
	    actions[a].buildRewTranFn();
	    actions[a].rewFn = OP.addMultVarElim(actions[a].rewTransFn,primeVarIndices);
	    
	    //----added this part to save the not scalarized reward matrix---used to be in dpBackup
	    //System.out.println("rewardObjectives:");
	    //rawpomdp.rewardObjectives.firstElement().display();
	    
	    //System.out.println("CostObjectives:");
	    //rawpomdp.CostObjectives.get(a).display();
	    if(rawpomdp.rewardObjectives==null || rawpomdp.rewardObjectives.isEmpty()){
	    
	    	rawpomdp.rewardObjectives.add(DDleaf.myNew(0));
	    	
	    }
	    rawpomdp.rewardObjectives.firstElement().display();
	    //System.exit(200);
	    	
	    actions[a].costObj=OP.sub(rawpomdp.rewardObjectives.firstElement(),rawpomdp.CostObjectives.get(a));
	    System.out.println("costObj:");
	    actions[a].costObj.display();
	    actions[a].buildcostObjTranFn();
	    actions[a].costObj= OP.addMultVarElim(actions[a].costObjTransFn,primeVarIndices);
	    System.out.println("costObj:");
	    actions[a].costObj.display();
	    //actions[a].costObj.setVar(1);
	    //actions[a].costObj.getVarSet();
 
	    // find stochastic transitions (and deterministic varMappings)
	    // not done yet - where is this used? 
	}
	//System.exit(200);
	// discount factor
	discFact = rawpomdp.discount.getVal();
	
	// make a DD version
	ddDiscFact = DDleaf.myNew(discFact);


	// the adjunct models
	nAdjuncts = rawpomdp.adjuncts.size();
	if (nAdjuncts > 0) {
	    adjuncts = new DD[nAdjuncts];
	    adjunctNames = new String[nAdjuncts];
	    for (int a=0; a<nAdjuncts; a++) {
		adjuncts[a] = rawpomdp.adjuncts.get(a);
		adjunctNames[a] = rawpomdp.adjunctNames.get(a);
	    }
	}
	
	// max reward value
	

	double maxVal = Double.NEGATIVE_INFINITY;
	double minVal = Double.POSITIVE_INFINITY;
	for (int a=0; a<nActions; a++) {
	    maxVal = Math.max(maxVal,OP.maxAll(OP.addN(actions[a].rewFn)));
	    minVal = Math.min(minVal,OP.minAll(OP.addN(actions[a].rewFn)));
	}
	maxRewVal = maxVal/(1-discFact);
	// tolerance
	if (rawpomdp.tolerance == null) {
	    double maxDiffRew = maxVal - minVal;
	    double maxDiffVal = maxDiffRew/(1-Math.min(0.95,discFact));
	    tolerance = 1e-5*maxDiffVal;
	} else {
	    tolerance = rawpomdp.tolerance.getVal();
	}
	// initial belief
	initialBelState = rawpomdp.init;

	// factored initial belief state
	initialBelState_f = new DD[nStateVars];
	for (int varId=0; varId<nStateVars; varId++) {
	    initialBelState_f[varId] = OP.addMultVarElim(initialBelState,MySet.remove(varIndices,varId+1));
	}
	
    }
    public DD beliefUpdate(DD belState, int actId, String [] obsnames) {
	if (obsnames.length != nObsVars) 
	    return null;
	int [] obsvals = new int[obsnames.length];
	for (int o=0; o<obsnames.length; o++) {
	    obsvals[o] = findObservationByName(o,obsnames[o])+1;
	    if (obsvals[o] < 0) 
		return null;
	}
	return beliefUpdate(belState, actId, obsvals);
    }
    
    public DD beliefUpdate(DD belState, int actId, int [] obsvals) {
	return beliefUpdate(belState,actId,stackArray(primeObsIndices,obsvals));
    }
    public DD beliefUpdate(DD belState, int actId, int [][] obsvals) {
	double [] zerovalarray = new double[1];

	DD [] restrictedObsFn = OP.restrictN(actions[actId].obsFn,obsvals); 
	DD nextBelState = OP.addMultVarElim(concatenateArray(belState,actions[actId].transFn,restrictedObsFn),varIndices);
	nextBelState = OP.primeVars(nextBelState,-nVars);
	DD obsProb = OP.addMultVarElim(nextBelState, varIndices);
	if (obsProb.getVal() < 1e-8) {
	    System.out.println("WARNING: Zero-probability observation, resetting belief state to a uniform distribution");
	    nextBelState = DD.one;
	}
	nextBelState = OP.div(nextBelState,OP.addMultVarElim(nextBelState, varIndices));
	return nextBelState;
    }
    public DD getGoalDD() {
	double [] onezero = {0};
	// get the best reward over all actions

	DD [] therewfns = new DD[nActions];
	for (int a=0; a<nActions; a++) {
	    therewfns[a] = actions[a].rewFn;
	}
	// max reward available at each state
	DD themaxdd = OP.maxN(therewfns);
	// need to do this to avoid rounding errors 
	themaxdd = OP.approximate(themaxdd,1e-6,onezero);

	// get the max of that
	double themaxmax = OP.maxAll(themaxdd);
	
	
	DD themaxmaxdd = DDleaf.myNew(themaxmax);

	// threshold the max dd
	DD goalDD = OP.threshold(themaxdd,themaxmax,0);
	goalDD = OP.div(goalDD,themaxmaxdd);
	// onlymaxdd has a 1 in the goal state, 0 everywhere else
	return goalDD; 	
    }
    public boolean checkGoal(DD goalDD, DD belState, double threshold) {
	DD belAtGoal = OP.addMultVarElim(concatenateArray(belState,goalDD),varIndices);
	// this should be a Leaf DD
	double goalbel = ((DDleaf) belAtGoal).getVal();
	System.out.println("Goal Bel is "+goalbel);
	return (goalbel > threshold);
    }

    public double [] evaluateObservations(int independentfeatures) {
	return evaluateObservations(independentfeatures,false);
    }
    public double [] evaluateObservations(int independentfeatures, boolean usemax) {
	// the old method
	if (independentfeatures < 2) {
	    return evaluateObservations(usemax);
	}
	// computes a fitness score for all the observation variables based on
	// their ability to distinguish between conditional plans
	// return value is a double array where obsfit[i] is the fraction of
	// belief-action pairs that at least one value of observation variable i disagrees
	// on the conditional plan to follow.  If this is close to 1, this is a very
	// good observation variable
	double [] obsfit = new double[nObsVars];
	int [][] obsval = new int[2][nObsVars];
	DD [] restrictedObsFn;
	int i,ii;
	double [] maxbval;
	DD tObsFn;
	DD [] nextBelState;
	double bval, maxvdiff, obscount, vdiff, maxvavg, totba;
	int maxa, currmaxa;
	boolean agree, done, onedone;
	double agreedegree;
	for (i=0; i<nObsVars; i++) {
	    obsval[0][i]=primeObsIndices[i];
	}
	for (i=0; i<nObsVars; i++) {
	    obsfit[i] = 0.0;
	    obscount = 0;
	    //System.out.println("--------------- checking observation "+i);
	    // reset all obsvar values in obsval
	    for (ii=0; ii<nObsVars; ii++) 
		obsval[1][ii]=1;
	    done = false;
	    totba = 0;
	    while (!done) {
		for (int actId=0; actId<nActions; actId++) {
		    // eliminate all other observation variables from this observation function
		    tObsFn = OP.addMultVarElim(actions[actId].obsFn, MySet.remove(primeObsIndices,primeObsIndices[i]));
		    
		    //System.out.println("tObsFn for i "+i+" is .........");
		    //tObsFn.display();
		    for (int b=0; b<belRegion.length; b++) {
			agree = true;
			currmaxa=0;
			agreedegree = 0.0;
			maxbval = new double[obsVars[i].arity];
			nextBelState = new DD[obsVars[i].arity];
			for (int j=0; agree && j<obsVars[i].arity; j++) {
			    // update the belief b on action actId based only on jth value of ith observation
			    // first compute a restricted observation function
			    obsval[1][i] = j+1;
			    restrictedObsFn = OP.restrictN(tObsFn,obsval); 
			    //System.out.println(" obs var "+i+"  actId "+actId+" belief "+b+" j "+j+" primeObsIndex "+obsval[0][0]+"  value "+obsval[1][0]);
			    //tObsFn.display();
			    //restrictedObsFn[0].display();
			    nextBelState[j] = OP.addMultVarElim(concatenateArray(belRegion[b],actions[actId].transFn,restrictedObsFn),varIndices);
			    nextBelState[j] = OP.primeVars(nextBelState[j],-nVars);
			    DD obsProb = OP.addMultVarElim(nextBelState[j], varIndices);
			    if (obsProb.getVal() < 1e-8) 
				nextBelState[j] = DD.one;
			    nextBelState[j] = OP.div(nextBelState[j],OP.addMultVarElim(nextBelState[j], varIndices));
			    //nextBelState[j].display();
			    maxbval[j] = Double.NEGATIVE_INFINITY;
			    maxa=0;
			    for (int a=0; a<alphaVectors.length; a++) {
				bval = OP.dotProduct(nextBelState[j],alphaVectors[a],varIndices);
				//System.out.println("a "+a+"  bval "+bval);
				if (bval > maxbval[j]) {
				    maxbval[j] = bval;
				    maxa = a;
				}
			    }
			    if (j==0)
				currmaxa = maxa;
			    agree = agree && (maxa==currmaxa);
			}
			// find largest difference in value and belief
			maxvdiff = Double.NEGATIVE_INFINITY;
			maxvavg = 0;
			double bdist, maxbdist;
			maxbdist = 0.0;
			for (int j=0; j<obsVars[i].arity; j++) {
			    for (int k=j+1; k<obsVars[i].arity; k++) {
				bdist = OP.maxAll(OP.abs(OP.sub(nextBelState[j],nextBelState[k])));
				if (bdist > maxbdist) 
				    maxbdist = bdist;
				//System.out.println(" value of each "+maxbval[j]+" "+maxbval[k]);
				vdiff = Math.abs(maxbval[j])+Math.abs(maxbval[k]);
				if (vdiff > 0) 
				    vdiff = Math.abs(maxbval[j]-maxbval[k])/vdiff;
				else
				    vdiff = 0.0;
				

				//vdiff = Math.abs(maxbval[j]-maxbval[k])/(Math.abs(maxbval[j])+Math.abs(maxbval[k]));
				if (vdiff > maxvdiff) 
				    maxvdiff = vdiff;
			    }
			}
			if (addbeldiff)
			    obscount += maxbdist;
			if (!agree) {
			    //System.out.println("they don't agree! on "+b+"  "+actId);
			    obscount++;
			} else {
			    // they do agree, but still might give large value differences
			    //System.out.println("they agree! on "+b+"   "+actId+ " with value "+maxvdiff+" and bdiff "+maxbdist);
			    obscount += maxvdiff;
			}
			totba++;
		    }
		}
		// increment observations by one
		ii=0;
		onedone = false;
		while (!onedone) {
		    if (ii != i && ii < nObsVars) {
			if (obsval[1][ii]==obsVars[ii].arity) {
			    ii++;
			} else {
			    obsval[1][ii]++;
			    onedone =true;
			}
		    }
		    if (ii == i) {
			ii++;
		    }
		    if (ii == nObsVars) {
			onedone = true;
			done = true;
		    }
		}
	    }
	    obsfit[i] = obscount/totba;
	    //System.out.println("obsfit["+i+"]="+obsfit[i]+" ... "+obscount+"   "+totba);
	}
	return obsfit;

	
    }
    public double [] evaluateObservations() {
	return evaluateObservations(false);
    }
    public double [] evaluateObservations(boolean usemax) {
	// computes a fitness score for all the observation variables based on
	// their ability to distinguish between conditional plans
	// return value is a double array where obsfit[i] is the fraction of
	// belief-action pairs that at least one value of observation variable i disagrees
	// on the conditional plan to follow.  If this is close to 1, this is a very
	// good observation variable
	double [] obsfit = new double[nObsVars];
	int [][] obsval = new int[2][1];
	DD [] restrictedObsFn;
	double [] maxbval;
	DD tObsFn;
	DD [] nextBelState;
	double bval, maxvdiff, obscount, vdiff, maxvavg, totba;
	int maxa, currmaxa;
	boolean agree;
	double agreedegree;
	totba = nActions*belRegion.length;
	for (int i=0; i<nObsVars; i++) {
	    obsfit[i] = 0.0;
	    obscount = 0;
	    //System.out.println("--------------- checking observation "+i);
	    for (int actId=0; actId<nActions; actId++) {
		// eliminate all other observation variables from this observation function
		tObsFn = OP.addMultVarElim(actions[actId].obsFn, MySet.remove(primeObsIndices,primeObsIndices[i]));
		
		//System.out.println("tObsFn for i "+i+" is .........");
		//tObsFn.display();
		for (int b=0; b<belRegion.length; b++) {
		    agree = true;
		    currmaxa=0;
		    agreedegree = 0.0;
		    maxbval = new double[obsVars[i].arity];
		    nextBelState = new DD[obsVars[i].arity];
		    for (int j=0; agree && j<obsVars[i].arity; j++) {
			// update the belief b on action actId based only on jth value of ith observation
			// first compute a restricted observation function
			obsval[0][0] = primeObsIndices[i];
			obsval[1][0] = j+1;
			restrictedObsFn = OP.restrictN(tObsFn,obsval); 
			//System.out.println(" obs var "+i+"  actId "+actId+" belief "+b+" j "+j+" primeObsIndex "+obsval[0][0]+"  value "+obsval[1][0]);
			//tObsFn.display();
			//restrictedObsFn[0].display();
			nextBelState[j] = OP.addMultVarElim(concatenateArray(belRegion[b],actions[actId].transFn,restrictedObsFn),varIndices);
			nextBelState[j] = OP.primeVars(nextBelState[j],-nVars);
			DD obsProb = OP.addMultVarElim(nextBelState[j], varIndices);
			if (obsProb.getVal() < 1e-8) 
			    nextBelState[j] = DD.one;
			nextBelState[j] = OP.div(nextBelState[j],OP.addMultVarElim(nextBelState[j], varIndices));
			//nextBelState[j].display();
			maxbval[j] = Double.NEGATIVE_INFINITY;
			maxa=0;
			for (int a=0; a<alphaVectors.length; a++) {
			    bval = OP.dotProduct(nextBelState[j],alphaVectors[a],varIndices);
			    //System.out.println("a "+a+"  bval "+bval);
			    if (bval > maxbval[j]) {
				maxbval[j] = bval;
				maxa = a;
			    }
			}
			if (j==0)
			    currmaxa = maxa;
			agree = agree && (maxa==currmaxa);
		    }
		    // find largest difference in value and belief
		    maxvdiff = Double.NEGATIVE_INFINITY;
		    maxvavg = 0;
		    double bdist, maxbdist;
		    maxbdist = 0.0;
		    for (int j=0; j<obsVars[i].arity; j++) {
			for (int k=j+1; k<obsVars[i].arity; k++) {
			    bdist = OP.maxAll(OP.abs(OP.sub(nextBelState[j],nextBelState[k])));
			    if (bdist > maxbdist) 
				maxbdist = bdist;
			    //System.out.println(" value of each "+maxbval[j]+" "+maxbval[k]);
			    //vdiff = Math.abs(maxbval[j]-maxbval[k]);
			    //if (vdiff > 0) 
			    vdiff = Math.abs(maxbval[j])+Math.abs(maxbval[k]);
			    if (vdiff > 0) 
				vdiff = Math.abs(maxbval[j]-maxbval[k])/vdiff;
			    else
				vdiff = 0.0;
				//vdiff = Math.abs(maxbval[j]-maxbval[k])/(Math.abs(maxbval[j])+Math.abs(maxbval[k]));
			    if (vdiff > maxvdiff) 
				maxvdiff = vdiff;
			}
		    }
		    if (!addbeldiff)
			maxbdist=0.0;

		    if (usemax) {
			if (!agree) {
			    obscount = Math.max(obscount,maxbdist+1);
			} else {
			    obscount = Math.max(obscount,maxbdist+maxvdiff);
			}
		    } else {
			if (addbeldiff)
			    obscount += maxbdist;
			if (!agree) {
			    //System.out.println("they don't agree! on "+b+"  "+actId);
			    obscount++;
			} else {
			    // they do agree, but still might give large value differences
			    //System.out.println("they agree! on "+b+"   "+actId+ " with value "+maxvdiff+" and bdiff "+maxbdist);
			    obscount += maxvdiff;
			}
		    }
		}
	    }
	    obsfit[i] = obscount/totba;
	    //System.out.println("obsfit["+i+"]="+obsfit[i]+" ... "+obscount+"   "+totba);
	}
	return obsfit;
    }
  

  // a heuristic policy for the handwashing problem
    public int policyQuery(DD belState, boolean heuristic) {
	if (!heuristic) {
	    return policyQuery(belState);
	} 
	/*
	  (engaged no confused yes)
	  (colrespond  yes no)
	  (cuerespond yes no)
	  (completed yes no))
	  
	  0 action resetchange
	  1 give_motiv_prompt
	  2 add_color
	  3 nothing
	  
	*/
	double lookingp = getSingleValue(belState,1,0);
	double engagedyp = getSingleValue(belState,2,2);
	double engagedcp = getSingleValue(belState,2,1);
	double colrespondp = getSingleValue(belState,3,0);
	double cuerespondp = getSingleValue(belState,4,0);
	double completedp = getSingleValue(belState,5,0);
	
	if (engagedyp > 0.5) {
	    return 3;
	} else {
	    if (lookingp > 0.7) {
		if (completedp > 0.9) {
		    return 0;
		}
		if (colrespondp > 0.7) {
		    return 2;
		} 
	    } else {
		if (cuerespondp > 0.7) {
		    return 1;
		} else {
		    if (completedp > 0.9) {
			return Global.random.nextInt(nActions);
		    } else {
			return 1+Global.random.nextInt(nActions-1);
		    }
		}
	    }
	}
	return 0;
    }
    public int policyQuery(DD belState) {
	return policyQuery(belState,alphaVectors,policy);
    }
    public int policyQuery(DD[] belState) {
	return policyQuery(belState,alphaVectors,policy);
    }
    public int findActionByName(String aname) {
	for (int a=0; a<nActions; a++) {
	    if (aname.equalsIgnoreCase(actions[a].name))
		return a;
	}
	return -1;
    }
    public int findObservationByName(int ob, String oname) {
	for (int o=0; o<obsVars[ob].arity; o++) {
	    if (oname.equalsIgnoreCase(obsVars[ob].valNames[o]))
		return o;
	}
	return -1;
    }
    public int policyQuery(DD belState, DD [] alphaVectors, int [] policy) {
	//single DD belief state
	double bestVal = Double.NEGATIVE_INFINITY;
	double val;
	int bestAlphaId=0,bestActId;
	for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
	    val = OP.dotProduct(belState, alphaVectors[alphaId], varIndices);
	    if (val > bestVal) {
		bestVal = val; 
		bestAlphaId = alphaId;
	    }
	}
	bestActId = policy[bestAlphaId];
	return bestActId;
    }
    public int policyQuery(DD [] belState, DD [] alphaVectors, int [] policy) {
	// factored DD belief state
	double [] values = OP.factoredExpectationSparseNoMem(belState,alphaVectors);
	double bestVal = Double.NEGATIVE_INFINITY;
	double val;
	int bestAlphaId=0,bestActId;
	for (int alphaId = 0; alphaId<alphaVectors.length; alphaId++) {
	    if (values[alphaId] > bestVal) {
		bestVal = values[alphaId]; 
		bestAlphaId = alphaId;
	    }
	}
	bestActId = policy[bestAlphaId];
	return bestActId;
    }
    
    public double evalBeliefStateQMDP(DD belState) {
	return evalBeliefState(belState,qFn,qPolicy);
	
    }
    public double evalBeliefState(DD belState) {
	return evalBeliefState(belState,alphaVectors, policy);
    }
    public double evalBeliefState(DD belState, DD [] alphaVectors, int [] policy) {
	double bestVal = Double.NEGATIVE_INFINITY;
	double val;
	int bestAlphaId=0,bestActId;
	for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
	    val = OP.dotProduct(belState, alphaVectors[alphaId], varIndices);
	    if (val > bestVal) {
		bestVal = val; 
		bestAlphaId = alphaId;
	    }
	}
	bestActId = policy[bestAlphaId];
	return bestVal;
    }
    public double findSimilarFactBelief(DD [] belState, DD [][] belRegion, int count) {
	return findSimilarFactBelief(belState,belRegion,count,0.001);
    }
    public double findSimilarFactBelief(DD [] belief, DD [][] belSet, int count, double threshold) {
	double smallestDist = Double.POSITIVE_INFINITY;
	int closestBelId = 1;
	double maxnorm,dist;
	boolean done1, done2;
	done1=false;
	for (int i=0; !done1 & i<count; i++) {
	    maxnorm = Double.NEGATIVE_INFINITY;
	    done2=false;
	    for (int varId=0; !done2 & varId<belief.length; varId++) {
		dist = OP.maxAll(OP.abs(OP.sub(belSet[i][varId],belief[varId])));
		if (dist > maxnorm) {
		    maxnorm = dist;
		    if (maxnorm >= smallestDist) {
			done2=true;
		    }
		}
	    }
	    if (maxnorm < smallestDist) {
		smallestDist = maxnorm;
		closestBelId = i;
		if (smallestDist <= threshold) {
		    done1=true;
		}
	    }
	}
	return smallestDist;
    }
    public double computeAdjunctValue(DD belState, String adjunctName) throws Exception {
	// find this adjunct to see if it exists
	int a=0;
	while (a<nAdjuncts && !adjunctNames[a].equalsIgnoreCase(adjunctName)) 
	    a++;
	if (a>=nAdjuncts) 
	    throw new Exception();
	// compute the dot product with the belief state
	double val = OP.dotProduct(belState,adjuncts[a],varIndices);
	return val;
    }
    public double getSingleValue(DD belState, int varId, int varVal) 
    {
	DD fbs = OP.addMultVarElim(belState,MySet.remove(varIndices,varId+1));
	int vid = fbs.getVar();
	if (vid == 0) {
	    // its already a leaf
	    return fbs.getVal();
	} else {
	    DD [] fbsc = fbs.getChildren();
	    return fbsc[varVal].getVal();
	}	
    }
    public void printBeliefState(DD belState) {
	// first factor it
	DD[] fbs = new DD[nStateVars];
	for (int varId=0; varId<nStateVars; varId++) {
	    fbs[varId] = OP.addMultVarElim(belState,MySet.remove(varIndices,varId+1));
	}
	printBeliefState(fbs);
    }
    public void printBeliefState(DD [] belState) {
	for (int j=0; j<belState.length; j++) {
	    belState[j].display();
	}
    }
    public void printBelRegion()
    {
	for (int i=0; i<belRegion.length; i++) {
	    System.out.println("belief "+i+":");
	    for (int j=0; j<belRegion[i].length; j++) {
		belRegion[i][j].display();
	    }
	}
    }
    public void setBelRegionFromData(int maxSize, double threshold, int [][] a, int [][][] o) {
	int count;
	int choice, actId;
	double distance;

	DD [] nextBelState = new DD[nStateVars];
	DD [] restrictedObsFn;
	DD [] belState;
	int [][] obsConfig;

	double [] zerovalarray = new double[1];
	zerovalarray[0]=0;

	DD [][] tmpBelRegion = new DD[maxSize][];

	count = 0;
	int numtries=0;
	tmpBelRegion[count] = new DD[initialBelState_f.length];

	System.arraycopy(initialBelState_f, 0, tmpBelRegion[count], 0, initialBelState_f.length);
	for (int epindex=0; count < maxSize && epindex<a.length; epindex++) {
	    belState = initialBelState_f;
	    for (int stepId=0; count < maxSize && stepId<a[epindex].length; stepId++) {
		//get action from data
		actId =  a[epindex][stepId];
		obsConfig = stackArray(primeObsIndices,o[epindex][stepId]);
		restrictedObsFn = OP.restrictN(actions[actId].obsFn,obsConfig);
		
		// update belState
		
		for (int varId = 0; varId<nStateVars; varId++) {
		    nextBelState[varId] = OP.addMultVarElim(concatenateArray(belState,actions[actId].transFn,restrictedObsFn),concatenateArray(MySet.remove(primeVarIndices,varId+nVars+1),varIndices));
		    nextBelState[varId] = OP.approximate(nextBelState[varId], 1e-6, zerovalarray);
		    nextBelState[varId] = OP.div(nextBelState[varId],OP.addMultVarElim(nextBelState[varId],primeVarIndices[varId]));
		}
		
		belState = OP.primeVarsN(nextBelState,-nVars);

		//add belState to tmpBelRegion
		distance = findSimilarFactBelief(belState, tmpBelRegion, count+1, threshold);
		//System.out.println("distance "+distance);
		if (!debug && distance > threshold) {
		    count = count + 1;
		    if (count < maxSize) {
			//System.out.println("bel State : count "+count+" distance "+distance+" threshold "+threshold);

			tmpBelRegion[count] = new DD[belState.length];
			System.arraycopy(belState, 0, tmpBelRegion[count], 0, belState.length);
			if (count%10 == 0)
			    System.out.println(" "+count+" belief states sampled"); 
		    }
		}
		Global.newHashtables();
	    }
	    //System.out.println("resetting to initial belief - "+count+" belief states so far");
	}
	// copy over 
	if (count < maxSize)
	    count = count + 1; // means we never found enough, so count is one less than total we found
	System.out.println("finished sampling  "+count+" belief states  "+tmpBelRegion.length);
	belRegion = new DD[count][];
	for (int i=0; i<count; i++) {
	    belRegion[i] = new DD[tmpBelRegion[i].length];
	    System.arraycopy(tmpBelRegion[i], 0, belRegion[i], 0,tmpBelRegion[i].length);
	}

	
    }
    public void simulateGeneric(int nits) 
    {
	
	// do simulation
	DD belState = initialBelState;
	int actId;
	String [] obsnames = new String[nObsVars];
	String inobs, inact;  
	InputStreamReader cin = new InputStreamReader(System.in);
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    
	    while (nits > 0) {
		System.out.println("current belief state: ");
		printBeliefState(belState);
		if (alphaVectors != null && alphaVectors.length>0) {
		    actId = policyQuery(belState);
		    System.out.println("action suggested by policy: "+actId+" which is "+actions[actId].name);
		} else {
		    System.out.print("enter action :");
		    inact = in.readLine();
		    actId = findActionByName(inact);
		}		    
		for (int o=0; o<nObsVars; o++) {
		    System.out.print("enter observation "+obsVars[o].name+": ");
		    inobs = in.readLine();
		    obsnames[o]=inobs;
		}
		belState = beliefUpdate(belState,actId,obsnames);
		nits--;
	    }
	}  catch (IOException e) {
	}
    }
    //-------------------convertAlphaMatrix2AlphaVector_2D---------------------------------------------------
    /*
     * this function is converting alphaMatrix to alphavector_2d to be used to multiply with belief (3x1)
     * listArrayAlphaMatrix.add---> alphaVectors_2D (2x3) 
     * listArrayAlphaMatrix_3x2 is used for scalarization (3x2) it will be multiplied with weights(2x1)
     * 
     */
    
    //-----------------convertBeliefSampleslistArraytoDD-----------------------------------------------------
    public DD[][] convertBeliefSampleslistArraytoDD (ArrayList<double[]> beliefSamples)
    {
 	   DD [][] belRegTemp = new DD [beliefSamples.size()][beliefSamples.get(0).length];
 	   for (int e=0; e<beliefSamples.size();e++)
 	   {
 		   double[] b = beliefSamples.get(e);
 		   belRegTemp[e]= convertDoubleAtoDD(b);
 		   
 		   //--------Testing-----------------------
 		   
 		 /* System.out.println("**********************************element: "+ e);
 		  System.out.println("---- from OLSAR-----");
 		  for (int z=0; z<b.length;z++)
 		  {
 			  System.out.print(b[z]+" , ");
 		  }
 		  System.out.println();
 		  System.out.println("---- DD version-----");
 		  DD d = belRegTemp[e][0];
 		  d.display();
 		  */
 		   //--------end Testing-------------------
 		   
 	   }
 	   
 	   return belRegTemp;
    }
    //----------------multiplyALphaMatrixAndBeliefState--------------------------------------
    public DD[] multiplyALphaMatrixAndBeliefState(DD[][]alphaMatrix, DD[]beliefState)
    {
    	DD[] resultMultiply = new DD[1];
    	//System.out.println(alphaMatrix.length);//=2
    	//System.out.println(alphaMatrix[0].length);//=1 if .getChildren()=3 
		DD[] children = new DD[alphaMatrix.length];
		
		//children[i] = DDleaf.myNew(w[i]);
		for(int c =0; c<children.length;c++)
		{
			//alphaMatrix[c][0].display();
			//beliefState[0].display();
			DD resultdd = OP.mult(alphaMatrix[c][0], beliefState[0]);
			DD d = DD.zero;
			for (int i =0; i<resultdd.getChildren().length;i++)
			{
				d = OP.add(d, resultdd.getChildren()[i]);
				//d.display();
			}
			children[c]= d;
			children[c].display();
		}
	
	   resultMultiply[0] = DDnode.myNew(1, children);
	   int [] n1= resultMultiply[0].getVarSet();
	   
		return resultMultiply;
    	
    }
    //--------------scalarizeAlphaMatrix--------------------
    /*
     * thhat will scalarize the alphamatrix initial one we shouldnt use this in inner loops 
     * alphaVectors is the scalarized version of alphaMatrixList
     */
    public void scalarizeAlphaMatrix(ArrayList<DD[][]> listAlphaMatrix)
    {
    	
    	//listAlphaMatrix = listArrayAlphaMatrix_3x2;
    	alphaVectors = new DD[listAlphaMatrix.size()];
    	DD [] scalarizedAlphas = new DD [listAlphaMatrix.size()];
    	for (int l=0; l<listAlphaMatrix.size();l++)
    	{
    		/*\
    		 * the alphaVectors[l] = DDnode.myNew(1, children); is wrong and now we will fix it in the for loop below 
    		 */
    		//TODO: this is not generalized
			/*DD[] childrenObj1 = new DD[3];
			DD[] childrenObj2 = new DD[3];
    		for (int x=0; x<listAlphaMatrix.get(l).length;x++)// this has 3 length each has 2 childs 
    		{
    			
    			
    			for (int q=0; q<listAlphaMatrix.get(l)[x].length;q++)// this is supposed to be 2 size 
    			{
    				if (q==0)
    					childrenObj1[q]=listAlphaMatrix.get(l)[x][q].getChildren()[q];
    				if (q==1)
    					childrenObj2[q]=listAlphaMatrix.get(l)[x][q].getChildren()[q];
    			}
    			
    		}
    		alphaVectors[0] = DDnode.myNew(1, childrenObj1);
    		alphaVectors[1] = DDnode.myNew(1, childrenObj2);
    		*/
    		//TODO: i am not sure of this scalarization and i don't think we use it for the multi-obj algorithm we created
    		//TODO: double check if we use it or not
    		DD[][] alphaMatrix = listAlphaMatrix.get(l);
    		
    		DD[] children = new DD[alphaMatrix.length];//3
    		
    		//children[i] = DDleaf.myNew(w[i]);
    		for(int c =0; c<children.length;c++)
    		{
    			DD resultAdd = DD.zero; 
    			if(ddweights[0].getChildren()!=null)
    			{
    				for (int we=0; we<ddweights[0].getChildren().length;we++)
        			{
    				
    					if ( alphaMatrix[c][0].getChildren()!=null) //number of objectives always same number of weights
    					{
    						DD resultdd = OP.mult(alphaMatrix[c][0].getChildren()[we], ddweights[0].getChildren()[we]);
        					resultAdd = OP.add(resultAdd, resultdd);
    					}else
    					{
    						DD resultdd = OP.mult(alphaMatrix[c][0], ddweights[0].getChildren()[we]);
        					resultAdd = OP.add(resultAdd, resultdd);
    					}
    					
    					//resultAdd.display();
    					
        			}
        			
    			}else
    			{
    				//weights are the same so one leaf
    				
    				DD resultdd = OP.mult(alphaMatrix[c][0], ddweights[0]);
					resultAdd = OP.add(resultAdd, resultdd);
    			}
    			
    			children[c]=resultAdd;
    			
    		}
    			
    		//scalarizedAlphas[l] = DDnode.myNew(1, children);
    		alphaVectors[l] = DDnode.myNew(1, children);
    		int [] n1 = alphaVectors[l].getVarSet();
    	}

    	
    	
    	//return scalarizedAlphas;

    }
    //----------------------------------------------------------------
    /*
     * May 2018 
     * scalarizing alpha matrix 
     */
    /*public static DD scalarizeAlphaMatri(DD AlphaMatrix)
    {
    	//--------test weights----------------
    	DD[] ddweights = new DD[1];
    	DD[] children_w = new DD[2];
    	children_w[0] = DDleaf.myNew(1);
    	children_w[1] = DDleaf.myNew(0);
		DD dd1 = DDnode.myNew(1, children_w);
		ddweights[0]=dd1;
		//------------------------------------
    
    	DD scalarizedAlphas = DD.zero;

		
	if(AlphaMatrix.getChildren()!=null)// if alphavector does not has only one leaf instrad it has children 
	{
		DD resultAdd_ = DD.zero;
		DD alphaChildren [] = AlphaMatrix.getChildren(); //number of children (level 1) = number of states 
		DD [] children = new DD [alphaChildren.length];
		for(int c=0; c<alphaChildren.length; c++)
		{
			DD alphavecChild = alphaChildren[c]; 
			
			if (alphavecChild.getChildren()!=null)// level 1 child
			{
				DD alphavecChild_n []= alphavecChild.getChildren();//objectives 
				DD resultAdd = DD.zero; // the DD after scalarize the multiple objectives 
				for (int a =0; a<alphavecChild_n.length;a++)
				{
					DD alphavecChild_n_ = alphavecChild_n[a];
					if(alphavecChild_n_.getChildren()!=null)//now we go to the leafs 
					{
						//now here multiply the alphavecChild_n_ with the first weight 
						//second round multiply by second weight
			    		
			    		//for (int we=0; we<ddweights[0].getChildren().length;we++)
			    		{
			    			if(ddweights[0].getChildren()!=null)
	                		{
	    						DD resultdd = OP.mult(alphavecChild_n_, ddweights[0].getChildren()[a]);
	    						resultAdd = OP.add(resultAdd, resultdd);
	            			
	                		}else
	                		{	
	                			DD resultdd = OP.mult(alphavecChild_n_, ddweights[0]);
	                			resultAdd = OP.add(resultAdd, resultdd);
	                		}
			    		}
					}else
					{
						resultAdd= alphavecChild_n_; 
						
					}
					children[c]=resultAdd; 
					//scalarizeAlphavectors[s].getChildren()[c] = resultAdd; 
					//sum the output of the multiply here 
					//add it to the DD result 
					
				}
				
			}else
			{
				System.out.println("level 1 child doesn't have children it is just one leaf so we keep the leaf and don't scalarize with weights");
				 
				/*
				if(alphaChildren.length>1)
				{
					if(ddweights[0].getChildren()!=null)
            		{
						DD resultdd = OP.mult(alphavecChild, ddweights[0].getChildren()[c]);//TODO:should it be c or no objective
						resultAdd_ = OP.add(resultAdd_, resultdd);
        			
            		}else
            		{	
            			DD resultdd = OP.mult(alphavecChild, ddweights[0]);
            			resultAdd_ = OP.add(resultAdd_, resultdd);
            		}
					
					//DD resultdd = OP.mult(alphavecChild, ddweights[0]);
        			//resultAdd = OP.add(resultAdd, resultdd);
				}
				else *///if (alphaChildren.length==1)
				/*{
					resultAdd_= alphavecChild;
				}
				//resultAdd = alphaChildren[c];
				//it is only one objective node -- as for both objectives are the same 
				children[c]=resultAdd_; 
				//scalarizeAlphavectors[s].getChildren()[c] = resultAdd_; 
				//scalarizeAlphavectors[s]=children[c];
			}
		}
		scalarizeAlphavectors [s]= DDnode.myNew(1, children);
	}else
	{
		System.out.println("the level1 is only a leaf not a node-- there is nothing to do");
		scalarizeAlphavectors[s]=AlphaMatrix;
		//only one leaf under the node means that all sates and objectives are the same so it shrink to one leaf 
		// return the value 
	}
	return scalarizedAlphas;

    }*/
    //--------------scalarizeAlphaMatrix override -------------------------------
    /*
     * this one will take a alphamatrix 2D and will scalarize with weights and return a DD also 
     */
    public static DD scalarizeAlphaMatrix(DD AlphaMatrix)
    {
    	/*---------------------------------------------------
    	 * test weights ONLY used when running TestMain.java
    	 * Otherwise should be commented
    	 */
    	/*DD[] ddweights = new DD[1];
    	DD[] children_w = new DD[2];
    	children_w[0] = DDleaf.myNew(1);
    	children_w[1] = DDleaf.myNew(0);
		DD dd1 = DDnode.myNew(1, children_w);
		ddweights[0]=dd1;*/
		//------------end part used for testing--------------
		
		List<DD> myList = new ArrayList<DD>();		
    	DD scalarizedAlphas = DD.zero;
    	DD[] children = AlphaMatrix.getChildren();
    	DD resultAdd = DD.zero; 
    	
    	for (int we=0; we<ddweights[0].getChildren().length;we++)// take one weight at a time
		{	  		  		
    		if(children.length==ddweights[0].getChildren().length)
    		{
    			if((ddweights[0].getChildren()!=null)&&(ddweights[0].getChildren().length>1))// if the weights are different 
    			{ 		
    				if (children[we].getChildren()!=null) //2 nodes and each node with 2 leafs
    				{
    					resultAdd = DD.zero; 
    					for (int a=0;a<children[we].getChildren().length;a++)
    					{
    						DD resultdd = OP.mult(children[we].getChildren()[a], ddweights[0].getChildren()[a]);
    						resultAdd = OP.add(resultAdd, resultdd);
    					}
    					
    				}else //1 node with 2 leafs
    				{
    					DD resultdd = OP.mult(children[we], ddweights[0].getChildren()[we]);
    					resultAdd = OP.add(resultAdd, resultdd);
    				}
    			}else// if the weights are the same it will be only one leaf 
    			{
    				//weights are the same so one leaf
    				DD resultdd = OP.mult(children[we], ddweights[0]);
					resultAdd = OP.add(resultAdd, resultdd);
    			}
    			myList.add(resultAdd);
    			
    		}else if(children.length>ddweights[0].getChildren().length)
    		{
    			myList.add(AlphaMatrix);
    		}
    		
		}// end loop weights 
    	
    	DD[] children_scalarized = new DD[myList.size()];
    	for(int x=0; x<myList.size();x++)
    	{
    		children_scalarized[x]=myList.get(x);
    	}
    	scalarizedAlphas = DDnode.myNew(1, children_scalarized);
	
    	return scalarizedAlphas;

    }
    //---------------recursiveScalarizeMatrix--------------------------------- 
    public static DD recursiveScalarizeMatrix(DD matrix)
    {
    	/*---------------------------------------------------
    	 * test weights ONLY used when running TestMain.java
    	 * Otherwise should be commented
    	 */
    	DD[] ddweights = new DD[1];
    	DD[] children_w = new DD[2];
    	children_w[0] = DDleaf.myNew(1);
    	children_w[1] = DDleaf.myNew(0);
		DD dd1 = DDnode.myNew(1, children_w);
		ddweights[0]=dd1;
		//------------end part used for testing--------------
		
		List<DD> myList = new ArrayList<DD>();		
    	DD scalarizedAlphas = DD.zero;
    	DD[] children = matrix.getChildren();
    	DD resultAdd = DD.zero; 
    	
    	//for (int we=0; we<ddweights[0].getChildren().length;we++)// take one weight at a time
    	for (int i=0; i<matrix.getChildren().length;i++)
		{	  		resultAdd = DD.zero;   		
    		//if(matrix.getNumLeaves()!=ddweights[0].getChildren().length)
    		//{    			
    			if((ddweights[0].getChildren()!=null)&&(ddweights[0].getChildren().length>1))// if the weights are different 
    			{ 	//for (int we=0; we<ddweights[0].getChildren().length;we++)
    				{
	    				if (children[i].getChildren()!=null)//&&(children.length==ddweights[0].getChildren().length))
	    				{
	    					//2 nodes and each node with 2 leafs
	    					/*resultAdd = DD.zero; 
	    					for (int a=0;a<children[we].getChildren().length;a++)
	    					{
	    						DD resultdd = OP.mult(children[we].getChildren()[a], ddweights[0].getChildren()[a]);
	    						resultAdd = OP.add(resultAdd, resultdd);
	    					}*/
	    					
	    					/*
	    					 * i could comment upper part and just call the function recursevly here until what left is only leafs
	    					 * in the else part
	    					 */
	    					resultAdd = recursiveScalarizeMatrix(children[i]);
	    					
	    				}else 
	    				{
	    					//1 node with 2 leafs
	    					for (int w=0; w<ddweights[0].getChildren().length;w++)
	    					{
	    						DD resultdd = OP.mult(children[w], ddweights[0].getChildren()[w]);
		    					resultAdd = OP.add(resultAdd, resultdd);
	    					}
	    					//break;
	    				}
    				}
    				
    			}else// if the weights are the same it will be only one leaf 
    			{
    				//weights are the same so one leaf
    				DD resultdd = OP.mult(children[i], ddweights[0]);
					resultAdd = OP.add(resultAdd, resultdd);
    			}
    			myList.add(resultAdd);
    			
    		/*}else
    		{
    			DD resultdd = OP.mult(matrix, ddweights[0].getChildren()[we]);
				resultAdd = OP.add(resultAdd, resultdd);
    			
    		}*/
    		//myList.add(matrix);
    		
		}// end loop weights 
    	
    	DD[] children_scalarized = new DD[myList.size()];
    	for(int x=0; x<myList.size();x++)
    	{
    		children_scalarized[x]=myList.get(x);
    	}
    	scalarizedAlphas = DDnode.myNew(1, children_scalarized);
	
return scalarizedAlphas;
    }
    //---------------scalarize_alphavector_withMultipleObjectives-----------------
    /*
     * April 2018 scalrize mo_Alphamatrix
     */
    public DD []scalarize_MOalphavector (final DD[] mo_Alphavectors)
    {
    	DD [] scalarizeAlphavectors = new DD [mo_Alphavectors.length];
    	//scalarizeAlphavectors = mo_Alphavectors; 
    	for (int s=0; s<mo_Alphavectors.length;s++)
    	{
    		DD alphavec = mo_Alphavectors[s];//alphavector 
    		//create create no of childs for the vector
    		if (alphavec!=null)// May 2018
    		{
    			
    		if(alphavec.getChildren()!=null)// if alphavector does not has only one leaf instrad it has children 
    		{
    			DD resultAdd_ = DD.zero;
    			DD alphaChildren [] = alphavec.getChildren(); //number of children (level 1) = number of states 
    			DD [] children = new DD [alphaChildren.length];
    			for(int c=0; c<alphaChildren.length; c++)
    			{
    				DD alphavecChild = alphaChildren[c]; 
    				
    				if (alphavecChild.getChildren()!=null)// level 1 child
    				{
    					DD alphavecChild_n []= alphavecChild.getChildren();//objectives 
    					DD resultAdd = DD.zero; // the DD after scalarize the multiple objectives 
    					for (int a =0; a<alphavecChild_n.length;a++)
    					{
    						DD alphavecChild_n_ = alphavecChild_n[a];
    						if(alphavecChild_n_.getChildren()!=null)//now we go to the leafs 
    						{
    							//now here multiply the alphavecChild_n_ with the first weight 
    							//second round multiply by second weight
    				    		
    				    		//for (int we=0; we<ddweights[0].getChildren().length;we++)
    				    		{
    				    			if(ddweights[0].getChildren()!=null)
    		                		{
    		    						DD resultdd = OP.mult(alphavecChild_n_, ddweights[0].getChildren()[a]);
    		    						resultAdd = OP.add(resultAdd, resultdd);
    		            			
    		                		}else
    		                		{	
    		                			DD resultdd = OP.mult(alphavecChild_n_, ddweights[0]);
    		                			resultAdd = OP.add(resultAdd, resultdd);
    		                		}
    				    		}
    						}else
    						{
    							resultAdd= alphavecChild_n_; 
    							
    						}
    						children[c]=resultAdd; 
    						//scalarizeAlphavectors[s].getChildren()[c] = resultAdd; 
    						//sum the output of the multiply here 
    						//add it to the DD result 
    						
    					}
    					
    				}else
    				{
    					System.out.println("level 1 child doesn't have children it is just one leaf so we keep the leaf and don't scalarize with weights");
    					 
    					/*
    					if(alphaChildren.length>1)
    					{
    						if(ddweights[0].getChildren()!=null)
	                		{
	    						DD resultdd = OP.mult(alphavecChild, ddweights[0].getChildren()[c]);//TODO:should it be c or no objective
	    						resultAdd_ = OP.add(resultAdd_, resultdd);
	            			
	                		}else
	                		{	
	                			DD resultdd = OP.mult(alphavecChild, ddweights[0]);
	                			resultAdd_ = OP.add(resultAdd_, resultdd);
	                		}
    						
    						//DD resultdd = OP.mult(alphavecChild, ddweights[0]);
                			//resultAdd = OP.add(resultAdd, resultdd);
    					}
    					else *///if (alphaChildren.length==1)
    					{
    						resultAdd_= alphavecChild;
    					}
    					//resultAdd = alphaChildren[c];
    					//it is only one objective node -- as for both objectives are the same 
    					children[c]=resultAdd_; 
    					//scalarizeAlphavectors[s].getChildren()[c] = resultAdd_; 
    					//scalarizeAlphavectors[s]=children[c];
    				}
    			}
    			scalarizeAlphavectors [s]= DDnode.myNew(1, children);
    		}else
    		{
    			System.out.println("the level1 is only a leaf not a node-- there is nothing to do");
    			scalarizeAlphavectors[s]=alphavec;
    			//only one leaf under the node means that all sates and objectives are the same so it shrink to one leaf 
    			// return the value 
    		}
    	}//end if not null
    	
    	}// end scalarizedAlphavectors loop
    	alphaVectors = scalarizeAlphavectors; 
    	
    	return scalarizeAlphavectors;
    }
    //---------------scalarizeAlphaMatrix_1----------------------------------------
    /*
     * WILL take arrayList and scalarize to DD[]  
     */
    public DD[] scalarizeAlphaMatrix_1(ArrayList<DD[][]> listAlphaMatrix)
    {
    	
    	DD [] scalarizedAlphas = new DD [listAlphaMatrix.size()];
    	DD[] children= new DD [1];//TODO: size should be generalized 
    	for (int l=0; l<listAlphaMatrix.size();l++)
    	{
    		
    		DD[][] alphaMatrix = listAlphaMatrix.get(l);
    		/*
    		 * this code will work if the alphaMatrix has this structure 
    		 * 
    		 * DDnode ---> the alphaMatrix 
    		 * 	DDnode 
    		 * 		DDnode
    		 * 			DDleaf
    		 * 			DDleaf
    		 * 			DDleaf
    		 * 	DDnode
    		 * 		DDnode
    		 * 			DDleaf
    		 * 			DDleaf
    		 * 			DDleaf
    		 */
    		
    		/*
    		 * When create alphas they are constructed like this 
    		 * 
    		 * DDnode --- AlphaMatrix 
    		 * 	DDnode
    		 * 		DDleaf
    		 * 		DDleaf	
    		 * 		DDleaf
    		 * 	DDnode 
    		 * 		DDleaf
    		 * 		DDleaf
    		 * 		DDleaf 
    		 */
    		DD resultAdd = DD.zero; 
    		
    		for (int we=0; we<ddweights[0].getChildren().length;we++)	
    		{
    			if(alphaMatrix[we].length==1)
    			{        				
    					if(ddweights[0].getChildren()!=null)
                		{
    						DD resultdd = OP.mult(alphaMatrix[we][0], ddweights[0].getChildren()[we]);
    						resultAdd = OP.add(resultAdd, resultdd);
            			
                		}else
                		{	
                			DD resultdd = OP.mult(alphaMatrix[we][0], ddweights[0]);
                			resultAdd = OP.add(resultAdd, resultdd);
                		}
    			}
    			else //there is no node, it is imediatly the node itself ..no need to loop through the length 
    			{
    				//will construct the DD 
    				DD column = DD.zero;
    				DD [] childColumn = new DD [alphaMatrix[we].length];
    				for(int f=0; f<alphaMatrix[we].length;f++)
    				{
    					//alphaMatrix[we][f].display();
    					childColumn[f]=alphaMatrix[we][f];
    				}
    				column=DDnode.myNew(1, childColumn);
    				if(ddweights[0].getChildren()!=null)
            		{	
						DD resultdd = OP.mult(column, ddweights[0].getChildren()[we]);
						resultAdd = OP.add(resultAdd, resultdd);
        			
            		}else
            		{
            			//weights are the same so one leaf          			
            			DD resultdd = OP.mult(column, ddweights[0]);
            			resultAdd = OP.add(resultAdd, resultdd);
            		}
    			}
    		}
    		children[0]= 	resultAdd;
    		
    		scalarizedAlphas[l] = DDnode.myNew(1, children);
    		int [] n1 = scalarizedAlphas[l].getVarSet();	
    	}
    	return scalarizedAlphas;

    }
    
    //----------------solve---------------------------------
    public void solve(int nRounds, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, int nIterations, int maxAlpha, String basename, double []w) {
	
    	solve(nRounds,maxSize, maxTries, episodeLength, threshold, explorProb, nIterations, maxAlpha, basename, false, w);

    }
    
    /*public <S, A, O> void solve(int nRounds, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, int nIterations, int maxAlpha, String basename, double []w, double[] iBelief, int nSamplez, int maxIter, double preciz, int typ, POMDP pomdp) {
    	solve(nRounds,maxSize, maxTries, episodeLength, threshold, explorProb, nIterations, maxAlpha, basename, false, w,iBelief,nSamplez,maxIter,preciz,typ, pomdp);
        }*/
    // solves the POMDP in nRounds, with 
    //maxSize belief states, 
    //episodeLength episode lengths while generating belief states
    // threshold as the threshold difference for accepting belief states
    // explorProb is the probability of exploration during belief point generation
    // nIterations is the number of iterations of Perseus per round
    // maxAlpha is the bound on the number of alpha vectors
    // name is the file name
    public void solve(int nRounds, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, int nIterations, int maxAlpha, String basename, boolean multinits, double []w) 
   // public <S, A, O> void solve(int nRounds, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, int nIterations, int maxAlpha, String basename, boolean multinits, double []w, double[] iBelief, int nSamplez, int maxIter, double preciz, int typ, POMDP pomdp) 
    {
       
        try 
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("performance_data.txt"), "utf-8"));
            writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("values_improvement.txt"), "utf-8"));
            writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("nextBeliefState.txt"), "utf-8"));
            
            //from boundedPersesusStartFromCurrent
            writerb_1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("performance_data_boundedPerseusStartFromCurrent1.txt"), "utf-8"));
            writerb_2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("performance_data_boundedPerseusStartFromCurrent2.txt"), "utf-8"));// alphas - policies 
            
            //from dpbackup
            writerd_1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("performance_data_dpbackup1.txt"), "utf-8"));
            writerd_2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("performance_data_dpbackup2.txt"), "utf-8"));// alphas - policies 
        } 
        catch (IOException ex) 
        {
        } 
        
	// first solve the MDP underlying
	if (!debug && explorProb < 1.0) 
	    solveQMDP();
	    
	
	// then  loop over rounds
	// probability of using the MDP policy over the POMDP policy if the 
	// choice is to exploit
	double mdpprob=1.0;
	int firstStep=0;
	String fname;
	int totaliterations=0;
	maxAlphaSetSize=maxAlpha;

	//-----get the alphamatrix from OLSAR---------------------
	//convertAlphaMatrix2AlphaVector_2D(lb);// this will get intial alpha matrix from OLSAR 2objx3states format 
	
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/*
	 * we will crease same initial alpha from Symbolic Perseus original but matrix 
	 */
	/*TODO: March -April 2018 
	 * 1)now this alpha need to be tested to see if it looks the same how we imagined it will look like= DONE
	 * 2)then: create belief region also 14 sample like original = DONE
	 * 3)then: i think we need to check the scalarization function we might modify it as well.
	 * 		   we should have something like the original alphavectors to test the scalarization function = DONE
	 * 4)then: we might change the whole alphamatrix because in this case i created alphavectors as 1d array not 2d. 
	 * 		   i will see if we can work it this way or not. because by the end of the 
	 * 		   day it is only a tree .. just add a level for objectives.. you cannot have 2D tree!! can we? 
	 */
	
	//like original numbers: alpha0
	/*DD c0 [] = new DD [2]; 
	c0[0] = DDleaf.myNew(-31.38);
	c0[1] = DDleaf.myNew(-5.53);
	
	DD c1 [] = new DD [2];
	c1[0] = DDleaf.myNew(1);
	c1[1] = DDleaf.myNew(0);
	
	DD c3 [] = new DD [2];
	c3[0]=  DDnode.myNew(1, c0);
	c3[1]=  DDnode.myNew(1, c1);
	
	DD c00 [] = new DD [2];
	c00[0] = DDleaf.myNew(-5.53);
	c00[1] = DDleaf.myNew(-31.38);
	
	
	DD c11 [] = new DD [2];
	c11[0] = DDleaf.myNew(0);
	c11[1] = DDleaf.myNew(1);
	
	
	DD c33 [] = new DD [2];
	c33[0]=  DDnode.myNew(1, c00);
	c33[1]=  DDnode.myNew(1, c11);
	
	DD c4 [] = new DD [2];
	c4[0] = DDnode.myNew(1, c3);
	c4[1] = DDnode.myNew(1, c33);
	
	//alpha1
	DD c44 [] = new DD [1];
	c44[0] = DDleaf.myNew(-13.84);
	//c44[1] = DDleaf.myNew(0);*/
	
	/*DD alphavectors_fromOriginal []= new DD [2];
	alphavectors_fromOriginal[0] = DDnode.myNew(4, c4);//first alpha
	alphavectors_fromOriginal[1] = DDnode.myNew(1, c44);//second alpha 
	
	alphaVectors_2D[0]=alphavectors_fromOriginal;
	
	alphaMatrix = alphavectors_fromOriginal;*/
	
	/*alphaMatrix = new DD [2];
	alphaMatrix[0] = DDnode.myNew(4, c4);//first alpha
	alphaMatrix[1] = DDnode.myNew(1, c44);//second alpha 

	scalarize_MOalphavector(alphaMatrix);*/
	
	/*
	 * March-April 2018
	 * beleif region from original symbolic persues
	 */
	
	//DD belRegion_o [][] = new DD [15][];
	
	//belRegion[0]
	/*DD cDD0 []= new DD [2];
	cDD0[0] = DDleaf.myNew(1);
	cDD0[1] = DDleaf.myNew(0);
	DD belNode0 = DDnode.myNew(1, cDD0);
	DD node0 [] = new DD [1];
	node0[0]= belNode0; 
	node0[0].setVar(1);
	belRegion_o[0]=node0;*/
	
	//belRegion[1]
	/*DD cDD1 []= new DD [2];
	cDD1[0] = DDleaf.myNew(0.5);
	cDD1[1] = DDleaf.myNew(0.5);
	DD belNode1 = DDnode.myNew(1, cDD1);
	DD node1 [] = new DD [1];
	node1[0]= belNode1; 
	belRegion_o[1]=node1;*/
	
	//belRegion[2]
	/*DD cDD2 []= new DD [2];
	cDD2[0] = DDleaf.myNew(1);
	cDD2[1] = DDleaf.myNew(0);
	DD belNode2 = DDnode.myNew(3, cDD2);
	DD node2 [] = new DD [1];
	node2[0]= belNode2;
	node2[0].setVar(3);
	belRegion_o[2]=node2;*/
	
	//belRegion[3]
	/*DD cDD3 []= new DD [2];
	cDD3[0] = DDleaf.myNew(0);
	cDD3[1] = DDleaf.myNew(1);
	DD belNode3 = DDnode.myNew(1, cDD3);
	DD node3 [] = new DD [1];
	node3[0]= belNode3;
	node3[0].setVar(1);
	belRegion_o[3]=node3;*/
	
	//belRegion[4]
	/*DD cDD4 []= new DD [2];
	cDD4[0] = DDleaf.myNew(0.15);
	cDD4[1] = DDleaf.myNew(0.85);
	DD belNode4 = DDnode.myNew(3, cDD4);
	DD node4 [] = new DD [1];
	node4[0]= belNode4; 
	node4[0].setVar(3);
	belRegion_o[4]=node4;*/
	
	//belRegion[5]
	/*DD cDD5 []= new DD [2];
	cDD5[0] = DDleaf.myNew(0.85);
	cDD5[1] = DDleaf.myNew(0.15);
	DD belNode5 = DDnode.myNew(3, cDD5);
	DD node5 [] = new DD [1];
	node5[0]= belNode5;
	node5[0].setVar(3);
	belRegion_o[5]=node5;*/

	//belRegion[6]
	/*DD cDD6 []= new DD [2];
	cDD6[0] = DDleaf.myNew(0.969);
	cDD6[1] = DDleaf.myNew(0.0302);
	DD belNode6 = DDnode.myNew(3, cDD6);
	DD node6 [] = new DD [1];
	node6[0]= belNode6;
	node6[0].setVar(3);
	belRegion_o[6]=node6;*/

	//belRegion[7]
	/*DD cDD7 []= new DD [2];
	cDD7[0] = DDleaf.myNew(0.9945);
	cDD7[1] = DDleaf.myNew(0.0054);
	DD belNode7 = DDnode.myNew(3, cDD7);
	DD node7 [] = new DD [1];
	node7[0]= belNode7;
	node7[0].setVar(3);
	belRegion_o[7]=node7;*/
	
	//belRegion[8]
	/*DD cDD8 []= new DD [2];
	cDD8[0] = DDleaf.myNew(0.0302);
	cDD8[1] = DDleaf.myNew(0.9697);
	DD belNode8 = DDnode.myNew(3, cDD8);
	DD node8 [] = new DD [1];
	node8[0]= belNode8;
	node8[0].setVar(3);
	belRegion_o[8]=node8;*/
	
	//belRegion[9]
	/*DD cDD9 []= new DD [2];
	cDD9[0] = DDleaf.myNew(0.00546);
	cDD9[1] = DDleaf.myNew(0.9945);
	DD belNode9 = DDnode.myNew(3, cDD9);
	DD node9 [] = new DD [1];
	node9[0]= belNode9;
	node9[0].setVar(3);
	belRegion_o[9]=node9;*/
	
	//belRegion[10]
	/*DD cDD10 []= new DD [2];
	cDD10[0] = DDleaf.myNew(0.9990311236573288);
	cDD10[1] = DDleaf.myNew(9.688763426712281E-4);
	DD belNode10 = DDnode.myNew(3, cDD10);
	DD node10 [] = new DD [1];
	node10[0]= belNode10; 
	node10[0].setVar(3);
	belRegion_o[10]=node10;*/
	
	//belRegion[11]
	/*DD cDD11 []= new DD [2];
	cDD11[0] = DDleaf.myNew(0.9998288852897682);
	cDD11[1] = DDleaf.myNew(1.711147102316738E-4);
	DD belNode11 = DDnode.myNew(3, cDD11);
	DD node11 [] = new DD [1];
	node11[0]= belNode11;
	node11[0].setVar(3);
	belRegion_o[11]=node11;*/
	
	//belRegion[12]
	/*DD cDD12 []= new DD [2];
	cDD12[0] = DDleaf.myNew(0.9999697990305696);
	cDD12[1] = DDleaf.myNew(3.020096943040475E-5);
	DD belNode12 = DDnode.myNew(3, cDD12);
	DD node12 [] = new DD [1];
	node12[0]= belNode12;
	node12[0].setVar(3);
	belRegion_o[12]=node12;*/

	//belRegion[13]
	/*DD cDD13 []= new DD [2];
	cDD13[0] = DDleaf.myNew(0.999);
	cDD13[1] = DDleaf.myNew(5.32971E-4);
	DD belNode13 = DDnode.myNew(3, cDD13);
	DD node13 [] = new DD [1];
	node13[0]= belNode13;
	node13[0].setVar(3);
	belRegion_o[13]=node13;*/
	
	//belRegion[14]
	/*DD cDD14 []= new DD [2];
	cDD14[0] = DDleaf.myNew(9.6887E-4);
	cDD14[1] = DDleaf.myNew(0.9903);
	DD belNode14 = DDnode.myNew(3, cDD14);
	DD node14 [] = new DD [1];
	node14[0]= belNode14; 
	node14[0].setVar(3);
	belRegion_o[14]=node14;
	
	belRegion=belRegion_o;*/
	//---------------------------------------------------------------------------------------------------------------------------------------------------
	
	//alpha 0 
		/*DD children0DD0 []= new DD [3];//this is not genral it is number of states 
		children0DD0[0] = DDleaf.myNew(-31.38);
		children0DD0[1] = DDleaf.myNew(-5.53);
		children0DD0[2] = DDleaf.myNew(0);
		DD alpha0node1 = DDnode.myNew(1, children0DD0);
		
		DD children1DD0 []= new DD [3];//this is not genral it is number of states 
		children1DD0[0] = DDleaf.myNew(-5.53);
		children1DD0[1] = DDleaf.myNew(-31.38);
		children1DD0[2] = DDleaf.myNew(0);
		DD alpha0node2 = DDnode.myNew(1, children1DD0);
		
		DD [] alpha0arraynode1 = new DD[1];
		alpha0arraynode1[0]=alpha0node1;
		DD [] alpha0arraynode2 = new DD[1];
		alpha0arraynode2[0]=alpha0node2;
		
		DD [][]alpha0 = new DD [2][]; 
		alpha0[0]=alpha0arraynode1;
		alpha0[1]=alpha0arraynode2;*/
		

	//alpha 1 
		/*DD children0DD1 []= new DD [3];//this is not genral it is number of states 
		children0DD1[0] = DDleaf.myNew(-13.84);
		children0DD1[1] = DDleaf.myNew(-13.84);
		children0DD1[2] = DDleaf.myNew(0);
		DD alpha1node1 = DDnode.myNew(1, children0DD1);
				
		DD children1DD1 []= new DD [3];//this is not genral it is number of states 
		children1DD1[0] = DDleaf.myNew(-13.84);
		children1DD1[1] = DDleaf.myNew(-13.84);
		children1DD1[2] = DDleaf.myNew(0);
		DD alpha1node2 = DDnode.myNew(1, children1DD1);
		
		DD [] alpha1arraynode1 = new DD[1];
		alpha1arraynode1[0]=alpha1node1;
		DD [] alpha1arraynode2 = new DD[1];
		alpha1arraynode2[0]=alpha1node2;
				
		DD [][]alpha1 = new DD [2][]; 
		alpha1[0]=alpha1arraynode1;
		alpha1[1]=alpha1arraynode2;
		
	
		listArrayAlphaMatrix_2x3 = new ArrayList<DD[][]>();
	 	listArrayAlphaMatrix_3x2= new ArrayList<DD[][]>();
		listArrayAlphaMatrix_2x3.add(0,alpha0);
		listArrayAlphaMatrix_2x3.add(1,alpha1);*/
	//------------testing alphaMatrix representation----------
	/*for(int i =0; i<listArrayAlphaMatrix_2x3.size();i++)
	{
		DD [][] alphaMatrix =listArrayAlphaMatrix_2x3.get(i);
		//alphaMatrix
		//create beliefstate as DD[] 
		double [] beliefS= {0.5,0.5,0};
		DD [] beliefDDState = convertDoubleAtoDD (beliefS);
		
		//multiply alpha matrix (3x2) with belief (1x3)--< result is (2x1) 
		DD [] resultMultiply = multiplyALphaMatrixAndBeliefState (alphaMatrix,beliefDDState);
		
		//System.exit(200);
	}
	*/
	//-----end test alphaMatrix--------------------------------
	
	//finalAlphaVectors = new ArrayList();
	/*
	 *  Jesse Hoey explaination about the rounds: 
	 *  The rounds are used because each time the policy is improved, 
	 *  although its a bootstrapping process which may not work.  
	 *  You start with e.g. the MDP policy, generate a set of beliefs 
	 *  based on that (with some noise), then solve for the value 
	 *  function and policy over those belief points. You then regenerate
	 *  a new set of belief points based on the new policy, which may be
	 *  different from the original set.  Solving a second time for a
	 *  new policy and value function yields something different.
	 *  Its on-policy learning essentially.  However, you can set nRounds=1
	 *  to do it in a single round if you want to use a different belief
	 *  generation policy (e.g. random). 
	 */
	
	for (int r=0; r<nRounds; r++) {
	    // generate a set of reachable belief points
		/*
		 * March-April 2018 
		 * I uncommented this part where the belief samples are randomly generated to test (instead of hard coding samples 
		 * from the original) reason is because when hardcode we should put the varSet and there is no clear way how to do that
		 * we can check the below functions to see that
		 * the varset is used then in NextBelState class in dpbackup function. 
		 */
	    if (!multinits) 
	    {
		reachableBelRegionCurrentPolicy(maxSize,maxTries,episodeLength,threshold,explorProb,mdpprob);
	    } else {
		reachableBelRegionCurrentPolicyMultipleInits(maxSize,maxTries,episodeLength,threshold,explorProb,mdpprob);
	    }
		
		/*
		 * March-April2018
		 * Commented this part
		 * GET the belief samples from OLSAR
		 */
		/*
		 * ReusingPerseusSolver reuseObj = new ReusingPerseusSolver (m, iBelief,nSamplez,maxIter, preciz, typ, pomdp);
		reuseObj.initSamples(); 
		ArrayList<double[]> beliefSamples=reuseObj.bSampleSet;
		DD [][] beliefPointsfromOLSAR = convertBeliefSampleslistArraytoDD(beliefSamples); 
		belRegion = beliefPointsfromOLSAR;
		
		*/
	    // possibly print this out (for debugging)
	    //printBelRegion();
	    // run symbolic Perseus
	    if (debug)
		Global.setSeed(8837328);
	    //--------instead of boundedPerseus will scalarize the alphaMatrix passed from OLSAR
	    //TODO: we need to remove this scalarization maybe later, now only to compelete the loop.
	   /*
	    * April 2018 we use original method to create initial alpha matrix
	    */
	    boundedPerseus(nIterations,maxAlphaSetSize,firstStep,nIterations);
	   
	  //scalarizeAlphaMatrix(listArrayAlphaMatrix_3x2);// do we need this? 
	  //scalarizeAlphaMatrix(listArrayAlphaMatrix_2x3);
	    /*
	     * March-April 2018
	     * Commented this part to try the alphavectors original using the scalarization method  
	     */
	  //alphaVectors = scalarizeAlphaMatrix_1(listArrayAlphaMatrix_2x3);
	  //alphaVectors = alphavectors; 
	 
	  /*
	   * March- April 2018
	   * since we are using a different type of alphamatrix (DD[]) than the listArray<DD[][]> we have to change the function
	   * boundedPerseusStartFromCurrent(maxAlphaSetSize, firstStep, nIterations,alphavectors_fromOriginal);
	   * I have to override it 
	   */
	   //boundedPerseusStartFromCurrent(maxAlphaSetSize, firstStep, nIterations, listArrayAlphaMatrix_2x3);
	  
	    //boundedPerseusStartFromCurrent(maxAlphaSetSize, firstStep, nIterations);
	   
	   
	   /*System.out.println("---print DD [] scalarizedAlphas");
	   for(int s=0; s<scalarizedAlphas.length;s++)
	   {
		   System.out.println("DD #: "+s);
		   scalarizedAlphas[s].display();
	   }
	   //scalarizedAlphas[0].display();
	   System.out.println("---print DD [] alphaVectors");
	   for(int s=0; s<alphaVectors.length;s++)
	   {
		   System.out.println("DD #: "+s);
		   alphaVectors[s].display();
	   }*/
	   //alphaVectors[0].display();
	  //System.exit(200);
	    totaliterations=firstStep+nIterations;
	    fname = basename+"-"+totaliterations+".pomdp";
	    System.out.println("saving current policy to "+fname);
	    try {
		save(fname);
	    } catch (FileNotFoundException err) {
		System.out.println("file not found error "+err);
		return;
	    } catch (IOException terr) {
		System.out.println("file write error"+terr);
	    }
	    firstStep += nIterations;
	    if (r==0) 
		mdpprob=0.5;
	}
	
	//try {writer.close();} catch (Exception ex) {/*ignore*/}
	try {writerb_1.close();} catch (Exception ex) {/*ignore*/}
	try {writerb_2.close();} catch (Exception ex) {/*ignore*/}
	try {writerd_1.close();} catch (Exception ex) {/*ignore*/}
	try {writerd_2.close();} catch (Exception ex) {/*ignore*/}
    }//end solve 
    //--------------------------------------------------------------------------
    public double evaluatePolicyStationary(int nRuns, int nSteps) {
	return evaluatePolicyStationary(nRuns,nSteps,false);
    }
    public double evaluatePolicyStationary(int nRuns, int nSteps, boolean verbose) {
	int [][] stateConfig, nextStateConfig, obsConfig;
	DD belState, nextBelState;
	double totRew, avRew, theRew;
	avRew = 0.0;
	double totdisc = 1.0;
	int runId, stepId, actId, i, j, k;
	DD [] restrictedTransFn, obsDistn, restrictedObsFn;
	for (runId=0; runId<nRuns; runId++) {
	    totRew = 0.0;
	    belState = initialBelState;
	    totdisc = 1.0;
	    stateConfig = OP.sampleMultinomial(belState,varIndices);
	    for (stepId=0; stepId<nSteps; stepId++) {
		if (alphaVectors != null && alphaVectors.length>0) {
		    actId =  policyQuery(belState);
		} else {
		    actId = Global.random.nextInt(nActions);
		}
		theRew = OP.eval(actions[actId].rewFn,stateConfig);
		totRew = totRew + totdisc*theRew;
		totdisc = totdisc*discFact;
		
		restrictedTransFn = OP.restrictN(actions[actId].transFn, stateConfig); 
		nextStateConfig = OP.sampleMultinomial(restrictedTransFn,primeVarIndices);
		obsDistn = OP.restrictN(actions[actId].obsFn, concatenateArray(stateConfig, nextStateConfig));
		obsConfig = OP.sampleMultinomial(obsDistn, primeObsIndices);

		if (verbose) {
		    System.out.print(" "+runId+" "+stepId+" state:");
		    for (j=0; j<stateConfig[1].length; j++) 
			System.out.print(" "+stateConfig[1][j]);
		    
		    System.out.print(": "+actId+" "+theRew+" obs:");
		    for (j=0; j<obsConfig[1].length; j++) 
			System.out.print(" "+obsConfig[1][j]);

		    System.out.println(":"+" "+totRew+" "+totdisc);
		}

		
		belState = beliefUpdate(belState,actId,obsConfig);

		stateConfig = Config.primeVars(nextStateConfig,-nVars);
		Global.newHashtables();
	    }
	    avRew = (runId*avRew + totRew)/ (runId+1);
	}
	return avRew;
    }

    // compute the reachable belief region from the MDP policy 
    public void reachableBelRegionMDPpolicy(int maxSize,  int maxTries, int episodeLength, double threshold, double explorProb) {
	solveQMDP();
	reachableBelRegionCurrentPolicy(maxSize,maxTries,episodeLength, threshold, explorProb, 1.0);
    }

    // maxSize and maxTries here apply to each initial belief state  - so there will potentially be maxSize*number_of_inits belief states
    public void reachableBelRegionCurrentPolicyMultipleInits(int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, double mdpp) {
	DD [] iBelState_f = new DD[nStateVars];
	belRegion = null;
	// search through adjuncts for names starting with init - these are the multiple initial belief states
	for (int i=0; i<adjunctNames.length; i++) {
	    if (adjunctNames[i].startsWith("init")) {
		// adjuncts[i] is an initial belief state - do a simulation from here 
		for (int varId=0; varId<nStateVars; varId++) {
		    iBelState_f[varId] = OP.addMultVarElim(adjuncts[i],MySet.remove(varIndices,varId+1));
		}
		System.out.println("generating belief region from initial belief "+adjunctNames[i]+":");
		printBeliefState(adjuncts[i]);
		reachableBelRegionCurrentPolicy(iBelState_f, belRegion, maxSize, maxTries, episodeLength, threshold, explorProb, mdpp);		
	    }
	}
	
    }
    // compute the reachable belief region starting from the initial belief state and no initial belief region (the default)
    public void reachableBelRegionCurrentPolicy(int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, double mdpp) {
	// factored initial belief state
	DD [] iBelState_f = new DD[nStateVars];
	for (int varId=0; varId<nStateVars; varId++) {
	    iBelState_f[varId] = OP.addMultVarElim(initialBelState,MySet.remove(varIndices,varId+1));
           // System.out.println("Test Belief: **************************************");iBelState_f[varId].display();
	}
        // then  loop over rounds
	// probability of using the MDP policy over the POMDP policy if the 
	// choice is to exploit
	//double mdpp=1.0;
	reachableBelRegionCurrentPolicy(iBelState_f, null, maxSize, maxTries, episodeLength, threshold, explorProb, mdpp);
    }
    // computes the reachable belief region using the current valueFunction and Policy
    // computes starting from ibel (unfactored belief state), and updates belRegion by adding input bRegion + the ones generated with this simulation
    public void reachableBelRegionCurrentPolicy(DD [] iBelState_f, DD [][] iBelRegion, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, double mdpp) 
    {
    // maxSize belief states, 
    // episodeLength episode lengths while generating belief states
    // threshold as the threshold difference for accepting belief states
    // explorProb is the probability of exploration during belief point generation
    // nIterations is the number of iterations of Perseus per round
    // maxAlpha is the bound on the number of alpha vectors
    // solve(nRounds, numBelStates, maxBelStates, episodeLength, threshold, explorProb, nIterations, maxAlphaSetSize, basename, multinits);
    // solve(int nRounds, int maxSize, int maxTries, int episodeLength, double threshold, double explorProb, int nIterations, int maxAlpha, String basename, boolean multinits) 
    
        mdpp          = 0.5;
        maxTries      = 10000;
        episodeLength = 50;  // when generating belief points
        threshold     = 0.000001;
        explorProb    = 1.0;       
        maxSize       = 50;
	int count;
	int choice, actId;
	double distance;

	DD [] nextBelState = new DD[nStateVars];
	DD obsDist;
	DD [] obsDistn, restrictedObsFn;
	DD [] ddState,belState, restrictedTransFn;
	int [][] stateConfig, nextStateConfig, obsConfig;

	double [] zerovalarray = new double[1];
	int [][] oneConfig = new int[2][1];
	double [] eprob = new double[2];
	double [] mdpprob = new double[2];
	zerovalarray[0]=0;
	DD [][] tmpBelRegion = new DD[maxSize][];
	eprob[0] = 1-explorProb;
	eprob[1] = explorProb;
	mdpprob[0]=1-mdpp;
	mdpprob[1]=mdpp;

	boolean isMDP = false;
	count = 0;
	int numtries=0;
	tmpBelRegion[count] = new DD[iBelState_f.length];
	System.arraycopy(iBelState_f, 0, tmpBelRegion[count], 0, iBelState_f.length);
	stateConfig = null;
	nextStateConfig=null;
	double maxbeldiff,beldiff;
	while (count < maxSize && numtries<maxTries) 
        {
	    belState = iBelState_f;
	    // figure out if we'll use the mdp or pomdp
	    if (mdpp < 1.0)
		choice = OP.sampleMultinomial(mdpprob);
	    else
		choice = 1;
	    if (choice == 0) 
		isMDP = false;
	    else
		isMDP = true;
	    if (isMDP) 
	    {	    
		stateConfig = OP.sampleMultinomial(belState,varIndices);        
		/*
		System.out.println("\n Sampling as MDP");
		for (int varId = 0; varId<nStateVars; varId++) 
		{
                    belState[varId].display();
		}
		*/
            }
	    for (int stepId=0; count < maxSize & stepId<episodeLength; stepId++) {
		//sample action
		choice = OP.sampleMultinomial(eprob);
		actId = 1;
		if (choice == 0) {
		    if (isMDP) 
			actId =  policyQuery(Config.convert2dd(stateConfig),qFn,qPolicy);
		    else 
			actId =  policyQuery(belState);
		} else {
		    actId = Global.random.nextInt(nActions);
		}
		//System.out.println("choice "+choice+" action "+actId);
		// sample observation
		if (isMDP) {
		    restrictedTransFn = OP.restrictN(actions[actId].transFn, stateConfig); 
		    nextStateConfig = OP.sampleMultinomial(restrictedTransFn,primeVarIndices);
		    obsDistn = OP.restrictN(actions[actId].obsFn, concatenateArray(stateConfig, nextStateConfig));
		    obsConfig = OP.sampleMultinomial(obsDistn, primeObsIndices);
		} else {
		   obsDist = OP.addMultVarElim(concatenateArray(belState,actions[actId].transFn,actions[actId].obsFn), 
						concatenateArray(varIndices,primeVarIndices));
		   obsConfig = OP.sampleMultinomial(obsDist, primeObsIndices);
		}
		restrictedObsFn = OP.restrictN(actions[actId].obsFn,obsConfig);
		
		// update belState
		
		maxbeldiff = 0.0;
		for (int varId = 0; varId<nStateVars; varId++) {
		    nextBelState[varId] = OP.addMultVarElim(concatenateArray(belState,actions[actId].transFn,restrictedObsFn),concatenateArray(MySet.remove(primeVarIndices,varId+nVars+1),varIndices));
		    nextBelState[varId] = OP.approximate(nextBelState[varId], 1e-6, zerovalarray);
		    nextBelState[varId] = OP.div(nextBelState[varId],OP.addMultVarElim(nextBelState[varId],primeVarIndices[varId]));		    
		    //System.out.println("Next    Belief: **************************************");nextBelState[varId].display();
		   // System.out.println("Next    Belief: **************************************");printBeliefState(nextBelState[varId]);
		    //System.out.println("Current Belief: **************************************");printBeliefState(belState[varId]);
		    //System.out.println("Current Belief: **************************************");belState[varId].display();
		    beldiff = OP.maxAll(OP.abs(OP.sub(OP.primeVars(nextBelState[varId],-nVars),belState[varId])));
		    //System.out.println("Diff is:" + beldiff);
		    if (beldiff > maxbeldiff)
			maxbeldiff=beldiff;
		}
                
		numtries++;
		// make sure belief state has changed
		//	System.out.println(" maxbeldiff "+maxbeldiff+" threshold "+threshold);
		if (stepId > 0 && maxbeldiff < threshold)
		    break;
		
		belState = OP.primeVarsN(nextBelState,-nVars);
		if (isMDP) 
		    stateConfig = Config.primeVars(nextStateConfig,-nVars);

		//add belState to tmpBelRegion
		//printBeliefState(belState);
		distance = findSimilarFactBelief(belState, tmpBelRegion, count+1, threshold);
		//System.out.println("Distance 1 is:" + distance);
		//System.out.println("distance "+distance);
		if (!debug && distance > threshold) {
		    count = count + 1;
		    if (count < maxSize) {
			//System.out.println("bel State : count "+count+" distance "+distance+" threshold "+threshold);

			tmpBelRegion[count] = new DD[belState.length];
			System.arraycopy(belState, 0, tmpBelRegion[count], 0, belState.length);
			if (count%10 == 0)
			    System.out.println(" Here 1: "+count+" belief states sampled"); 
		    }
		}
		//System.out.println("Count is "+count);
		// add pure state to tmpBelRegion
		// if we're doing this for the MDP policy only
		if ((debug || isMDP) && count < maxSize) {
		    ddState = new DD[nStateVars];
		    for (int varId = 0; varId < nStateVars; varId++) {
			oneConfig[0][0]=stateConfig[0][varId];
			oneConfig[1][0]=stateConfig[1][varId];
			ddState[varId] = Config.convert2dd(oneConfig);
		    }
		    //printBeliefState(ddState);
		    distance = findSimilarFactBelief(ddState,tmpBelRegion,count+1,threshold);
		    //System.out.println("Distance 2 is:" + distance);
		    if (distance > threshold)  {
			count = count + 1;
			if (count < maxSize) {
			    //  System.out.println("ddState : count "+count+" distance "+distance+" threshold "+threshold);

			    tmpBelRegion[count] = new DD[ddState.length];
			    System.arraycopy(ddState, 0, tmpBelRegion[count], 0,ddState.length);
			    if (count % 10 == 0)  
				System.out.println(" Here 2: "+count+" belief states sampled"); 
			}
		    }
		}
		Global.newHashtables();
	    }
	    //System.out.println("resetting to initial belief - "+count+" belief states so far");
	}
	// copy over 
	if (count < maxSize)
	    count = count + 1; // means we never found enough, so count is one less than total we found
	System.out.println("finished sampling  "+count+" belief states  "+tmpBelRegion.length);
        //System.exit(1);
	int ii=0;
	if (iBelRegion != null) {
	    belRegion = new DD[count+iBelRegion.length][];
	    // copy over the ones that were passed in
	    for (ii=0; ii<iBelRegion.length; ii++) {
		belRegion[ii] = new DD[iBelRegion[ii].length];
		System.arraycopy(iBelRegion[ii], 0, belRegion[ii], 0, iBelRegion[ii].length);
	    }
	    
	} else {
	    belRegion = new DD[count][];
	    ii=0;
	}
	// copy over the new ones
	for (int i=ii; i<ii+count; i++) {
	    belRegion[i] = new DD[tmpBelRegion[i-ii].length];
	    System.arraycopy(tmpBelRegion[i-ii], 0, belRegion[i], 0,tmpBelRegion[i-ii].length);
	}
    }

	/*
	 * May 2018 
	 * use this to create the intial alphamatrix 
	 */
   public void boundedPerseus(int nIterations, int maxAlpha, int firstStep, int nSteps) {
	DD newAlpha,prevAlpha;
	double bellmanErr;
	double [] onezero = {0};
	boolean dominated;
	double steptolerance;
	//check if the value function exists yet
	DD [] tmpalphaVectors = new DD[nActions];

	maxAlphaSetSize=maxAlpha;

	numnewAlphaMatrix = 0;
	
	// this is done in pureStrategies now- still to do
	for (int actId=0; actId<nActions; actId++) 
	{
	    newAlpha = DD.zero;
	    bellmanErr = tolerance;
	    for (int i=0; i<50; i++) 
	    {
	    	prevAlpha = newAlpha;
	    	newAlpha = OP.primeVars(newAlpha,nVars);
	    	/*
	    	 * May 2018
	    	 * 2nd modification: will check the newalpha if has two nested nodes will make children nodes var = 1 just parent 3
	    	 */    	
	    	System.out.println("actionID:"+actId+" i:"+i);
	    	newAlpha = OP.addMultVarElim(concatenateArray(ddDiscFact,actions[actId].transFn,newAlpha),primeVarIndices);
	    	/*
	    	 * May 2018
	    	 * first modficiation: add the if statement 
	    	 */
	    	/*if(newAlpha.getVar()!=0 && newAlpha.getChildren()!=null)
	    	{
	    		for(int x=0; x<newAlpha.getChildren().length;x++)
	    		{
	    			if(newAlpha.getChildren()[x].getVar()!=0)
	    				newAlpha.getChildren()[x].setVar(1);
	    		}
	    	}*/
	    	/*if(newAlpha.getVar()!=0)
	    		newAlpha.setVar(1);*/
	    	
	    	//newAlpha = OP.addN(concatenateArray(actions[actId].rewFn, newAlpha));
	    	/*
	    	 * May 2018
	    	 * scalarize the costs 
	    	 */
	    	DD costObject_copy= DD.zero;
	    	if(actions[actId].costObj.getChildren()!=null)//its not just a leaf
	    	{	
	    		costObject_copy=recursiveScalarizeMatrix(actions[actId].costObj);	
	    	}
	    	newAlpha = OP.addN(concatenateArray(costObject_copy, newAlpha));
	    	newAlpha = OP.approximate(newAlpha,bellmanErr*(1-discFact)/2.0,onezero);
	    	bellmanErr = OP.maxAll(OP.abs(OP.sub(newAlpha,prevAlpha)));
	    	if (bellmanErr <= tolerance) 
	    		break;
	    	Global.newHashtables();
	    }
	    // now add this vector only if not dominated
	    dominated=false;
	    int aid = 0;
	    while (!dominated && aid < numnewAlphaMatrix) 
	    {
	    	if (OP.maxAll(OP.sub(newAlpha,tmpalphaVectors[aid])) < tolerance) 
	    		dominated=true;
	    	aid++;
	    }
	    if (!dominated) 
	    {
	    	tmpalphaVectors[numnewAlphaMatrix]=newAlpha;
	    	numnewAlphaMatrix++;
	    }
	}
	//alphaVectors = new DD[numnewAlphaMatrix];
	alphaMatrix = new DD [numnewAlphaMatrix];
	origAlphaVectors = new DD[numnewAlphaMatrix];
	
	for (int aid=0; aid<numnewAlphaMatrix; aid++)  
	{
	    //alphaVectors[aid] = tmpalphaVectors[aid];
		alphaMatrix [aid] = tmpalphaVectors[aid];
	    origAlphaVectors[aid] = tmpalphaVectors[aid];
	}
	/*
	 * May 2018 
	 * scalarize the alphamatrix to alphavector: 
	 * 1) it should look like original (compare alphavectors)
	 * 2) test with scalarized vector and check best improvment 
	 */
	alphaVectors = scalarize_MOalphavector(tmpalphaVectors);
	
	boundedPerseusStartFromCurrent(maxAlpha, firstStep, nSteps);
    }
   
    public DD[]  getAlphaVectors() {
	return alphaVectors;
    } 
    public int [] getPolicy() {
	return policy;
    }

    public DD [][] getBelRegion() {
	return belRegion;
    }
    public void setAlphaVectors( DD [] newAlphaMatrix, int [] newpolicy) {
	alphaVectors = new DD[newAlphaMatrix.length];
	policy = new int[newpolicy.length];
	for (int i=0; i<newAlphaMatrix.length; i++) {
	    alphaVectors[i] = newAlphaMatrix[i];
	    policy[i]=newpolicy[i];
	}
    }
    public void setBelRegion(DD [][] newBelRegion) {
	belRegion = new DD[newBelRegion.length][];
	for (int i=0; i<newBelRegion.length; i++) {
	    belRegion[i] = new DD[newBelRegion[i].length];
	    System.arraycopy(newBelRegion[i], 0, belRegion[i], 0, newBelRegion[i].length);
	}
    }
    //------------------------------------------------------------------------
    /*
     * March - April 2018 
     * Override the boundedPerseusStartFromCurrent to have alphamatrix as DD[]
     * TODO: alphaMatrix should be global variable and not passed like this. so that it can be used in iterations. 
     */
    public void boundedPerseusStartFromCurrent(int maxAlpha, int firstStep, int nSteps) {
    	DD newAlpha,prevAlpha;
    	double bellmanErr;
    	double [] onezero = {0};
    	boolean dominated;
    	double steptolerance;

    	maxAlphaSetSize=maxAlpha;

    	bellmanErr = 20*tolerance;

    	/*
    	 * May 2018
    	 * try to pass alphavectors (scalarized alphamatrix) 
    	 */
    	//currentPointBasedValues = OP.factoredExpectationSparseNoMem(belRegion,alphaVectors);
    	
    	currentPointBasedValues = OP.factoredExpectationSparseNoMem(belRegion,alphaMatrix);

    	
    	
    	DD [] primedV, primedVmatrix;
    	double maxAbsVal=0, maxAbsVal_matrix=0;
    	int counterf=0; 
    	for (int stepId=firstStep; stepId<firstStep+nSteps; stepId++) 
    	{
    		System.out.println("_____________"+counterf+"_______________");
    	    steptolerance = tolerance;
    	    /*
    	     * May 2018
    	     * commented the alphavector part becz pulled error - after i have put setVar =1 in boundedPersues 
    	     * and added !=null in scalarized alphamatrix to make alphavector 
    	     * comparing to the original, if dominated cannot have alphavectors (== null) so this is normal
    	     */
    	    //primedV = new DD[alphaVectors.length];
    	    primedVmatrix = new DD[alphaMatrix.length];
    	    /*for (int i=0; i<alphaVectors.length; i++) 
    	    {
    	    	primedV[i] = OP.primeVars(alphaVectors[i],nVars);
    	    }*/
    	    
    	    for (int i=0; i<alphaMatrix.length; i++) 
    	    {
    	    	if(alphaMatrix[i]!=null)
    	    		primedVmatrix[i]=OP.primeVars(alphaMatrix[i],nVars);
    	    }

    	    //maxAbsVal = Math.max(OP.maxabs(concatenateArray(OP.maxAllN(alphaVectors),OP.minAllN(alphaVectors))),1e-10);//original
    	    maxAbsVal_matrix = Math.max(OP.maxabs(concatenateArray(OP.maxAllN(alphaMatrix),OP.minAllN(alphaMatrix))),1e-10);

    	    
    	    int count=0;
    	    int choice;
    	    int nDpBackups = 0;
    	    RandomPermutation permutedIds = new RandomPermutation(Global.random,belRegion.length,debug);
    	    // could be one more than the maximum number at most
    	    
    	    newAlphaMatrix  = new AlphaVector[maxAlphaSetSize+1];
    	    newPointBasedValues = new double[belRegion.length][maxAlphaSetSize+1];
    	    numnewAlphaMatrix = 0;

    	    AlphaVector newMatrix; //TODO: double check do we need to change the class of Alphavector
    	    double [] diff = new double[belRegion.length];
    	    double [] maxcurrpbv;
    	    double [] maxnewpbv;
    	    double [] newValues;
    	    double improvement;

    	    int counter =0; 
    	    // we allow the number of new alpha vectors to get one bigger than 
    	    // the maximum allowed size, since we may be able to cull more than one 
    	    // alpha vector when trimming, bringing us back below the cutoff
    	    while (numnewAlphaMatrix < maxAlphaSetSize && !permutedIds.isempty()) 
    	    {
    		System.out.println("****************** "+counter+"***************");
    	    	if (nDpBackups >= 2*alphaMatrix.length) // Doesn't matter it will be same size alphamatrix and alphavector 
    	    	{
    	    		computeMaxMinImprovement();
    	    		if (bestImprovement > tolerance && bestImprovement > -2*worstDecline)
    	    			break;
    			}
    	    	Global.newHashtables();
    	    	count = count + 1;
    	    	if (count % 100 == 0) 
    	    		System.out.println("count is "+count);
    	    	if (numnewAlphaMatrix == 0)
    	    	{
    	    		choice = 0;
    	    	} else 
    	    	{
    	    		maxcurrpbv = OP.getMax(currentPointBasedValues,permutedIds.permutation);
    	    		maxnewpbv = OP.getMax(newPointBasedValues,numnewAlphaMatrix,permutedIds.permutation);
    	    		permutedIds.getNewDoneIds(maxcurrpbv,maxnewpbv,steptolerance);
    	    		diff = permutedIds.getDiffs(maxcurrpbv,maxnewpbv,steptolerance);
    		    
    	    		if (debug) 
    	    		{
    	    			System.out.print("diff is ");
    	    			for (int k=0; k<diff.length; k++)
    	    				System.out.print(" "+k+":"+diff[k]);
    	    			System.out.println();
    	    		}
    	    		if (permutedIds.isempty())
    	    			break;
    	    		choice = OP.sampleMultinomial(diff);
    	    	}
    	    	if (debug) 
    	    	{
    	    		permutedIds.display();
    	    	}
    	    	int i=permutedIds.getSetDone(choice);
    	    	System.out.println(" num backups so far "+nDpBackups+" num belief points left "+permutedIds.getNumLeft()+" choice "+choice+" i "+i+"tolerance "+steptolerance);
    	    	
    	    	if (numnewAlphaMatrix < 1 || (OP.max(newPointBasedValues[i],numnewAlphaMatrix) - OP.max(currentPointBasedValues[i]) < steptolerance)) 
    	    	{
    	    		
    	    		newMatrix = dpBackup(belRegion[i], primedVmatrix, maxAbsVal_matrix);
    	    		/*
    	    		 * May 2018
    	    		 * Testing the scalarized alphamatrix to be alphavector- we want to see the result plot
    	    		 */
    	    		//newMatrix.alphaVector = scalarize_MOalphavector(newMatrix.alphaVector);
    	    		
    	    		newMatrix.alphaVector = OP.approximate(newMatrix.alphaVector,bellmanErr*(1-discFact)/2.0,onezero);
    	    			    		
    	    		newMatrix.setWitness(i);

    	    		System.out.println(" "+OP.nEdges(newMatrix.alphaVector)+" edges, "+
    	    				OP.nNodes(newMatrix.alphaVector)+" nodes, "+
    				       OP.nLeaves(newMatrix.alphaVector)+" leaves");
    	    		
    	    		nDpBackups = nDpBackups + 1;
    	    		// merge and trim

    	    		newValues = OP.factoredExpectationSparseNoMem(belRegion,newMatrix.alphaVector);
    	    		if (numnewAlphaMatrix < 1)  
    	    		{
    	    			improvement = Double.POSITIVE_INFINITY;
    	    		} else 
    	    		{
    	    			improvement = OP.max(OP.sub(newValues,OP.getMax(newPointBasedValues,numnewAlphaMatrix)));
    	    		}
    	    		//Equation: 
    	    		/*
    	    		 *  in symbolic Perseus algorithm 
    	    		 *  
    	    		 * \alpha^* = arg max_{\alpha^{*}_{a}} b.\alpha^{*}_{a}
    	    		 * 
    	    		 * \aleph^{'}= \aleph^{'} \cup \{ \alpha^* \}
    	    		 */
    	    		if (improvement > tolerance) 
    	    		{
    	    			for (int belid=0; belid<belRegion.length; belid++) 
    	    				newPointBasedValues[belid][numnewAlphaMatrix]=newValues[belid];
    	    			newAlphaMatrix[numnewAlphaMatrix]=newMatrix;
    	    			numnewAlphaMatrix++;
    	    		}
    	    	}
    	    	counter++;
    	    }// end while 
    	    //iteration is over, 
    	    System.out.println("iteration "+stepId+" is over...number of new alpha vectors: "+numnewAlphaMatrix+"   numdp backupds "+nDpBackups);

    	    // compute statistics
    	    // 
    	    computeMaxMinImprovement();

    	  //save data and copy over new to old
    	    alphaMatrix = new DD[numnewAlphaMatrix];
    	    currentPointBasedValues = new double[newPointBasedValues.length][numnewAlphaMatrix];
    	    System.out.println("policy/values are: ");
    	    policy = new int[numnewAlphaMatrix];
    	    policyvalue = new double[numnewAlphaMatrix];
    	    for (int j=0; j<nActions; j++) 
    		uniquePolicy[j]=false;

    	   // double rews0 =0; 
    	   // double rews1 =0; 
    	   // double rews2 =0; 
    	    for (int j=0; j<numnewAlphaMatrix; j++) 
    	    {
    	    	alphaMatrix[j] = newAlphaMatrix[j].alphaVector;
    	    	System.out.println(" "+newAlphaMatrix[j].v_actId+"/"+newAlphaMatrix[j].v_value);
    	    	policy[j] = newAlphaMatrix[j].v_actId;
    	    	
    	    	try 
                {
                	writerb_2.write(Integer.toString(newAlphaMatrix[j].v_actId)+","+Double.toString(newAlphaMatrix[j].v_value)+"\n");
                } 
                catch (IOException e) 
                {
                    System.err.println("Problem writing to the file");
                }
    		/*DD d = actions[policy[j]].rewFn;
    		if(d.getChildren()!=null)
    		{
    			for (int u=0; u<d.getChildren().length;u++)
    			{
    				switch (u)
    				{
    				case 0: rews0 =d.getChildren()[u].getVal();break;
    				case 1: rews1 =d.getChildren()[u].getVal();break;
    				case 2: rews2 =d.getChildren()[u].getVal();break;
    				}
    			}
    			
    		}else
    		{
    			rews0=rews1=rews2=d.getVal();
    		}
    		try 
            {
                writer1.write(Double.toString(rews0)+","+Double.toString(rews1)+","+Double.toString(rews2)+"\n");
                
            } 
            catch (IOException e) 
            {
                System.err.println("Problem writing to the file");
            }*/
    		policyvalue[j] = newAlphaMatrix[j].v_value;
    		if (false && debug)
    		    alphaVectors[j].display();
    		uniquePolicy[policy[j]]=true;
    	    }
    	    
    	    /*try 
            {
    	    	writer1.write("--------------------\n");
            }catch (IOException e) 
            {
                System.err.println("Problem writing to the file");
            }*/
            
    	    
    	    System.out.println("unique policy :");
    	    for (int j=0; j<nActions; j++) 
    		if (uniquePolicy[j]) 
    		    System.out.print(" "+j);
    	    System.out.println();

    	    for (int i=0; i<alphaMatrix.length; i++) {
    		double bval = OP.factoredExpectationSparseNoMem(belRegion[newAlphaMatrix[i].v_witness],alphaMatrix[i]);
    		System.err.println(" "+stepId+" "+policy[i]+" "+bval);
    	    }
    	    for (int j=0; j<belRegion.length; j++) {
    		System.arraycopy(newPointBasedValues[j],0,currentPointBasedValues[j],0,numnewAlphaMatrix);
    	    }
    	    System.out.println("best improvement: "+bestImprovement+"  worstDecline "+worstDecline);
    	    bellmanErr = Math.min(10,Math.max(bestImprovement,-worstDecline));

                try 
                {
                	writerb_1.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+"\n");
                
                } 
                catch (IOException e) 
                {
                    System.err.println("Problem writing to the file");
                }
    	}
     }

    //---------------boundedPerseusStartFromCurrent that takes ArrayList<DD[][]>-------------------------------
    public void boundedPerseusStartFromCurrent(int maxAlpha, int firstStep, int nSteps, ArrayList<DD[][]> alphaMatrixArrayList) {
	DD newAlpha,prevAlpha;
	double bellmanErr;
	double [] onezero = {0};
	boolean dominated;
	double steptolerance;

	alphaMatrixArrayList = listArrayAlphaMatrix_2x3;
	//alphaMatrixArrayList = listArrayAlphaMatrix_3x2;
	maxAlphaSetSize=maxAlpha;

	bellmanErr = 20*tolerance;
	
	//intial alphavectors -- we take it as intial alphamatrix from OLSAR
	/*for(int p=0; p<alphaMatrixArrayList.size();p++)
	{
		DD [][] alphamatrix = alphaMatrixArrayList.get(p);
		alphaVectors = new DD[alphamatrix.length]; 
		for(int s=0; s<alphamatrix.length;s++)
		{
			//System.out.println("s:"+s);
			
			for(int d=0; d<alphamatrix[s].length;d++)
			{
				//System.out.println("d:"+d);
				//alphamatrix[s][d].display();
				alphaVectors[s]=alphamatrix[s][d];
			}
		}
	}*/
	//System.exit(2000);
	 //scalarizeAlphaMatrix(alphaMatrixArrayList);// we don't need this (updating the alphavectod) 
	//-----------------HARD CODE---------------
	/*
	 * we will pass (HARD CODE) the intial alphaVectors and belRegion from original SP just to check
	 * because we get it now from OLSAR and we find the imp are very small comparing to what it used to be 
	 * and this causes the ballmanErr to be constant. 
	 * we want to check if the values from OLSAR causing this or the algorithm we did for multi-obj of the symbolic perseus causing this 
	 */
	// alphaVectors 
	//alphaVectors = new DD[1];
	//double [] alphaArray= { -18.461100494465715,-18.461100494465715,0.0};
	//alphaVectors = convertDoubleAtoDD (alphaArray);
	
	
	//belRegion
	/*double [] b0 = {1.0,0.0,0.0};
	double [] b1 = {0.5,0.5,0.0};
	double [] b2 = {0.15, 0.85,0.0};
	double [] b3 = {0.0,1.0,0.0};
	double [] b4 = {0.0302013422818792,0.9697986577181209,0.0};
	double [] b5 = {0.85 ,0.15,0.0};
	double [] b6 = {0.9697986577181209,0.0302013422818792,0.0};
	double [] b7 = {0.0054655870445344135,0.9945344129554655,0.0};
	double [] b8 = {0.9945344129554655,0.0054655870445344135,0.0};
	double [] b9 = {0.9990311236573288,9.688763426712281E-4,0.0};
	double [] b10 = {9.688763426712281E-4 ,0.9990311236573288,0.0};
	double [] b11 = {1.711147102316738E-4, 0.9998288852897682,0.0};
	double [] b12 = {3.020096943040475E-5,0.9999697990305696,0.0};
	double [] b13 = {0.9998288852897683,1.7111471023167384E-4,0.0};
	double [] b14 = {0.9999697990305696,3.020096943040475E-5,0.0};
	ArrayList<double[]> beliefSamples= new ArrayList<double[]>();
	beliefSamples.add(b0);
	beliefSamples.add(b1);
	beliefSamples.add(b2);
	beliefSamples.add(b3);
	beliefSamples.add(b4);
	beliefSamples.add(b5);
	beliefSamples.add(b6);
	beliefSamples.add(b7);
	beliefSamples.add(b8);
	beliefSamples.add(b9);
	beliefSamples.add(b10);
	beliefSamples.add(b11);
	beliefSamples.add(b12);
	beliefSamples.add(b13);
	beliefSamples.add(b14);
	
	DD [][] beliefPointsfromOLSAR = convertBeliefSampleslistArraytoDD(beliefSamples); */
	//belRegion = beliefPointsfromOLSAR;
	
	//I will create beliefs and alphavectors same as the ones used in Original POMDP
	//alphavectors
	DD [] copyAlphavectors = new DD [2];
	
	
	//beliefRegions
	
	//--------------end HARD CODE --------------

	//-------print intial alphavectors and belRegion--------------
		/*System.out.println("print intial alphaVectors: ");
		for (int o=0; o<alphaVectors.length;o++)
		{
			System.out.println("alphaVectors no.: "+o);
			alphaVectors[o].display();
		}
		
		System.out.println("printing belRegion");
		for (int t=0; t<belRegion.length;t++)
		{
			System.out.println("belRegion no.: "+t);
			for (int y=0; y<belRegion[t].length;y++)
			{
				belRegion[t][y].display();
			}
		}
		
		
		System.exit(2000);*/
		//-----end printing alphavectors and belregion-------------
		/*
		 * alphavectors is scalarized version of alpha matrix 
		 */
	//alphaVectors = scalarizeAlphaMatrix_1 (alphaMatrixArrayList);// this step already done before calling boundedPersuesStartFromCurrent .. but it doesn't matter if we call it again at this stage. 
	currentPointBasedValues = OP.factoredExpectationSparseNoMem(belRegion,alphaVectors);//--------------------------------------->>
	//-------print currentPointBasedValues-----------
	//System.out.println("-------print currentPointBasedValues before the for-----");
	/*for (int d=0; d<currentPointBasedValues.length;d++)
	{
		for (int s=0; s<currentPointBasedValues[d].length;s++)
		{
			System.out.println(currentPointBasedValues[d][s]);
		}
	}*/
	//------end print currentPointBasedValues---------
	
	DD [] primedV;
	ArrayList<DD[][]> primedV_matrix;
	double maxAbsVal=0;
	int counterf=0; 
	for (int stepId=firstStep; stepId<firstStep+nSteps; stepId++) 
	{
		System.out.println("_____________"+counterf+"_______________");
	    steptolerance = tolerance;

	    /*System.out.println(" there are "+alphaVectors.length+" alpha vectors:");
	    if (false && debug) 
	    {
	    	for (int i=0; i<alphaVectors.length; i++) 
	    	{
	    		System.out.println("alpha vector "+i+":");
	    		displayAlphaVectorSums(alphaVectors[i]);
		    
	    	}
	    }*/
	    //----------------PRINT ALPHAVECTOR IN FILE-----------------------
		/*try 
        {
            writer1.write("-------------------printing alphaVectors" + "\n");
            for (int i=0; i<alphaVectors.length; i++) {
    		    System.out.println("alpha vector "+i+":");
    		    displayAlphaVectorSums(alphaVectors[i]);
    		    if(alphaVectors[i].getChildren()!=null)
    		    {
    		    for (int r =0; r<alphaVectors[i].getChildren().length;r++)
        		{
        			
        			writer1.write(Double.toString(alphaVectors[i].getChildren()[r].getVal())+"\n");
        			
        		}}else
        		{
        			writer1.write(Double.toString(alphaVectors[i].getVal())+"\n");
        		}
            }
            writer1.write("-------------------end printing alphaVectors" + "\n");
        } 
        catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }
		*///-------------------------------------
	
	    /*
	     * the alphamatrix is scalarized and passed to alphavectors so the primedV is here the scalarized alphaMatrix
	     */
	    primedV = new DD[alphaVectors.length];

	    //nVars = nStateVars+nObsVars;//=2
	    
	    for (int i=0; i<alphaVectors.length; i++) 
	    {
	    	//System.out.println("---alphaVectors[i]");
	    	//alphaVectors[i].display();
	    	primedV[i] = OP.primeVars(alphaVectors[i],nVars);
	    	//System.out.println("---primedV[i]");
	    	//primedV[i].display();
	    }
	    //---------create primedV_matrix---------
	    primedV_matrix = new ArrayList<DD[][]>(); 
	    //primedV_matrix = new ArrayList<DD[alphaMatrixArrayList.get(0).length][alphaMatrixArrayList.get(0)[0].length]>(); 
	    for(int p=0; p<alphaMatrixArrayList.size();p++)
	    {
	    	DD [][] alphamatrix = alphaMatrixArrayList.get(p);
	    	for(int m =0; m<alphamatrix.length;m++)
	    	{
	    		for(int n=0; n<alphamatrix[m].length;n++)
	    		{
	    			alphamatrix[m][n]=OP.primeVars(alphamatrix[m][n],nVars);
	    			//System.out.println("print alphaMatrix:"); 
	    			//alphamatrix[m][n].display();
	    		}
	    	}
	    	
	    	primedV_matrix.add(p, alphamatrix);	
	    }
	    
	    System.out.println("########################################");
	    System.out.println("alphaMatrixArrayList size: "+ alphaMatrixArrayList.size());
	    System.out.println("########################################");
	    
	    
	  
	    //print primedV_matrix
	    /*System.out.println("primedV_matrix: first round ");
	    for (int u=0; u<primedV_matrix.size();u++)
	    {
	    	for (int e=0; e<primedV_matrix.get(u).length;e++)
	    	{
	    		System.out.println("element DD no."+ e+ "of element:"+ u);
	    		for (int q=0; q<primedV_matrix.get(u)[e].length;q++)
	    		{
	    			primedV_matrix.get(u)[e][q].display();
	    		}
	    	}
	    		
	    }*/
	   // System.exit(200);
	    //---------------------------------------
	    // we should use alphaMatrixArrayList instead of the alphaVectors 
	    //maxAbsVal = Math.max(OP.maxabs(concatenateArray(OP.maxAllN(alphaVectors),OP.minAllN(alphaVectors))),1e-10);//original
	    
	    //double [][] maxarray = OP.maxAllN(alphaMatrixArrayList);
	    //double [][] maxarray = OP.maxAllN(alphaMatrixArrayList);
	    //alphaVectors = scalarizeAlphaMatrix_1 (alphaMatrixArrayList);//already alphavectors scalarized 
	    //alphavectors are the alphamatrix scalarized 
	    maxAbsVal = Math.max(OP.maxabs(concatenateArray(OP.maxAllN(alphaVectors),OP.minAllN(alphaVectors))),1e-10);//original
	   

	    int count=0;
	    int choice;
	    int nDpBackups = 0;
	    RandomPermutation permutedIds = new RandomPermutation(Global.random,belRegion.length,debug);
	    // could be one more than the maximum number at most
	    //newAlphaMatrix  = new Alpha_Matrix[maxAlphaSetSize+1];//MARCH APRIL 2018
	    newPointBasedValues = new double[belRegion.length][maxAlphaSetSize+1];
	    numnewAlphaMatrix = 0;

	    Alpha_Matrix newVector; 
	    double [] diff = new double[belRegion.length];
	    double [] maxcurrpbv;
	    double [] maxnewpbv;
	    double [] newValues;
	    double improvement;

	    int counter =0; 
	    // we allow the number of new alpha vectors to get one bigger than 
	    // the maximum allowed size, since we may be able to cull more than one 
	    // alpha vector when trimming, bringing us back below the cutoff
	    while (numnewAlphaMatrix < maxAlphaSetSize && !permutedIds.isempty()) 
	    {
		System.out.println("****************** "+counter+"***************");
	    	if (nDpBackups >= 2*alphaVectors.length) 
	    	{
	    		computeMaxMinImprovement();
	    		if (bestImprovement > tolerance && bestImprovement > -2*worstDecline)
	    			break;
			}
	    	Global.newHashtables();
	    	count = count + 1;
	    	if (count % 100 == 0) 
	    		System.out.println("count is "+count);
	    	if (numnewAlphaMatrix == 0)
	    	{
	    		choice = 0;
	    	} else 
	    	{
	    		maxcurrpbv = OP.getMax(currentPointBasedValues,permutedIds.permutation);
	    		maxnewpbv = OP.getMax(newPointBasedValues,numnewAlphaMatrix,permutedIds.permutation);
	    		permutedIds.getNewDoneIds(maxcurrpbv,maxnewpbv,steptolerance);
	    		diff = permutedIds.getDiffs(maxcurrpbv,maxnewpbv,steptolerance);
		    
	    		if (debug) {
	    			System.out.print("diff is ");
	    			for (int k=0; k<diff.length; k++)
	    				System.out.print(" "+k+":"+diff[k]);
	    			System.out.println();
	    		}
	    		if (permutedIds.isempty())
	    			break;
	    		choice = OP.sampleMultinomial(diff);
	    	}
	    	if (debug) {
	    		permutedIds.display();
	    	}
	    	int i=permutedIds.getSetDone(choice);
	    	System.out.println(" num backups so far "+nDpBackups+" num belief points left "+permutedIds.getNumLeft()+" choice "+choice+" i "+i+"tolerance "+steptolerance);
	    	
	    	if (numnewAlphaMatrix < 1 || (OP.max(newPointBasedValues[i],numnewAlphaMatrix) - OP.max(currentPointBasedValues[i]) < steptolerance)) 
	    	{
	    		// dpBackup
	    		//newVector = dpBackup(belRegion[i], primedV, maxAbsVal, alphaMatrixArrayList);//---------------------------------->>TODO: to be modified
	    		newVector = dpBackup(belRegion[i], primedV, maxAbsVal, alphaMatrixArrayList, primedV_matrix);
	    		
	    		//newVector.alphaMatrix should be alphavectors? 
	    		//DD  alphaMatrix_scalarized = scalarizeAlphaMatrix(newVector.alphaMatrix);
	    		//alphaMatrix_scalarized = OP.approximate(alphaMatrix_scalarized,bellmanErr*(1-discFact)/2.0,onezero);
	    		newVector.alphaMatrix = OP.approximate(newVector.alphaMatrix,bellmanErr*(1-discFact)/2.0,onezero);
	    			    		
	    		newVector.setWitness(i);

	    		System.out.println(" "+OP.nEdges(newVector.alphaMatrix)+" edges, "+
	    				OP.nNodes(newVector.alphaMatrix)+" nodes, "+
				       OP.nLeaves(newVector.alphaMatrix)+" leaves");
	    		
	    		nDpBackups = nDpBackups + 1;
	    		// merge and trim
	    		
	    		/*
	    		 * before multiply with the belRegion, alpha matrix should be scalarized 
	    		 */
	    		
	    		//DD  alphaMatrix_scalarized = scalarizeAlphaMatrix(newVector.alphaMatrix);
	    		
	    		//newValues = OP.factoredExpectationSparseNoMem(belRegion,alphaMatrix_scalarized);
	    		
	    		newValues = OP.factoredExpectationSparseNoMem(belRegion,newVector.alphaMatrix);
	    		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+newValues.length);
	    		for(int qq=0; qq<newValues.length;qq++)
	    		{
	    			System.out.println("newValues"+qq+": "+newValues[qq]);
	    		}
	    		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	    		
	    		//------------end print newValues---------
	    		if (numnewAlphaMatrix < 1)  
	    		{
	    			improvement = Double.POSITIVE_INFINITY;
	    		} else 
	    		{
	    			improvement = OP.max(OP.sub(newValues,OP.getMax(newPointBasedValues,numnewAlphaMatrix)));
	    		}
	    		//Equation: 
	    		/*
	    		 *  in symbolic Perseus algorithm 
	    		 *  
	    		 * \alpha^* = arg max_{\alpha^{*}_{a}} b.\alpha^{*}_{a}
	    		 * 
	    		 * \aleph^{'}= \aleph^{'} \cup \{ \alpha^* \}
	    		 */
	    		if (improvement > tolerance) 
	    		{
	    			for (int belid=0; belid<belRegion.length; belid++) 
	    				newPointBasedValues[belid][numnewAlphaMatrix]=newValues[belid];
	    			//newAlphaMatrix[numnewAlphaMatrix]=newVector;////MARCH APRIL 2018
	    			numnewAlphaMatrix++;
	    		}
	    	}
	    	counter++;
	    }// end while 
	    //iteration is over, 
	    System.out.println("iteration "+stepId+" is over...number of new alpha vectors: "+numnewAlphaMatrix+"   numdp backupds "+nDpBackups);

	    // compute statistics
	    // 
	    computeMaxMinImprovement();

	   
	    //System.out.println("&&&&&&&&&&&&&&&&&&&&&&& numnewAlphaMatrix: "+numnewAlphaMatrix);
	    //save data and copy over new to old
	    //alphaVectors = new DD[numnewAlphaMatrix];//--------------------------------------------------------------------->>TODO: to take new definition 
	    currentPointBasedValues = new double[newPointBasedValues.length][numnewAlphaMatrix];
	    System.out.println("policy/values are: ");
	    policy = new int[numnewAlphaMatrix];
	    policyvalue = new double[numnewAlphaMatrix];
	    for (int j=0; j<nActions; j++) 
		uniquePolicy[j]=false;

	    finalAlphaVectors = new ArrayList<>();//----------------------------------------------->TODO: in order to have evry iteration the latest vectors (not all) 
    	
	    DD [] alphamatrcies = new DD[numnewAlphaMatrix]; 
	    
	    for (int j=0; j<numnewAlphaMatrix; j++) 
	    {
	    	//alphaVectors[j] = newAlphaMatrix[j].alphaMatrix;//------------------------------------------------------->>TODO: to take new alpha 
		
	    	//finalAlphaVectors.add(newAlphaMatrix[j]);//MARCH APRIL 2018
		
	    	//alphamatrcies[j]=newAlphaMatrix[j].alphaMatrix;////MARCH APRIL 2018
	    	System.out.println("========================>"+newAlphaMatrix[j].v_actId+"/"+newAlphaMatrix[j].v_value);
	    	policy[j] = newAlphaMatrix[j].v_actId;
	    	policyvalue[j] = newAlphaMatrix[j].v_value;
	    	if (false && debug)
	    		alphaVectors[j].display();
	    	uniquePolicy[policy[j]]=true;
	    	
	    	try 
            {
                //writer.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+","+ Double.toString(maxAbsVal) + ","+ Double.toString(bestImprovement)+","+Integer.toString(numnewAlphaMatrix)+","+Double.toString(bval)+","+Integer.toString(alphaMatrixArrayList.size())+"\n");
                //writer1.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+","+ Double.toString(maxAbsVal) + "\n");
            	writerb_2.write(Integer.toString(newAlphaMatrix[j].v_actId)+","+Double.toString(newAlphaMatrix[j].v_value)+"\n");
            
            } 
            catch (IOException e) 
            {
                System.err.println("Problem writing to the file");
            }
	    }
	    
	  
	    //################ finalAlphaVectors vs. alphaMatrixArrayList##########
	    /*
	     * the problem is that in boundedPerseusStartFromCurrent there is for loop and in this one 
	     * it created primedV from the alphavectors of every iteration (outcome of while loop). but in our case
	     * the primedV is always the same which is the initial alphavector. now i have i see that the finalAlphaVectors is 
	     * the alphavectors that we pass to OLSAR at the end. which should be the same used in primedV
	     * 
	     * TODO: one question: the alphavectors parameter is updated or every iteration is new. how come i will send the all or exact for every iteration 
	     * this lead also to the fact that symbolic persues only takes the improved vectors not previous ones also to form the 50
	     */
	    /*System.out.println("###########ALPHAVECTORS###############");
	    for(int o=0; o<alphaVectors.length;o++)
	    {
	    	System.out.println("***alphaVectors no: "+o);
	    	alphaVectors[o].display();
	    }
	    System.out.println("##########finalALphaVectors############");
	    for(int h=0; h<finalAlphaVectors.size();h++)
	    {
	    	System.out.println("***finalAlphaVectors no: "+h);
	    	finalAlphaVectors.get(h).alphaMatrix.display();
	    }
	    System.out.println("##########alphaMatrixArrayList#########");
	    for (int b=0; b<alphaMatrixArrayList.size();b++)
	    {
	    	DD [][] alphamatrix = alphaMatrixArrayList.get(b);
	    	for(int m =0; m<alphamatrix.length;m++)
	    	{
	    		for(int n=0; n<alphamatrix[m].length;n++)
	    		{
	    			System.out.println("***alphamatrix: "+m + ","+n);
	    			alphamatrix[m][n].display();
	    			
	    		}
	    	}
	    	
	    }*/
	   
	    /*
	     * now we need to update alphaMatrixArrayList with the finalAlphaVectors since 
	     * alphaMatrixArrayList what is going to be used every round of the for to create primedv 
	     * the current version will update by adding new alphas (doesn't remove previous ones)
	     */
	    	
	    alphaMatrixArrayList = new ArrayList <DD[][]>();
	    
	    DD [][] matrix = new DD[finalAlphaVectors.get(0).alphaMatrix.getChildren().length][];// 2x3
	    for(int h=0; h<finalAlphaVectors.size();h++)// we will loop the number of elements in the array 
	    {
	    	
	    	for (int x=0; x<matrix.length;x++)
	    	{
	    		if(finalAlphaVectors.get(h).alphaMatrix.getChildren()[x].getChildren()!=null)
	    		{
	    			matrix[x]=finalAlphaVectors.get(h).alphaMatrix.getChildren()[x].getChildren();
	    		}else
	    		{
	    			DD [] temp = new DD [1];
	    			temp [0]=finalAlphaVectors.get(h).alphaMatrix.getChildren()[x];
	    			matrix[x]= temp; 
	    		}
	    		
	    	}
	    	
	    	alphaMatrixArrayList.add(h,matrix);//TODO debgug double check 
	    }
	    
	    listArrayAlphaMatrix_2x3 = alphaMatrixArrayList;
	    //####################################################
	    System.out.println("unique policy :");
	    for (int j=0; j<nActions; j++) 
		if (uniquePolicy[j]) 
		    System.out.print(" "+j);
	    System.out.println();

	    double bval=0;
	    alphaVectors = scalarizeAlphaMatrix_1 (listArrayAlphaMatrix_2x3);// i added this line
	    for (int i=0; i<alphaVectors.length; i++) 
	    {
	    	 bval = OP.factoredExpectationSparseNoMem(belRegion[newAlphaMatrix[i].v_witness],alphaVectors[i]);
	    	System.err.println(" "+stepId+" "+policy[i]+" "+bval);
	    }
	    for (int j=0; j<belRegion.length; j++) 
	    {
	    	System.arraycopy(newPointBasedValues[j],0,currentPointBasedValues[j],0,numnewAlphaMatrix);
	    }
	    System.out.println("best improvement: "+bestImprovement+"  worstDecline "+worstDecline);
	    //System.out.println("Math.max(bestImprovement,worstDecline): "+Math.max(bestImprovement,worstDecline));
	   
	    bellmanErr = Math.min(10,Math.max(bestImprovement,-worstDecline));// original line 
	    //bellmanErr = Math.min(10,Math.max(bestImprovement,worstDecline));
	   // System.out.println("bellmanErr: "+bellmanErr);
            try 
            {
                //writer.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+","+ Double.toString(maxAbsVal) + ","+ Double.toString(bestImprovement)+","+Integer.toString(numnewAlphaMatrix)+","+Double.toString(bval)+","+Integer.toString(alphaMatrixArrayList.size())+"\n");
                //writer1.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+","+ Double.toString(maxAbsVal) + "\n");
            	writerb_1.write(Double.toString(bestImprovement)+","+ Double.toString(bellmanErr)+","+ Double.toString(maxAbsVal) + ","+ Double.toString(bestImprovement)+","+Integer.toString(numnewAlphaMatrix)+","+Double.toString(bval)+","+Integer.toString(alphaMatrixArrayList.size())+"\n");
            
            } 
            catch (IOException e) 
            {
                System.err.println("Problem writing to the file");
            }
            //System.exit(2000);
            counterf++;
		}// end for loop 
	    
    }
    //---------------computeMaxMinImprovement--------------------------------------------
    public void computeMaxMinImprovement() 
    {
    	double imp;
    	bestImprovement = Double.NEGATIVE_INFINITY;
    	worstDecline = Double.POSITIVE_INFINITY;
    	for (int j=0; j<belRegion.length; j++) 
    	{
    		// find biggest improvement at this belief point
    		System.out.println("************************");
    		System.out.println("newPointBasedValues[j]:"+newPointBasedValues[j].length);
    		System.out.println("numnewAlphaMatrix: "+numnewAlphaMatrix);
    		System.out.println("currentPointBasedValues[j]: "+currentPointBasedValues[0][0]);//+" , "+currentPointBasedValues[0][1]);
    		System.out.println("************************");
    		//System.out.println("OP.max(newPointBasedValues[j],numnewAlphaMatrix): "+OP.max(newPointBasedValues[j],numnewAlphaMatrix)+" OP.max(currentPointBasedValues[j]: "+OP.max(currentPointBasedValues[j]));
    		imp = OP.max(newPointBasedValues[j],numnewAlphaMatrix)-OP.max(currentPointBasedValues[j]);
    		
    		System.out.println("#####################################################");
    		System.out.println("imp: "+imp);
    		System.out.println("#####################################################");
    		if (imp > bestImprovement) 
    			bestImprovement = imp;
    		if (imp < worstDecline) 
    			worstDecline = imp;
    	}
    }
    //--------------------------------------------------------------------
    public void save(String filename) throws FileNotFoundException, IOException {
	
	FileOutputStream f_out;
	// save to disk
	// Use a FileOutputStream to send data to a file
	// called myobject.data.
	f_out = new FileOutputStream (filename);

	// Use an ObjectOutputStream to send object data to the
	// FileOutputStream for writing to disk.
	ObjectOutputStream obj_out = new
	    ObjectOutputStream (f_out);
	    
	// Pass our object to the ObjectOutputStream's
	// writeObject() method to cause it to be written out
	// to disk.
	obj_out.writeObject (this);
    }
    public void displayPolicy() {
	System.out.print("  "+alphaVectors.length+" alpha vectors ... policy/values are: ");
	for (int j=0; j<alphaVectors.length; j++) {
	    System.out.println(" "+policy[j]);
	    alphaVectors[j].display();
	}
	System.out.println("");
    }
    // this replaces newPointBasedValues based on the input argument pointBasedValues
    // and newAlphaMatrix based on the input in alphaVectors
    public int trim(Alpha_Matrix [] alphaVectors, int nVectors, double [][] pointBasedValues, int maxSize, double minImprovement)
    {
	if (nVectors <= 1)
	    return 0;
	
	double [] improvement = new double[nVectors];
	boolean [] toremove = new boolean[nVectors];
	int numremoved=0;
	double maxv,maxd,minimp,thediff;
	int minimpi=0;
	minimp = Double.POSITIVE_INFINITY;
	for (int i=0; i<nVectors; i++) {
	    // find the belief point at which the ith alpha vector
	    // has the greatest improvement over all other alpha vectors
	    // the amount of improvement is improvement[i]
	    if (!toremove[i]) {
		maxd = Double.NEGATIVE_INFINITY;
		for (int j=0; j<pointBasedValues.length; j++) {
		    // get the maximum over all other vectors for jth belief point
		    maxv = Double.NEGATIVE_INFINITY;
		    for (int k=0; k<nVectors; k++) {
			if (!toremove[k] && !(k==i)) {
			    if (pointBasedValues[j][k] > maxv) 
				maxv = pointBasedValues[j][k];
			}
		    }
		    thediff = pointBasedValues[j][i]-maxv;
		    if (thediff > maxd) 
			maxd = thediff;
		}
		improvement[i] = maxd;
		if (improvement[i] < minImprovement) {
		    toremove[i] = true;
		    numremoved++;
		}
		if (improvement[i] < minimp) {
		    minimp = improvement[i];
		    minimpi = i;
		}
	    }
	}
	// if none were removed, we may still have to cull one  
	// cull the one with the smallest improvement.
	if (nVectors > maxSize && numremoved == 0) {
	    toremove[minimpi]=true;
	    numremoved=1;
	}
 	//System.out.println("min improvement "+minimp+" num removed "+numremoved);

	// finally, actually remove the vectors that should be 
	int j=0;
	for (int i=0; i<nVectors; i++) {
	    if (!toremove[i]) {
		//newAlphaMatrix[j] = alphaVectors[i];////MARCH APRIL 2018
		for (int k=0; k<pointBasedValues.length; k++) 
		    newPointBasedValues[k][j]=pointBasedValues[k][i];
		j++;
	    } else {
		System.out.print(" removed alpha vector "+i);
	    }
	}
	return numremoved;
    }
    //------------- original class AlphaVector-----------------------------------
    /*public class AlphaVector implements Serializable 
    {
    
    	public DD[] alphaVectorArray;
    	public DD alphaVector;
    	double value;
    	double []values;
    	int actId;
    	int witness;
    	int [] obsStrat;
    	
    	public AlphaVector(DD[] a, double v, int act, int [] os) 
    	{
	    
    		alphaVectorArray = a;
    		value = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public AlphaVector(DD a, double v, int act, int [] os) 
    	{
	    
    		alphaVector = a;
    		value = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public AlphaVector(DD a, double [] v, int act, int [] os) // i override the constructor to add the array of values
    	{
	    
    		alphaVector = a;
    		values = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public AlphaVector(AlphaVector a) 
    	{
    		alphaVector = a.alphaVector;
    		value = a.value;
    		actId = a.actId;
    		witness = a.witness;
    		obsStrat = a.obsStrat;
    	}
    	public void setWitness(int i) 
    	{
    		witness = i;
    	}

    }*/
    //-------------Class AlphaMatrix----------------------------------
    /*
     * basically override AlphaVector class and same methods
     * purpose: to make sure we are not using alphavector global variables 
     * and to eliminate confusion -- we are dealing with matrix not vectors (multi-obj) 
     * 
     * NOTE: Alpha_Matrix is this class.. AlphaMatrix is class in OLSAR
     */
    
    public class Alpha_Matrix implements Serializable 
    {
    
    	public DD[] alphaVectorArray;// confirm if we are using this or not? 
    	public DD alphaMatrix;
    	double value;
    	double []values;
    	int actId;
    	int witness;
    	int [] obsStrat;
    	
    	public Alpha_Matrix(DD[] a, double v, int act, int [] os) 
    	{
	    
    		alphaVectorArray = a;
    		value = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public Alpha_Matrix(DD a, double v, int act, int [] os) 
    	{
	    
    		alphaMatrix = a;
    		value = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public Alpha_Matrix(DD a, double [] v, int act, int [] os) 
    	{
	    
    		alphaMatrix = a;
    		values = v;
    		actId = act;
    		obsStrat = os;
    	}
    	public Alpha_Matrix(Alpha_Matrix a) 
    	{
    		alphaMatrix = a.alphaMatrix;
    		value = a.value;
    		actId = a.actId;
    		witness = a.witness;
    		obsStrat = a.obsStrat;
    	}
    	public void setWitness(int i) 
    	{
    		witness = i;
    	}

    }

    //-----------------------------------------------
    public void displayAlphaVectorSum(DD alphaVector, int varId) {
	DD tempDD = OP.addMultVarElim(alphaVector,MySet.remove(varIndices,varId+1));
	tempDD.display();
    }
    public void displayAlphaVectorSums(DD alphaVector) {
	for (int i=0; i<nStateVars; i++)
	    displayAlphaVectorSum(alphaVector, i);
    }
    public void displayAlphaVectorPrimeSum(DD alphaVector, int varId) {
	DD tempDD = OP.addMultVarElim(alphaVector,MySet.remove(primeVarIndices,varId+nVars+1));
	tempDD.display();
    }
    public void displayAlphaVectorPrimeSums(DD alphaVector) {
	for (int i=0; i<nStateVars; i++)
	    displayAlphaVectorPrimeSum(alphaVector, i);
    }
   //------Alphavector class------------------
    /*
     * May 2018
     * Added the DD alphaMatrix 
     */
    public class AlphaVector implements Serializable {
	DD alphaVector;
	double v_value;
	int v_actId;
	int v_witness;
	int [] v_obsStrat;
	
	DD alphaMatrix;
	double m_value;
	int m_actId;
	int m_witness;
	int [] m_obsStrat;
	
	public AlphaVector(DD vector, double v_v, int v_act, int [] v_os) 
	{
	    alphaVector = vector;
	    v_value = v_v;
	    v_actId = v_act;
	    v_obsStrat = v_os;
	    
	    /*alphaMatrix = matrix;
	    m_value = m_v;
	    m_actId = m_act;
	    m_obsStrat = m_os;*/
	}
	public AlphaVector(AlphaVector v) 
	{
	    alphaVector = v.alphaVector;    
	    v_value = v.v_value;
	    v_actId = v.v_actId;
	    v_witness = v.v_witness;
	    v_obsStrat = v.v_obsStrat;
	    
	    /*alphaMatrix = m.alphaMatrix;
	    m_value = m.m_value;
	    m_actId = m.m_actId;
	    m_witness = m.m_witness;
	    m_obsStrat = m.m_obsStrat;*/
	}
	public void setWitness(int i) 
	{
	    v_witness = i;
	    //m_witness = i;
	}

    }
    //---------------dpbackup function------------------------------------------------------------------
    /*
     * March-May 2018
     * I have used the original dpbackup and override it 
     */
    
    public AlphaVector dpBackup(DD [] belState, DD [] primedV_matrix, double maxAbsVal_matrix)
    {
    	NextBelState [] nextBelStates;

    	double smallestProb;
    	if (ignoremore) 
    		smallestProb = tolerance;
    	else
    		smallestProb = tolerance/maxAbsVal_matrix;

    	nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);

    	// precompute obsVals
    	for (int actId = 0; actId<nActions; actId++) 
    	{
    		nextBelStates[actId].getObsVals(primedV_matrix);
    	}
    	double bestValue = Double.NEGATIVE_INFINITY;
    	double actValue;
    	int bestActId=0;
    	int [] bestObsStrat = new int[nObservations];
	
    	for (int actId = 0; actId<nActions; actId++) 
    	{
    		actValue = 0.0;
    		// compute immediate rewards
    		//actValue = actValue + OP.factoredExpectationSparseNoMem(belState,actions[actId].rewFn);
    		DD costObject_copy= DD.zero;
	    	if(actions[actId].costObj.getChildren()!=null)//its not just a leaf
	    	{	
	    		costObject_copy=recursiveScalarizeMatrix(actions[actId].costObj);	
	    	}
    		//actValue = actValue + OP.factoredExpectationSparseNoMem(belState,actions[actId].costObj);
    		actValue = actValue + OP.factoredExpectationSparseNoMem(belState,costObject_copy);
    		
    		// compute observation strategy
    		nextBelStates[actId].getObsStrat();
    		actValue = actValue + discFact*nextBelStates[actId].getSumObsValues();
	    
    		if (actValue > bestValue) 
    		{
    			bestValue = actValue;
    			bestActId = actId;
    			bestObsStrat = nextBelStates[actId].obsStrat;
    		}
    	}
    	// construct corresponding alpha vector
    	DD newAlpha = DD.zero;
    	DD nextValFn = DD.zero;
    	DD obsDd;
    	int tobsid;
    	int [] obsConfig = new int[nObsVars];
    	for (int alphaId = 0; alphaId < alphaMatrix.length; alphaId++) //it doesn't matter if alphamatrix or alphavectors length as long we scalarize alphavector and are kept
    	{
    		if (MySet.find(bestObsStrat,alphaId)>=0) 
    		{
    			obsDd = DD.zero;
    			//for (int obsId = 0; obsId < bestObsStrat.length; obsId++) {
    			for (int obsId = 0; obsId < nObservations; obsId++) 
    			{
    				if (bestObsStrat[obsId]==alphaId) 
    				{
    					obsConfig = statedecode(obsId+1,nObsVars,obsVarsArity);
    					obsDd = OP.add(obsDd,Config.convert2dd(stackArray(primeObsIndices,obsConfig)));
    				}
    			}
    			nextValFn = OP.add(nextValFn,OP.multN(concatenateArray(DDleaf.myNew(discFact),obsDd,primedV_matrix[alphaId])));
    		}
    	}

    	
    	newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn), 
				     concatenateArray(primeVarIndices,primeObsIndices));
    	/*
    	 * May 2018
    	 * first modification: added this line 
    	 */
    	/*if(newAlpha.getVar()!=0 && newAlpha.getChildren()!=null)
    	{
    		for(int x=0; x<newAlpha.getChildren().length;x++)
    		{
    			if(newAlpha.getChildren()[x].getVar()!=0)
    				newAlpha.getChildren()[x].setVar(1);
    		}
    	}
    	if(newAlpha.getVar()!=0)
    		newAlpha.setVar(1);*/
    	//newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].rewFn));
    	
    	DD costObject_copy= DD.zero;
    	if(actions[bestActId].costObj.getChildren()!=null)//its not just a leaf
    	{	
    		costObject_copy=recursiveScalarizeMatrix(actions[bestActId].costObj);	
    	}

    	//newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].costObj));
    	newAlpha = OP.addN(concatenateArray(newAlpha,costObject_copy));

    	bestValue = OP.factoredExpectationSparse(belState,newAlpha);
    	// package up to return
    	AlphaVector returnAlpha = new AlphaVector(newAlpha,bestValue,bestActId,bestObsStrat);
	
    	try
        {
        	writerd_1.write(Double.toString(smallestProb)+","+Double.toString(bestValue)+","+Integer.toString(bestActId)+","+Double.toString(bestValue)+","+"\n");
        }catch (IOException e)
        {
        	System.err.println("Problem writing to the file");
        }
    	return returnAlpha;
    }
    //--------------modified dpbackup--------------------------------------------------------
    public Alpha_Matrix dpBackup(DD [] belState, DD [] primedV, double maxAbsVal, ArrayList<DD[][]> alphaMatrixArrayList,ArrayList<DD[][]> primedVArrayList)
    {
    	//Equation 
    	/*
    	 * this equation from SYmbolic Perseus algorithm 
    	 * \alpha^{*}_{a} = r_{a} + \gamma \sum_{z} T^{a,z} argmax_{\alpha\in\aleph}b^{a}_{z}.\alpha
    	 */	
    	boolean debug =false; 
    		
    	NextBelState [] nextBelStates; 
    	// get next unnormalised belief states
    	//System.out.println("smallestProb "+tolerance);
    	double smallestProb;
    	if (ignoremore) 
    		smallestProb = tolerance;
    	else
	    smallestProb = tolerance/maxAbsVal;
	
    	nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);// have 3 belStates for three actions  
	
	// precompute obsVals
	for (int actId = 0; actId<nActions; actId++) 
	{
	   // nextBelStates[actId].getObsVals(primedV);
	    nextBelStates[actId].getObsVals(primedVArrayList);
	
	}
	
	
	//-------modified part----primedVmatrix----------------
	/*
	 * we decided we will not change the first part of the dpback for now where bestActionId is selected. so the primeV will be used
	 * (the scalarized matrix will be used in this case), but later on when calculate the newAlpha we will use primedVArrayList the actual alphaMatrix
	 */
	/*for (int actId = 0; actId<nActions; actId++) 
	{
	    nextBelStates[actId].getObsVals(primedVArrayList);//----------------------------------------------------->>TODO: 
	
	}*/
	//primedVArrayList
	//-----------------------------------------------------
	
	double bestValue = Double.NEGATIVE_INFINITY;
	
	double [] bestValue_multiObj = new double [nObj]; // this array is used for calculate the bestValue at the end of this function. 
	for(int d =0; d<bestValue_multiObj.length;d++)
	{
		bestValue_multiObj[d]=Double.NEGATIVE_INFINITY;
	}
	
	double actValue;
	int bestActId=0;
	int [] bestObsStrat = new int[nObservations];
	
	
	for (int actId = 0; actId<nActions; actId++) 
	{
	    actValue = 0.0;
	    // compute immediate rewards
	    actValue = actValue + OP.factoredExpectationSparseNoMem(belState,actions[actId].rewFn);  
	    // compute observation strategy
	    nextBelStates[actId].getObsStrat(); 
	    actValue = actValue + discFact*nextBelStates[actId].getSumObsValues();
	    if (actValue > bestValue) 
	    {
	    	bestValue = actValue;
	    	bestActId = actId;
			bestObsStrat = nextBelStates[actId].obsStrat;
	    }
	}
	// construct corresponding alpha vector
	DD newAlpha = DD.zero;
	DD newAlpha_matrix = DD.zero;
	DD nextValFn = DD.zero;
	DD obsDd;
	int [] obsConfig = new int[nObsVars];
	
	for (int alphaId = 0; alphaId < primedVArrayList.size(); alphaId++) //---- length of arrayList
	{

	    if (MySet.find(bestObsStrat,alphaId)>=0) 
	    {
			
	    	obsDd = DD.zero;
	    	//for (int obsId = 0; obsId < bestObsStrat.length; obsId++) {
	    	
	    	for (int obsId = 0; obsId < nObservations; obsId++) 
	    	{
	    	
	    		if (bestObsStrat[obsId]==alphaId) 
	    		{
	    			obsConfig = statedecode(obsId+1,nObsVars,obsVarsArity);
	    			
	    			obsDd = OP.add(obsDd,Config.convert2dd(stackArray(primeObsIndices,obsConfig)));
	    			
	    		}
	    	}

	    	//obsDd.setVar(1);
	    	
	    	DD [][] newValFn_Matrix = new DD [primedVArrayList.get(alphaId).length][];
	    	DD [] newValFn_children = new DD [primedVArrayList.get(alphaId).length]; 
	    	//Step 1: concatunate array 
	    	for(int u=0; u<primedVArrayList.get(alphaId).length;u++)
	    	{
	    		for(int r=0; r<primedVArrayList.get(alphaId)[u].length;r++)
	    		{
	    			if (primedVArrayList.get(alphaId)[u].length==1)
	    			{
	    				newValFn_Matrix[u]=concatenateArray(DDleaf.myNew(discFact),obsDd,primedVArrayList.get(alphaId)[u][r]);
	    			}	
	    			else
	    			{
	    				DD column = DD.zero;
	    				DD [] childColumn = new DD [primedVArrayList.get(alphaId)[u].length];
	    				for(int f=0; f<primedVArrayList.get(alphaId)[u].length;f++)
	    				{
	    					childColumn[f]=primedVArrayList.get(alphaId)[u][f];
	    				}
	    				column=DDnode.myNew(1, childColumn);
	    				
	    				newValFn_Matrix[u]=concatenateArray(DDleaf.myNew(discFact),obsDd,column);
	    			}
	    			//Step 2: multN what is inside the DD[] will be multiplied with each other 
	    			newValFn_children[u]=OP.multN(newValFn_Matrix[u]);
	    			//newValFn_children[u].setVar(1);
	    		}
	    	}
	    	
	    	//Step 3: add and construct the DD nextValFn 
	    	DD children = DDnode.myNew(1, newValFn_children);
	    	int [] n1 = children.getVarSet();
	    	nextValFn = OP.add(nextValFn,children);
	    	
	    	
	    	//nextValFn = OP.add(nextValFn,OP.multN(concatenateArray(DDleaf.myNew(discFact),obsDd,primedV[alphaId])));//------original
	    	
	    }
	}// end for loop # of alphas
	
	
		//addMultarElim (summout variables from a product of DDs using variable elimincation)
	DD[] nextValFn_children;
	if(nextValFn.getChildren()!=null)
	{
		nextValFn_children = nextValFn.getChildren();
	}else
	{
		nextValFn_children= new DD [0];
		nextValFn_children [0] = nextValFn;
	}
	
	/*
	 * create the newALpha, for matrix each column of the nextValFn is calculated alone.. 
	 */

	DD[]newAlpha_children = new DD [nextValFn_children.length]; 
	for(int c = 0; c<nextValFn_children.length;c++)
	{
		newAlpha_children[c] =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn_children[c]), 
			     concatenateArray(primeVarIndices,primeObsIndices));
		
	}
	newAlpha_matrix = DDnode.myNew(1, newAlpha_children);
	
	//newAlpha_matrix = OP.addN(concatenateArray(newAlpha_matrix,actions[bestActId].costObj));
	
	/*
	 * create the newALpha, where the two columns together 
	 */

	// 1st i try to scalarize 
	/*DD  alphaMatrix_scalarized = scalarizeAlphaMatrix(nextValFn);
	newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,alphaMatrix_scalarized), 
		     concatenateArray(primeVarIndices,primeObsIndices));*/
	/*nextValFn.setVar(1);
	if(nextValFn.getChildren()!=null)
	{
		for(int x=0; x<nextValFn.getChildren().length;x++)
		{
			if(nextValFn.getChildren()[x].getChildren()!=null)
			{
				int z=0;
				for(; z<nextValFn.getChildren()[x].getChildren().length;z++)
				{
					
				}
					
				nextValFn.getChildren()[x].setVar(z);
			}
		}
	}*/
	
	/*
	 * i write the following code to transpose the matrix of nextVal fun from 2x3 to be 3x2 
	 */
	//DD nextValfun = scalarizeAlphaMatrix(nextValFn);
	//DD nextValfun = OP.transposeMatrix(nextValFn);
      /*
       * end transpose
       */


	
	//newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn), 
		    // concatenateArray(primeVarIndices,primeObsIndices));
	
	
	/*else
	{
		newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn), 
			     concatenateArray(primeVarIndices,primeObsIndices));
	}*/
	
	
	//newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn), 
				    // concatenateArray(primeVarIndices,primeObsIndices)); 
	
	
	/*DD []newAlpha__children = new DD[actions[bestActId].costObj.getChildren().length];

	for(int x=0; x<actions[bestActId].costObj.getChildren().length;x++)
	{
		
		System.out.println("bestActId : "+bestActId);
		actions[bestActId].costObj.getChildren()[x].display();
		
		newAlpha__children[x]= OP.addN(concatenateArray(newAlpha.getChildren()[x],actions[bestActId].costObj.getChildren()[x]));
		//System.out.println("print newAlpha__children[x]");
		//newAlpha__children[x].display();
		
	}
	
	newAlpha = DDnode.myNew(1, newAlpha__children);*/
	
	//newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].rewFn));
	//DD cost = actions[bestActId].costObj;
	//DD[] conc = concatenateArray(newAlpha,actions[bestActId].costObj);
	//newAlpha.setVar(1);
	/*actions[bestActId].costObj.setVar(1);
	if(actions[bestActId].costObj.getChildren()!=null)
	{
		for(int x=0; x<actions[bestActId].costObj.getChildren().length;x++)
		{
			if(actions[bestActId].costObj.getChildren()[x].getChildren()!=null)
			{
				int z=0;
				for(; z<actions[bestActId].costObj.getChildren()[x].getChildren().length;z++)
				{
					
				}
					
				actions[bestActId].costObj.getChildren()[x].setVar(z);
			}
		}
	}*/
	
	/*
	 * we convert the newAlpha back to be 2x3 from 3x2 
	 */
	
	//newAlpha = OP.transposeMatrix(newAlpha);
	
	
	//newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].costObj));
	
	newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].rewFn));

	/*
	 * TODO: for the bestValue it is a double so one value. but we have 2 childs for 2 columns for the newAlpha DD so in this case: do we scalarize the newAlpha and calculate the bestValue
	 * OR we do each column with the belState and then we end up having a double [] for bestValue which will correspond to each column
	 * this version of function works on two columns and double [] bestValue --> no scalarization of the new alpha
	 */
	/*if(newAlpha.getChildren()!=null)
	{
		for (int n=0; n<newAlpha.getChildren().length;n++)
		{
		
		bestValue_multiObj[n] = OP.factoredExpectationSparse(belState,newAlpha.getChildren()[n]);//TODO: run all SP there is error indexOutOfBoundExeption

		}
	}else
	{
		bestValue_multiObj[0] = OP.factoredExpectationSparse(belState,newAlpha);
	}*/
	
	
	bestValue = OP.factoredExpectationSparse(belState,newAlpha);
	//bestValue = OP.factoredExpectationSparse(belState,newAlpha_matrix);
	
	try
    {
    	writerd_1.write(Double.toString(smallestProb)+","+Double.toString(bestValue)+","+Integer.toString(bestActId)+","+Double.toString(bestValue)+","+"\n");
    	//writerd_1.write("----------------------------------------\n");
    }catch (IOException e)
    {
    	System.err.println("Problem writing to the file");
    }
	
	
	// package up to return
	//AlphaVector returnAlpha = new AlphaVector(newAlpha,bestValue,bestActId,bestObsStrat);
	//Alpha_Matrix returnAlpha = new Alpha_Matrix(newAlpha,bestValue_multiObj,bestActId,bestObsStrat);
	
	
	//Alpha_Matrix returnAlpha = new Alpha_Matrix(newAlpha,bestValue,bestActId,bestObsStrat);
	Alpha_Matrix returnAlpha = new Alpha_Matrix(newAlpha_matrix,bestValue,bestActId,bestObsStrat);
	
		//return returnAlpha;
    	
    	if(debug)
    	{
    	//System.out.println("********dpBackup***********");
    	//------------print DD[] belState
    	//System.out.println("==========================belState==============================");
    	/*try 
        {
            writer1.write("==========================belState==============================\n");
            for(int u =0; u<belState.length;u++)
        	{
            	//belState[u].display();// its only one DD 
        		for (int r =0; r<belState[u].getChildren().length;r++)
        		{
        			
        			writer1.write(Double.toString(belState[u].getChildren()[r].getVal())+"\n");
        			
        		}
        	}
            //System.exit(200);
          //------------print DD[] primedV
        	//System.out.println("==========================primedV==============================");
        	writer1.write("==========================primedV==============================\n");
        	for(int u =0; u<primedV.length;u++)
        	{
        		if(primedV[u].getChildren()!=null)
        		{
        			for (int r =0; r<primedV[u].getChildren().length;r++)
        			{
        				//primedV[u].getChildren()[r].display();
        				writer1.write(Double.toString(primedV[u].getChildren()[r].getVal())+"\n");
        			}
        		}else
        		{
        			//primedV[u].display();
        			writer1.write(Double.toString(primedV[u].getVal())+"\n");
        		}
        	}
        	//------------print maxAbsVal
        	//System.out.println("==========================maxAbsVal==============================");
        	writer1.write("==========================maxAbsVal==============================\n");
        	writer1.write(Double.toString(maxAbsVal)+"\n");
        	//System.out.println(maxAbsVal);
        
        } 
        catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }*/

	NextBelState [] nextBelStates1;//----------------------------------------------------------------->>TODO: 
	// get next unnormalised belief states
	//System.out.println("smallestProb "+tolerance);
	double smallestProb1;
	if (ignoremore) 
	    smallestProb1 = tolerance;
	else
	    smallestProb1 = tolerance/maxAbsVal;
	System.out.println("nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);");
	System.out.println("--------------------------------smallestProb");
	System.out.println(smallestProb1);
	System.out.println("---------------------------------belState");
	for(int l=0; l<belState.length;l++)
	{
		System.out.println("belState: "+l);
		belState[l].display();
	}
	nextBelStates1 = oneStepNZPrimeBelStates(belState, true, smallestProb1);//------------------------->>TODO: 
	System.out.println("---------------------------nextBelStates = oneStepNZPrimeBelStates");
	for(int k=0; k<nextBelStates1.length;k++)
	{
		System.out.println("nextBelStates[k]:"+k+" print nextBelStates DD[][]");
		for(int m=0; m<nextBelStates1[k].nextBelStates.length;m++)
		{
			for(int n=0; n<nextBelStates1[k].nextBelStates[m].length;n++)
			{
				nextBelStates1[k].nextBelStates[m][n].display();
			}
		}
		
	}
	//System.exit(200);
	// precompute obsVals
	System.out.println("precompute obsVals-- will loop number of actions");
	for (int actId = 0; actId<nActions; actId++) 
	{
	    nextBelStates1[actId].getObsVals(primedV);//----------------------------------------------------->>TODO: 
	    
	
	}
	double bestValue1 = Double.NEGATIVE_INFINITY;
	double actValue1;
	int bestActId1=0;
	int [] bestObsStrat1 = new int[nObservations];
	
	for (int actId = 0; actId<nActions; actId++) 
	{
	    actValue1 = 0.0;
	    // compute immediate rewards
	    actValue1 = actValue1 + OP.factoredExpectationSparseNoMem(belState,actions[actId].rewFn); //----->>TODO: should this be scalarized rewards or MultiObjectives costs ? 
	    // compute observation strategy
	    nextBelStates1[actId].getObsStrat();//--------------------------------------------------------->>TODO: 
	    actValue1 = actValue1 + discFact*nextBelStates1[actId].getSumObsValues();//----------------------->>TODO
	    if (actValue1 > bestValue1) 
	    {
	    	bestValue1 = actValue1;
	    	bestActId1 = actId;
			bestObsStrat1 = nextBelStates1[actId].obsStrat;//------------------------------------------->>TODO: 
	    }
	}
	// construct corresponding alpha vector
	DD newAlpha1 = DD.zero;
	DD nextValFn1 = DD.zero;
	DD obsDd1;
	int tobsid1;
	int [] obsConfig1 = new int[nObsVars];
	for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) //----------------------------->>TODO: length of arrayList
	{
		System.out.println("*******************alphaId: "+ alphaId+"**********");
	    if (MySet.find(bestObsStrat1,alphaId)>=0) 
	    {
	    	//System.out.println("alphaId is "+alphaId);
	    	obsDd1 = DD.zero;
	    	//for (int obsId = 0; obsId < bestObsStrat.length; obsId++) {
	    	for (int obsId = 0; obsId < nObservations; obsId++) 
	    	{
	    		if (bestObsStrat1[obsId]==alphaId) 
	    		{
	    			System.out.println("----------------------if (bestObsStrat[obsId]==alphaId");
	    			System.out.println("----------------------obsVarsArity");
	    			for(int g=0; g<obsVarsArity.length;g++)
	    			{
	    				System.out.println("obsVarsArity: "+g);
	    				System.out.println(obsVarsArity[g]);
	    			}
	    			System.out.println("----------------------nObsVars");
	    			System.out.println(nObsVars);
	    			System.out.println("obsConfig = statedecode(obsId+1,nObsVars,obsVarsArity);");
	    			
	    			obsConfig1 = statedecode(obsId+1,nObsVars,obsVarsArity);
	    			for(int n=0; n<obsConfig1.length;n++)
	    			{
	    				System.out.println("obsConfig:  "+n);
	    				System.out.println(obsConfig1[n]);
	    			}
	    			System.out.println("obsDd = OP.add(obsDd,Config.convert2dd(stackArray(primeObsIndices,obsConfig)));");
	    			System.out.println("-----------------------obsConfig");
	    			for(int n=0; n<obsConfig1.length;n++)
	    			{
	    				System.out.println("obsConfig:  "+n);
	    				System.out.println(obsConfig1[n]);
	    			}
	    			System.out.println("----------------------primeObsIndices");
	    			for(int h=0; h<primeObsIndices.length;h++)
	    			{
	    				System.out.println("primeObsIndices[h]:"+h);
	    				System.out.println(primeObsIndices[h]);
	    			}
	    			System.out.println("----------------------stackArray(primeObsIndices,obsConfig)");
	    			for(int n=0;n<stackArray(primeObsIndices,obsConfig1).length;n++)
	    			{
	    				for(int m =0; m<stackArray(primeObsIndices,obsConfig1)[n].length;m++)
	    				{
	    					System.out.println(m+"  "+n);
	    					System.out.println(stackArray(primeObsIndices,obsConfig1)[n][m]);
	    				}
	    			}
	    			System.out.println("------------------------Config.convert2dd(stackArray(primeObsIndices,obsConfig))");
	    			Config.convert2dd(stackArray(primeObsIndices,obsConfig1)).display();
	    			System.out.println("--------------------------obsDd");
	    			obsDd1.display();
	    			System.out.println("-------------------------obsDd = OP.add(obsDd,Config.");
	    			obsDd1 = OP.add(obsDd1,Config.convert2dd(stackArray(primeObsIndices,obsConfig1)));
	    			obsDd1.display();
	    		}
	    	}
	    	//----------------print to understand how the nextValFn is calculated------------
	    	System.out.println("nextValFn = OP.add(nextValFn,OP.multN(concatenateArray(DDleaf.myNew(discFact),obsDd,primedV[alphaId])));");
	    	System.out.println("------primedV for alpha id:" + alphaId); primedV[alphaId].display();
	    	System.out.println("------obsDd");obsDd1.display();
	    	System.out.println("------DDleaf.myNew(discFact)");DDleaf.myNew(discFact).display();
	    	System.out.println("------DD[] result of concatenateArray(DDleaf.myNew(discFact),obsDd,primedV[alphaId])");
	    	DD[] resultofConcat = concatenateArray(DDleaf.myNew(discFact),obsDd1,primedV[alphaId]);
	    	for (int q=0; q<resultofConcat.length;q++)
	    	{
	    		System.out.println("----resultofConcat DD#: "+q);
	    		resultofConcat[q].display();
	    	}
	    	System.out.println("-------OP.multN(concatenateArray()");
	    	DD multNdd = OP.multN(concatenateArray(DDleaf.myNew(discFact),obsDd1,primedV[alphaId]));
	    	multNdd.display();
	    	System.out.println("-------nextValFn: ");
	    	nextValFn1.display();
	    	System.out.println("nextValFn = OP.add(nextValFn,OP.multN(concatenateArray() ---> the new nextValFn");
	    	
	    	//------------------end print nextValFn---------------------------------------------
	    	nextValFn1 = OP.add(nextValFn1,OP.multN(concatenateArray(DDleaf.myNew(discFact),obsDd1,primedV[alphaId])));//------------->>TODO: CHANGE primedV
	    	nextValFn1.display();
	    }
	}
	//----------------------------print newAlpha =  OP.addMultVarElim---------------------------
	/*System.out.println("newAlpha =  OP.addMultVarElim(concatenateArray(actions[bestActId].transFn, actions[bestActId].obsFn,nextValFn), concatenateArray(primeVarIndices,primeObsIndices));");
	System.out.println("--------------------int [] primeObsIndices");
	for(int n =0; n<primeObsIndices.length;n++)
	{
		System.out.println(primeObsIndices[n]);
	}
	System.out.println("--------------------int [] primeVarIndices");
	for(int d=0; d<primeVarIndices.length;d++)
	{
		System.out.println(primeVarIndices[d]);
	}
	System.out.println("-------------------concatenateArray(primeVarIndices,primeObsIndices))");
	int [] concat = concatenateArray(primeVarIndices,primeObsIndices);
	for(int u =0; u<concat.length;u++)
	{
		System.out.println(concat[u]);
	}
	System.out.println("-------------------nextValFn");
	nextValFn1.display();
	System.out.println("-------------------DD[] actions[bestActId].obsFn");
	for (int b=0; b<actions[bestActId1].obsFn.length;b++)
	{
		actions[bestActId1].obsFn[b].display();
	}
	System.out.println("------------------actions[bestActId].transFn");
	for(int v=0; v<actions[bestActId1].transFn.length;v++)
	{
		actions[bestActId1].transFn[v].display();
	}
	System.out.println("------------------concatenateArray");
	DD[] conc = concatenateArray(actions[bestActId1].transFn, actions[bestActId1].obsFn,nextValFn1);
	for(int c=0; c<conc.length;c++)
	{
		conc[c].display();
	}
	System.out.println("------------------newAlpha= OP.addMultVarElim");*/
	//---------------------------end print newAlpha =  OP.addMultVarElim------------------------
	newAlpha1 =  OP.addMultVarElim(concatenateArray(actions[bestActId1].transFn, actions[bestActId1].obsFn,nextValFn1), 
				     concatenateArray(primeVarIndices,primeObsIndices));//------------------------------------------------------->>	TODO: primtVarIndices will be changed? 
	newAlpha1.display();
	//---------------------------print newAlpha=OP.addN(concatenateArray(newAlpha-----------------------
	System.out.println("newAlpha = OP.addN(concatenateArray(newAlpha,actions[bestActId].rewFn));");
	System.out.println("-------------------actions[bestActId].rewFn");
	actions[bestActId1].rewFn.display();
	System.out.println("-------------------newAlpha");
	newAlpha1.display();
	System.out.println("-------------------concatenateArray(newAlpha,actions[bestActId].rewFn)");
	for(int d=0; d<concatenateArray(newAlpha1,actions[bestActId1].rewFn).length;d++)
	{
		System.out.println("concatenate DD[]");
		concatenateArray(newAlpha1,actions[bestActId1].rewFn)[d].display();
	}
	System.out.println("-------------------newAlpha = OP.addN(");
	//---------------------------end print OP.addN(concatenateArray(newAlpha-------------------
	newAlpha1 = OP.addN(concatenateArray(newAlpha1,actions[bestActId1].rewFn));
	newAlpha1.display();
	//---------------------------print bestValue-----------------------------------------------
	System.out.println("bestValue = OP.factoredExpectationSparse(belState,newAlpha);");
	System.out.println("------------------newAlpha");
	newAlpha1.display();
	System.out.println("------------------belState");
	for (int g=0; g<belState.length;g++)
	{
		System.out.println("---belState:"+g);
		belState[g].display();
	}
	System.out.println("-------------------bestValue = OP.factoredExpectationSparse");
	//---------------------------end print bestValue-------------------------------------------
	bestValue1 = OP.factoredExpectationSparse(belState,newAlpha1);
	System.out.println("------------------bestValue"+ bestValue1);
	
	// package up to return
	Alpha_Matrix returnAlpha1 = new Alpha_Matrix(newAlpha1,bestValue1,bestActId1,bestObsStrat1);
	
	System.out.println("############################### returnAlpha################################3");
	returnAlpha1.alphaMatrix.display();
	//System.exit(200);
	return returnAlpha1;
    	}//end if debug
    	
    	return returnAlpha;
}
    
     
    //------------------oneStepNZPrimeBelStates------------------------------------------------------
    // one step normalized beleif states 
    public NextBelState []  oneStepNZPrimeBelStates(DD []  belState, boolean normalize, double smallestProb)
    {

    	int [][] obsConfig = new int[nObservations][nObsVars];
    	double [] obsProbs;
    	double [] onezero = {0};
    	DD [] marginals = new DD[nStateVars+1];
    	DD dd_obsProbs;
    	
    	for (int obsId=0; obsId<nObservations; obsId++) 
    	{
    		obsConfig[obsId] = statedecode(obsId+1,nObsVars,obsVarsArity);
    	}
    	Global.newHashtables();
	
    	NextBelState [] nextBelStates = new NextBelState[nActions];
    	for (int actId=0; actId<nActions; actId++) 
    	{
    		dd_obsProbs = OP.addMultVarElim(concatenateArray(belState,actions[actId].transFn,actions[actId].obsFn),
					    concatenateArray(varIndices,primeVarIndices));
			obsProbs = OP.convert2array(dd_obsProbs,primeObsIndices);
		
			nextBelStates[actId] = new NextBelState(obsProbs, smallestProb);
			// compute marginals
			if (!nextBelStates[actId].isempty()) 
			{
				marginals = OP.marginals(concatenateArray(belState,actions[actId].transFn,actions[actId].obsFn),primeVarIndices,varIndices);
				nextBelStates[actId].restrictN(marginals,obsConfig);
			}
	    }
	
	return nextBelStates;
	
    }
    //*************class NextBelState*****************************
    public class NextBelState 
    {
	DD [][] nextBelStates;
	int [] nzObsIds;
	double [][] obsVals;
	int numValidObs;
	public int [] obsStrat;
	double [] obsValues;
	double sumObsValues;
	public NextBelState(double [] obsProbs, double smallestProb) 
	{
		
	    numValidObs=0;
	    for (int i=0; i<obsProbs.length; i++) 
		if (obsProbs[i] > smallestProb) 
		    numValidObs++;
	    //System.out.println("number of valid observations "+numValidObs);
	    nextBelStates = new DD[numValidObs][nStateVars+1];
	    nzObsIds = new int[numValidObs];
	    obsStrat = new int[nObservations];
	    obsValues = new double[numValidObs];
	    int j=0;
	    for (int i=0; i<obsProbs.length; i++) 
		if (obsProbs[i] > smallestProb) 
		    nzObsIds[j++] = i;
	    
	    /*try 
        {
            writer2.write("=====================create obj: NextBelState(double [] obsProbs, double smallestProb)"+ "\n");
            writer2.write("------------DD[][] nextBelStates\n");
            for(int z =0; z<nextBelStates.length;z++)
            {
            	if(nextBelStates[z]!=null)
            	{
            	for(int q=0; q<nextBelStates[z].length;q++)
            	{
            		
            		if(nextBelStates[z][q]!=null)
            		{
            			for (int x =0;x<nextBelStates[z][q].getChildren().length;x++)
            			{
            				writer2.write(Double.toString(nextBelStates[z][q].getChildren()[x].getVal())+"\n");
            				nextBelStates[z][q].getChildren()[x].display();
            			}
            		}else
            		{
            			//writer2.write(Double.toString(nextBelStates[z][q].getVal())+"\n");
            			
            		}
            		
            	}
            	}else
            	{
            		System.out.println("nextBelStates is empty");
            	}
            }
            
            writer2.write("------------int[] nzObsIds\n");
            for(int y=0; y<nzObsIds.length;y++)
            {
            	writer2.write(Double.toString(nzObsIds[y])+"\n");
            }
            
            
            writer2.write("-------------- double [][]obsVals \n");
            if(obsVals!=null)
            {
            for(int y=0; y<obsVals.length;y++)
            {
            	for(int t=0; t<obsVals[y].length;t++)
            	{
            		writer2.write(Double.toString(obsVals[y][t])+"\n");
            	}
            }}
            else 
            	//System.out.println("obsVals is empty");
            
            writer2.write("-------------- int numValidObs \n");
            writer2.write(Double.toString(numValidObs)+"\n");

            writer2.write("-------------- int [] obsStrat \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsStrat[h])+"\n");
            }
            
            writer2.write("-------------- double [] obsValues \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsValues[h])+"\n");
            }
            
            writer2.write("-------------- double sumObsValues \n");
            writer2.write(Double.toString(sumObsValues)+"\n");
            
        } 
        catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }*/
		
	}
	public NextBelState(NextBelState a) 
	{
	    nextBelStates = new DD[a.nextBelStates.length][a.nextBelStates[0].length];
	    for (int i=0; i<a.nextBelStates.length; i++) {
		for (int j=0; j<a.nextBelStates[i].length; j++) 
		    nextBelStates[i][j] = a.nextBelStates[i][j];
	    }
	    obsVals = new double[a.obsVals.length][];
	    for (int i=0; i<a.obsVals.length; i++) 
		obsVals[i] = a.obsVals[i];
	    obsStrat = a.obsStrat;

	    nzObsIds = a.nzObsIds;
	    numValidObs = a.numValidObs;
	    obsValues = a.obsValues;
	    sumObsValues = a.sumObsValues;
	    
	   /* try 
        {
            writer2.write("=====================create obj: NextBelState(NextBelState a)"+ "\n");
            writer2.write("------------DD[][] nextBelStates\n");
            for(int z =0; z<nextBelStates.length;z++)
            {
            	for(int q=0; q<nextBelStates[z].length;q++)
            	{
            		if(nextBelStates[z][q].getChildren()!=null)
            		{
            			for (int x =0;x<nextBelStates[z][q].getChildren().length;x++)
            			{
            				writer2.write(Double.toString(nextBelStates[z][q].getChildren()[x].getVal())+"\n");
            			}
            		}else
            		{
            			writer2.write(Double.toString(nextBelStates[z][q].getVal())+"\n");
            			
            		}
            		
            	}
            }
            
            writer2.write("------------int[] nzObsIds\n");
            for(int y=0; y<nzObsIds.length;y++)
            {
            	writer2.write(Double.toString(nzObsIds[y])+"\n");
            }
            
            
            writer2.write("-------------- double [][]obsVals \n");
            for(int y=0; y<obsVals.length;y++)
            {
            	for(int t=0; t<obsVals[y].length;t++)
            	{
            		writer2.write(Double.toString(obsVals[y][t])+"\n");
            	}
            }
            
            writer2.write("-------------- int numValidObs \n");
            writer2.write(Double.toString(numValidObs)+"\n");

            writer2.write("-------------- int [] obsStrat \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsStrat[h])+"\n");
            }
            
            writer2.write("-------------- double [] obsValues \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsValues[h])+"\n");
            }
            
            writer2.write("-------------- double sumObsValues \n");
            writer2.write(Double.toString(sumObsValues)+"\n");
            
        } 
        catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }*/
		
	}
	public boolean isempty() 
	{
	    return (numValidObs == 0);
	}
	public void restrictN(DD [] marginals, int [][] obsConfig) 
	{
		//System.out.println("+++++++++++++ call restrictN()+++++++++++++");
	    int obsId;
	    //System.out.println("will loop numValidObs: "+ numValidObs+ " times");
	    for (int obsPtr=0; obsPtr<numValidObs; obsPtr++) 
	    {
	    	
	    	obsId = nzObsIds[obsPtr];
	    	//System.out.println("-------------obsId = nzObsIds[obsPtr];"+ obsId);
	    	//System.out.println("nextBelStates[obsPtr] = OP.restrictN(marginals,stackArray(primeObsIndices,obsConfig[obsId]));");
	    	//System.out.println("-------------obsConfig[obsId]");
	    	//for(int o=0; o<obsConfig[obsId].length;o++)
	    	{
	    		//System.out.println(obsConfig[obsId][o]);
	    	}
	    	//System.out.println("------------primeObsIndices");
	    	//for(int p=0; p<primeObsIndices.length;p++)
	    	{
	    		//System.out.println(primeObsIndices[p]);
	    	}
	    	//System.out.println("------------stackArray(primeObsIndices,obsConfig[obsId]) int[][]");
	    	//for(int m=0; m<stackArray(primeObsIndices,obsConfig[obsId]).length;m++)
	    	{
	    		//for(int n=0; n<stackArray(primeObsIndices,obsConfig[obsId])[m].length;n++)
	    		{
	    			//System.out.print(" "+stackArray(primeObsIndices,obsConfig[obsId])[m][n]);
	    		}
	    		//System.out.println();
	    	}
	    	//System.out.println("-------------marginals DD[][]");
	    	//for(int f=0;f<marginals.length;f++)
	    	{
	    		//marginals[f].display();
	    	}
	    	//System.out.println("nextBelStates[obsPtr] = OP.restrictN DD[][]");
	    	nextBelStates[obsPtr] = OP.restrictN(marginals,stackArray(primeObsIndices,obsConfig[obsId]));
	    	
	    	//for(int x=0; x<nextBelStates[obsPtr].length;x++)
	    	{
	    		//nextBelStates[obsPtr][x].display();
	    	}
	    }
	    //System.out.println("+++++++++++++ end call restrictN()+++++++++++++");
	   /* try 
        {
            writer2.write("=====================call function: restrictN(DD [] marginals, int [][] obsConfig) "+ "\n");
            writer2.write("------------DD[][] nextBelStates\n");
            for(int z =0; z<nextBelStates.length;z++)
            {
            	for(int q=0; q<nextBelStates[z].length;q++)
            	{
            		if(nextBelStates[z][q].getChildren()!=null)
            		{
            			for (int x =0;x<nextBelStates[z][q].getChildren().length;x++)
            			{
            				writer2.write(Double.toString(nextBelStates[z][q].getChildren()[x].getVal())+"\n");
            			}
            		}else
            		{
            			writer2.write(Double.toString(nextBelStates[z][q].getVal())+"\n");
            			
            		}
            		
            	}
            }
        }  catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }*/
	}
	//-----------getObsVals() modified--------------------------------------
	/* TODO: 
	 * for now this function is not used, if we want to use it there is loop that has to be constructed 
	 * and obsVal should be changed (new definition) maybe ArrayList<double[][]> so each double[][] is for a column of the matrix 
	 * but then this arrayList<double[][]> obsVal will be used in the next function so there we need also loops to go through one column by one column
	 */
		// get the observation values
		// obsVals[i][j] is the value expected if we see observation i
		// and then follow the conditional plan j
		public void getObsVals(ArrayList<DD[][]> primedV_arraylist) 
		{

		    if (!isempty())  
		    {
		    	/*System.out.println("numValidObs:"+numValidObs);
		    	System.out.println("primedV.length:"+primedV.length);
		    	System.out.println("primedV:");
		    	for(int d =0; d<primedV.length;d++)
		    	{
		    		primedV[d].display();
		    	}
		    	System.out.println("nextBelStates");
		    	for(int m=0; m<nextBelStates.length;m++)
		    	{
		    		for (int n=0; n<nextBelStates[m].length;n++)
		    		{
		    			nextBelStates[m][n].display();
		    		}
		    	}*/
		    	/*
		    	 * 1) scalarize the alpha_matrix 
		    	 * 
		    	 */
		    	 DD [] scalarizedAlphas =  scalarizeAlphaMatrix_1(primedV_arraylist);
		    	// alphaVectors is scalarized alphamatrix
		    	 if(scalarizedAlphas[0].getChildren()!=null)
		    		 obsVals = new double[numValidObs][scalarizedAlphas[0].getChildren().length];
		    	 else
		    		 obsVals = new double[numValidObs][scalarizedAlphas.length];
		    	obsVals = OP.factoredExpectationSparseNoMem(nextBelStates,scalarizedAlphas); 
		    	
		    	//System.out.println("obsVals");
		    	/*for(int o=0; o<obsVals.length;o++)
		    	{
		    		for (int h=0; h<obsVals[o].length;h++)
		    		{
		    			System.out.println(obsVals[o][h]);
		    		}
		    	}*/
		    }
		    //System.exit(200);

		    
		}
	//-----------getObsVals() original--------------------------------------
	// get the observation values
	// obsVals[i][j] is the value expected if we see observation i
	// and then follow the conditional plan j
	public void getObsVals(DD [] primedV) 
	{

	    if (!isempty())  
	    {
	    	System.out.println("numValidObs: "+numValidObs+" "+"primedV.length: "+primedV.length);
	    	obsVals = new double[numValidObs][primedV.length];
	    	obsVals = OP.factoredExpectationSparseNoMem(nextBelStates,primedV); 
	    	
	    }	    
	}
	//----------------------------------------------------
	public double getSumObsValues() {
	    return sumObsValues;
	}
	// get observation strategy
	// obsStrat[i] is the best conditional to plan to follow
	// if observation i is seen
	// this is just the index that maximizes over the obsVals
	// and alphaValue is the value of that conditional plan given than observation
	public void getObsStrat() 
	{
	    double alphaValue=0;
	    sumObsValues=0;
	    int obsId;
	    double obsProb;
	    for (int obsPtr=0; obsPtr<nObservations; obsPtr++) 
	    {
	    	obsStrat[obsPtr]=0;
	    }
	    for (int obsPtr=0; obsPtr<numValidObs; obsPtr++) 
	    {
	    	obsId = nzObsIds[obsPtr];
	    	obsProb = nextBelStates[obsPtr][nStateVars].getVal();
	    	alphaValue = obsVals[obsPtr][0];
	    	for (int i=1; i<obsVals[obsPtr].length; i++) 
	    	{
	    		if (obsVals[obsPtr][i] > alphaValue) //--------->> update to get the greater value
	    		{
	    			alphaValue = obsVals[obsPtr][i];  
	    			obsStrat[obsId] = i;
	    		}
	    	}
	    	obsValues[obsPtr]=obsProb*alphaValue;// b.alpha 
		
	    	sumObsValues += obsValues[obsPtr];
	    }
	    
	    /*try 
        {
            writer2.write("=====================call function: getObsStrat "+ "\n");
            writer2.write("------------DD[][] nextBelStates\n");
            for(int z =0; z<nextBelStates.length;z++)
            {
            	for(int q=0; q<nextBelStates[z].length;q++)
            	{
            		if(nextBelStates[z][q].getChildren()!=null)
            		{
            			for (int x =0;x<nextBelStates[z][q].getChildren().length;x++)
            			{
            				writer2.write(Double.toString(nextBelStates[z][q].getChildren()[x].getVal())+"\n");
            			}
            		}else
            		{
            			writer2.write(Double.toString(nextBelStates[z][q].getVal())+"\n");
            			
            		}
            		
            	}
            }
            
            writer2.write("------------int[] nzObsIds\n");
            for(int y=0; y<nzObsIds.length;y++)
            {
            	writer2.write(Double.toString(nzObsIds[y])+"\n");
            }
            
            
            writer2.write("-------------- double [][]obsVals \n");
            for(int y=0; y<obsVals.length;y++)
            {
            	for(int t=0; t<obsVals[y].length;t++)
            	{
            		writer2.write(Double.toString(obsVals[y][t])+"\n");
            	}
            }
            
            writer2.write("-------------- int numValidObs \n");
            writer2.write(Double.toString(numValidObs)+"\n");

            writer2.write("-------------- int [] obsStrat \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsStrat[h])+"\n");
            }
            
            writer2.write("-------------- double [] obsValues \n");
            for(int h=0; h<obsStrat.length;h++)
            {
            	writer2.write(Double.toString(obsValues[h])+"\n");
            }
            
            writer2.write("-------------- double sumObsValues \n");
            writer2.write(Double.toString(sumObsValues)+"\n");
            
            
           
        }catch (IOException e) 
        {
            System.err.println("Problem writing to the file");
        }*/
	    
	    
	}// end getObsStrat()
    }// end class NextBelState
    //*******************************************
    
    public int [] statedecode(int statenum, int n) 
    {
	int [] bases = new int[n];
	for (int i=0; i<n; i++) 
	    bases[i]=2;
	return statedecode(statenum,n,bases);
    }
    public int [] statedecode(int statenum, int n, int [] bases) 
    {
	int [] statevec = new int[n];
	for (int i=0; i<n; i++) 
	    statevec[i]=0;

	if (statenum==1) {
	    for (int i=0; i<n; i++) 
		statevec[i]=1;
	    return statevec;
	} 
	statenum--;
	int res=statenum;
	int remd;
	for (int i=0; i<n; i++) {
	    if (res==1) {
		statevec[i]=1;
		break;
	    }
	    remd = res % bases[i];
	    res=((int) Math.floor(res/bases[i]));
	    statevec[i]=remd;
	}
	for (int i=0; i<n; i++) {
	    statevec[i]++;
	}
	return statevec;
    }
}
