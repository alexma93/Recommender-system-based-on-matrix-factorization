package testMiei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.librec.conf.Configuration;
import net.librec.data.model.TextDataModel;
import net.librec.eval.RecommenderEvaluator;
import net.librec.eval.rating.RMSEEvaluator;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.math.structure.DenseMatrix;
import net.librec.recommender.MatrixFactorizationRecommender;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.ItemKNNRecommender;
import net.librec.recommender.cf.rating.SVDPlusPlusRecommender;
import net.librec.recommender.context.rating.TimeSVDRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.CosineSimilarity;
import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;

public class MainClass {
	public static void main(String[] args) throws Exception {

		// build data model
		Configuration conf = new Configuration();
		conf.set("dfs.data.dir", "C:/Users/alessio/workspace/librec/data");
		TextDataModel dataModel = new TextDataModel(conf);
		dataModel.buildDataModel();

		// build recommender context
		RecommenderContext context = new RecommenderContext(conf, dataModel);

		// build similarity
		conf.set("rec.recommender.similarities" ,"user");
		RecommenderSimilarity similarity = new CosineSimilarity();
		similarity.buildSimilarityMatrix(dataModel);
		context.setSimilarity(similarity);

		// build recommender
		Recommender recommender = new TimeSVDRecommender();
		recommender.setContext(context);

		// run recommender algorithm
		recommender.recommend(context);

		// evaluate the recommended result
		RecommenderEvaluator evaluator = new RMSEEvaluator();
		System.out.println("RMSE:" + recommender.evaluate(evaluator));


		MatrixFactorizationRecommender rec = (MatrixFactorizationRecommender) recommender;
		DenseMatrix similarityMatrix = EvaluationMatrixFactorization.createSimilarityMatrix(rec.getUserFactors());
		List<UserSimilarity> similarities = EvaluationMatrixFactorization.getRecommendedList(similarityMatrix, 
				rec.userMappingData.inverse(), rec.itemMappingData.inverse());


		// set id list of filter
		List<String> userIdList = new ArrayList<>();
		//List<String> itemIdList = new ArrayList<>();
		userIdList.add("\"142052768\"");
		//itemIdList.add("9");
		//userIdList.add("244");
		//userIdList.add("224");
		
		printRecommendedUsers(userIdList,similarities,10);

		/*// filter the recommended result
		List<RecommendedItem> recommendedItemList = recommender.getRecommendedList();
		GenericRecommendedFilter filter = new GenericRecommendedFilter();
		filter.setUserIdList(userIdList);
		filter.setItemIdList(itemIdList);
		recommendedItemList = filter.filter(recommendedItemList);

		// print filter result
		for (RecommendedItem recommendedItem : recommendedItemList) {
			System.out.println(
					"user:" + recommendedItem.getUserId() + " " +
							"item:" + recommendedItem.getItemId() + " " +
							"value:" + recommendedItem.getValue()
					);
		}*/
	}

	public static void printRecommendedUsers(List<String> userIdList, List<UserSimilarity> similarities, int quantity) throws IOException {
		int i;
		List<String> recommended;
		
		Map<String,List<String>> hashtags = getUserHashtags();
		for (String id : userIdList) {
			i = 0;
			recommended = new LinkedList<>();
			for( UserSimilarity us : similarities) {
				if (us.getU1().equals(id)) {
					i++;
					recommended.add(us.getU2());
				}
				else if (us.getU2().equals(id)) {
					i++;
					recommended.add(us.getU1());
				}
				if (i >= quantity)
					break;
			}
			System.out.println(id+": "+ hashtags.get(id).toString());
			for(String us : recommended) {
				try {
				System.out.println("\t"+us+": "+ hashtags.get(us).toString());
				} catch(Exception e){}
			}
			System.out.println();
			
			for(String us : recommended)
				try {
				System.out.println(id+" "+us+": "+ commonHashtags(hashtags.get(id),hashtags.get(us)));
				} catch(Exception e){}
			System.out.println();
			System.out.println();
		}
	}
	
	private static List<String> commonHashtags(List<String> list, List<String> list2) {
		List<String> res = new LinkedList<>();
		for(String s : list)
			for(String t : list2)
				if (t.equals(s))
					res.add(s);
		return res;
	}

	public static Map<String,List<String>> getUserHashtags() throws IOException {
		FileReader r;
		r = new FileReader("C:/Users/alessio/workspace/librec/data/twitter/UHTfIdfTrainingSet.csv");
		BufferedReader br = new BufferedReader(r);
		
		Map<String,List<String>> hashtags = new HashMap<>();
		
		String sRead = br.readLine();
		while(sRead!=null) {
			String[] fields = sRead.split(" ");
			if (!hashtags.containsKey(fields[0]))
				hashtags.put(fields[0], new LinkedList<String>());
			hashtags.get(fields[0]).add(fields[1]);
			
			sRead = br.readLine();
		}
		
		br.close();
		return hashtags;
	}

	
	
	
	
}
