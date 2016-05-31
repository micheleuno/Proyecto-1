package FP;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import MCDP.model.MCDPData;
import MCDP.model.MCDPModel;
import MCDP.model.MCDPNeighbourSolution;
import MCDP.model.MCDPRandomSolution;
import MCDP.model.Solution;

public class FlowerPollination {
	// Parametros de la metaheuristica
	private int numberPoblation;
	private int numberIteration;
	private double delta;
	private double switchProbability;

	private ArrayList<Solution> poblation; // Un arreglo de soluciones
	private Solution bestSolution, tempSolution; // Mejor Solucion
	int tempFitness = 0;

	// Dataset (benchmark)
	private MCDPData data;

	// Estadisticas
	// veces que un random genera una solucion que cumple las restricciones
	private long numAcceptedMoves;
	private long numRejectedMoves;

	Random rn;
	double d;

	public FlowerPollination(int numberPoblation, int numberIteration, MCDPData data, double delta,
			double switchProbability) {
		this.numberPoblation = numberPoblation;
		this.numberIteration = numberIteration;
		this.data = data;
		this.poblation = new ArrayList<Solution>();
		this.bestSolution = new Solution();
		this.numAcceptedMoves = 0;
		this.numRejectedMoves = 0;
		this.delta = delta;
		this.switchProbability = switchProbability;
	}

	/**
	 * Este es el procedimiento base de la metaheuristica, generalmente es el
	 * pseudoc贸digo que aparece en los papers.
	 */
	public void run() {
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
		rn = new Random();

		while (iteration < this.numberIteration) {
			System.out.println("\n>> Iteraci髇 n鷐ero (" + (iteration + 1) + ")");

			// generateNeighbourSolution();

			for (int i = 0; i < numberPoblation; i++) {
				d = rn.nextDouble(); // random value in range 0.0 - 1.0

				tempSolution = new Solution(poblation.get(i).getMachine_cell(), poblation.get(i).getPart_cell(),
						poblation.get(i).getFitness());

				if (d < switchProbability) {
					generarMovimiento(1);
				} else {
					generarMovimiento(2);
				}

				if (tempFitness < poblation.get(i).getFitness()) {
					System.out.println("Fitness solucion creada levy " + tempFitness + " fitness solucion actual "
							+ poblation.get(i).getFitness());
					// Escoger una nueva mejor solucion
					poblation.get(i).setMachine_cell(tempSolution.getMachine_cell());
					poblation.get(i).setPart_cell(tempSolution.getPart_cell());
					poblation.get(i).setFitness(tempFitness);
				}
			}

			chooseBestSolutionInPoblation();
			toConsoleBestSolution();
			iteration++;
		}
	}

