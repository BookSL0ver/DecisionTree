import java.util.ArrayList;
import java.util.List;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */
public class DecisionTreeImpl {
	public DecTreeNode root;
	public List<List<Integer>> trainData;
	public int maxPerLeaf;
	public int maxDepth;
	public int numAttr;

	// Build a decision tree given a training set
	DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
		this.trainData = trainDataSet;
		this.maxPerLeaf = mPerLeaf;
		this.maxDepth = mDepth;
		if (this.trainData.size() > 0) this.numAttr = trainDataSet.get(0).size() - 1;
		this.root = buildTree();
	}
	
	private DecTreeNode buildTree() {
		// TODO: add code here	
		DecTreeNode r = new DecTreeNode(-2, 0, 0);
		r = buildTree(r, trainData, 0);
		return r;
	}
	
	private DecTreeNode buildTree(DecTreeNode r, List<List<Integer>> set, int depth)
	{
		String best;
		if (set.size() == 0)
		{
			best = "leaf";
		}
		else
		{
			best = helper(set);
		}
		if (best.equals("leaf") || depth == maxDepth) //|| set.size() <= maxPerLeaf)	//TODO: depth, number of instances is less than or equal to the maximum instances per leaf
		{
			//leaf (count 0s and 1s and set the classLabel accordingly)
			int zeros = 0;
			int ones = 0;
			for (int x = 0; x < set.size(); x++)
			{
				int c = this.classify(set.get(x));
				if (c == 0)
				{
					zeros++;
				}
				else
				{
					ones++;
				}
			}
			DecTreeNode a;
			if (zeros > ones)
			{
				a = new DecTreeNode(0, 0, 0);
				a.left = null; 
				a.right = null;
			}
			else
			{
				a = new DecTreeNode(1, 0, 0);
				a.left = null;
				a.right = null;
			}
			return a;
		}
		else
		{
			int att = Integer.parseInt(best.substring(0, 1)); //attribute
			int thresh = Integer.parseInt(best.substring(1)); //the threshold number
			
			DecTreeNode a = new DecTreeNode(-1, att, thresh);
			List<List<Integer>> left = new ArrayList<List<Integer>>();
			List<List<Integer>> right = new ArrayList<List<Integer>>();
			for (int x = 0; x < set.size(); x++)
			{
				if (set.get(x).get(a.attribute) <= a.threshold)
				{
					left.add(set.get(x));
				}
				else
				{
					right.add(set.get(x));
				}
			}
			depth++;
			a.left = buildTree(a, left, depth);
			a.right = buildTree(a, right, depth);
			return a;
			
		}
	}
	
	public int classify(List<Integer> instance) {
		// TODO: add code here
		// Note that the last element of the array is the label.
		return instance.get(instance.size() - 1);
	}
	
	private String helper(List<List<Integer>> training)
	{
		//need to look at every threshold for every attribute and select the best attribute
		//and threshold
		List<Integer> att = new ArrayList<Integer>();
		List<String> thresh = new ArrayList<String>();
		List<Double> ingain = new ArrayList<Double>();
		for (int y = 1; y < 10; y++)
		{
			for (int i = 0; i < training.get(0).size() - 1; i++)
			{
				double lte = 0;
				double gt = 0;
				for (int x = 0; x < training.size(); x++)
				{
					if (training.get(x).get(i) <= y)
					{
						lte++;
					}
					else
					{
						gt++;
					}
				}
				//System.out.println(lte/training.size());
				double hlte = ((lte/training.size()))*(Math.log(lte/training.size())/Math.log(2));
				//System.out.println("HLTE: " + hlte);
				att.add(i);
				thresh.add("" + y);
				ingain.add(Math.abs(hlte));
				double hgt = ((gt/training.size()))*(Math.log(gt/training.size()/Math.log(2)));
				//System.out.println("HGT: " + hgt);
				att.add(i);
				thresh.add("" + y);
				ingain.add(Math.abs(hgt));
			}
		}
		int indexMaxIngain = -1;
		double maxIngain = -1.0;
		for (int b = 0; b < ingain.size(); b++)
		{
			if (ingain.get(b) > maxIngain)
			{
				maxIngain = ingain.get(b);
				indexMaxIngain = b;
			}
		}
		
		if (maxIngain == 0)
		{
			//make a leaf
			//System.out.println("leaf");
			return "leaf";
		}
		else
		{
			//return the best attribute and it's best threshold
			//System.out.println(att.get(indexMaxIngain) + " " + ingain.get(indexMaxIngain));
			return ("" + att.get(indexMaxIngain) + thresh.get(indexMaxIngain));
		}
	}
	
	// Print the decision tree in the specified format
	public void printTree() {
		printTreeNode("", this.root);
	}

	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + "X_" + node.attribute;
		System.out.print(printStr + " <= " + String.format("%d", node.threshold));
		if(node.left.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.left.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%d", node.threshold));
		if(node.right.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.right.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
	}
	
	public double printTest(List<List<Integer>> testDataSet) {
		int numEqual = 0;
		int numTotal = 0;
		for (int i = 0; i < testDataSet.size(); i ++)
		{
			int prediction = classify(testDataSet.get(i));
			int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
			System.out.println(prediction);
			if (groundTruth == prediction) {
				numEqual++;
			}
			numTotal++;
		}
		double accuracy = numEqual*100.0 / (double)numTotal;
		System.out.println(String.format("%.2f", accuracy) + "%");
		return accuracy;
	}
}
