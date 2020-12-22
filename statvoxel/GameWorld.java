package statvoxel;
import static statvoxel.Funcs.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameWorld{
	
	public Set<Node> nodesSet = new HashSet();
	
	public Node findRandomNode(){
		if(nodesSet.isEmpty()) add(Funcs.randVoxels(Rand.weakRand, 5)); //FIXME this is just here so you see something instead of thinking its broken
		List<Node> nodes = sortedNodes();
		return nodes.get(Rand.strongRand.nextInt(nodes.size()));
	}
	
	/** does not add the Node into this GameWorld. its just a wrapper. All returned voxels exist in this gameworld,
	though it may contain duplicates (FIXME sort them to avoid that, then add more to fill quantity...
	or faster... sort it then replace any voxel at index i where index i+1 has same voxel, and repeat a few times
	keep sorting until they're all unique???)
	*/
	public Node findRandomVoxels(Random rand, int quantity){
		long[] ret = new long[quantity];
		for(int i=0; i<quantity; i++){
			//TODO optimize by not getting a random node each time
			ret[i] = findRandomVoxel(rand); //TODO optimize. this is very very slow to not chose how many from each node you want first.
		}
		return wrap(ret);
	}
	
	public long findRandomVoxel(Random rand){
		long[] buckets = findRandomNode().buckets;
		return buckets[rand.nextInt(buckets.length)];
	}
	
	public void nextState(){
		
		Funcs.stream(findRandomNode(),findRandomNode());
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