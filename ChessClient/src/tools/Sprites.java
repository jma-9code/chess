package tools;

import java.awt.Image;

import javax.swing.ImageIcon;

import main.Config;
import model.chessmens.Bishop;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;

import org.apache.log4j.Logger;

import commun.EColor;

public class Sprites {

	private static Sprites instance = null;
	private static final Logger logger = Logger.getLogger(Sprites.class);

	private Image[][] sprites;
	private Image[][] small_sprites;
	private ImageIcon[] countries;
	private ImageIcon star;
	private ImageIcon online;
	private ImageIcon offline;
	private ImageIcon busy;
	private ImageIcon refresh;
	private ImageIcon refreshlive;
	private ImageIcon loading;

	private Sprites() {
		// Chargement des images
		sprites = GImage.loadSprite("/res/images/sprite.png");
		small_sprites = new Image[sprites.length][sprites[0].length];
		for (int i = 0; i < small_sprites.length; i++) {
			for (int j = 0; j < small_sprites[0].length; j++) {
				small_sprites[i][j] = GImage.scale(sprites[i][j], Config.CHESSMEN_TAKED_SIZE, Config.CHESSMEN_TAKED_SIZE);
			}
		}
		setCountries(GImage.loadCountries());
		star = new ImageIcon(Sprites.class.getResource("/res/images/rank/star.png"));
		online = new ImageIcon(Sprites.class.getResource("/res/images/online.png"));
		offline = new ImageIcon(Sprites.class.getResource("/res/images/offline.png"));
		busy = new ImageIcon(Sprites.class.getResource("/res/images/busy.png"));
		refresh = new ImageIcon(Sprites.class.getResource("/res/images/refresh.png"));
		refreshlive = new ImageIcon(Sprites.class.getResource("/res/images/refresh-ongoing.gif"));
		loading = new ImageIcon(Sprites.class.getResource("/res/images/loading.gif"));
	}

	public ImageIcon getStar() {
		return star;
	}

	public ImageIcon getOnline() {
		return online;
	}

	public void setOnline(ImageIcon online) {
		this.online = online;
	}

	public void setStar(ImageIcon star) {
		this.star = star;
	}

	public Image getChessmen(Class type, EColor color, boolean small) {
		Image[][] sprite = (small) ? small_sprites : sprites;
		int offset = Config.CHESSMEN_STYLE;
		if (type == Pawn.class) {
			return sprite[5][color.ordinal() + offset];
		}
		else if (type == Rook.class) {
			return sprite[2][color.ordinal() + offset];
		}
		else if (type == Knight.class) {
			return sprite[4][color.ordinal() + offset];
		}
		else if (type == Bishop.class) {
			return sprite[3][color.ordinal() + offset];
		}
		else if (type == Queen.class) {
			return sprite[1][color.ordinal() + offset];
		}
		else if (type == King.class) {
			return sprite[0][color.ordinal() + offset];
		}
		return null;
	}

	public static Sprites getInstance() {
		if (instance == null) {
			instance = new Sprites();
		}

		return instance;
	}

	public Image[][] getSprites() {
		return sprites;
	}

	public void setSprites(Image[][] sprites) {
		this.sprites = sprites;
	}

	public Image[][] getSmall_sprites() {
		return small_sprites;
	}

	public void setSmall_sprites(Image[][] small_sprites) {
		this.small_sprites = small_sprites;
	}

	public ImageIcon[] getCountries() {
		return countries;
	}

	public void setCountries(ImageIcon[] countries) {
		this.countries = countries;
	}

	public ImageIcon getOffline() {
		return offline;
	}

	public void setOffline(ImageIcon offline) {
		this.offline = offline;
	}

	public ImageIcon getBusy() {
		return busy;
	}

	public void setBusy(ImageIcon busy) {
		this.busy = busy;
	}

	public ImageIcon getRefresh() {
		return refresh;
	}

	public void setRefresh(ImageIcon refresh) {
		this.refresh = refresh;
	}

	public ImageIcon getRefreshlive() {
		return refreshlive;
	}

	public void setRefreshlive(ImageIcon refreshlive) {
		this.refreshlive = refreshlive;
	}

	public ImageIcon getLoading() {
		return loading;
	}

	public void setLoading(ImageIcon loading) {
		this.loading = loading;
	}

}
