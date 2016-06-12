import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import FP.FlowerPollination;
import MCDP.benchmark.Benchmark;
import MCDP.model.MCDPData;
import MCDP.model.MCDPModel;

public class main {

	public static void main(String[] args) throws Exception
	{
		// Crear parametros iniciales de la metaheuristica
		int numberPoblation = 40;
		int numberIteration = 100;
		float delta= 1.5f;
		float switch_probability=0.1f;
		
		Logger log = Logger.getLogger(main.class);
		
		log.info("Read all filenames");
		Benchmark benchmark = new Benchmark();
		ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("/resources/MBO_MCDP_BENCHMARK_FILES--boctor.txt");
		
		ArrayList<MCDPData> modelSet = benchmark.getSetModelsByNames(dataFiles);	
		System.out.println("Read all filenames");
		Iterator<MCDPData> iterator = modelSet.iterator();
		
		while (iterator.hasNext()){
			MCDPData model = iterator.next();
			System.out.println("Problema [" + model.getIdentificator()+"] Maquinas ["+model.M+"] Celdas ["+model.C+"] Piezas ["+model.P+"] Max Maquinas ["+model.mmax+"]");
			for(int i=0;i<model.getA().length;i++){
				System.out.println(Arrays.toString(model.getA()[i]));
			}
		}
		
		
		
		// Crear parametros del benchmark del modelo de boctor
		/*MCDPData data = new MCDPData();
		
		// Crear la metaheuristica
		FlowerPollination metaheuristic = new FlowerPollination(numberPoblation, numberIteration, data,delta ,switch_probability);
		
		// Ejecutar la metaheuristica
		metaheuristic.run();
		
		// imprimir estadisticas
		metaheuristic.toConsoleFinalReport();*/
	}

}
