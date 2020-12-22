package statvoxel;
import static statvoxel.Funcs.*;

import java.util.Arrays;
import java.util.Random;

public class Start{
	public static void main(String[] args){
		Random r = Rand.weakRand;
		GameWorld world = new GameWorld();
		
		Node vox = randVoxels(r, 50000);
		
		
		//changes to interactiveVideo display in UI after UI.repaint()
		Node interactiveVideo = Funcs.screenPixels512x512_wrapXAndYIntoThatIgnoringZ();
		long startBackgroundAs = strongrandVoxel();
		Arrays.fill(interactiveVideo.buckets, startBackgroundAs);
		UI ui = new UI(interactiveVideo, 512);
		
		ScreenUtil.testDisplayWithExitOnClose(ui);
		
		world.add(vox);
		world.add(interactiveVideo);
		
		world.becomeWriter(vox);
		
		stream(vox, interactiveVideo);
		
		int print = 20;
		for(long voxel : interactiveVideo.buckets){
			if(voxel != startBackgroundAs){
				System.out.println("voxel in interactiveVideo: "+Funcs.toString(voxel));
				if(print-- <= 0) break;
			}
		}
		
		ui.repaint();
		
		while(true){
			
			for(int i=0; i<10000; i++) interactiveVideo.accept(strongrandVoxel());
			
			world.nextState();
			
			//TODO use statistics to sleep only until its time to paint again, or use that time to GameWorld.nextState() more
			//and just Thread.yield here?
			//Time.sleepNoThrow(.003);
			Thread.yield();
		}
		
		//int backgroundColor = 0xff000000; //black
		//int[] image = image2d_1024_1024_ARGB(vox, backgroundColor);
		
		//boolean is16x16SizeVoxelsInsteadOf4x4 = false;
		//magnify (ugly hack where they overlap) each pixel to 4x4 or 16x16
		//image2d_1024_1024_ARGB_expandTo4x4ByORs_or16x16(image, is16x16SizeVoxelsInsteadOf4x4);
		//image2d_1024_1024_ARGB_expandTo4x4ByXORss_or16x16(image, is16x16SizeVoxelsInsteadOf4x4);
		
		//ui.setPixels(image, 1024);
		
	}
}
