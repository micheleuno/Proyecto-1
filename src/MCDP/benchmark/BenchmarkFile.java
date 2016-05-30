package MCDP.benchmark;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import MCDP.model.ModelIntMCDP;
import MCDP.util.Convert;

/**	
#-----FILE-DETAILS------------------------------------
File details=value
Version=1
Created by=
Date created=
Modified by=
Date modified=
#-----BECHMARK-INFO-----------------------------------
Benchmark info=
References=
Best Solution=
#-----MCDP-VALUES-------------------------------------
Machines=5
Parts=7
Cells=2
Mmax=4
Mmin=0
Sum=1
Var_1=value
Var_2=value
Var_3=value
Matrix=\
1 0 0 0 1 1 1 \
0 1 1 1 1 0 0 \
0 0 1 1 1 1 0 \
1 1 1 1 0 0 0 \
0 1 0 1 1 1 0
*/


public class BenchmarkFile
{
	private ArrayList<String> fileNameSet;
	
	public BenchmarkFile()
	{
		fileNameSet = new ArrayList<String>();
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
			
			ModelIntMCDP model = new ModelIntMCDP(M, P, C, Mmax, Sum, A);
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
				System.out.println(str);
				fileNameSet.add(str + ".txt");
			}
			br.close();	
		}
		catch (IOException io)
		{
			throw new Exception(io);
		} 
		catch (Exception e)
		{
			throw e;
		}
		
		return fileNameSet;
	}

}
