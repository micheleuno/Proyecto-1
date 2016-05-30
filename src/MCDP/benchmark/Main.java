package MCDP.benchmark;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;

import MBO.kernel.MigratingBirdsOptimization;
import MCDP.model.ModelIntMCDP;


public class Main
{

	public Main()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception
	{
		// Create a Benchmark
		int n = 51;
		int k = 3;
		int m = 10;
		int x = 1;
		int iterationLimit = 1020;
		int execution = 30;
		
		
		// Create log.
		Logger log = Logger.getLogger(Main.class);
		
		
		// Read all filenames.
		log.info("Read all filenames");
		Benchmark benchmark = new Benchmark();
		ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("resources/MBO_MCDP_BENCHMARK_FILES--boctor.txt");
		//ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("MohammadMahdiPaydar/MBO_MCDP_BENCHMARK_FILES_MohammadMahdiPaydar.txt");
		
		// Read Models
		ArrayList<ModelIntMCDP> modelSet = benchmark.getSetModelsByNames(dataFiles);	
			
		// Load model.
		Iterator<ModelIntMCDP> iterator = modelSet.iterator();
		
		// Create file to save
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String benchmarkFileConfig = "n_"+n+"_k_"+k+"_m_"+m+"_x_"+x+"_il_"+iterationLimit+"";
		
		// En este directorio se guardara todas las pruebas
		String directory = "benchmark/Boctor/" + timeStamp + "_" + benchmarkFileConfig;
		//String directory = "benchmark/MohammadMahdiPaydar/" + timeStamp + "_" + benchmarkFileConfig;
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

		// Save statistics.
		// Main resume
		Statistics mainResume = new Statistics();
		mainResume.setDirectory(directory);
		mainResume.setFileName("Main_Resume");
		mainResume.createExcelFile();
		
		int row = 0;
		
		while (iterator.hasNext())
		{
			ModelIntMCDP model = iterator.next();
			log.info("Run " + model.getIdentificator() + " problem model.");
			
			String fileName = model.getIdentificator();			
			log.info("Filename: " + fileName);
			
			Statistics statistics = new Statistics();
			statistics.setDirectory(directory);
			statistics.setFileName(fileName);
			statistics.createExcelFile();
			
			for (int i = 0; i < execution; i++)
			{
				// Create Metaheuristics
				MigratingBirdsOptimization mbo = new MigratingBirdsOptimization(n, k, x, m, iterationLimit, model);
				
			    //Date object
				long startBenchmark = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSS", Locale.ENGLISH);    
				Date resultdate = new Date(startBenchmark);
				System.out.println("Start: "+sdf.format(resultdate));
				
				// Run metaheuristics.
				mbo.run();
				
				long endBenchmark = System.currentTimeMillis();
				Date resultdate2 = new Date(endBenchmark);
				System.out.println("End: "+sdf.format(resultdate2));


				statistics.openExcelFile();
 
				// Save statistics
				statistics.setStatistics(	i, mbo.getStatisticsIntegerData(), mbo.getCycleFitness(),
						mbo.getSolutionY(), mbo.getSolutionZ(),
						startBenchmark, endBenchmark);

				statistics.saveSolutions("Solution_"+i);
				statistics.saveExecutions("Execution_"+i);
			}
			statistics.openExcelFile();
			statistics.saveResume("Resume");
			
			mainResume.openExcelFile();
			
			mainResume.saveLittleResume("Little_Resume", row, statistics.getFileName(), statistics.getBestFitness(),
					statistics.getMean(), statistics.getMedian(), statistics.getVariance(),
					statistics.getStandardDeviation());
			
			/*
			mainResume.saveLittleResume("Little_Resume", row, statistics.getFileName(),
										statistics.getGroupingEfficacyBestFitness(),
										statistics.getGroupingEfficacyMean(),
										statistics.getGroupingEfficacyMedian(),
										statistics.getGroupingEfficacyVariance(),
										statistics.getGroupingEfficacyStandardDeviation(),
										statistics.getGroupingEfficiencyBestFitness(),
										statistics.getGroupingEfficiencyMean(),
										statistics.getGroupingEfficiencyMedian(),
										statistics.getGroupingEfficiencyVariance(),
										statistics.getGroupingEfficiencyStandardDeviation(),
										statistics.getGCIBestFitness(),
										statistics.getGCIMean(),
										statistics.getGCIMedian(),
										statistics.getGCIVariance(),
										statistics.getGCIStandardDeviation(),
										statistics.getE_VBestFitness(),
										statistics.getE_VMean(),
										statistics.getE_VMedian(),
										statistics.getE_VVariance(),
										statistics.getE_VStandardDeviation(),
										statistics.getVoidsBestFitness(),
										statistics.getVoidsMean(),
										statistics.getVoidsMedian(),
										statistics.getVoidsVariance(),
										statistics.getVoidsStandardDeviation(),
										statistics.getExceptionsBestFitness(),
										statistics.getExceptionsMean(),
										statistics.getExceptionsMedian(),
										statistics.getExceptionsVariance(),
										statistics.getExceptionsStandardDeviation());
										*/
			mainResume.closeExcelFile();
			
			row++;
			statistics.closeExcelFile();
		}
		
		//mainResume.closeExcelFile();		
	}

}
