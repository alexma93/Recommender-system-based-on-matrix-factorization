package testMiei;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.BiMap;

import net.librec.math.structure.DenseMatrix;
import net.librec.similarity.CosineSimilarity;

public class EvaluationMatrixFactorization {

	public static DenseMatrix createSimilarityMatrix(DenseMatrix userFactors) {
		double cosSimilarity;
		DenseMatrix similarityMatrix = new DenseMatrix(userFactors.numRows,userFactors.numRows);
		CosineSimilarity similarity = new CosineSimilarity();


		for(int i=0; i< userFactors.numRows; i++) // per ogni coppia di utenti
			for(int j=i+1; j< userFactors.numRows; j++) {

				List<Double> u1 = doubleArrayToList(userFactors.row(i).getData());
				List<Double> u2 = doubleArrayToList(userFactors.row(j).getData());
				cosSimilarity = similarity.getSimilarity(u1, u2);
				similarityMatrix.add(i, j, cosSimilarity);
				similarityMatrix.add(j, i, cosSimilarity);
			}
		return similarityMatrix;
	}

	public static List<UserSimilarity> getRecommendedList(DenseMatrix similarityMatrix, 
			BiMap<Integer, String> userMappingInverse, BiMap<Integer, String> itemMappingInverse) {
		List<UserSimilarity> userItemList = new ArrayList<>();
		for(int i=0; i< similarityMatrix.numRows; i++) // per ogni coppia di utenti
			for(int j=i+1; j< similarityMatrix.numColumns; j++) {
				String userId = userMappingInverse.get(i);
				String itemId = userMappingInverse.get(j);
				if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(itemId)) {
					userItemList.add(new UserSimilarity(userId, itemId, similarityMatrix.get(i, j)));
				}
			}
		userItemList.sort(null);
		return userItemList;
	}

	/*public static List<UserSimilarity> getTopSimilar(DenseMatrix similarityMatrix, int num) {
		List<UserSimilarity> res = new LinkedList<>();
		for(int i=0; i< similarityMatrix.numRows; i++) // per ogni coppia di utenti
			for(int j=i+1; j< similarityMatrix.numColumns; j++)
				res.add(new UserSimilarity(i,j,similarityMatrix.get(i, j)));

		res.sort(null);
		return res.subList(0, num);
	} */



	private static List<Double> doubleArrayToList(double[] a) {
		List<Double> l = new ArrayList<>(a.length);
		for(int i = 0; i< a.length; i++)
			l.add(a[i]);
		return l;
	}
}
