package model;

import org.apache.log4j.Logger;

public class StrokeIA {

	private static final Logger logger = Logger.getLogger(StrokeIA.class);
	
	int src_x = 0;
	int src_y = 0;
	int dst_x = 0;
	int dst_y = 0;
	int quality = 0;

	public StrokeIA ( int _quality, int src_x, int src_y, int dst_x, int dst_y ) {
		super();
		quality = _quality;
		this.src_x = src_x;
		this.src_y = src_y;
		this.dst_x = dst_x;
		this.dst_y = dst_y;
	}

	public int getSrc_x() {
		return src_x;
	}

	public void setSrc_x(int src_x) {
		this.src_x = src_x;
	}

	public int getSrc_y() {
		return src_y;
	}

	public void setSrc_y(int src_y) {
		this.src_y = src_y;
	}

	public int getDst_x() {
		return dst_x;
	}

	public void setDst_x(int dst_x) {
		this.dst_x = dst_x;
	}

	public int getDst_y() {
		return dst_y;
	}

	public void setDst_y(int dst_y) {
		this.dst_y = dst_y;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

}
