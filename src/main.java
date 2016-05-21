
public class main {

	public static void main(String[] args)
	{
		// Crear parametros iniciales de la metaheuristica
		int numberPoblation = 25;
		int numberIteration = 500;
		
		// Crear parametros del benchmark del modelo de boctor
		MCDPData data = new MCDPData();
		
		// Crear la metaheuristica
		Metaheuristic metaheuristic = new Metaheuristic(numberPoblation, numberIteration, data);
		
		// Ejecutar la metaheuristica
		metaheuristic.run();
		
		// imprimir estadisticas
		metaheuristic.toConsoleFinalReport();
	}

}
