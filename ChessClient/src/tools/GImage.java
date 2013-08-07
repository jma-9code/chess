package tools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Locale;

import javax.swing.ImageIcon;

import main.Config;

import org.apache.log4j.Logger;

public class GImage {

	private static final Logger logger = Logger.getLogger(GImage.class);

	/**
	 * Charge les fichiers png correspondant aux pays présents dans le package
	 * res.images.country
	 */
	public static ImageIcon[] loadCountries() {
		String[] country = Locale.getISOCountries();
		ImageIcon[] countries = new ImageIcon[country.length];
		int i = 0;
		for (String str : country) {
			try {
				countries[i] = new ImageIcon(GImage.class.getResource("/res/images/country/" + str.toString().toLowerCase() + ".png"));
				countries[i].setDescription(str.toUpperCase());
				i++;
			}
			catch (Exception e) {
				logger.warn("Fichier " + str.toString().toLowerCase() + ".png indisponible");
			}
		}
		return countries;
	}

	/**
	 * Permet de remplir le tableau sprites
	 * 
	 * @param img
	 * @return
	 */
	public static Image[][] loadSprite(String path) {
		Image tab[][] = new Image[6][12];
		Image img = loadImage(path);
		if (img == null) {
			return null;
		}
		BufferedImage buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		buffImg.getGraphics().drawImage(img, 0, 0, null);
		Graphics2D g2d = (Graphics2D) buffImg.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < tab.length; i++) {
			for (int j = 0; j < tab[0].length; j++) {
				tab[i][j] = buffImg.getSubimage(i * Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE * j, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);
			}
		}
		return tab;
	}

	/**
	 * Permet de charger les images.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static Image loadImage(final String path) {
		URL imageURL = GImage.class.getResource(path);
		if (imageURL != null) {
			ImageIcon icon = new ImageIcon(imageURL);
			return icon.getImage();
		}
		return null;
	}

	/**
	 * Redimensionne une image.
	 * 
	 * @param source
	 *            Image à redimensionner.
	 * @param width
	 *            Largeur de l'image cible.
	 * @param height
	 *            Hauteur de l'image cible.
	 * @return Image redimensionnée.
	 */
	public static Image scale(Image source, int width, int height) {
		/* On crée une nouvelle image aux bonnes dimensions. */
		BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		/* On dessine sur le Graphics de l'image bufferisée. */
		Graphics2D g = buf.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(source, 0, 0, width, height, null);
		g.dispose();

		/* On retourne l'image bufferisée, qui est une image. */
		return buf;
	}

}
