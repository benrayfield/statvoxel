package statvoxel;
import static statvoxel.Funcs.*;
import java.util.Random;

public class Start{
	public static void main(String[] args){
		UI ui = new UI();
		ScreenUtil.testDisplayWithExitOnClose(ui);
		Random r = Rand.weakRand;
		long[] vox = randVoxels(r, 50000);
		int backgroundColor = 0xff000000; //black
		int[] image = image2d_1024_1024_ARGB(vox, backgroundColor);
		boolean is16x16SizeVoxelsInsteadOf4x4 = false;
		//magnify (ugly hack where they overlap) each pixel to 4x4 or 16x16
		//image2d_1024_1024_ARGB_expandTo4x4ByORs_or16x16(image, is16x16SizeVoxelsInsteadOf4x4);
		image2d_1024_1024_ARGB_expandTo4x4ByXORss_or16x16(image, is16x16SizeVoxelsInsteadOf4x4);
		ui.setPixels(image, 1024);
	}
}
