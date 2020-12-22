package statvoxel;
import static statvoxel.Funcs.*;

import java.util.Arrays;
import java.util.Random;

public class Start{
	public static void main(String[] args){
		Random r = Rand.weakRand;
		GameWorld world = new GameWorld();
		
		//Node vox = randVoxels(r, 50000);
		Node vox = randVoxels(r, 500);
		
		
		//changes to interactiveVideo display in UI after UI.repaint()
		Node interactiveVideo = Funcs.screenPixels512x512_wrapXAndYIntoThatIgnoringZ();
		long startBackgroundAs = strongrandVoxel();
		Arrays.fill(interactiveVideo.buckets, startBackgroundAs);
		UI ui = new UI(interactiveVideo, 512);
		
		ScreenPixels512x512_mutableAftrans4x4 display3d = new ScreenPixels512x512_mutableAftrans4x4();
		display3d.aftrans[3][3] = display3d.aftrans[2][2] = display3d.aftrans[1][1] = .01f;
		UI ui2 = new UI(display3d, 512);
		
		ScreenUtil.testDisplayWithExitOnClose(ui);
		ScreenUtil.testDisplayWithExitOnClose(ui2);
		
		world.add(vox);
		world.add(interactiveVideo);
		
		//TODO world.add(ui.controls); but FIXME controls isnt a Node yet, its just a long[] in UI
		//and isnt limiting the values to just changing color15 (changes the low 48 bits which it shouldnt)
		
		world.becomeWriter(vox);
		world.becomeReader(display3d);
		
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
			
			//for(int i=0; i<10000; i++) interactiveVideo.accept(strongrandVoxel());
			long a = world.findRandomVoxel(Rand.strongRand);
			long b = world.findRandomVoxel(Rand.strongRand);
			//interactiveVideo.accept(midpoint(a, b));
			interactiveVideo.accept(midpoint(midpoint(midpoint(a,b),a),a));
			
			for(int j=0; j<4; j++){
				for(int k=0; k<4; k++){
					//move/bend randomly the aftrans 3d view a little each video frame, just to see it rotating etc.
					display3d.aftrans[j][k] += (float)(Rand.strongRand.nextGaussian()*.00001);
				}
			}
			
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
