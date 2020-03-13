import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class Main {

	public static void main(String[] args) throws Exception {

		System.out.println("Starting!");
		
		//test();
		processAll();
		
		System.out.println("Finished!");

	}
	
	private static void test() throws IOException {
		File in = new File("img/test/in.png");
		File out = new File("img/test/out.png");
		File bg = new File("img/bg.png");
		processImage(in, out, bg);
	}
	
	private static void processAll() throws IOException {
		File folder = new File("img/in/");
		File[] listOfFiles = folder.listFiles();
		
		File bg = new File("img/bg.png");
		
		for(File f : listOfFiles) {
			
			String fileName = f.getName();
			File output = new File("img/out/" + fileName);
			processImage(f, output, bg);
		}
		
	}
	
	private static void processImage(File in, File out, File background) throws IOException {
		System.out.println("Processing '" + in.getName() + "'");
		BufferedImage bi = ImageIO.read(in);
		Graphics g = bi.getGraphics();
		removePageNumber(g);
		bi = resizeImageToBackgroundImage(bi);
		bi = makeImageTransparent(bi);
		
		
		//background
		BufferedImage bgBi = ImageIO.read(background);
		Graphics bgG = bgBi.getGraphics();
		
		bgG.drawImage(bi, 0, 0, null);
		
		ImageIO.write(bgBi, "PNG", out);
	}

	private static void removePageNumber(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(1560, 45, 45, 45);
	}
	
	private static BufferedImage makeImageTransparent(BufferedImage source) {
		final Image imageWithTransparency = makeColorTransparent(source);
		return imageToBufferedImage(imageWithTransparency);
	}

	private static BufferedImage imageToBufferedImage(final Image image) {
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}

	/*
	private static Image makeColorTransparent(final BufferedImage im, final Color color) {
		final ImageFilter filter = new RGBImageFilter() {
			private int markerRGB = color.getRGB() | 0xFFFFFFFF;

			@Override
			public final int filterRGB(final int x, final int y, final int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					return 0x00FFFFFF & rgb;
				}
				else {
					return rgb;
				}
			}
		};

		final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
	*/
	
	private static Image makeColorTransparent(final BufferedImage im) {
		final ImageFilter filter = new RGBImageFilter() {

			@Override
			public final int filterRGB(final int x, final int y, final int rgb) {
				Color crgb = new Color(rgb);
				float[] hsv = Color.RGBtoHSB(crgb.getRed(), crgb.getGreen(), crgb.getBlue(), null);
				
				//if its 50% black, we just call it good. Removes white edging on the image
				if (hsv[2] > .5) {
					return 0x00FFFFFF & rgb;
				}
				else {
					return rgb;
				}
			}
		};

		final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
	
	private static BufferedImage resizeImageToBackgroundImage(BufferedImage src) throws IOException {
		Dimension newMaxSize = new Dimension(1200, 1552);
		return Scalr.resize(src, Scalr.Method.QUALITY,newMaxSize.width, newMaxSize.height);
	}

}
