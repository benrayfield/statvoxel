package statvoxel;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/** Given any 2 voxels, derives another voxel. Return 0L to not paint anything.
For example, given a voxel representing mouse position the mouse x position is 539
(stored in the 15 bits of color, like wave amplitude could also be stored there, or variety of other things)
and any other voxel, those 2 together might imply that other voxel offset by 539,
just as a very basic experiment,
but that leaves the old voxel positions and the new positions both existing,
and you'd then get another copy as 539+539 from where they started, and so on.
This is where buckets are very useful. They each hold at most 1 long,
so the total voxels in the system is limited by the sum of Node.bucket.length,
for all Node in GameWorld.nodesSet. Which are kept and which are overwritten
depends on the LongToIntFunctions in the Nodes and sortVal for which write those of lower sortVal.
<br><br>
Its of course very expensive to show a Painter all pairs of voxels in the system,
so I'm unsure how complete this might be used,
but it could at least try many random pairs of voxels.
*/
public class BinPainter{
	
	public final LongBinaryOperator implies;
	
	public BinPainter(LongBinaryOperator implies){
		this.implies = implies;
	}
	
	/** uses the first of 2 params, ignoring the second */
	public BinPainter(UnaryPainter implies){
		this(implies.implies);
	}
	
	public BinPainter(LongUnaryOperator implies){
		this((long x, long y)->implies.applyAsLong(x)); //TODO optimize: get LongUnaryOperator out of it and put in final var before creating the LongBinaryOperator
	}

}
