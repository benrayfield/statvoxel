package statvoxel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameWorld{
	
	public Set<Node> nodesSet = new HashSet();
	
	public Node randomNode(){
		if(nodesSet.isEmpty()) add(Funcs.randVoxels(Rand.weakRand, 5)); //FIXME this is just here so you see something instead of thinking its broken
		List<Node> nodes = sortedNodes();
		return nodes.get(Rand.strongRand.nextInt(nodes.size()));
	}
	
	public void nextState(){
		Funcs.stream(randomNode(),randomNode());
	}
	
	/** sorted by Node.sortVal. voxels are copied from high sortVal to low sortVal. sortVal of a Node can change. */
	public List<Node> sortedNodes(){
		List<Node> list = new ArrayList(nodesSet);
		Collections.sort(list, (Node a, Node b)->{
			return Double.compare(a.sortVal, b.sortVal); //FIXME reverse?
		});
		return list;
	}
	
	public double minSortVal(){
		double ret = Double.POSITIVE_INFINITY;
		for(Node n : nodesSet) ret = Math.min(ret, n.sortVal);
		return ret;
	}
	
	public double maxSortVal(){
		double ret = Double.NEGATIVE_INFINITY;
		for(Node n : nodesSet) ret = Math.max(ret, n.sortVal);
		return ret;
	}
	
	/** puts at the reader end, so it reads from all other Nodes, and no Node reads from it (instead of somewhere in middle) */
	public void becomeReader(Node n){
		if(!nodesSet.isEmpty()) n.sortVal = minSortVal()-1;
		nodesSet.add(n);
	}
	
	/** puts at the writer end, so it writes to all other Nodes, and no Node writes to it (instead of somewhere in middle) */
	public void becomeWriter(Node n){
		if(!nodesSet.isEmpty()) n.sortVal = maxSortVal()+1;
		nodesSet.add(n);
	}
	
	public void add(Node n){
		nodesSet.add(n);
	}
	
	public void remove(Node n){
		nodesSet.remove(n);
	}

}