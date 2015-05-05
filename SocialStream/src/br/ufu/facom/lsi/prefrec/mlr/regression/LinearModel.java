package br.ufu.facom.lsi.prefrec.mlr.regression;

import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.management.openmbean.InvalidKeyException;

import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.mymedialite.data.EntityMapping;
import org.mymedialite.data.IEntityMapping;
import org.mymedialite.data.IRatings;
import org.mymedialite.data.RatingType;
import org.mymedialite.datatype.SparseBooleanMatrix;
import org.mymedialite.io.AttributeData;
import org.mymedialite.io.StaticRatingData;
import org.mymedialite.ratingprediction.ItemAttributeKNN;
import org.mymedialite.ratingprediction.ItemKNN;
import org.mymedialite.ratingprediction.ItemKNNPearson;
import org.mymedialite.ratingprediction.MatrixFactorization;

import br.ufu.facom.lsi.prefrec.mlr.matrix.Matrix;

public class LinearModel {

	// object's variable
	int numItems;
	int numUsers;
	List<Quartet<Double, Double, Double, Double>> predictions;

	static IEntityMapping item_mapping;
	static IEntityMapping user_mapping;

	MatrixFactorization recommender1;
	ItemKNN recommender2;
	ItemAttributeKNN recommender3;

	static final Object _locker = new Object();

	// class' constructor
	public LinearModel(String featureFileName, String trainingFileName)
			throws IOException {

		// create internal data structures
		item_mapping = new EntityMapping();
		user_mapping = new EntityMapping();
		IEntityMapping attribute_mapping = new EntityMapping();

		// load the data
		SparseBooleanMatrix attributeData = AttributeData.read(featureFileName,
				item_mapping, attribute_mapping);
		IRatings trainingData = StaticRatingData.read(trainingFileName,
				user_mapping, item_mapping, RatingType.DOUBLE, false);

		// IRatings trainingData = RatingData.read(trainingFileName,
		// user_mapping,
		// item_mapping, false);

		// get number of distinct users present in the training data
		this.numItems = item_mapping.internalIDs().size();
		this.numUsers = user_mapping.internalIDs().size();
		this.predictions = new ArrayList<Quartet<Double, Double, Double, Double>>();

		// set up recommenders
		this.recommender1 = new MatrixFactorization();
		this.recommender1.setRatings(trainingData);
		this.recommender1.train();

		this.recommender2 = new ItemKNNPearson();
		// this.recommender2. =
		// MyMediaLite.Correlation.RatingCorrelationType.Pearson;
		this.recommender2.setRatings(trainingData);
		this.recommender2.train();

		this.recommender3 = new ItemAttributeKNN();
		this.recommender3.setRatings(trainingData);
		this.recommender3.setItemAttributes(attributeData);
		// this.recommender3. = true;
		this.recommender3.train();
	}

	public Matrix deriveLinearModel() throws Exception {

		Matrix x = new Matrix(this.predictions.size(), 3);
		Matrix y = new Matrix(this.predictions.size(), 1);
		int rowNum = 0;
		for (Quartet<Double, Double, Double, Double> quartet : this.predictions) {

			for (int colNum = 0; colNum < 3; colNum++) {
				x.setValueAt(rowNum, colNum, (double) quartet.getValue(colNum));
			}
			y.setValueAt(rowNum, 0, (double) quartet.getValue3());
			rowNum++;
		}

		MultiLinear ml = new MultiLinear(x, y);
		return ml.calculate();

		// MultivariateSample predictedScores = new MultivariateSample(4);
		//
		// ///http://metanumerics.codeplex.com/wikipage?title=Multivariate%20Samples
		//
		// foreach (prediction in this.predictions)
		// predictedScores.Add(new List<Double>(new Double[]
		// {prediction.Item1, prediction.Item2, prediction.Item3,
		// prediction.Item4}));
		//
		// FitResult regression = predictedScores.LinearRegression(3);
		//
		// double[] results = new double[3];
		//
		// results[0] = regression.Parameter(0).Value;
		// results[1] = regression.Parameter(1).Value;
		// results[2] = regression.Parameter(2).Value;
		//
		// return results;
	}

	public void predictScores(int threadId, int NumThreads,
			int[] userRandomSample) {

		for (int index = threadId; index < userRandomSample.length; index += NumThreads) {
			int userId = userRandomSample[index];
			IntList allItems = this.recommender1.getRatings().allItems();

			for (int itemId : allItems) {

				Double rating = this.recommender1.getRatings().tryGet(userId,
						itemId);
				if (rating != null) {
					Double prediction1 = (Double) this.recommender1.predict(
							userId, itemId);
					Double prediction2 = (Double) this.recommender2.predict(
							userId, itemId);
					Double prediction3 = (Double) this.recommender3.predict(
							userId, itemId);

					synchronized (_locker) {
						this.predictions
								.add(new Quartet<Double, Double, Double, Double>(
										prediction1, prediction2, prediction3,
										rating));
					}
				}
			}
		}
	}

