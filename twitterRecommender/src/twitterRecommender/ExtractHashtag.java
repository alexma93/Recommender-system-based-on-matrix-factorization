package twitterRecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExtractHashtag {

	public static void main(String[] args) throws IOException {
		FileReader f;
		f = new FileReader("C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/dataset.csv");
		BufferedReader br = new BufferedReader(f);
		FileWriter w;
		w = new FileWriter("C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/UserHashtagDataset.csv");
		BufferedWriter bw = new BufferedWriter (w);

		String sRead, sWrite, sDate;
		char cur;
		sRead = br.readLine();
		sWrite = "";
		sDate = "";
		int trovati = 0; // numero di £§ trovati
		List<String> hashtagTrovati = new LinkedList<>();
		StringBuilder hashtag = null; // se ho trovato l'hashtag
		/* questo ciclo crea i vari rating a partire da un file con il seguente formato di strighe:
		 * "id utente"£§"nome utente"£§" parole #hashtag parole"£§"2012-05-14"
		 */
		while(sRead!=null) {
			for(int j = 0; j < sRead.length(); j++) {
				cur = sRead.charAt(j);
				if (trovati == 2) { // sono nel tweet
					if (cur=='£' && sRead.charAt(j+1)=='§') {
						trovati++;
						j++;
					} 
					else if(cur=='#')
						hashtag = new StringBuilder();
					else if (hashtag!=null && !((cur>='a' && cur <='z')||(cur>='A' && cur <='Z')||(cur>='0'&&cur<='9')))  {
						if (hashtag.length()>0)
							hashtagTrovati.add(hashtag.toString());
						hashtag = null;
					}
					else if (hashtag!=null)
						hashtag.append(cur);
				}
				else if (trovati==3) { // la data
					sDate += cur;
				}
				else { // sono nell'id utente e nome utente
					if (cur=='£' && sRead.charAt(j+1)=='§') {
						trovati ++;
						j++;
					} else if (trovati ==0) // ==1 se voglio il nome utente
						sWrite += cur;

				}
			}
			if (trovati<3) { // se il tweet contiene un accapo
				sRead = br.readLine();
			}
			else {
				int date;
				for(String ht : hashtagTrovati) {
					date = convertDate(sDate);
					bw.write(sWrite+","+ht.toLowerCase()+","+date+"\n");
				}
				sRead = br.readLine();
				sWrite = "";
				sDate = "";
				trovati = 0;
				hashtagTrovati = new LinkedList<>();
				hashtag = null;
			}

		}
		bw.flush();
		br.close();
		bw.close();

	}

	// ricavo la data come il numero di giorni a partire dal 14-05-12 da stringhe del tipo "2012-05-15"
	private static int convertDate(String sDate) {
		try {
		int mese = Integer.parseInt(sDate.substring(6, 8));
		int giorno = Integer.parseInt(sDate.substring(9, 11));
		
		return (mese-5)*30 + giorno - 13;
		}
		catch(Exception e) {
			return 2000000; // un numero individuabile nel file di testo
		}
	}

}
