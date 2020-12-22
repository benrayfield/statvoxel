package statvoxel;

import java.util.function.LongUnaryOperator;

/** like BinPainter except its just 1 voxel in and 1 voxel out. Return 0L to not paint anything. */
public class UnaryPainter{
	
	public final LongUnaryOperator implies;
	
	public UnaryPainter(LongUnaryOperator implies){
		this.implies = implies;
	}

}
