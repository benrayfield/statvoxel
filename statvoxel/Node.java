package statvoxel;

import java.util.Arrays;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.LongToIntFunction;

/** a mutable long[] and a functon mapping all possible longs to int address in the long[],
and a sortVal for comparing any 2 Nodes, as those sorted higher copy into those sorted lower
(todo or reverse that semantic?). They partially duplicate eachother,
but since multiple possible longs fit into each index of a certain Node,
it doesnt get overpowered by too many longs in 1 place or pattern of places.
<br><br>
Example: (long voxel)->{ distance of that voxel to a certain point, times 10,
	or long[].length-1 if its farther away than that }.
<br><br>
Example: (long voxel)->{ distance of that voxel to anywhere on a straight line,
	so you can read into that node from many other nodes,
	which selects only those near a straight line somewhere in 3d, with some max distance,
	then paint them all the same color, then write from that node to all others
	by putting its sortVal higher than all others and letting "fluid mixing of nodes" handle it. }
<br><br>
Example: (long voxel)->{ ((x(voxel)&1023)<<10)|(y(voxel%1023)), to copy voxels into an image1024x1024
	where their x y and z positions are ignored and just display the color
	(so can affine transform during copy, or fisheye lens, or whatever transform of 3d to 2d you want)}
<br><br>
All possible random interactions between any pair of nodes converges to exactly the same node states,
as long as no 2 nodes have exactly the same sortVal,
but that might take a long time and it will approx converge gradually in realtime,
so use it like fluid mixing... LongConsumer is allowed
to see any long that exists in a Node of higher sortVal than this Node's sortVal,
like if Node.sortVal is index of that Node in a Node[],
and to read from all other nodes you put a Node at index 0,
and to write to all other nodes you put it at index Node[].length-1.
BUT thats only if all nodes have enough gas to do that many calculations/sharingOfLongs.
Gas is like attention or energy.
*/
public class Node implements LongConsumer/*, LongSupplier would maybe need a Random or a loop counter which is slower*/{
	
	/** TODO
	decrement gasIn for each incoming voxel. Decrement gasOut for each outgoing voxel.
	For any 2 Nodes higherNode and lowerNode, where higherNode.sortVal>lowerNode.sortVal,
	and higherNode.gasOut>0 and lowerNode.gasIn>0, its valid to grab any random voxel in higherNode
	and show it to lowerNode, which happens by using lowerNode as LongConsumer.
	*
	public int gasIn, gasOut;
	*/
	
	public final LongToIntFunction voxelToBucket;
	
	/** each bucket contains at most 1 long. If it contains 0 longs, maybe just use the constant 0 to mean that?
	buckets[voxelToBucket.applyAsInt(voxel)] = voxel, for some voxel but one voxel can replace another voxel
	in the same bucket, depending on order they enter this node. Next one in overwrites the previous one in, or its 0.
	*/
	public final long[] buckets;

	
	public final String comment;
	
	public double sortVal;
	
	/** 0 <= voxelToBucket.applyAsInt(voxel) < buckets.length, for all long voxel */ 
	public Node(int buckets, LongToIntFunction voxelToBucket){
		this.buckets = new long[buckets];
		this.voxelToBucket = voxelToBucket;
		this.comment = ""+this;
	}
	
	public Node(String comment, int buckets, LongToIntFunction voxelToBucket){
		this(comment, new long[buckets], voxelToBucket);
	}
	
	public Node(String comment, long[] buckets, LongToIntFunction voxelToBucket){
		this.comment = comment;
		this.buckets = buckets;
		this.voxelToBucket = voxelToBucket;
	}
	
	public void accept(long voxel){
		buckets[voxelToBucket.applyAsInt(voxel)] = voxel;
	}
	
	/** removes all voxels by setting all bucket's contents to 0L */
	public void clear(){
		Arrays.fill(buckets, 0L);
	}
	
	public String toString(){
		return comment;
	}

}