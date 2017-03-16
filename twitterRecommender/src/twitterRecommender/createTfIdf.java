package twitterRecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class createTfIdf {

	public static double totalHashtag;
	public static boolean time = true; // se stampare anche la colonna del tempo

	public static void main(String[] args) throws IOException {
		FileReader f;
		f = new FileReader("C:/Users/alessio/workspace/twitterRecommender/UserHashtagDataset.csv");
		BufferedReader br = new BufferedReader(f);


		Map<String,List<Hashtag>> rating = new HashMap<>();
		Map<String,Integer> numTwitt = new HashMap<>();
		Map<String,Integer> peopleUsedHashtag = new HashMap<>();
		totalHashtag = 0;
		List<Hashtag> hashtags;
		Hashtag h;
		String sRead, user, ht;
		int date;
		String[] userHashtagDate;
		sRead = br.readLine();
		while(sRead!=null) {
			userHashtagDate = sRead.split(",");
			user = userHashtagDate[0];
			ht = userHashtagDate[1];
			date = Integer.parseInt(userHashtagDate[2]);

			if(numTwitt.containsKey(user))
				numTwitt.put(user, numTwitt.get(user)+1);
			else numTwitt.put(user, 1);

			if (rating.containsKey(user)) {
				hashtags = rating.get(user);
				h = getHashtag(hashtags,ht);
				if (h!=null) {
					h.increment();
					h.setDate(date);
				}
				else {
					hashtags.add(new Hashtag(ht,date));

					if(peopleUsedHashtag.containsKey(ht))
						peopleUsedHashtag.put(ht, peopleUsedHashtag.get(ht)+1);
					else peopleUsedHashtag.put(ht, 1);

				}
			}
			else {
				hashtags = new LinkedList<>();
				hashtags.add(new Hashtag(ht,date));
				rating.put(user,hashtags );

				peopleUsedHashtag.put(ht, 1);
			}

			sRead = br.readLine();
			totalHashtag ++;
		}


		br.close();
		writeRatings(rating, numTwitt, peopleUsedHashtag);

	}

	private static void writeRatings(Map<String, List<Hashtag>> rating, Map<String,Integer> numTwitt,
			Map<String,Integer> peopleUsedHashtag) throws IOException {
		FileWriter w,w2;
		w = new FileWriter("C:/Users/alessio/workspace/librec/data/twitter/UHTfIdfTrainingSet.csv");
		BufferedWriter bw = new BufferedWriter (w);
		w2 = new FileWriter("C:/Users/alessio/workspace/librec/data/twitter/UHTfIdfTestSet.csv");
		BufferedWriter bw2 = new BufferedWriter (w2);
		Map<Hashtag,Double> tiMap = new HashMap<>();
		
		List<String> keyList = new LinkedList<>(rating.keySet());
		java.util.Collections.sort(keyList);

		double tfIdf;
		for(String user : keyList)
			for(Hashtag h : rating.get(user)) {
				tfIdf = getTfIdf(rating,numTwitt,peopleUsedHashtag,user,h);
				tiMap.put(h, (double)tfIdf);
			}
		double media = 0; //media armonica
		for(double t : tiMap.values())
			media += 1.0/t;
		media = tiMap.size()/media;
		
		String s;
		for(String user : keyList)
			for(Hashtag h : rating.get(user)) {
				double diff = (tiMap.get(h) - media)/media;
				diff = diff*5 + 5;
				if (diff>10)
					diff = 10;
				s = user+" "+h.getHashtag()+" "+tiMap.get(h);
				if (time)
					s += " "+h.getDate()+"\n";
				else s += "\n";
				if(h.getDate()<32)
					bw.write(s);
				else bw2.write(s);
			}

		bw.flush();
		bw2.flush();
		bw.close();
		bw2.close();
	}

	private static double getTfIdf(Map<String, List<Hashtag>> rating, Map<String, Integer> numTwitt, 
			Map<String, Integer> peopleUsedHashtag, String user, Hashtag h) {
		double tf = h.getCount() / (double) numTwitt.get(user);
		double idf = Math.log(rating.size() / (peopleUsedHashtag.get(h.getHashtag()))); //rating.size() = numero utenti
		return Math.round(tf*idf * 100000d) / 10000d;
	}

	public static Hashtag getHashtag(List<Hashtag> list, String s) {
		for(Hashtag h : list)
			if (h.getHashtag().equals(s))
				return h;
		return null;
	}

}
