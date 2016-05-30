package MCDP.benchmark;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Statistics
{

	/**
	 * MBO Configuration
	 */
	private int n;
	private int k;
	private int x;
	private int m;

	/**
	 * Other parameter of MBO
	 */
	private int iterationLimit;
	private double iExcel;
	private int leaderExchangeMode;
	
	/**
	 * MCDP data
	 */
	private int M;
	private int P;
	private int C;
	private int Mmax;
	private int Sum;
	
	private int[][] A;
	private int[][] Y;
	private int[][] Z;
	
	private int localZ;
	private int globalZ;
	
	/**
	 * Benchmark data
	 */
	private Hashtable<Integer,Integer> cycleFitnessHashTable;
	private int cycleThatTheBestPerformanceWasFound;
	private int bestFitness;
	private long startOfBenchmark;
	private long endOfBenchmark;
	private long measuringElapsedTime;
	
	private ArrayList<Integer> executionValues;
	
	/**
	 * 
	 */
	private String fileName;
	private String directory;
	
	/**
	 * Other parameters
	 */
	Logger log;
	
	/**
	 * Excel
	 */
	File fileXLS;
	Workbook book;
	FileOutputStream fileOutputStream;
	
	public Statistics()
	{
		// Inicializate log.
		log = Logger.getLogger(Statistics.class);

		// Other Values
		executionValues = new ArrayList<Integer>();
		
	}
	

	
	public Statistics(	Hashtable<String,Integer> hashTable,
						Hashtable<Integer,Integer> cycleFitnessHashTable,
						int[][] Y, int[][] Z, long start, long end)
	{
		
		n = hashTable.get("n");
		k = hashTable.get("k");
		x = hashTable.get("x");
		m = hashTable.get("m");
		
		iterationLimit = hashTable.get("iterationLimit");
		iExcel = hashTable.get("iExcel");
		leaderExchangeMode = hashTable.get("leaderExchangeMode");
		
		M = hashTable.get("Machines");
		P = hashTable.get("Parts");
		C = hashTable.get("Cells");
		Mmax = hashTable.get("Mmax");
		Sum = hashTable.get("Sum");
		localZ = hashTable.get("z");
		this.Y = Y;
		this.Z = Z;
		// crear nueva matriz A[][]
		
		this.cycleFitnessHashTable = cycleFitnessHashTable;
		cycleThatTheBestPerformanceWasFound = hashTable.get("cycleThatTheBestPerformanceWasFound");
		bestFitness = hashTable.get("tempBestFitness");
		startOfBenchmark = start;
		endOfBenchmark = end;
		measuringElapsedTime = end - start;
		
		// Inicializate log.
		log = Logger.getLogger(Statistics.class);

	}
	
	public void setStatistics(int i, Hashtable<String,Integer> hashTable,
			Hashtable<Integer,Integer> cycleFitnessHashTable,
			int[][] Y, int[][] Z, long start, long end)
	{

		n = hashTable.get("n");
		k = hashTable.get("k");
		x = hashTable.get("x");
		m = hashTable.get("m");

		iterationLimit = hashTable.get("iterationLimit");
		iExcel = hashTable.get("iExcel");
		leaderExchangeMode = hashTable.get("leaderExchangeMode");

		M = hashTable.get("Machines");
		P = hashTable.get("Parts");
		C = hashTable.get("Cells");
		Mmax = hashTable.get("Mmax");
		Sum = hashTable.get("Sum");
		localZ = hashTable.get("z");
		this.Y = Y;
		this.Z = Z;
		// crear nueva matriz A[][]

		this.cycleFitnessHashTable = cycleFitnessHashTable;
		cycleThatTheBestPerformanceWasFound = hashTable.get("cycleThatTheBestPerformanceWasFound");
		bestFitness = hashTable.get("tempBestFitness");
		startOfBenchmark = start;
		endOfBenchmark = end;
		measuringElapsedTime = end - start;
		
		// Save historical fitness
		executionValues.add(i, bestFitness);

		// Inicializate log.
		log = Logger.getLogger(Statistics.class);

	}
	
	public void createExcelFile() throws IOException
	{	
		// Primeramente definimos un objeto de tipo String para almacenar
		// la ruta del archivo que vamos a crear de la siguiente manera:
		
		// Una vez hecho esto creamos un objeto de tipo File para que contenga el archivo...
		fileXLS = new File(directory +"/"+ getFileName() + ".xls");
		
		// ...verificamos si existe dentro del sistema y de ser así lo eliminamos para
		// crear una nueva copia de trabajo...
		if (fileXLS.exists())
		{
			fileXLS.delete();
		}
		
		fileXLS.createNewFile();
		log.info("Create New File: " + directory +"/"+ getFileName() + ".xls");
		
		// Ya con el archivo físico creado y disponible comenzamos con el trabajo específico de Excel,
		// para ello creamos el libro con el objeto Workbook.
		book = new HSSFWorkbook();
	}
	
	public void openExcelFile() throws FileNotFoundException
	{
		// Posteriormente inicializamos el flujo de datos con el archivo que creamos previamente...
		fileOutputStream = new FileOutputStream(fileXLS);
		log.info("Open Excel File");
	}
	
	public void writeExcelFile()
	{
		

	}
	
	public void closeExcelFile() throws IOException
	{
		// ...y cerramos el flujo de datos...
		fileOutputStream.close();
		log.info("Close Excel File");
	}



	public void saveSolutions(String sheetName)
	{
		// ...y creamos la hoja dentro del libro dándole el nombre de "Mi hoja de trabajo 1"...
		Sheet sheet = book.createSheet(sheetName);
		
		/**
		 * MBO Configuration
		 */
		// Escribir en las celdas del libro.
		Row row_n = sheet.createRow(0);					
		Cell cell_n = row_n.createCell(0);
		cell_n.setCellValue("n");					
		Cell cellData_n = row_n.createCell(1);
		cellData_n.setCellValue(n);
				
		Row row_k = sheet.createRow(1);		
		Cell cell_k = row_k.createCell(0);
		cell_k.setCellValue("k");					
		Cell cellData_k = row_k.createCell(1);
		cellData_k.setCellValue(k);
				
		Row row_x = sheet.createRow(2);		
		Cell cell_x = row_x.createCell(0);
		cell_x.setCellValue("x");					
		Cell cellData_x = row_x.createCell(1);
		cellData_x.setCellValue(x);
				
		Row row_m = sheet.createRow(3);		
		Cell cell_m = row_m.createCell(0);
		cell_m.setCellValue("m");				
		Cell cellData_m = row_m.createCell(1);
		cellData_m.setCellValue(m);
		
		
		/**
		 * Other parameter of MBO
		 */
		Row row_il = sheet.createRow(4);		
		Cell cell_il = row_il.createCell(0);
		cell_il.setCellValue("Iteration Limit");					
		Cell cellData_il = row_il.createCell(1);
		cellData_il.setCellValue(iterationLimit);
		
		Row row_lem = sheet.createRow(5);		
		Cell cell_lem = row_lem.createCell(0);
		cell_lem.setCellValue("leaderExchangeMode");			
		Cell cellData_lem = row_lem.createCell(1);
		cellData_lem.setCellValue(leaderExchangeMode);
		
		
		/**
		 * Benchmark data
		 */
		Row row_cycleThatTheBestPerformanceWasFound = sheet.createRow(6);		
		Cell cell_cycleThatTheBestPerformanceWasFound = row_cycleThatTheBestPerformanceWasFound.createCell(0);
		cell_cycleThatTheBestPerformanceWasFound.setCellValue("cycleThatTheBestPerformanceWasFound");				
		Cell cellData_cycleThatTheBestPerformanceWasFound = row_cycleThatTheBestPerformanceWasFound.createCell(1);
		cellData_cycleThatTheBestPerformanceWasFound.setCellValue(cycleThatTheBestPerformanceWasFound);
		
		Row row_bestFitness = sheet.createRow(7);		
		Cell cell_bestFitness = row_bestFitness.createCell(0);
		cell_bestFitness.setCellValue("bestFitness");			
		Cell cellData_bestFitness = row_bestFitness.createCell(1);
		cellData_bestFitness.setCellValue(bestFitness);
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);    
		
		Row row_startOfBenchmark = sheet.createRow(8);		
		Cell cell_startOfBenchmark = row_startOfBenchmark.createCell(0);
		cell_startOfBenchmark.setCellValue("startOfBenchmark");			
		Cell cellData_startOfBenchmark = row_startOfBenchmark.createCell(1);
		Date resultdate = new Date(startOfBenchmark);
		cellData_startOfBenchmark.setCellValue(sdf.format(resultdate));

		Row row_endOfBenchmark = sheet.createRow(9);		
		Cell cell_endOfBenchmark = row_endOfBenchmark.createCell(0);
		cell_endOfBenchmark.setCellValue("endOfBenchmark");			
		Cell cellData_endOfBenchmark = row_endOfBenchmark.createCell(1);
		Date resultdateEnd = new Date(endOfBenchmark);
		cellData_endOfBenchmark.setCellValue(sdf.format(resultdateEnd));
		
		Row row_measuringElapsedTime = sheet.createRow(10);		
		Cell cell_measuringElapsedTime = row_measuringElapsedTime.createCell(0);
		cell_measuringElapsedTime.setCellValue("measuringElapsedTime");			
		Cell cellData_measuringElapsedTime = row_measuringElapsedTime.createCell(1);
		cellData_measuringElapsedTime.setCellValue(measuringElapsedTime);

		/**
		 * MCDP data
		 */
		Row row_M = sheet.createRow(12);		
		Cell cell_M = row_M.createCell(0);
		cell_M.setCellValue("M");			
		Cell cellData_M = row_M.createCell(1);
		cellData_M.setCellValue(M);
		
		Row row_P = sheet.createRow(13);		
		Cell cell_P = row_P.createCell(0);
		cell_P.setCellValue("P");			
		Cell cellData_P = row_P.createCell(1);
		cellData_P.setCellValue(P);
		
		Row row_C = sheet.createRow(14);		
		Cell cell_C = row_C.createCell(0);
		cell_C.setCellValue("C");			
		Cell cellData_C = row_C.createCell(1);
		cellData_C.setCellValue(C);
		
		Row row_Mmax = sheet.createRow(15);		
		Cell cell_Mmax = row_Mmax.createCell(0);
		cell_Mmax.setCellValue("Mmax");			
		Cell cellData_Mmax = row_Mmax.createCell(1);
		cellData_Mmax.setCellValue(Mmax);

		Row row_Sum = sheet.createRow(16);		
		Cell cell_Sum = row_Sum.createCell(0);
		cell_Sum.setCellValue("Sum");			
		Cell cellData_Sum = row_Sum.createCell(1);
		cellData_Sum.setCellValue(Sum);

		Row row_localZ = sheet.createRow(17);		
		Cell cell_localZ = row_localZ.createCell(0);
		cell_localZ.setCellValue("localZ");			
		Cell cellData_localZ = row_localZ.createCell(1);
		cellData_localZ.setCellValue(localZ);
		
		Row row_textY = sheet.createRow(19);		
		Cell cell_textY = row_textY.createCell(0);
		cell_textY.setCellValue("Y");
		for (int i = 0; i < M; i++)
		{
			Row row_Y = sheet.createRow(i + 20);
			for (int k = 0; k < C; k++)
			{
				Cell cell_Y = row_Y.createCell(k);
				cell_Y.setCellValue(Y[i][k]);		
			}
		}
		
		Row row_textZ = sheet.createRow(M + 25);		
		Cell cell_textZ = row_textZ.createCell(0);
		cell_textZ.setCellValue("Z");
		for (int j = 0; j < this.P; j++)
		{
			Row row_Z = sheet.createRow(j + M + 25 + 1);
			for (int k = 0; k < C; k++)
			{
				Cell cell_Z = row_Z.createCell(k);
				cell_Z.setCellValue(Z[j][k]);		
			}
		}
		
		


	}

	
	public void saveExecutions(String sheetName) throws IOException
	{	
		// ...y creamos la hoja dentro del libro dándole el nombre de "Mi hoja de trabajo 1"...
		Sheet sheet = book.createSheet(sheetName);
		
		// Load data.
		int fitness = 0;
		for (int i = 0; i < iExcel; i++)
		{
			fitness = cycleFitnessHashTable.get(i+1);
			
			// Escribir en las celdas del libro.
			Row row = sheet.createRow(i);
			
			Cell cell = row.createCell(0);
			cell.setCellValue(i);
			
			Cell cellData = row.createCell(1);
			cellData.setCellValue(fitness);
			
		}
		
		// Al finalizar ambos ciclos escribimos el archivo...
		book.write(fileOutputStream);
		log.info("Write Excel File");

	}
	
	public void saveResume(String sheetName) throws IOException
	{	
		log.info("Save Resume");
		// ...y creamos la hoja dentro del libro dándole el nombre de "Mi hoja de trabajo 1"...
		Sheet sheet = book.createSheet(sheetName);
		book.setSheetOrder(sheetName, 0);
		
		// Escribir en las celdas del libro.
		Row rowTitle = sheet.createRow(0);					
		Cell cellTitle = rowTitle.createCell(0);
		cellTitle.setCellValue("Best fitness");
		Cell cellFitness = rowTitle.createCell(1);
		cellFitness.setCellValue(Collections.min(executionValues));
		
		Row rowOtherTitle = sheet.createRow(1);
		Cell cellOtherTitle = rowOtherTitle.createCell(0);
		cellOtherTitle.setCellValue("Best fitness for each execution");
		
		// Load data.
		for (int i = 0; i < executionValues.size(); i++)
		{			
			// Escribir en las celdas del libro.
			Row row = sheet.createRow(i+2);
			
			Cell cell = row.createCell(0);
			cell.setCellValue(i);
			
			Cell cellData = row.createCell(1);
			cellData.setCellValue(executionValues.get(i));
			
		}
		
		// Al finalizar ambos ciclos escribimos el archivo...
		book.write(fileOutputStream);
		log.info("Write Excel File");

	}
	
	public void saveLittleResume(String sheetName, int row, String benchmarkName, int bestFitness,
								double mean, double median, double variance, double standardDeviation) throws IOException
	{	
		log.info("Save Little Resume");

		Sheet sheet;
		if (row == 0)
		{
			sheet = book.createSheet(sheetName);
			
			Row rowTitle = sheet.createRow(0);
			
			Cell cellTBenchmarkName = rowTitle.createCell(0);
			cellTBenchmarkName.setCellValue("benchmarkName");
			
			Cell cellTFitness = rowTitle.createCell(1);
			cellTFitness.setCellValue("bestFitness");
			
			Cell cellTMean = rowTitle.createCell(2);
			cellTMean.setCellValue("mean");
			
			Cell cellTMedian = rowTitle.createCell(3);
			cellTMedian.setCellValue("median");
			
			Cell cellTVariance = rowTitle.createCell(4);
			cellTVariance.setCellValue("variance");
			
			Cell cellTStandardDeviation = rowTitle.createCell(5);
			cellTStandardDeviation.setCellValue("standardDeviation");
			
		}
		else
		{
			sheet = book.getSheet("Little_Resume");
		}
			
			// Escribir en las celdas del libro.
			Row rowData = sheet.createRow(row + 1);
			
			Cell cellBenchmarkName = rowData.createCell(0);
			cellBenchmarkName.setCellValue(benchmarkName);
			
			Cell cellFitness = rowData.createCell(1);
			cellFitness.setCellValue(bestFitness);
			
			Cell cellMean = rowData.createCell(2);
			cellMean.setCellValue(mean);
			
			Cell cellMedian = rowData.createCell(3);
			cellMedian.setCellValue(median);
			
			Cell cellVariance = rowData.createCell(4);
			cellVariance.setCellValue(variance);
			
			Cell cellStandardDeviation = rowData.createCell(5);
			cellStandardDeviation.setCellValue(standardDeviation);
			
			// Al finalizar ambos ciclos escribimos el archivo...
			
			book.write(fileOutputStream);
			log.info("Write Excel File");

	}
	
	public int getBestFitness() throws IOException
	{	
		log.info("Get Best Fitnnes");		
		return Collections.min(executionValues);
	}
	
	public double getMean()
	{	
		log.info("Get Mean");
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int i = 0; i < executionValues.size(); i++)
		{
			stats.addValue(executionValues.get(i));
		}
		
		return stats.getMean();
	}
	
	public double getMedian()
	{	
		log.info("Get Median");
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int i = 0; i < executionValues.size(); i++)
		{
			stats.addValue(executionValues.get(i));
		}
		
		return stats.getPercentile(50);
	}

	
	public double getVariance()
	{
		log.info("Get Standard Deviation");
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int i = 0; i < executionValues.size(); i++)
		{
			stats.addValue(executionValues.get(i));
		}
		
		return stats.getVariance();
	}
	
	public double getStandardDeviation()
	{
		log.info("Get Standard Deviation");
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int i = 0; i < executionValues.size(); i++)
		{
			stats.addValue(executionValues.get(i));
		}
		
		return stats.getStandardDeviation();
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getDirectory()
	{
		return directory;
	}
	
	private void createFullDirectory()
	{
		File directory = new File(getDirectory());
        log.info(getDirectory());
        boolean b = directory.mkdirs();
        
        if (b)
        {
        	System.out.printf("Successfully created new directory: %s%n", directory);
        }
        else
        {
        	System.out.printf("Failed to create new directory: %s%n", directory);
        }
	}
	
	private void createFile()
	{
		File file = new File(fileName +".xls");
        
        boolean b = file.mkdirs();
        
        if (b)
        {
        	System.out.printf("Successfully created new file: %s%n", file);
        }
        else
        {
        	System.out.printf("Failed to create new file: %s%n", file);
        }
	}

	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public void setDirectory(String directory)
	{
		this.directory = directory;
	}



}
