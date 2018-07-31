import java.util.SortedSet;

/*
 * TestMain is used to test the functions of this project
 */
public class TestMain {
	public static void main(String args[]) {
		
		//DD1
		DD[] children_dd1 = new DD[2];
		children_dd1[0] = DDleaf.myNew(1);
		children_dd1[1] = DDleaf.myNew(2);
		//children_dd1[2] = DDleaf.myNew(3);
		DD dd1 = DDnode.myNew(1, children_dd1);
		//System.out.println("DD1");dd1.display(); 
		
		//DD2
		DD[] children_dd2 = new DD[2];
		children_dd2[0] = DDleaf.myNew(3);
		children_dd2[1] = DDleaf.myNew(4);
		//children_dd2[2] = DDleaf.myNew(6);
		DD dd2 = DDnode.myNew(2, children_dd2);
		//System.out.println("DD2");dd2.display(); 

		//DD3
		DD[] children_dd3 = new DD[2];
		children_dd3[0] = dd1;
		children_dd3[1] = dd2;
		DD dd3 = DDnode.myNew(3, children_dd3);
		dd3.getVarSet();
		//System.out.println("DD3");dd3.display();
		
		
		//DD4
		DD[] children_dd4 = new DD[2];
		children_dd4[0] = DDleaf.myNew(5);
		children_dd4[1] = DDleaf.myNew(6);
		DD dd4 = DDnode.myNew(1, children_dd4);
		//System.out.println("DD4");dd4.display(); 
		
		//DD5
		DD[] children_dd5 = new DD[2];
		children_dd5[0] = DDleaf.myNew(7);
		children_dd5[1] = DDleaf.myNew(8);
		DD dd5 = DDnode.myNew(1, children_dd5);
		//System.out.println("DD5");dd5.display(); 
		
		//DD6
		DD[] children_dd6 = new DD[2];
		children_dd6[0] = DDleaf.myNew(9);
		children_dd6[1] = DDleaf.myNew(10);
		DD dd6 = DDnode.myNew(1, children_dd6);
		//System.out.println("DD6");dd6.display(); 
				
		//DD7
		DD[] children_dd7 = new DD[2];
		children_dd7[0] = DDleaf.myNew(11);
		children_dd7[1] = DDleaf.myNew(12);
		DD dd7 = DDnode.myNew(1, children_dd7);
		//System.out.println("DD7");dd7.display(); 
				
		//DD8
		DD[] children_dd8 = new DD[2];
		children_dd8[0] = dd4;
		children_dd8[1] = dd5;
		DD dd8 = DDnode.myNew(3, children_dd8);
		dd8.getVarSet();
				
		//DD9
		DD[] children_dd9 = new DD[2];
		children_dd9[0] = dd6;
		children_dd9[1] = dd7;
		DD dd9 = DDnode.myNew(3, children_dd9);
		dd9.getVarSet();
				
		//DD10
		DD[] children_dd10 = new DD[3];
		children_dd10[0] = dd3;
		children_dd10[1] = dd8;
		children_dd10[2] = dd9;
		DD dd10 = DDnode.myNew(1, children_dd10);
		System.out.println("DD10");dd10.display(); 
		
		/*
		 * test OP functions 
		 */
		//////////////////////////////////////
		// add 2 DDs - DD add(DD dd1, DD dd2)
		/////////////////////////////////////
		/*DD resultAdd = OP.add(dd2, dd1);
		System.out.println("result of OP.add");resultAdd.display();*/
		
		/////////////////////////////////////////
		//subtract 2 DDs - DD sub(DD dd1, DD dd2)
		/////////////////////////////////////////
		/*DD resultsubtract = OP.sub(dd1, dd2);
		System.out.println("result of OP.sub");resultsubtract.display();*/
		
		////////////////////////////////////
		// add N DDs - DD addN(DD[] ddArray)
		////////////////////////////////////
		/*DD resultsaddN = OP.addN(children_dd2);
		System.out.println("result of OP.addN");resultsaddN.display();*/
		
		/////////////////////////////////////////
		// absolute value of a DD - DD abs(DD dd)
		/////////////////////////////////////////
		/*DD resultsabs = OP.abs(dd1);
		System.out.println("result of OP.abs");resultsabs.display();*/
		
		/////////////////////////////////
		// negate a DD - DD neg(DD dd)
		/////////////////////////////////
		/*DD resultsneg = OP.neg(dd1);
		System.out.println("result of OP.neg");resultsneg.display();*/
		
		////////////////////////////////////////////
		// primeVar - DD primeVars(DD dd, int n) 
		//////////////////////////////////////////
		/*DD resultsprimeVar = OP.primeVars(dd3, 1);
		System.out.println("result of OP.primeVar");resultsprimeVar.display();*/
		
		///////////////////////////////////////////////////////////////////
		// swap variables - DD[] swapVars(DD[] ddArray, int[][] varMapping)
		//////////////////////////////////////////////////////////////////
		/*int[][] varMapping ={{4,5},{6,7}};
		DD [] resultswapVars = OP.swapVars(children_dd1, varMapping);
		System.out.println("result of OP.resultswapVars");
		for(int i=0; i<resultswapVars.length;i++)
		{
			resultswapVars[i].display();
		}*/
		
		///////////////////////////////////////////
		// multiply 2 DDs -DD mult(DD dd1, DD dd2)
		//////////////////////////////////////////
		/*DD resultsmult = OP.mult(dd1, dd2);
		System.out.println("result of OP.mult");resultsmult.display();*/
		
		///////////////////////////////////////////
		// divide 2 DDs - DD div(DD dd1, DD dd2)
		///////////////////////////////////////////
		/*DD resultsdiv = OP.div(dd1, dd2);
		System.out.println("result of OP.div");resultsdiv.display();*/
		
		////////////////////////////////////////////
		// multiply N DDs - DD multN(DD[] ddArray) 
		//////////////////////////////////////////
		/*DD resultsmultN = OP.multN(children_dd1);
		System.out.println("result of OP.multN");resultsmultN.display();*/
		
		///////////////////////////////////
		// inverse of a DD - DD inv(DD dd)
		//////////////////////////////////
		/*DD resultsinv = OP.inv(dd1);
		System.out.println("result of OP.inv");resultsinv.display();*/
		
		///////////////////////////////////////////////////////////////////////////////
		// replace val1 with val2 in a DD - DD replace(DD dd, double val1, double val2)
		///////////////////////////////////////////////////////////////////////////////
		/*DD resultsreplace = OP.replace(dd1,1,11);
		System.out.println("result of OP.replace");resultsreplace.display();*/
		
		///////////////////////////////////////////////////////////////////
		// addout (sumout) a variable from a DD- DD addout(DD dd, int var) 
		///////////////////////////////////////////////////////////////////
		/*DD resultsaddout = OP.addout(dd1,1);
		System.out.println("result of OP.addout");resultsaddout.display();*/
		
		///////////////////////////////////////////////////////////////////////
		// selectVarGreedily - int selectVarGreedily(DD[] ddArray, int[] vars) 
		///////////////////////////////////////////////////////////////////////
		/*int[] vars ={6,1};
		int var = OP.selectVarGreedily(children_dd1,vars); 
		System.out.println("result of OP.selectVarGreedily: "+var);*/
		
		/////////////////////////////////////////////////////////////////
		// dotProductNoMem  (Set container, don't store results) - double dotProductNoMem(DD dd1, DD dd2, SortedSet<Integer> vars)
		/////////////////////////////////////////////////////////////////
	    //below code for the vars in from Global.java
		/*int N = 0;
		boolean[] varMask = new boolean[3*N+1];
	      int[] vars = new int[3*N];
	      for (int varId=1; varId<=3*N; varId++) {
	      varMask[varId]=true;
	      vars[varId-1] = varId;
	      }
		double resultdotProductNoMem = OP.dotProductNoMem(dd1, dd2,  vars);
		System.out.println("result of OP.dotProductNoMem: "+resultdotProductNoMem);*/
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// dotProduct2  (My set, store results)- double[][] dotProductLeafPrune(DD[] dd1Array, DD[] dd2Array, int[] vars)
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*int N = 0;
		boolean[] varMask = new boolean[3*N+1];
	      int[] vars = new int[3*N];
	      for (int varId=1; varId<=3*N; varId++) {
	      varMask[varId]=true;
	      vars[varId-1] = varId;
	      }
		double [][] dotproduct = OP.dotProductLeafPrune( children_dd1, children_dd2, vars);
		System.out.println("result of OP.dotProductLeafPrune: ");
		for (int i =0; i<dotproduct.length;i++)
		{
			for (int j=0; j<dotproduct[i].length;j++)
			{
				System.out.println(dotproduct[i][j]);
			}
		}*/
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// dotProduct  (My set, store results)- double[][] dotProduct(DD[] dd1Array, DD[] dd2Array, int[] vars)
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		/*int N = 0;
		boolean[] varMask = new boolean[3*N+1];
	      int[] vars = new int[3*N];
	      for (int varId=1; varId<=3*N; varId++) {
	      varMask[varId]=true;
	      vars[varId-1] = varId;
	      }
		double [][] dotproduct = OP.dotProduct( children_dd1, children_dd2, vars);
		System.out.println("result of OP.dotProduct: ");
		for (int i =0; i<dotproduct.length;i++)
		{
			for (int j=0; j<dotproduct[i].length;j++)
			{
				System.out.println(dotproduct[i][j]);
			}
		}*/
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// dotProductNoMem  (My set, don't store results) - double[][] dotProductNoMem(DD[] dd1Array, DD[] dd2Array, int[] vars)
		/////////////////////////////////////////////////////////////////////////////////////////
		/*int N = 0;
		boolean[] varMask = new boolean[3*N+1];
	      int[] vars = new int[3*N];
	      for (int varId=1; varId<=3*N; varId++) {
	      varMask[varId]=true;
	      vars[varId-1] = varId;
	      }
		double [][] dotproduct = OP.dotProductNoMem( children_dd1, children_dd2, vars);
		System.out.println("result of OP.dotProductNoMem: ");
		for (int i =0; i<dotproduct.length;i++)
		{
			for (int j=0; j<dotproduct[i].length;j++)
			{
				System.out.println(dotproduct[i][j]);
			}
		}*/
		
		///////////////////////////////////////////////////////////////////////////////
		// factoredExpectation - double factoredExpectationSparse(DD[] factDist, DD dd)
		///////////////////////////////////////////////////////////////////////////////
		/*double resultfactoredExpectationSparse = OP.factoredExpectationSparse(children_dd2, dd1);
		System.out.println("result of OP.factoredExpectationSparse: "+resultfactoredExpectationSparse);*/
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// addMultVarElim (summout variables from a product of DDs using variable elimincation) -DD addMultVarElim(DD[] dds, int[] vars)
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		/*int[] vars = {1};
		DD resultaddMultarElim =  OP.addMultVarElim(dd1,vars);
		System.out.println("result of OP.addMultVarElim: ");resultaddMultarElim.display();*/
		
		////////////////////////////////////////////////////////////
		// threshold dd- DD threshold(DD dd, double val, int parity)
		////////////////////////////////////////////////////////////
		/*DD resultthreshold =  OP.threshold(dd1,2,1);
		System.out.println("result of OP.threshold: ");resultthreshold.display();*/
		
		////////////////////////////////////////
		// max of 2 DDs -DD max(DD dd1, DD dd2)
		////////////////////////////////////////
		/*DD resultsmax = OP.max(dd1,dd2);
		System.out.println("result of OP.max");resultsmax.display();*/
		
		////////////////////////////////////////
		// max of N DDs - DD maxN(DD[] ddArray)
		///////////////////////////////////////
		/*DD resultsmaxN = OP.maxN(children_dd1);
		System.out.println("result of OP.maxN");resultsmaxN.display();*/
		
		///////////////////////////////////////
		// min of 2 DDs - DD min(DD dd1, DD dd2)
		///////////////////////////////////////
		/*DD resultsmin = OP.min(dd1,dd2);
		System.out.println("result of OP.min");resultsmin.display();*/
		
		//////////////////////////////////////////////////////////////////////
		// maxNormDiff - boolean maxNormDiff(DD dd1, DD dd2, double threshold)
		/////////////////////////////////////////////////////////////////////
		/*boolean resultsmaxNormDiff = OP.maxNormDiff(dd1,dd2,0.1);
		System.out.println("result of OP.maxNormDiff: "+resultsmaxNormDiff );*/
		
		/////////////////////////////////////////////////////////////////////
		// maxAll (find leaf with maximum value) - double[] maxAllN(DD[] dds)
		////////////////////////////////////////////////////////////////////
		/*double[] resultsmaxAllN = OP.maxAllN(children_dd2);
		System.out.println("result of OP.maxAllN: " );
		for(int i=0; i<resultsmaxAllN.length;i++)
		{
			System.out.println(resultsmaxAllN[i]);
		}*/
		
		//////////////////////////////////////////////////////////////////////
		// minAll (find leaf with minimum value) - double[] minAllN(DD[] dds)
		/////////////////////////////////////////////////////////////////////
		/*double[] resultsminAllN = OP.minAllN(children_dd2);
		System.out.println("result of OP.minAllN: " );
		for(int i=0; i<resultsminAllN.length;i++)
		{
			System.out.println(resultsminAllN[i]);
		}*/
		
		///////////////////////////////////////////////////////////////////////////////
		// restrict some variables to some values - DD restrict(DD dd, int[][] config)
		//////////////////////////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		DD resultrestrict = OP.restrict(dd1,config);
		System.out.println("result of OP.restrict");resultrestrict.display();*/
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// restrictOrdered (faster restrict fn that assumes a variable ordering) - DD restrictOrdered(DD dd, int[][] config)
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		DD resultrestrictOrdered = OP.restrictOrdered(dd1,config);
		System.out.println("result of OP.restrictOrdered");resultrestrictOrdered.display();*/
		
		////////////////////////////////////////////////////////////////////////////////////////
		// evaluate a DD for some configuration of variables - double eval(DD dd, int[][] config)
		////////////////////////////////////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		double resulteval = OP.eval(dd1, config);
		System.out.println("result of OP.eval: "+resulteval);*/
		
		////////////////////////////////////////////////////////////
		// restrict N DDs - DD[] restrictN(DD[] dds, int[][] config)
		/////////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		DD[] resultrestrictN = OP.restrictN(children_dd1, config);
		System.out.println("resultrestrictN");
		for(int i=0; i<resultrestrictN.length;i++)
		{
			resultrestrictN[i].display();	
		}*/
		
		////////////////////////////////////////////////////////////////////////////
		// restrict N ordered DDs - DD[] restrictOrderedN(DD[] dds, int[][] config)
		///////////////////////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		DD[] resultrestrictOrderedN = OP.restrictOrderedN(children_dd1, config);
		System.out.println("restrictOrderedN");
		for(int i=0; i<resultrestrictOrderedN.length;i++)
		{
			resultrestrictOrderedN[i].display();	
		}*/
		
		///////////////////////////////////////////////////////////
		// evaluate N DDs - double[] evalN(DD[] dds, int[][] config)
		///////////////////////////////////////////////////////////
		/*int[][] config = {{4,5},{6,7}};
		double[] resultevalN = OP.evalN(children_dd1, config);
		System.out.println("resultevalN");
		for(int i=0; i<resultevalN.length;i++)
		{
			System.out.println(resultevalN[i]);
		}*/
		
		///////////////////////////////////////////////////////////
		// maxout a variable from a DD - DD maxout(DD dd, int var)
		//////////////////////////////////////////////////////////
		/*DD resultsmaxout = OP.maxout(dd1,1);
		System.out.println("result of OP.maxout");resultsmaxout.display();*/
		
		//////////////////////////////////////////////////////////
		// minout a variable from a DD - DD minout(DD dd, int var)
		//////////////////////////////////////////////////////////
		/*DD resultsminout = OP.minout(dd1,1);
		System.out.println("result of OP.minout");resultsminout.display();*/
		
		/////////////////////////////////////////////////////////////////////
		// maxAddVarElim (maxout some variables from a sum of DDs using variable elimination) - DD maxAddVarElim(DD[] dds, int[] vars)
		/////////////////////////////////////////////////////////////////////
		/*int []var = {1};
		DD resultmaxAddVarElim = OP.maxAddVarElim(dd1,var);
		System.out.println("result of OP.maxAddVarElim");resultmaxAddVarElim.display();*/
		
		////////////////////////////////////////////////////////////////////////
		// minAddVarElim (minout som variables from a sum of DDs using variable elimination - DD minAddVarElim(DD[] dds, int[] vars)
		////////////////////////////////////////////////////////////////////////
		/*int []var = {1};
		DD resultminAddVarElim = OP.minAddVarElim(dd1,var);
		System.out.println("result of OP.minAddVarElim");resultminAddVarElim.display();*/
		
		/////////////////////////////////////////////
		// orderLast - DD orderLast(DD dd, int varId)
		/////////////////////////////////////////////
		/*DD resultsorderLast = OP.orderLast(dd1,1);
		System.out.println("result of OP.orderLast");resultsorderLast.display();*/
		
		//////////////////////////////////////////////////////////////////////
		// reorder a DD according to the variable ordering - DD reorder(DD dd)
		/////////////////////////////////////////////////////////////////////
		/*DD resultsreorder = OP.reorder(dd1);
		System.out.println("result of OP.reorder");resultsreorder.display();*/
		
		/////////////////////////////////////
		// reorderCost- DD reorderCost(DD dd)
		/////////////////////////////////////
		/*DD resultsreorderCost = OP.reorderCost(dd1);
		System.out.println("result of OP.reorderCost");resultsreorderCost.display();*/
		
		//////////////////////////////////////////////////////
		// extractConfig - DD extractConfig(DD dd, int[] vars)
		//////////////////////////////////////////////////////
		/*int[] vars = {1};
		DD resultextractConfig = OP.extractConfig(dd1,vars);
		System.out.println("result of OP.extractConfig");resultextractConfig.display();*/
		
		//////////////////////////////////////
		// clearConfig - DD clearConfig(DD dd)
		/////////////////////////////////////
		/*DD resultsclearConfig = OP.clearConfig(dd1);
		System.out.println("result of OP.clearConfig");resultsclearConfig.display();*/
		
		////////////////////////////////////////////////////////////////////////////////////////////
		// printPolicySpuddFormat - void printPolicySpuddFormat(String filename, DD[] valuef, int[] pol)
		////////////////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////
		// displaySpuddFormat - String displaySpuddFormat(DD dd, int indentation)
		/////////////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////
		// sampleMultinomial - int sampleMultinomial(double [] pdist)
		/////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////
		// getNumLeavesDepth - int getNumLeavesDepth(DD dd)
		/////////////////////////////////////////////////////
		/*int resultgetNumLeavesDepth = OP.getNumLeavesDepth(dd1);
		System.out.println("result of OP.getNumLeavesDepth: "+resultgetNumLeavesDepth);*/
		
		// setupIP - int[][] setupIP(DD dd, int[] var2row, int colId)
		
		////////////////////////////////////////////////////
		// enumerateLeaves - double[] enumerateLeaves(DD dd)
		////////////////////////////////////////////////////
		/*double[] resultsenumerateLeaves = OP.enumerateLeaves(dd2);
		System.out.println("result of OP.enumerateLeaves: " );
		for(int i=0; i<resultsenumerateLeaves.length;i++)
		{
			System.out.println(resultsenumerateLeaves[i]);
		}*/
		
		/////////////////////////////
		// nEdges - int nEdges(DD dd)
		////////////////////////////
		/*int resultsnEdges = OP.nEdges(dd1);
		System.out.println("result of OP.nEdges: "+ resultsnEdges);*/
		
		////////////////////////////////
		// nLeaves - int nLeaves(DD dd)
		//////////////////////////////
		/*int resultsnLeaves = OP.nLeaves(dd1);
		System.out.println("result of OP.nLeaves: "+ resultsnLeaves);*/
		
		//////////////////////////////
		// nNodes - int nNodes(DD dd)
		/////////////////////////////
		/*int resultsnNodes = OP.nNodes(dd1);
		System.out.println("result of OP.nNodes: "+ resultsnNodes);*/
		
		//////////////////////////////////////////////////////////////
		// approximateAll - DD approximateAll(DD dd, double tolerance)
		//////////////////////////////////////////////////////////////
		/*DD resultsapproximateAll = OP.approximateAll(dd1,1);
		System.out.println("result of OP.approximateAll");resultsapproximateAll.display();*/
		
		////////////////////////////////////////////////////////
		// approximate - DD approximate(DD dd, double tolerance)
		////////////////////////////////////////////////////////
		/*DD resultsapproximate = OP.approximate(dd1,2);
		System.out.println("result of OP.approximate");resultsapproximate.display();*/
		
		/////////////////////////////////////////
		// findLeaf - DD findLeaf(DD dd, DD leaf)
		/////////////////////////////////////////
		/*DD resultsfindLeaf = OP.findLeaf(dd1,DDleaf.myNew(4));
		System.out.println("result of OP.findLeaf");resultsfindLeaf.display();*/
		
		////////////////////////////////////////////////
		// convert2array - double[] convert2array(DD dd)
		////////////////////////////////////////////////
		/*double[] resultsconvert2array = OP.convert2array(dd2);
		System.out.println("result of OP.convert2array: " );
		for(int i=0; i<resultsconvert2array.length;i++)
		{
			System.out.println(resultsconvert2array[i]);
		}*/
		
		////////////////////////////////////////////////////////////////////////////
		// marginals - DD[] marginals(DD[] cpts, int[] margIds, int[] summoutIds)
		////////////////////////////////////////////////////////////////////////////
		/*int[] margIds = {4};
		int[] summoutIds = {5};
		DD[] resultmarginals = OP.marginals(children_dd1, margIds, summoutIds);
		System.out.println("result of OP.marginals:");
		for(int i=0; i<resultmarginals.length;i++)
		{
			resultmarginals[i].display();
		}*/
		
		///////////////////////////////////////////////////////////////////////////////
		// scalarize - DD scalarizeAlphaMatrix(DD AlphaMatrix)
		///////////////////////////////////////////////////////////////////////////////
		System.out.println("scalarizing: ");
		/*DD scalarized = POMDP.scalarizeAlphaMatrix(dd3.getChildren()[0]);
		System.out.println("printing scalarized first node");
		scalarized.display();
		scalarized = POMDP.scalarizeAlphaMatrix(dd3.getChildren()[1]);
		System.out.println("printing scalarized second node");
		scalarized.display();*/
		 
		DD scalarized = POMDP.recursiveScalarizeMatrix2(dd10);
		System.out.println("printing scalarized");
		scalarized.display();
		
	}//end main()

}
