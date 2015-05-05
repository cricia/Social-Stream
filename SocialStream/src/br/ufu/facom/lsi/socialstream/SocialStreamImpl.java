package br.ufu.facom.lsi.socialstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import br.ufu.facom.lsi.prefrec.mlr.matrix.Matrix;
import br.ufu.facom.lsi.prefrec.mlr.regression.LinearModel;
import br.ufu.facom.lsi.prefrec.model.User;

public class SocialStreamImpl {

	static final String INPUT_DIR = "/Users/cricia/Documents/workspace-sts-3.4.0.RELEASE/SocialStream/input/";

	static List<Map<Long, List<User>>> clustersList;
	static List<Map<Long, Double[]>> centroidsList;
	static TreeMap<Integer, Integer> itemMap;

	public static void main(String[] args) throws Exception {

		// Verify number of parameters
		// if (args.length != 6) {
		// System.out.println("\n\t\t***ERROR: wrong number of parameters!\n");
		// usage();
		// System.exit(-1);
		// }

		// Get input parameters
		String FEATURE_FILE = args[0];
		float SAMPLE_PERCENTAGE = Float.valueOf(args[1]);
		int NUM_THREADS = Integer.valueOf(args[2]);

		// Open input files: List of clusters
		final Set<Integer> NUM_CLUSTERS = fetchNumClusters();
		for (int h : NUM_CLUSTERS) {

			loadFromInputFolder(h);

			// execute on files
			for (Map<Long, Double[]> centroids : centroidsList) {

				String TRAIN_FILE = centroidToTrainFile(centroids).getName();

				// create class' object
				LinearModel streamModel = new LinearModel(FEATURE_FILE,
						TRAIN_FILE);

				// select random users
				int[] userRandomSample = streamModel
						.selectRandomUser(SAMPLE_PERCENTAGE);

				// int[] userRandomSample = streamModel
				// .selectRandomUser();

				// Generate predictions for subset of known ratings
				Thread[] threadsArray = new Thread[NUM_THREADS];
				for (int index = 0; index < NUM_THREADS; index++) {
					int startIndex = index;
					threadsArray[index] = new Thread(new Runnable() {

						@Override
						public void run() {
							streamModel.predictScores(startIndex, NUM_THREADS,
									userRandomSample);
						}
					});
					threadsArray[index].start();
				}

				// sincroniza a execucao de todas as threads
				for (int index = 0; index < NUM_THREADS; index++) {
					threadsArray[index].join();
				}

				// Combine predictions using Multivariate Linear Regression
				Matrix modelParameters = streamModel.deriveLinearModel();

				// Generate users' recommendations
				for (int index = 0; index < NUM_THREADS; index++) {
					int startIndex = index;
					threadsArray[index] = new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								streamModel.recommendItems(startIndex,
										NUM_THREADS, /* OUT_FILE, TOP_N, */
										modelParameters);
							} catch (Exception e) {
								e.printStackTrace();
								System.exit(1);
							}
						}
					});
					threadsArray[index].start();
				}

				// sincroniza a execucao de todas as threads
				for (int index = 0; index < NUM_THREADS; index++) {
					threadsArray[index].join();
				}
			}
		}
	}

	private static File centroidToTrainFile(Map<Long, Double[]> centroids)
			throws Exception {

		if (Files.exists(Paths.get("myFile.txt"))) {
			Files.delete(Paths.get("myFile.txt"));
		}

		Path result = Files.createTempFile("myFile", ".txt");
		for (Long clusterId : centroids.keySet()) {

			StringBuilder sb = new StringBuilder();

			Double[] points = centroids.get(clusterId);
			for (int i = 0; i < points.length; i++) {
				if (!points[i].equals(-1.0)) {
					sb.append(clusterId).append(",").append(itemMap.get(i))
							.append(",").append(points[i])
							.append(System.lineSeparator());
				}
			}
			Files.write(result, sb.toString().getBytes());
		}
		return result.toFile();
	}

	private static Set<Integer> fetchNumClusters() throws Exception {

		Set<Integer> numClusters = new HashSet<>();
		Path dir = Paths.get(INPUT_DIR);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path entry : stream) {
				try {
					String fileName = entry.toFile().getName();
					Integer id = Integer.parseInt(fileName.substring(0,
							fileName.indexOf('_')));
					numClusters.add(id);
				} catch (Exception e) {
					continue;
				}
			}
		}
		return numClusters;
	}

	@SuppressWarnings("unchecked")
	private static void loadFromInputFolder(Integer firstFileLetter)
			throws Exception {

		clustersList = new ArrayList<>();
		centroidsList = new ArrayList<>();
		itemMap = new TreeMap<>();
		
		Path dir = Paths
				.get("/Users/cricia/Documents/workspace-sts-3.4.0.RELEASE/SocialStream/input/");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path entry : stream) {
				String fileName = entry.toFile().getName();
				if (fileName.startsWith(firstFileLetter.toString())) {

					if (fileName.endsWith("cluster")) {
						FileInputStream fin = new FileInputStream(
								entry.toFile());
						ObjectInputStream ois = new ObjectInputStream(fin);
						Map<Long, List<User>> cluster = (HashMap<Long, List<User>>) ois
								.readObject();
						ois.close();
						clustersList.add(cluster);
					} else if (fileName.endsWith("centroid")) {

						FileInputStream fin = new FileInputStream(
								entry.toFile());
						ObjectInputStream ois = new ObjectInputStream(fin);
						Map<Long, Double[]> centroids = (HashMap<Long, Double[]>) ois
								.readObject();
						ois.close();
						centroidsList.add(centroids);

					} else {

						FileInputStream fin = new FileInputStream(
								entry.toFile());
						ObjectInputStream ois = new ObjectInputStream(fin);
						itemMap = (TreeMap<Integer, Integer>) ois.readObject();
						ois.close();
					}
				}
			}
		}
	}

	static void usage() {

		System.out
				.println("\nIn order to run this script you should give 6 input parameters:\n");

		System.out.println("\t Training data file name");
		System.out.println("\t Feature file name");
		System.out.println("\t Output file name");
		System.out.println("\t Percentage fo sampling in the training data");
		System.out.println("\t Number of recommendations");
		System.out.println("\t Number of threads\n");

	}
}
