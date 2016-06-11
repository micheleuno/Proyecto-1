import java.util.ArrayList;

import org.apache.log4j.Logger;

import FP.FlowerPollination;
import MCDP.benchmark.Benchmark;
import MCDP.benchmark.Main;
import MCDP.model.MCDPData;
import MCDP.model.MCDPModel;

public class main {

	public static void main(String[] args) throws Exception
	{
		// Crear parametros iniciales de la metaheuristica
		int numberPoblation = 5;
		int numberIteration = 500;
		
		/*Logger log = Logger.getLogger(main.class);
		
		log.info("Read all filenames");
		Benchmark benchmark = new Benchmark();
		ArrayList<String> dataFiles = benchmark.readSetFileBenchmark("resources/MBO_MCDP_BENCHMARK_FILES--boctor.txt");
		
		ArrayList<MCDPModel> modelSet = benchmark.getSetModelsByNames(dataFiles);	
		System.out.println("Read all filenames");
		*/
		
		
		
		
		// Crear parametros del benchmark del modelo de boctor
		MCDPData data = new MCDPData();
		
		// Crear la metaheuristica
		FlowerPollination metaheuristic = new FlowerPollination(numberPoblation, numberIteration, data, 1.5,0.8);
		
		// Ejecutar la metaheuristica
		metaheuristic.run();
		
		// imprimir estadisticas
		metaheuristic.toConsoleFinalReport();
	}

}
