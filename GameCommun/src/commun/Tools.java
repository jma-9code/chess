package commun;

public class Tools {	
	
	public static int getNewScore(int scorePlayer, int scoreAdverse, boolean win){
		double p1 = 1d / (1d + Math.pow(10d, ((double)(-scorePlayer + scoreAdverse)) / 400d));
		if (win){
			return scorePlayer + (int) (Math.floor(50d * (1d - p1) + 0.5d));
		}else{
			return scorePlayer + (int) (Math.floor(50d * (0d - p1) + 0.5d));
		}
	}
}
