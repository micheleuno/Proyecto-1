import java.io.File;
import java.io.IOException;
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

public class main {

	public static void main(String[] args) throws Exception
	{
		// Crear parametros iniciales de la metaheuristica
		int numberPoblation = 80;
		int numberIteration = 100;
		float delta= 1.5f;
		float switch_probability=0.1f;
		int executions=1;
		int row;
		int best_fitness=999999999;
		float mean_fitness=0f;
		int optimal_global=0;
		
		Logger log = Logger.getLogger(main.class);
		
		log.info("Read all filenames");
		Benchmark benchmark = new Benchmark();
		ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("/resources/MBO_MCDP_BENCHMARK_FILES--boctor.txt");
		
		ArrayList<MCDPData> modelSet = benchmark.getSetModelsByNames(dataFiles);	
		System.out.println("Read all filenames");
		Iterator<MCDPData> iterator = modelSet.iterator();
		
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String benchmarkFileConfig = "n_"+numberPoblation+"_d_"+delta+"_SWp_"+switch_probability+"_in_"+numberIteration+"";
		
		String directory = "Test/" + timeStamp + "_" + benchmarkFileConfig;
		File directoryFile = new File(directory);
		boolean b = directoryFile.mkdir();
		if (b)
		{
			System.out.printf("Successfully created new directory: %s%n", directory);
		}
		else
		{
			System.out.printf("Failed to create new directory: %s%n", directory);
		}
		
		/*Statistics mainResume = new Statistics();
		mainResume.setDirectory(directory);
		mainResume.setFileName("Main_Resume");
		mainResume.createExcelFile();*/
		row = 0;
		while (iterator.hasNext()){
			
			MCDPData model = iterator.next();
			
			/*
			String fileName = model.getIdentificator();		
			Statistics statistics = new Statistics();
			statistics.setDirectory(directory);
			statistics.setFileName(fileName);
			statistics.createExcelFile();*/
			optimal_global=obtenerOptimo("src/resources/"+model.getIdentificator());
			model.setBestSGlobal(optimal_global);
			for (int i = 0; i < executions; i++){
			
			FlowerPollination metaheuristic = new FlowerPollination(numberPoblation, numberIteration, model,delta ,switch_probability);
			//Date object
			//Inicio
			/*long startBenchmark = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSS", Locale.ENGLISH);    
			Date resultdate = new Date(startBenchmark);
			System.out.println("Start: "+sdf.format(resultdate));*/
			
			metaheuristic.run();
			//Fin
			long endBenchmark = System.currentTimeMillis();
			Date resultdate2 = new Date(endBenchmark);
			//System.out.println("End: "+sdf.format(resultdate2));
			
			//metaheuristic.toConsoleFinalReport();
			Solution bestSolution= metaheuristic.getBestSolution();
			//System.out.println();
			//System.out.println("Mejor solucion ejecucion ("+(i+1)+"): "+bestSolution.getFitness());
			mean_fitness=mean_fitness+bestSolution.getFitness();
			if(best_fitness>bestSolution.getFitness()){
				best_fitness=bestSolution.getFitness();
			}
			
		//	statistics.openExcelFile();
			 
			// Save statistics
		/*	statistics.setStatistics(	i, metaheuristic.getStatisticsIntegerData(), metaheuristic.getCycleFitness(),
					metaheuristic.getSolutionY(), metaheuristic.getSolutionZ(),
					startBenchmark, endBenchmark);*/

			//statistics.saveSolutions("Solution_"+i);
			//statistics.saveExecutions("Execution_"+i);
			
			
			}
			mean_fitness = mean_fitness/executions;
			System.out.println("Promedio del mejor fitness:["+mean_fitness+"] "+"Mejor Solucion: ["+best_fitness+"]");
			mean_fitness=0;
			best_fitness=999999999;
			/*statistics.openExcelFile();
			statistics.saveResume("Resume");
			
			mainResume.openExcelFile();
			
			/*mainResume.saveLittleResume("Little_Resume", row, statistics.getFileName(), statistics.getBestFitness(),
					statistics.getMean(), statistics.getMedian(), statistics.getVariance(),
					statistics.getStandardDeviation());		*/
			
			
			
			/*mainResume.closeExcelFile();			
			row++;
			statistics.closeExcelFile();*/
			
			System.out.println("Problema [" + model.getIdentificator()+"]");

			System.out.println("Best solution global "+optimal_global);
			System.out.println("=============================");
			
		}
		
		
		
		// Crear parametros del benchmark del modelo de boctor
		//MCDPData data = new MCDPData();
		
		// Crear la metaheuristica
	
		
		// Ejecutar la metaheuristica
		//metaheuristic.run();
		
		// imprimir estadisticas
	//
	}
	public static int obtenerOptimo (String directorio) throws IOException{
		String dato[];
		String line32 = Files.readAllLines(Paths.get(directorio)).get(21);	
		dato= line32.split("=");
		return Integer.parseInt(dato[1]);
	}

}
