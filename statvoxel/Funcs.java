package statvoxel;

import java.util.Arrays;
import java.util.Random;
import java.util.function.LongToIntFunction;

/** functions that loop over a long[] and write into another long[]
or into an int[] for the last calculation of generating the pixels,
and may take more than 1 long[] param such as to include UI.controls() which is a long[128]
including 1 long per keyboard button, mouse button, mouseX position, etc (TODO more longs for bigger VK_ codes?).
*/
public class Funcs{
	private Funcs(){}
	
	public static short z(long voxel){
		return (short)(voxel>>>32);
	}
	
	public static short y(long voxel){
		return (short)(voxel>>>16);
	}
	
	public static short x(long voxel){
		return (short)voxel;
	}
	
	public static int pixelIndexIn1024By1024(long voxel){
		return ((y(voxel)&1023)<<10)|(x(voxel)&1023);
	}
	
	public static short color16(long voxel){
		return (short)(voxel>>>48);
	}
	
	public static int colorARGB(long voxel){
		//the high 16 bits contain color. Its highest bit, maybe leave that for possible transparency/erasing things later.
		//5 bits for each of red green blue.
		
		int high16Bits = (int)(voxel>>>48);
		return
			0xff000000
			| ((high16Bits<<9) &0xff0000)
			| ((high16Bits<<6) &0x00ff00)
			| ((high16Bits<<3)&0x0000ff);
		
		/* No, this would be to just get the 15 bits...
		(voxel>>>58)&0b111110000000000;
		(voxel>>>53)&0b000001111100000;
		(voxel>>>48)&0b000000000011111;
		*/
	}
	
	/** modifies param to OR with itself whataver pixels are there up to 3 pixels to the right and 3 pixels down,
	so its size 4x4 voxels except where multiple voxels overlap you get OR of their colors.
	*/
	public static void image2d_1024_1024_ARGB_expandTo4x4ByORs_or16x16(int[] image1024x1024, boolean is16x16_elseIs4x4){
		int end = (1<<20)-(4*(1<<10)); //exclude last 4 rows. ugly but faster.
		int[] retImage = new int[image1024x1024.length];
		for(int i=0; i<end; i++){
			retImage[i] = image1024x1024[i]|image1024x1024[i+1]|image1024x1024[i+2]|image1024x1024[i+3];
		}
		for(int i=0; i<end; i++){
			image1024x1024[i] = retImage[i]|retImage[i+(1<<10)]|retImage[i+(2<<10)]|retImage[i+(3<<10)];
		}
		if(is16x16_elseIs4x4){
			end = (1<<20)-(16*(1<<10)); //exclude last 16 rows. ugly but faster.
			for(int i=0; i<end; i++){
				retImage[i] = image1024x1024[i]|image1024x1024[i+4]|image1024x1024[i+8]|image1024x1024[i+12];
			}
			for(int i=0; i<end; i++){
				image1024x1024[i] = retImage[i]|retImage[i+(4<<10)]|retImage[i+(8<<10)]|retImage[i+(12<<10)];
			}
		}
	}
	
	public static void image2d_1024_1024_ARGB_expandTo4x4ByXORss_or16x16(int[] image1024x1024, boolean is16x16_elseIs4x4){
		int end = (1<<20)-(4*(1<<10)); //exclude last 4 rows. ugly but faster.
		int[] retImage = new int[image1024x1024.length];
		for(int i=0; i<end; i++){
			retImage[i] = image1024x1024[i]^image1024x1024[i+1]^image1024x1024[i+2]^image1024x1024[i+3];
		}
		for(int i=0; i<end; i++){
			image1024x1024[i] = 0xff000000 | (retImage[i]^retImage[i+(1<<10)]^retImage[i+(2<<10)]^retImage[i+(3<<10)]);
		}
		if(is16x16_elseIs4x4){
			end = (1<<20)-(16*(1<<10)); //exclude last 16 rows. ugly but faster.
			for(int i=0; i<end; i++){
				retImage[i] = image1024x1024[i]^image1024x1024[i+4]^image1024x1024[i+8]^image1024x1024[i+12];
			}
			for(int i=0; i<end; i++){
				image1024x1024[i] = 0xff000000 | (retImage[i]^retImage[i+(4<<10)]^retImage[i+(8<<10)]^retImage[i+(12<<10)]);
			}
		}
	}
	
	/** paints into the colorARGB dense 2d image */
	public static int[] image2d_1024_1024_ARGB(long[] voxels, int backgroundColor){
		int[] image2d_1024_1024_ARGB = new int[1<<20];
		Arrays.fill(image2d_1024_1024_ARGB, backgroundColor);
		//if(color.length != (1<<20)) throw new RuntimeException("only allows 1 size. todo");
		for(long voxel : voxels){
			image2d_1024_1024_ARGB[pixelIndexIn1024By1024(voxel)] = colorARGB(voxel);
		}
		return image2d_1024_1024_ARGB;
	}
	
	/** returns a Node that always overwrites index 0 for any incoming voxel, since they have to go somewhere in it,
	so it leaves the other longs as they are.
	*/
	public static Node wrap(String comment, long... array){
		return new Node(comment, array, (long voxel)->0);
	}
	
	public static Node wrap(long... array){
		return new Node(array.length, (long voxel)->0);
	}
	
	public static long strongrandVoxel(){
		return Rand.strongRand.nextLong()|(1L<<63);
	}
	
	public static Node randVoxels(Random rand, int quantity){
		long[] voxels = new long[quantity];
		//sign bit is alpha (nontransparent). 0 to delete/invisible. TODO, ignoring alpha for now 2020-12-22.
		for(int i=0; i<quantity; i++) voxels[i] = rand.nextLong()|(1L<<63);
		return wrap("startedAsRandVoxels"+quantity, voxels);
	}
	
