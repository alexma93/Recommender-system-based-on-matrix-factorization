package testMiei;

public class UserSimilarity implements Comparable<UserSimilarity> {
	private String u1;
	private String u2;
	private double similarity;

	public UserSimilarity(String i, String j,double val) {
		this.u1 = i;
		this.u2 = j;
		this.similarity = val;
	}

	
	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getU2() {
		return u2;
	}

	public void setU2(String u2) {
		this.u2 = u2;
	}

	public String getU1() {
		return u1;
	}

	public void setU1(String u1) {
		this.u1 = u1;
	}


	@Override
	public int compareTo(UserSimilarity arg0) {
		if ((this.similarity - arg0.similarity) >0)
			return -1;
		else return 1;
	}
}