	public void recommendItems(int threadId, int NumThreads, 
			//String fileName,
	// int TOP_N
			Matrix modelParameters) throws Exception {
		// create a writer and open the file
		//String outFileName = fileName + "." + threadId;
		//BufferedWriter outFile = new BufferedWriter(new FileWriter(outFileName));
		File file = new File("../result_" + new Date().getTime() + ".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		Map<Integer, Double> finalPredictions = new TreeMap<Integer, Double>(
				new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
						return o2.compareTo(o1);
					}
				});

		List<Integer> candidateList = IntStream
				.rangeClosed(0, (this.numItems - 1)).boxed()
				.collect(Collectors.toList());

		for (int userId = threadId; userId < this.numUsers; userId += NumThreads) {
			StringBuilder content = new StringBuilder();
			
			List<Triplet<Integer, Double, Double>> userItems = new ArrayList<>();
			// store prediction in the shared memory
			for (int itemId : candidateList) {

				double prediction1 = (double) this.recommender1.predict(userId,
						itemId);
				double prediction2 = (double) this.recommender2.predict(userId,
						itemId);
				double prediction3 = (double) this.recommender3.predict(userId,
						itemId);

				double combinedScore = modelParameters.getValueAt(1, 0)
						+ prediction1 * modelParameters.getValueAt(1, 0)
						+ prediction2 * modelParameters.getValueAt(2, 0)
						+ prediction3 * modelParameters.getValueAt(3, 0);
				finalPredictions.put(itemId, combinedScore);

				try {
					// Handle not found rate for a given user.
					Triplet<Integer, Double, Double> triplet = new Triplet<>(
							itemId, combinedScore, this.recommender1
									.getRatings().get(userId, itemId));
					userItems.add(triplet);

				} catch (InvalidKeyException ike) {
					continue;
				}
			}
			
			double[] precisionRecall = this.calcPrecisionRecall(userItems);
			content.append(user_mapping.toOriginalID(userId) + ";"
					+ precisionRecall[0] + ";" + precisionRecall[1]
					+ System.lineSeparator());


			finalPredictions.clear();
			bw.write(content.toString());
			bw.flush();
		}
		// close outfile
		bw.close();
		fw.close();
	}

	public int[] selectRandomUser(float samplePercentage) {

		// If the input value is invalid, set samplePercentage to the default
		// value of 1/3 of the input set
		if ((samplePercentage < 0.0) || (samplePercentage > 1.0))
			samplePercentage = (float) 0.333;

		int sampleSize = (int) (((float) this.numUsers) * samplePercentage);

		int[] resultingSample = new int[sampleSize];
		int[] dirtyFlag = new int[this.numUsers];

		for (int index = 0; index < this.numUsers; index++)
			dirtyFlag[index] = 0;

		for (int index = 0; index < sampleSize; index++) {
			// Random randomNumber = new Random(123);
			Random randomNumber = new Random();
			int selectedId = randomNumber.nextInt(this.numUsers);

			while (dirtyFlag[selectedId] == 1) {
				selectedId++;
				if (selectedId == this.numUsers)
					selectedId = 0;
			}

			dirtyFlag[selectedId] = 1;
			resultingSample[index] = selectedId;
		}

		return resultingSample;
	}

	public int[] selectRandomUser() {
		final float samplePercentage = (float) 0.333;

		int sampleSize = (int) (((float) this.numUsers) * samplePercentage);

		int[] resultingSample = new int[sampleSize];
		int[] dirtyFlag = new int[this.numUsers];

		for (int index = 0; index < this.numUsers; index++)
			dirtyFlag[index] = 0;

		for (int index = 0; index < sampleSize; index++) {
			int selectedId = index;

			while (dirtyFlag[selectedId] == 1) {
				selectedId++;
				if (selectedId == this.numUsers)
					selectedId = 0;
			}

			dirtyFlag[selectedId] = 1;
			resultingSample[index] = selectedId;
		}

		return resultingSample;
	}

	/**
	 * @param itemList
	 *            (item id, predicted rate, real rate)
	 * @return precicion, recall
	 */
	public double[] calcPrecisionRecall(
			List<Triplet<Integer, Double, Double>> itemList) {

		double rightPrediction = 0;
		double wrongPrediction = 0;
		double uncomparable = 0;

		Set<Integer> alreadyAnalysed = new HashSet<>();

		for (Triplet<Integer, Double, Double> outer : itemList) {

			//Double entryPredictedRate = Math.round(outer.getValue1() * 10.0) / 10.0;
			 Double entryPredictedRate = outer.getValue1();
			Double entryRealRate = outer.getValue2();
			for (Triplet<Integer, Double, Double> inner : itemList) {

				if (outer.getValue0().equals(inner.getValue0())
						|| alreadyAnalysed.contains(inner.getValue0())) {
					continue;
				} else {

					//Double innerPredictedRate = Math
							//.round(inner.getValue1() * 10.0) / 10.0;
					Double innerPredictedRate = inner.getValue1();
					Double innerRealRate = inner.getValue2();

					int predictedResult = entryPredictedRate
							.compareTo(innerPredictedRate);

					int realResult = entryRealRate.compareTo(innerRealRate);

					if (predictedResult == 0) {
						uncomparable++;
					} else if (predictedResult == realResult) {
						rightPrediction++;
					} else {
						wrongPrediction++;
					}
				}
			}
			alreadyAnalysed.add(outer.getValue0());
		}

		double precision = 0.0;
		try {
			precision = rightPrediction / (rightPrediction + wrongPrediction);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		double recall = 0.0;
		try {
			recall = rightPrediction
					/ (rightPrediction + wrongPrediction + uncomparable);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		return new double[] { precision, recall };
	}
}