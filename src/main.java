import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;

import FP.FlowerPollination;
import MCDP.benchmark.Benchmark;
import MCDP.benchmark.Statistics;
import MCDP.model.MCDPData;
import MCDP.model.Solution;

public class Main {

	public static void main(String[] args) throws Exception {
		// Crear parametros iniciales de la metaheuristica
		int numberPoblation = 160;
		int numberIteration = 100;
		float delta = 0.1f;
		float switch_probability = 0.1f;
		int executions = 31;
		int best_fitness = 999999999;
		float mean_fitness = 0f;
		int optimal_global = 0;
		int numIteracion = 0;
		int iterationOpt = 0;
		float iterationOptAvg = 0f;
		String ParamAutonomous;
		String parametros[];
		File f = new File("Parametros.txt");
		BufferedReader  br= null;
		if(f.exists() && !f.isDirectory()) { 
			 br = new BufferedReader(new InputStreamReader( new FileInputStream(f)));
			parametros = (br.readLine().split("="));
			numberIteration=Integer.parseInt(parametros[1]);
			parametros = (br.readLine().split("="));
			numberPoblation=Integer.parseInt(parametros[1]);
			parametros = (br.readLine().split("="));
			delta=Float.parseFloat(parametros[1]);
			parametros = (br.readLine().split("="));
			switch_probability=Float.parseFloat(parametros[1]);
			parametros = (br.readLine().split("="));
			executions=Integer.parseInt(parametros[1]);
			br.readLine();
			System.out.println("asdasdasdasd "+numberIteration);
		}
		
			
		
		ParamAutonomous=br.readLine();//Lectura inicial parametros AS
		while(ParamAutonomous!=null){ //While que lo controla la cantidad de pruebas
		Logger log = Logger.getLogger(Main.class);

		log.info("Read all filenames");
		Benchmark benchmark = new Benchmark();
		ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("/resources/MCDP_BENCHMARK_FILES.txt");
		
		ArrayList<MCDPData> modelSet = benchmark.getSetModelsByNames(dataFiles);
		System.out.println("Read all filenames");
		Iterator<MCDPData> iterator = modelSet.iterator();

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String benchmarkFileConfig = "n_" + numberPoblation + "_d_" + delta + "_SWp_" + switch_probability + "_in_"
				+ numberIteration + "";

		String currentDirectory = timeStamp + "_" + benchmarkFileConfig;
		String folder = "Results";
		String directory = folder + "/" + currentDirectory;

		File directoryFile = new File(folder);
		boolean b = directoryFile.mkdir();
		if (b) {
			System.out.printf("Successfully created new folder: %s%n", folder);
		} else {
			System.out.printf("Failed to create new folder: ", folder);
			if (directoryFile.exists()) {
				System.out.printf("folder " + folder + " already exists\n");
			}
		}

		directoryFile = new File(directory);
		b = directoryFile.mkdir();
		if (b) {
			System.out.printf("Successfully created new directory: %s%n\n", directory);
		} else {
			System.out.printf("Failed to create new directory: %s%n\n", directory);
		}
		
		while (iterator.hasNext()) {

			MCDPData model = iterator.next();

			optimal_global = obtenerOptimo("src/resources/" + model.getIdentificator());
			model.setBestSGlobal(optimal_global);

			long startBenchmark = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSS", Locale.ENGLISH);
			Date resultdate = new Date(startBenchmark);
			System.out.println("Process Start, with " + executions + " executions: " + sdf.format(resultdate));

			for (int i = 0; i < executions; i++) {

				FlowerPollination metaheuristic = new FlowerPollination(numberPoblation, numberIteration, model, delta,
						switch_probability, directory);

				iterationOpt = metaheuristic.run(ParamAutonomous);
				iterationOptAvg = iterationOpt + iterationOptAvg;
				Solution bestSolution = metaheuristic.getBestSolution();
				mean_fitness = mean_fitness + bestSolution.getFitness();
				if (best_fitness > bestSolution.getFitness()) {
					best_fitness = bestSolution.getFitness();
				}

			}
			mean_fitness = mean_fitness / executions;
			iterationOptAvg = iterationOptAvg / executions;
			System.out.println("Mean Best Fitness:[" + mean_fitness + "] " + "Best Solution: [" + best_fitness + "]");
			Statistics.createTable(numIteracion, model.mmax, model.getBestSGlobal(), best_fitness, mean_fitness,
					model.getIdentificator(), currentDirectory,model.getC(),iterationOptAvg);
			mean_fitness = 0;
			best_fitness = 999999999;
			numIteracion++;

			System.out.println("Problem [" + model.getIdentificator() + "]");

			System.out.println("Best solution global " + optimal_global);

			long endBenchmark = System.currentTimeMillis();
			Date resultdate2 = new Date(endBenchmark);
			System.out.println("Process End: " + sdf.format(resultdate2));
			System.out.println("==========================================================");
		}
		ParamAutonomous=br.readLine();
		}
		br.close();
		Toolkit.getDefaultToolkit().beep();;
	}

	public static int obtenerOptimo(String directorio) throws IOException {
		String dato[];
		String line32 = Files.readAllLines(Paths.get(directorio)).get(21);
		dato = line32.split("=");
		return Integer.parseInt(dato[1]);
	}

}
