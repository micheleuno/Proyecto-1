import java.util.ArrayList;

public class Metaheuristic
{
	// Parametros de la metaheuristica
	private int numberPoblation;
	private int numberIteration;
	
	private ArrayList<Solution> poblation;	// Un arreglo de soluciones
	private Solution bestSolution;			// Mejor Solución
	
	// Dataset (benchmark)
	private MCDPData data;
	
	// Estadisticas
	//veces que un random genera una solucion que cumple las restricciones
	private long numAcceptedMoves;
	private long numRejectedMoves;
	
	public Metaheuristic(int numberPoblation, int numberIteration, MCDPData data)
	{
		this.numberPoblation = numberPoblation;
		this.numberIteration = numberIteration;
		this.data = data;
		this.poblation = new ArrayList<Solution>();
		this.bestSolution = new Solution();
		this.numAcceptedMoves = 0;
		this.numRejectedMoves = 0;
	}
	
	/**
	 * Este es el procedimiento base de la metaheuristica, generalmente es el pseudocódigo que aparece
	 * en los papers.
	 */
	public void run()
	{
		// Generate initial poblation
		System.out.println(">> Generar poblacion inicial\n");
		generateInitialPoblation();
		toConsolePoblation();
		
		// Choose best solution in poblation
		chooseBestSolutionInPoblation();
		toConsoleBestSolution();
		
		// Metaheuristic cycle
		System.out.println("\n>> Comenzar el ciclo de la metaheuristica");
		int iteration = 0;
		while (iteration < this.numberIteration)
		{
			System.out.println("\n>> Iteración número ("+(iteration+1)+")");
			
			// Línea 6 y 7 del documento word (lo hice junto)
			generateNeighbourSolution();
			//toConsolePoblation();
			
			chooseBestSolutionInPoblation();
			toConsoleBestSolution();
			iteration++;
		}
	}
	
	/**
	 * Esta función genera randomicamente las soluciones iniciales.
	 * Generar poblacion aleatorea es EXPLORACION
	 */
	private void generateInitialPoblation()
	{
		for (int i = 0; i < numberPoblation; i++)
		{
			// Inicialite procedure
			boolean constraintOK = false;
			//crear una solucion segun los datos leidos
			MCDPRandomSolution randomSolution = new MCDPRandomSolution(data.A, data.M, data.P, data.C, data.mmax);
			int randomSolutionFitness = 0;
			
			// Estoy en el ciclo hasta generar una solución randomica que satisfaga las restricciones
			while (constraintOK == false)
			{
				// Create random solution
				randomSolution.createRandomSolution();
				
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
														randomSolution.getMachine_cell(),
														randomSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();
				
				if (constraintOK == true)
				{
					randomSolutionFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				}
				else
				{
					this.numRejectedMoves++;
				}
			}
			
			// Create Solution
			Solution s = new Solution(randomSolution.getMachine_cell(), randomSolution.getPart_cell(), randomSolutionFitness);
			
			// Add Solution in poblation
			poblation.add(s);
		}
	}
	
	private void toConsolePoblation()
	{
		for (int i = 0; i < numberPoblation; i++)
		{
			System.out.println(">> Poblation > Solution ["+(i+1)+"]");
			poblation.get(i).toConsoleMachineCell();
			poblation.get(i).toConsolePartCell();
			poblation.get(i).toConsoleFitness();
			System.out.println("");
		}
	}
	
	/**
	 * Genera una solución vecina (S') para solución de la población (S) y escoge entre la solución (S y S') cual
	 * es la mejor y la reemplaza en el arreglo población.
	 * Generar una solución vecina se denomina INTENSIFICACIÓN
	 */
	private void generateNeighbourSolution()
	{
		for (int i = 0; i < numberPoblation; i++)
		{
			// Inicialite procedure
			boolean constraintOK = false;
			MCDPNeighbourSolution neighbourSolution = new MCDPNeighbourSolution(data.A, data.M, data.P, data.C, data.mmax,
															poblation.get(i).getMachine_cell(),
															poblation.get(i).getPart_cell());
			int neighbourSolutionFitness = 0;
			
			// Estoy en el ciclo hasta generar una solución vecina que satisfaga las restricciones
			while (constraintOK == false)
			{
				// Genero una solución vecina
				neighbourSolution.createNeighbourSolution();
				
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
															neighbourSolution.getMachine_cell(),
															neighbourSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();
				
				if (constraintOK == true)
				{
					neighbourSolutionFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				}
				else
				{
					this.numRejectedMoves++;
				}
			}
			
			// Comparo cual es la mejor solución y la reemplazo.
			// En caso contrario lo dejo tal cual.
			if (neighbourSolutionFitness < poblation.get(i).getFitness())
			{
				// Escoger una nueva mejor solucion
				poblation.get(i).setMachine_cell(neighbourSolution.getMachine_cell());
				poblation.get(i).setPart_cell(neighbourSolution.getPart_cell());
				poblation.get(i).setFitness(neighbourSolutionFitness);
			}
		}
	}
	
	private void chooseBestSolutionInPoblation()
	{
		// Escoger temporalmente el primer elemento como la mejor solucion.
		bestSolution.setMachine_cell(poblation.get(0).getMachine_cell());
		bestSolution.setPart_cell(poblation.get(0).getPart_cell());
		bestSolution.setFitness(poblation.get(0).getFitness());
		
		for (int i = 1; i < numberPoblation; i++)
		{
			if (poblation.get(i).getFitness() < bestSolution.getFitness())
			{
				// Escoger una nueva mejor solucion
				bestSolution.setMachine_cell(poblation.get(i).getMachine_cell());
				bestSolution.setPart_cell(poblation.get(i).getPart_cell());
				bestSolution.setFitness(poblation.get(i).getFitness());
			}
		}
	}
	
	private void toConsoleBestSolution()
	{
		System.out.println(">> Mejor solución");
		bestSolution.toConsoleMachineCell();
		bestSolution.toConsolePartCell();
		bestSolution.toConsoleFitness();
	}
	
	public void toConsoleFinalReport()
	{
		System.out.println("===============================");
		System.out.println(">> Reporte final");
		toConsoleBestSolution();
		System.out.println(">> Número de movimientos aceptados: "+this.numAcceptedMoves);
		System.out.println(">> Número de movimientos fallados : "+this.numRejectedMoves);
		MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
				this.bestSolution.getMachine_cell(),
				this.bestSolution.getPart_cell());
		boctorModel.convertToFinalMatrix();
	}
}
