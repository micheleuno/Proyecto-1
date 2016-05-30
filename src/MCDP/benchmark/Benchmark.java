package MCDP.benchmark;

import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;

import MCDP.model.ModelIntMCDP;
import MCDP.util.Convert;

public class Benchmark
{
	/**
	 * 
	 */
	private ArrayList<String> fileNameSet;
	
	/**
	 * Other parameters
	 */
	Logger log;
	
	public Benchmark()
	{
		fileNameSet = new ArrayList<String>();
		
		// Inicializate log.
		log = Logger.getLogger(Benchmark.class);
	}
	
	// leo a traves de un listado de nombres de archivo, los modelos con datos.
	public ArrayList<ModelIntMCDP> getSetModelsByNames(ArrayList<String> filenames) throws IOException
	{
		ArrayList<ModelIntMCDP> models = new ArrayList<ModelIntMCDP>();
		Iterator<String> nombreIterator = filenames.iterator();

		while (nombreIterator.hasNext())
		{
			String filename = nombreIterator.next();
			Hashtable<String,String> hashTable = new Hashtable<String,String>();

			PropertiesRead p = new PropertiesRead();
			hashTable = p.getPropEssentialValues(filename);

			Convert convert = new Convert();			

			// Get Values for MCDP Model.
			int M = Integer.parseInt(hashTable.get("Machines"), 10);
			int P = Integer.parseInt(hashTable.get("Parts"), 10);
			int C = Integer.parseInt(hashTable.get("Cells"), 10);
			int Mmax = Integer.parseInt(hashTable.get("Mmax"), 10);
			int Sum = Integer.parseInt(hashTable.get("Sum"), 10);
			String string = hashTable.get("Matrix").toString();
			int A[][] = convert.StringToMatrixInt(string, M, P);

			// Set values into model.
			ModelIntMCDP model = new ModelIntMCDP(M, P, C, Mmax, Sum, A);

			// Set identificator Model (KEY).
			int pos = filename.lastIndexOf(".");
			if (pos > 0)
			{
				filename = filename.substring(0, pos);
			}
			model.setIdentificator(filename);

			// Add model into model set.
			models.add(model);		
		}

		return models;
	}
	
	public ArrayList<String> readSetFileBenchmark(String file) throws Exception
	{
		try
		{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			
			while ((str = br.readLine()) != null)
			{
				log.info(str);
				fileNameSet.add(str);
			}
			br.close();	
		}
		catch (IOException io)
		{
			throw new Exception(io);
		} 
		catch (Exception e)
		{
			log.error("", e);
		}
		
		setFileNameSet(fileNameSet);
		
		return fileNameSet;
	}
	
	public ArrayList<String> getFileNameSet()
	{
		return fileNameSet;
	}

	public void setFileNameSet(ArrayList<String> fileNameSet)
	{
		this.fileNameSet = fileNameSet;
	}
}