	/**
	 * Esta funci髇 genera randomicamente las soluciones iniciales. Generar
	 * poblacion aleatorea es EXPLORACION
	 */
	private void generateInitialPoblation() {
		for (int i = 0; i < numberPoblation; i++) {
			// Inicialite procedure
			boolean constraintOK = false;
			// crear una solucion segun los datos leidos
			MCDPRandomSolution randomSolution = new MCDPRandomSolution(data.A, data.M, data.P, data.C, data.mmax);
			int randomSolutionFitness = 0;

			// Estoy en el ciclo hasta generar una soluci贸n randomica que
			// satisfaga las restricciones
			while (constraintOK == false) {
				// Create random solution
				randomSolution.createRandomSolution();

				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						randomSolution.getMachine_cell(), randomSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					randomSolutionFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					this.numRejectedMoves++;
				}
			}

			// Create Solution
			Solution s = new Solution(randomSolution.getMachine_cell(), randomSolution.getPart_cell(),
					randomSolutionFitness);

			// Add Solution in poblation
			poblation.add(s);
		}
	}

	private void toConsolePoblation() {
		for (int i = 0; i < numberPoblation; i++) {
			System.out.println(">> Poblation > Solution [" + (i + 1) + "]");
			poblation.get(i).toConsoleMachineCell();
			poblation.get(i).toConsolePartCell();
			poblation.get(i).toConsoleFitness();
			System.out.println("");
		}
	}

	private void generarMovimiento(int tipoMovimiento) {
		boolean constraintOK = false;
		tempFitness = 0;

		if (tipoMovimiento == 1) {
			while (constraintOK == false) {
				tempSolution = generarPasoLevy(tempSolution);
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						tempSolution.getMachine_cell(), tempSolution.getPart_cell());

				/*
				 * for(int k=0;k<data.M;k++){ for(int l=0;l<data.C;l++){
				 * System.out.print("["+tempSolution.getMachine_cell()[k][l]+"]"
				 * ); } System.out.println(); }
				 */
				toConsoleBestSolution();
				Scanner nc = new Scanner(System.in);
				// nc.nextLine();

				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					System.out.println("ACEPTADA");
					tempFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					System.out.println("RECHAZADA");
					this.numRejectedMoves++;
				}
			}
		}
		if (tipoMovimiento == 2) {
			while (constraintOK == false) {
				tempSolution = generarPasoLocal(tempSolution);
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						tempSolution.getMachine_cell(), tempSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();

				/*
				 * for(int k=0;k<data.M;k++){ for(int l=0;l<data.C;l++){
				 * System.out.print("["+tempSolution.getMachine_cell()[k][l]+"]"
				 * ); } System.out.println(); }
				 */
				toConsoleBestSolution();
				Scanner nc = new Scanner(System.in);
				// nc.nextLine();

				if (constraintOK == true) {
					tempFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					this.numRejectedMoves++;
				}
			}
		}
	}

	private Solution generarPasoLevy(Solution tempSolution) {
		Vuelo_levy L = new Vuelo_levy();
		double step_levy,resultado,discretizacion;
		int binarizacion;
		do {
			step_levy = L.levy_step(1.5, 1);
		} while (Double.isNaN(step_levy));

		double temp_machine_cell[][] = new double[data.M][data.C];

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				temp_machine_cell[i][j] = tempSolution.getMachine_cell()[i][j];
			}
		}

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				resultado = temp_machine_cell[i][j]
						+ step_levy * (bestSolution.getMachine_cell()[i][j] - temp_machine_cell[i][j]);
				discretizacion = VShaped.V2((float)resultado);
				binarizacion = binarizacion(discretizacion);
				tempSolution.getMachine_cell()[i][j] = binarizacion;
				// System.out.println("RESULTADO "+binarizacion);
			}
		}

		// Posteriormente generamos manualmente la matriz PxC
		for (int j = 0; j < data.P; j++) // Rellenar la matriz pieza脳celda de
			// "0"
		{
			for (int k = 0; k < data.C; k++) {
				tempSolution.getPart_cell()[j][k] = 0;
			}
		}

		for (int j = 0; j < data.P; j++) {
			int[] tempPart = new int[data.M];
			int[] cellCount = new int[data.C];

			for (int k = 0; k < data.C; k++) {
				for (int i = 0; i < data.M; i++) {
					// Esto hace una multiplicaci贸n para revisar si: P(j) es
					// subconjunto de C(k)
					tempPart[i] = tempSolution.getMachine_cell()[i][k] * data.A[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}
			// Extraer el 铆ndice de la posici贸n con el n煤mero m谩s grande.
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			}
			// Puts un 1 in the new cell
			tempSolution.getPart_cell()[j][maxIndex] = 1;
		}

		return tempSolution;
	}

	private Solution generarPasoLocal(Solution tempSolution) {
		double epsilon,resultado,discretizacion;
		int randomPoblationK, randomPoblationJ,binarizacion;
		randomPoblationK = rn.nextInt((numberPoblation - 1) - 0 + 1);
		randomPoblationJ = rn.nextInt((numberPoblation - 1) - 0 + 1);
		epsilon = rn.nextDouble();

		double temp_machine_cell[][] = new double[data.M][data.C];

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				temp_machine_cell[i][j] = tempSolution.getMachine_cell()[i][j];
			}
		}

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				resultado = temp_machine_cell[i][j]
						+ epsilon * (poblation.get(randomPoblationJ).getMachine_cell()[i][j]
								- poblation.get(randomPoblationK).getMachine_cell()[i][j]);
				discretizacion = VShaped.V2((float)resultado);
				binarizacion = binarizacion(discretizacion);
				tempSolution.getMachine_cell()[i][j] = binarizacion;
				// System.out.println("RESULTADO "+binarizacion);
			}
		}

		// Posteriormente generamos manualmente la matriz PxC
		for (int j = 0; j < data.P; j++) // Rellenar la matriz pieza脳celda de
			// "0"
		{
			for (int k = 0; k < data.C; k++) {
				tempSolution.getPart_cell()[j][k] = 0;
			}
		}

		for (int j = 0; j < data.P; j++) {
			int[] tempPart = new int[data.M];
			int[] cellCount = new int[data.C];

			for (int k = 0; k < data.C; k++) {
				for (int i = 0; i < data.M; i++) {
					// Esto hace una multiplicaci贸n para revisar si: P(j) es
					// subconjunto de C(k)
					tempPart[i] = tempSolution.getMachine_cell()[i][k] * data.A[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}
			// Extraer el 铆ndice de la posici贸n con el n煤mero m谩s grande.
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			}
			// Puts un 1 in the new cell
			tempSolution.getPart_cell()[j][maxIndex] = 1;
		}

		return tempSolution;
	}

	public int binarizacion(double numDiscreto) {
		/*double random = rn.nextDouble();
		if (random <= numDiscreto) {
			return 1;
		}
		return 0;*/
		return Math.round((float)numDiscreto);
	}

	/**
	 * Genera una soluci贸n vecina (S') para soluci贸n de la poblaci贸n (S) y
	 * escoge entre la soluci贸n (S y S') cual es la mejor y la reemplaza en el
	 * arreglo poblaci贸n. Generar una soluci贸n vecina se denomina
	 * INTENSIFICACI脫N
	 */
	private void generateNeighbourSolution() {
		for (int i = 0; i < numberPoblation; i++) {
			// Inicialite procedure
			boolean constraintOK = false;
			MCDPNeighbourSolution neighbourSolution = new MCDPNeighbourSolution(data.A, data.M, data.P, data.C,
					data.mmax, poblation.get(i).getMachine_cell(), poblation.get(i).getPart_cell());
			int neighbourSolutionFitness = 0;

			// Estoy en el ciclo hasta generar una soluci贸n vecina que
			// satisfaga las restricciones
			while (constraintOK == false) {
				// Genero una soluci贸n vecina
				neighbourSolution.createNeighbourSolution();

				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						neighbourSolution.getMachine_cell(), neighbourSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					neighbourSolutionFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					this.numRejectedMoves++;
				}
			}

			// Comparo cual es la mejor soluci贸n y la reemplazo.
			// En caso contrario lo dejo tal cual.
			if (neighbourSolutionFitness < poblation.get(i).getFitness()) {
				// Escoger una nueva mejor solucion
				poblation.get(i).setMachine_cell(neighbourSolution.getMachine_cell());
				poblation.get(i).setPart_cell(neighbourSolution.getPart_cell());
				poblation.get(i).setFitness(neighbourSolutionFitness);
			}
		}
	}

	private void chooseBestSolutionInPoblation() {
		// Escoger temporalmente el primer elemento como la mejor solucion.
		bestSolution.setMachine_cell(poblation.get(0).getMachine_cell());
		bestSolution.setPart_cell(poblation.get(0).getPart_cell());
		bestSolution.setFitness(poblation.get(0).getFitness());

		for (int i = 1; i < numberPoblation; i++) {
			if (poblation.get(i).getFitness() < bestSolution.getFitness()) {
				// Escoger una nueva mejor solucion
				bestSolution.setMachine_cell(poblation.get(i).getMachine_cell());
				bestSolution.setPart_cell(poblation.get(i).getPart_cell());
				bestSolution.setFitness(poblation.get(i).getFitness());
			}
		}
	}

	private void toConsoleBestSolution() {
		System.out.println(">> Mejor soluci贸n");
		bestSolution.toConsoleMachineCell();
		bestSolution.toConsolePartCell();
		bestSolution.toConsoleFitness();
	}

	public void toConsoleFinalReport() {
		System.out.println("===============================");
		System.out.println(">> Reporte final");
		toConsoleBestSolution();
		System.out.println(">> N煤mero de movimientos aceptados: " + this.numAcceptedMoves);
		System.out.println(">> N煤mero de movimientos fallados : " + this.numRejectedMoves);
		MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
				this.bestSolution.getMachine_cell(), this.bestSolution.getPart_cell());
		boctorModel.convertToFinalMatrix();

		for (int i = 0; i < data.M; i++) {
			for (int a = 0; a < numberPoblation; a++) {
				for (int j = 0; j < data.C; j++) {
					System.out.print("[" + poblation.get(a).getMachine_cell()[i][j] + "]");
				}
				System.out.print(" || ");
			}
			System.out.println();
		}
	}
}