	public static long distanceSquared(long voxelA, long voxelB){
		long dx = x(voxelB)-x(voxelA);
		long dy = y(voxelB)-y(voxelA);
		long dz = z(voxelB)-z(voxelA);
		return dx*dx + dy*dy + dz*dz;
	}
	
	public static long voxel(short color, short z, short y, short x){
		return ((((long)color)<<48)&0xffff000000000000L)
			| ((((long)z)<<32)&0xffff00000000L)
			| ((((long)y)<<16)&0xffff0000L)
			| ((((long)x))&0xffffL);
	}
	
	public static short ave(short a, short b){
		return (short)((a+b)>>1);
	}
	
	public static long midpoint(long voxelA, long voxelB){
		//FIXME each uint5 of red green and blue has to be averaged separately,
		//so for now lets just use the color of voxelA
		return voxel(color16(voxelA), ave(z(voxelA),z(voxelB)), ave(y(voxelA),y(voxelB)), ave(x(voxelA),x(voxelB)));
		
		//TODO try this optimization
		//long mask = 0x8001000100010001L; //select the lowest bit of color, z, y, and x, and the highest bit aka alpha
		//return ((voxelA&(~mask))>>1)+((voxelB&(~mask))>>1);
		
	}
	
	/*public static void sortByDistanceTo(long[] voxels, long here){
		Arrays.sort(voxels, c);
		Arrays.sort((Long[])voxels, (long a, long b)->{
			long distA = distanceSquared(a,here);
			long distB = distanceSquared(b,here);
			Long.
		});
	}*/
	
	public static void stream(Node from, Node to){
		if(from.sortVal > to.sortVal){
			//slow: for(long voxel : from.buckets) to.accept(voxel); Do the same thing but faster below...
			final long[] fromBuckets = from.buckets;
			final long[] toBuckets = to.buckets;
			final LongToIntFunction bucketChooser = to.voxelToBucket;
			for(int i=0; i<fromBuckets.length; i++){
				long voxel = fromBuckets[i];
				toBuckets[bucketChooser.applyAsInt(voxel)] = voxel;
			}
		}
	}
	
	public static Node screenPixels512x512_wrapXAndYIntoThatIgnoringZ(){
		return new Node("screenPixels512x512_wrapXAndYIntoThatIgnoringZ", 512*512, (long voxel)->{
			return ((y(voxel)&511)<<9)|(x(voxel)&511);
		});
	}
	
	public static class ScreenPixels512x512_mutableAftrans4x4 extends Node{
		public final float[][] aftrans;
		
		//TODO try this optimization (but for now just do float[4][4] and start moving around in the 3d world).
		//color must be 0. Does 3 multiplies at a time.
		//BUT fixme must be adjusted to use uint16 in those 3 ranges as signed int16,
		//public long aftransX, aftransY, aftransZ, aftransMove;
		
		public static final int Z = 3, Y = 2, X = 1, MOVE = 0;
		
		public ScreenPixels512x512_mutableAftrans4x4(){
			super("ScreenPixels512x512_mutableAftrans4x4", 512*512, null);
			aftrans = new float[4][4];
			aftrans[0][0] = aftrans[1][1] = aftrans[2][2] = aftrans[3][3] = 1;
			this.voxelToBucket = (long voxel)->{
				int z = z(voxel), y = y(voxel), x = x(voxel);
				int bucketY = (int)(aftrans[Z][Y]*z+aftrans[Y][Y]*y+aftrans[X][Y]*x+aftrans[MOVE][Y]); //FIXME
				int bucketX = (int)(aftrans[Z][X]*z+aftrans[Y][X]*y+aftrans[X][X]*x+aftrans[MOVE][X]); //FIXME
				bucketY = Math.max(0, Math.min(bucketY, 511));
				bucketX = Math.max(0, Math.min(bucketX, 511));
				return ((bucketY<<9)|bucketX);
			};
		}
	}
	
	/** maps voxels into buckets (like a long[512][512] image) based on [4][4] affine transform
	like normally used in 3d games. The fourth dimension is used for position offset.
	This affine transform is mutable so will accumulate voxels gradually and continue displaying
	what was in the last video frame until a voxel comes to replace it at same x and y.
	*
	public static Node screenPixels512x512_mutableAftrans4x4(){
		
		
		/*return new Node("screenPixels512x512_mutableAftrans4x4", 512*512, (long voxel)->{
			return this.Node.aftrans.length;
			//return 345; //FIXME
			//return ((y(voxel)&511)<<9)|(x(voxel)&511);
		}){
			public final float[][] aftrans = new float[4][4];
			{ this.aftrans[0][0] = this.aftrans[1][1] = this.aftrans[2][2] = this.aftrans[3][3] = 1; }
		};*
		
		/*return new Node("screenPixels512x512_mutableAftrans4x4", 512*512, (long voxel)->{
			return 345; //FIXME
			//return ((y(voxel)&511)<<9)|(x(voxel)&511);
		}){
			public final float[][] aftrans = new float[4][4];
			{ this.aftrans[0][0] = this.aftrans[1][1] = this.aftrans[2][2] = this.aftrans[3][3] = 1; }
		};*
	}*/
	
	public static String toString(long voxel){
		return "("+z(voxel)+" "+y(voxel)+" "+x(voxel)+" #"+colorARGB(voxel)+")";
	}
	
	/*public static Node circle(int buckets, long voxelAtCenter, float radius){
		return new Node(buckets, (long voxel)->{
			TODO
		});
	}*/

}
