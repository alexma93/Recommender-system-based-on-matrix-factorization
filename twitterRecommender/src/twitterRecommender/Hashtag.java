package twitterRecommender;

public class Hashtag {

	private String hashtag;
	private int date;
	private int count;
	
	public Hashtag(String h,int d) {
		this.setHashtag(h);
		this.setDate(d);
		this.count = 1;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increment() {
		this.count ++;
	}
	
	
}
