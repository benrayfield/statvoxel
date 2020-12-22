package statvoxel;

import java.util.Arrays;
import java.util.Random;

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
	
	public static long[] randVoxels(Random rand, int quantity){
		long[] voxels = new long[quantity];
		//sign bit is alpha (nontransparent). 0 to delete/invisible. TODO, ignoring alpha for now 2020-12-22.
		for(int i=0; i<quantity; i++) voxels[i] = rand.nextLong()|(1L<<63);
		return voxels;
	}
	
	public static long distanceSquared(long voxelA, long voxelB){
		long dx = x(voxelB)-x(voxelA);
		long dy = y(voxelB)-y(voxelA);
		long dz = z(voxelB)-z(voxelA);
		return dx*dx + dy*dy + dz*dz;
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
			for(long voxel : from.buckets) to.accept(voxel);
		}
	}
	
	public static Node screenPixels512x512_wrapXAndYIntoThatIgnoringZ(){
		return new Node(512*512, (long voxel)->{
			return ((y(voxel)&511)<<9)|(x(voxel)&511);
		});
	}
	
	/*public static Node circle(int buckets, long voxelAtCenter, float radius){
		return new Node(buckets, (long voxel)->{
			TODO
		});
	}*/

}
