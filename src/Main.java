import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) throws Exception {

		System.out.println("Starting!");
		
		File folder = new File("img/in/");
		File[] listOfFiles = folder.listFiles();
		for(File f : listOfFiles) {
			
			String fileName = f.getName();
			File output = new File("img/out/" + fileName);
			processImage(f, output);
		}
		
		System.out.println("Finished!");

	}
	
	private static void processImage(File in, File out) throws IOException {
		System.out.println("Processing '" + in.getName() + "'");
		BufferedImage bi = ImageIO.read(in);
		Graphics g = bi.getGraphics();
		removePageNumber(g);
		bi = makeImageTransparent(bi);
		ImageIO.write(bi, "PNG", out);
	}

	private static void removePageNumber(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(1560, 45, 45, 45);
	}
	
	private static BufferedImage makeImageTransparent(BufferedImage source) {
		final int color = source.getRGB(0, 0);
		final Image imageWithTransparency = makeColorTransparent(source, new Color(color));
		return imageToBufferedImage(imageWithTransparency);
	}

	private static BufferedImage imageToBufferedImage(final Image image) {
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}

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

}
