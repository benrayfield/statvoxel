/** Ben F Rayfield offers this software opensource MIT license */
package statvoxel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.swing.JPanel;

public class UI extends JPanel{
	
	protected BufferedImage img;
	
	protected Node interactiveVideo;
	
	protected int interactiveVideoHeight, interactiveVideoWidth;
	
	//TODO make this a Node (todo choose some LongToIntFunction that prevents others from
	//overwriting it unless they know precisely which few possible long values go anywhere except index 0,
	//such as near 1 of the corners of the 2^16 x 1^16 x 2^16 space).
	//Would have to fix the code such as "controls['X'] = e.getX();" to only change their color (15 bits),
	//but 15 bits is enough for sound amplitudes and mouse positions and bluetooth xbox controller joystick axis etc.
	protected long[] controls = new long[128];
	/*public Node controls = new Node(128, (long voxel)->{
		TODO
	});*/
	
	/*public UI(){
		this(null,0);
	}*/
	
	public UI(Node interactiveVideo, int interactiveVideoWidth){
		this.interactiveVideo = interactiveVideo;
		this.interactiveVideoWidth = interactiveVideoWidth;
		setMinimumSize(new Dimension(100,100));
		setPreferredSize(new Dimension(1000,1000));
		setMaximumSize(new Dimension(10000,10000));
		displaySomethingVerySimple();
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				controls['X'] = e.getX();
				controls['Y'] = e.getY();
				onUIEvent();
			}
			public void mouseDragged(MouseEvent e){
				mouseMoved(e);
			}
		});
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				controls['O'] += e.getClickCount(); //FIXME this needs statistical norming. get that code from listweb, and maybe decay it toward 0.
				onUIEvent();
			}
		});
		addMouseListener(new MouseListener(){
			public void mouseReleased(MouseEvent e){
				controls[e.getButton()] = 0;
				onUIEvent();
			}
			public void mousePressed(MouseEvent e){
				controls[e.getButton()] = 1;
				onUIEvent();
			}
			public void mouseExited(MouseEvent e){
				controls['M'] = 0; //0 when mouse is in this component, cuz all controls being 0 should be a working state
				onUIEvent();
			}
			public void mouseEntered(MouseEvent e){
				controls['M'] = 1;
				onUIEvent();
			}
			public void mouseClicked(MouseEvent e){}
		});
		addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){}
			public void keyReleased(KeyEvent e){
				controls[e.getKeyCode()&0x7f] = 0;
				onUIEvent();
			}
			public void keyPressed(KeyEvent e){
				controls[e.getKeyCode()&0x7f] = 1;
				onUIEvent();
			}
		});
		addFocusListener(new FocusListener(){
			public void focusLost(FocusEvent e){
				controls['F'] = 1;
				onUIEvent();
			}
			public void focusGained(FocusEvent e){
				controls['F'] = 0; //0 when this component has focus, cuz all controls being 0 should be a working state
				onUIEvent();
			}
		});
		resetControls();
		/*if(interactiveVideo != null){ //FIXME this is just during transition from int[] to long[] for screen pixels.
			setPixels(interactiveVideo, interactiveVideoWidth,true);
		}*/
	}
	
	protected void onUIEvent(){
		repaint(); //waits until all ui events have finished so doesnt repeat multiple times at once
	}
	
	protected void resetControls(){
		Arrays.fill(controls, 0);
	}
	
	/** copies it */
	protected long[] controls(){
		return controls.clone();
	}
	
	protected void displaySomethingVerySimple(){
		int[] colorARGB = new int[256];
		for(int i=0; i<256; i++) colorARGB[i] = 0xff000000 | (i*0x10101); //grayscale gradual increase of brightness
		setPixels(colorARGB, 16); //display something simple when it starts
	}
	
	/** image.buckets[y*width+x] is voxel to display at that x and y. It only has 15 bits of color, near the high end.
	Copy its color to the BufferedImage, for each 2d pixel. Repaint().
	*/
	public void setPixels(Node image, int width, boolean repaint){
		long[] voxels = image.buckets;
		int height = voxels.length/width;
		if(width*height != voxels.length) throw new RuntimeException("Doesnt divide evenly");
		if(img == null || img.getWidth() != width || img.getHeight() != height){
			img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		}
		int i = 0;
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int colorARGB = Funcs.colorARGB(voxels[i++]);
				img.setRGB(x, y, colorARGB);
			}
		}
		if(repaint) repaint();
	}
	
	/** This works (as of 2020-12-22-1240p), but you should probably use setPixels(Node,int) instead */
	public void setPixels(int[] colorARGB, int width){
		int height = colorARGB.length/width;
		if(width*height != colorARGB.length) throw new RuntimeException("Doesnt divide evenly");
		if(img == null || img.getWidth() != width || img.getHeight() != height){
			img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		}
		
		/**WritableRaster wr = img.getRaster();
		
		** FIXME this used to be DataBufferInt but in later version of java or maybe this jvm (openjdk11) its DataBufferByte,
		and I want it to be compatible, so handle both or use BufferedImage directly...
		
		Exception in thread "main" java.lang.ClassCastException: class java.awt.image.DataBufferByte cannot be cast to class java.awt.image.DataBufferInt (java.awt.image.DataBufferByte and java.awt.image.DataBufferInt are in module java.desktop of loader 'bootstrap')
		at statvoxel.UI.setPixels(UI.java:104)
		at statvoxel.UI.displaySomethingVerySimple(UI.java:94)
		at statvoxel.UI.<init>(UI.java:32)
		at statvoxel.Start.main(Start.java:5)
		*
		DataBufferInt buffer = (DataBufferInt) wr.getDataBuffer();
		*
		DataBuffer buffer = wr.getDataBuffer();
		if(buffer instanceof DataBufferInt){
			wr.setPixels(0, 0, width, height, colorARGB);
		}else if(buffer instanceof DataBufferByte){
			buffer.
			wr.setpi
			wr.setPixels(0, 0, width, height, colorARGB);
		}else{
		*/
			int i = 0;
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					img.setRGB(x, y, colorARGB[i++]);
				}
			}
		//}
		repaint();
	}
	
	public void paint(Graphics g){
		setPixels(interactiveVideo, interactiveVideoWidth, false); //copy interactiveVideo to BufferedImage img
		ScreenUtil.paintOGIYXHW(this, g, img, 0, 0, getHeight(), getWidth());
		//g.setColor(Color.blue);
		//g.drawLine(0, 0, getWidth(), getHeight());
	}

}
