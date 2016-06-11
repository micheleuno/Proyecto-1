package FP;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

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

	private int matrizSimilitud[][];

	// Dataset (benchmark)
	private MCDPData data;

	// Estadisticas
	// veces que un random genera una solucion que cumple las restricciones
	private long numAcceptedMoves;
	private long numRejectedMoves;

	Random rn;
	double d;
	Scanner sc=new Scanner(System.in);

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

	public void run() {
		matrizSimilitud = new int[data.M][data.M];
		calcularSimilitudMaquinas();
		System.out.println(">> Generar poblacion inicial\n");
		generateInitialPoblation();
		chooseBestSolutionInPoblation();

		// Metaheuristic cycle
		System.out.println("\n>> Comenzar el ciclo de la metaheuristica");
		int iteration = 0;
		rn = new Random();

		while (iteration < this.numberIteration) {
			System.out.println("\n>> Iteración número (" + (iteration + 1) + ")");
			//toConsolePoblation();
			toConsoleBestSolution();

			for (int i = 0; i < numberPoblation; i++) {
				//toConsoleSingleSolutio(i);
				d = rn.nextDouble(); // random value in range 0.0 - 1.0

				tempSolution = new Solution(poblation.get(i).getMachine_cell(), poblation.get(i).getPart_cell(),
						poblation.get(i).getFitness());

				if (d < switchProbability) {
					generarMovimiento(1, i);
				} else {
					generarMovimiento(2, i);
				}

				if (tempFitness < poblation.get(i).getFitness()) {
					//System.out.println("tempFitness: "+tempFitness+"||"+"["+i+"]"+"poblacionFitness: "+poblation.get(i).getFitness());
					poblation.get(i).setMachine_cell(tempSolution.getMachine_cell());
					poblation.get(i).setPart_cell(tempSolution.getPart_cell());
					poblation.get(i).setFitness(tempFitness);
				}
			}

			chooseBestSolutionInPoblation();
			iteration++;
			//sc.nextLine();
		}
	}

	public void calcularSimilitudMaquinas() {
		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.M; j++) {
				if (i != j) {
					matrizSimilitud[i][j] = contarSimilitud(i, j);
					matrizSimilitud[j][i] = matrizSimilitud[i][j];
				}
			}
		}
	}

	private int contarSimilitud(int i, int j) {
		int similitud = 0;

		for (int k = 0; k < data.P; k++) {
			if ((data.A[i][k] == 1) && (data.A[i][k] == data.A[j][k])) {
				similitud++;
			}
		}

		return similitud;
	}

	/**
	 * Esta función genera randomicamente las soluciones iniciales. Generar
	 * poblacion aleatorea es EXPLORACION
	 */
	private void generateInitialPoblation() {
		for (int i = 0; i < numberPoblation; i++) {
			// Inicialite procedure
			boolean constraintOK = false;
			// crear una solucion segun los datos leidos
			MCDPRandomSolution randomSolution = new MCDPRandomSolution(data.A, data.M, data.P, data.C, data.mmax);
			int randomSolutionFitness = 0;

			// Estoy en el ciclo hasta generar una soluciÃ³n randomica que
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

	private void toConsoleSingleSolutio(int i) {

		System.out.println(i + "Poblacion del vector soluciones");
		poblation.get(i).toConsoleMachineCell();
		poblation.get(i).toConsolePartCell();
		poblation.get(i).toConsoleFitness();
		System.out.println("");

	}

	private void generarMovimiento(int tipoMovimiento, int poblacion) {
		boolean constraintOK = false;
		tempFitness = 0;

		if (tipoMovimiento == 1) {
			while (constraintOK == false) {
				tempSolution = generarPasoLevy(tempSolution);
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						tempSolution.getMachine_cell(), tempSolution.getPart_cell());

				//System.out.println("Solucion generada por levy");
/*				for (int k = 0; k < data.M; k++) {
					for (int l = 0; l < data.C; l++) {
						System.out.print("[" + tempSolution.getMachine_cell()[k][l] + "]");
					}
					System.out.println();
				}*/

				// toConsoleBestSolution();
				Scanner nc = new Scanner(System.in);
				// nc.nextLine();

				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					tempFitness = boctorModel.calculateFitness();
					//System.out.println("ACEPTADA "+"FITNESS: "+tempFitness);
					this.numAcceptedMoves++;
					//nc.nextLine();
					break;
				} else {
					//System.out.println("RECHAZADA mov 1");
					//constraintOK = repararSolucion();
					/*
					System.out.println(constraintOK);
					System.out.println("SOLUCION REPARADA");
					for (int k = 0; k < data.M; k++) {
						for (int l = 0; l < data.C; l++) {
							System.out.print("[" + tempSolution.getMachine_cell()[k][l] + "]");
						}
						System.out.println();
					}*/					
					/*if (constraintOK == false) {
						tempSolution = poblation.get(poblacion);
					}else{
						tempFitness = boctorModel.calculateFitness();
						break;
					}*/
					// System.out.println("RECHAZADA");
					tempSolution = poblation.get(poblacion);
					this.numRejectedMoves++;
				}
				//nc.nextLine();
			}
		}
		if (tipoMovimiento == 2) {
			while (constraintOK == false) {
				tempSolution = generarPasoLocal(tempSolution);
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						tempSolution.getMachine_cell(), tempSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();

				//System.out.println("Solucion generada localmente");
/*				for (int k = 0; k < data.M; k++) {
					for (int l = 0; l < data.C; l++) {
						System.out.print("[" + tempSolution.getMachine_cell()[k][l] + "]");
					}
					System.out.println();
				}*/

				// toConsoleBestSolution();
				Scanner nc = new Scanner(System.in);
				// nc.nextLine();

				if (constraintOK == true) {
					tempFitness = boctorModel.calculateFitness();
					//System.out.println("ACEPTADA FITNESS: "+tempFitness);
					this.numAcceptedMoves++;
					//nc.nextLine();
					break;
				} else {			
					//System.out.println("RECHAZADA mov 2");
					//constraintOK = repararSolucion();
					/*System.out.println("RECHAZADA");
					System.out.println(constraintOK);
					System.out.println("SOLUCION REPARADA");
					for (int k = 0; k < data.M; k++) {
						for (int l = 0; l < data.C; l++) {
							System.out.print("[" + tempSolution.getMachine_cell()[k][l] + "]");
						}
						System.out.println();
					}*/
					/*if (constraintOK == false) {
						tempSolution = poblation.get(poblacion);
					}else{
						tempFitness = boctorModel.calculateFitness();
						break;
					}*/
					tempSolution = poblation.get(poblacion);
					this.numRejectedMoves++;
				}
				//nc.nextLine();
			}
		}
	}

	public boolean repararSolucion() {
		boolean constraintOK = false;
		int contAsignaciones = 0, colConMenosAsig = 0, maqMenosAfin = 0, flag = 0, mejorUbicacionCelda;

		MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax, tempSolution.getMachine_cell(),
				tempSolution.getPart_cell());

		constraintOK = boctorModel.consistencyConstraint_1();

		if (!constraintOK) {
			for (int i = 0; i < data.M; i++) {
				flag = 0;
				for (int j = 0; j < data.C; j++) {
					if (tempSolution.getMachine_cell()[i][j] == 1) {
						if (flag == 1) {
							mejorUbicacionCelda = buscarCeldaCorrecta(i);
							for (int k = 0; k < data.C; k++) {
								tempSolution.getMachine_cell()[i][k] = 0;
							}
							tempSolution.getMachine_cell()[i][mejorUbicacionCelda] = 1;
							continue;
						} else {
							flag = 1;
						}
					}
				}
			}
		}

		boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax, tempSolution.getMachine_cell(),
				tempSolution.getPart_cell());
		constraintOK = boctorModel.consistencyConstraint_3();

		if (!constraintOK) {
			for (int j = 0; j < data.C; j++) {
				contAsignaciones = 0;
				for (int i = 0; i < data.M; i++) {
					if (tempSolution.getMachine_cell()[i][j] == 1) {
						contAsignaciones++;
					}
				}
				if (contAsignaciones > data.mmax) {
					int dif = contAsignaciones - data.mmax;
					while (dif > 0) {
						colConMenosAsig = buscarColumnaMenorAsig(j);
						maqMenosAfin = buscarMaqMenorSim(j);
						for (int k = 0; k < data.C; k++) {
							tempSolution.getMachine_cell()[maqMenosAfin][k] = 0;
						}
						tempSolution.getMachine_cell()[maqMenosAfin][colConMenosAsig] = 1;
						dif--;
					}
				} else {

				}
			}
		}
		for (int j = 0; j < data.P; j++)
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
					tempPart[i] = tempSolution.getMachine_cell()[i][k] * data.A[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			}
			tempSolution.getPart_cell()[j][maxIndex] = 1;
		}

		boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax, tempSolution.getMachine_cell(),
				tempSolution.getPart_cell());
		constraintOK = boctorModel.checkConstraint();
		return constraintOK;
	}

	public int buscarMaqMenorSim(int columActual) {
		int contSim = 0, menSimilitud = 1000000000, maquina = 0;

		for (int j = 0; j < data.M; j++) {
			contSim = 0;
			if (tempSolution.getMachine_cell()[j][columActual] == 1) {
				for (int k = 0; k < data.M; k++) {
					contSim = contSim + matrizSimilitud[j][k];
				}
				if (menSimilitud > contSim) {
					menSimilitud = contSim;
					maquina = j;
				}
			}
		}

		return maquina;
	}

	public int buscarColumnaMenorAsig(int columActual) {
		int contAsig = 0, menAsig = 1000000000, colum = 0;
		for (int i = columActual; i < data.C; i++) {
			contAsig = 0;
			for (int j = 0; j < data.M; j++) {
				if (tempSolution.getMachine_cell()[j][i] == 1) {
					contAsig++;
				}
			}
			if (menAsig > contAsig) {
				menAsig = contAsig;
				colum = i;
			}
		}
		return colum;
	}

	public int buscarCeldaCorrecta(int maquina) {
		int similitud = 0, simAnterior = 0, celda = 0;
		for (int j = 0; j < data.C; j++) {
			for (int i = 0; i < maquina; i++) {
				if (tempSolution.getMachine_cell()[i][j] == 1) {
					similitud = similitud + matrizSimilitud[maquina][i];
				}
			}
			if (similitud > simAnterior) {
				simAnterior = similitud;
				celda = j;
			}
		}
		return celda;
	}

	private Solution generarPasoLevy(Solution tempSolution) {
		Vuelo_levy L = new Vuelo_levy();
		double step_levy, resultado, discretizacion;
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

		//for (int i = 0; i < data.M; i++) {
			//for (int j = 0; j < data.C; j++) {
				//resultado = temp_machine_cell[i][j]	+ step_levy * (bestSolution.getMachine_cell()[i][j] - temp_machine_cell[i][j]);
				resultado = subtraction(bestSolution,tempSolution);
				resultado=resultado*step_levy;
				tempSolution = addition(tempSolution,resultado);
				//discretizacion = VShaped.V2((float) resultado);
				//binarizacion = binarizacion(discretizacion);
				//tempSolution.getMachine_cell()[i][j] = binarizacion;
				
		//	}
	//	}

		// Posteriormente generamos manualmente la matriz PxC
	/*	for (int j = 0; j < data.P; j++){
			for (int k = 0; k < data.C; k++) {
				tempSolution.getPart_cell()[j][k] = 0;
			}
		}

		for (int j = 0; j < data.P; j++) {
			int[] tempPart = new int[data.M];
			int[] cellCount = new int[data.C];

			for (int k = 0; k < data.C; k++) {
				for (int i = 0; i < data.M; i++) {
					tempPart[i] = tempSolution.getMachine_cell()[i][k] * data.A[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			}
			tempSolution.getPart_cell()[j][maxIndex] = 1;
		}*/

		return tempSolution;
	}

	private Solution generarPasoLocal(Solution tempSolution) {
		double epsilon, resultado, discretizacion;
		int randomPoblationK, randomPoblationJ, binarizacion;
		randomPoblationK = rn.nextInt((numberPoblation - 1) - 0 + 1);
		randomPoblationJ = rn.nextInt((numberPoblation - 1) - 0 + 1);
		epsilon = rn.nextDouble();

		double temp_machine_cell[][] = new double[data.M][data.C];

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				temp_machine_cell[i][j] = tempSolution.getMachine_cell()[i][j];
			}
		}
		
		resultado = subtraction(poblation.get(randomPoblationK),poblation.get(randomPoblationJ));
		resultado=resultado*epsilon;
		tempSolution = addition(tempSolution,resultado);
		

		/*for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) 
				resultado = temp_machine_cell[i][j] + epsilon * (poblation.get(randomPoblationJ).getMachine_cell()[i][j]
						- poblation.get(randomPoblationK).getMachine_cell()[i][j]);
				discretizacion = VShaped.V2((float) resultado);
				binarizacion = binarizacion(discretizacion);
				tempSolution.getMachine_cell()[i][j] = binarizacion;
			}
		}

		// Posteriormente generamos manualmente la matriz PxC
		for (int j = 0; j < data.P; j++){
			for (int k = 0; k < data.C; k++) {
				tempSolution.getPart_cell()[j][k] = 0;
			}
		}

		for (int j = 0; j < data.P; j++) {
			int[] tempPart = new int[data.M];
			int[] cellCount = new int[data.C];

			for (int k = 0; k < data.C; k++) {
				for (int i = 0; i < data.M; i++) {
					tempPart[i] = tempSolution.getMachine_cell()[i][k] * data.A[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}
			
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			}
			tempSolution.getPart_cell()[j][maxIndex] = 1;
		}*/

		return tempSolution;
	}

	public int binarizacion(double numDiscreto) {
		/*
		 * double random = rn.nextDouble(); if (random <= numDiscreto) { return
		 * 1; } return 0;
		 */
		if(numDiscreto>0.2
				){
			return 1;
		}
		//return Math.round((float) numDiscreto);
		return 0;
	}

	private void chooseBestSolutionInPoblation() {
		// Escoger temporalmente el primer elemento como la mejor solucion.
		double [][] maquina_celda = new double[data.M][data.C];
		double [][] parte_celda = new double[data.P][data.C];
		
	
		
		
		bestSolution.setMachine_cell(poblation.get(0).getMachine_cell());
		bestSolution.setPart_cell(poblation.get(0).getPart_cell());
		bestSolution.setFitness(poblation.get(0).getFitness());
		
		
		  for (int i = 0; i < data.M; i++){
	        	for(int j = 0; j < data.C; j++){
	        		maquina_celda[i][j]=poblation.get(0).getMachine_cell()[i][j];        		
	        	}
	        }
		  
		  for (int i = 0; i < data.P; i++)
	        {
	        	for(int j = 0; j <data.C; j++){
	        		parte_celda[i][j]=poblation.get(0).getPart_cell()[i][j];
	        	}
	        }
		  
		  bestSolution.setDoubleMachine_cell(maquina_celda);
		  bestSolution.setDoublePart_cell(parte_celda);
		
		  
		for (int i = 1; i < numberPoblation; i++) {
			if (poblation.get(i).getFitness() < bestSolution.getFitness()) {
				// Escoger una nueva mejor solucion
				bestSolution.setMachine_cell(poblation.get(i).getMachine_cell());
				bestSolution.setPart_cell(poblation.get(i).getPart_cell());
				bestSolution.setFitness(poblation.get(i).getFitness());
			  for (int k = 0; k < data.M; k++){
		        	for(int j = 0; j < data.C; j++){
		        		maquina_celda[k][j]=poblation.get(i).getMachine_cell()[k][j];        		
		        	}
		        }				  
			  for (int k = 0; k < data.P; k++)
		        {
		        	for(int j = 0; j < data.C; j++){
		        		parte_celda[k][j]=poblation.get(i).getPart_cell()[k][j];
		        	}
		        }
			  bestSolution.setDoubleMachine_cell(maquina_celda);
			  bestSolution.setDoublePart_cell(parte_celda);
			}
		}
	}

	private void toConsoleBestSolution() {
		System.out.println(">> Mejor solucion");
		bestSolution.toConsoleMachineCell();
		bestSolution.toConsolePartCell();
		bestSolution.toConsoleFitness();
	}

	public void toConsoleFinalReport() {
		System.out.println("===============================");
		System.out.println(">> Reporte final");
		toConsoleBestSolution();
		System.out.println(">> NÃºmero de movimientos aceptados: " + this.numAcceptedMoves);
		System.out.println(">> NÃºmero de movimientos fallados : " + this.numRejectedMoves);
		MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
				this.bestSolution.getMachine_cell(), this.bestSolution.getPart_cell());
		boctorModel.convertToFinalMatrix();

		/*
		 * for (int i = 0; i < data.M; i++) { for (int a = 0; a <
		 * numberPoblation; a++) { for (int j = 0; j < data.C; j++) {
		 * System.out.print("[" + poblation.get(a).getMachine_cell()[i][j] +
		 * "]"); } System.out.print(" || "); } System.out.println(); }
		 */
	}

	public double subtraction(Solution solution1, Solution solution2) {
		//System.out.println("Subtraccion");
		// Realizar la resta de g* - xi(t)
		// g* = MxC, PxC
		// xi(t) = MxC, PxC
		// Para ello definimos el valor de la resta generada como h. La resta es
		// la distancia entre las matrices.
		// g* - xi = h
		// h(MxC) = g*(MxC) - xi(MxC)
		// h(PxC) = g*(PxC) - xi(PxC)
		double[][] temp_gMxC = solution1.getDoubleMachine_cell();
		double[][] temp_gPxC = solution1.getDoublePart_cell();
		double[][] temp_xiMxC = solution2.getDoubleMachine_cell();
		double[][] temp_xiPxC = solution2.getDoublePart_cell();
		RealMatrix gPxC = new Array2DRowRealMatrix(temp_gPxC);
		RealMatrix gMxC = new Array2DRowRealMatrix(temp_gMxC);		
		RealMatrix xiMxC = new Array2DRowRealMatrix(temp_xiMxC);
		RealMatrix xiPxC = new Array2DRowRealMatrix(temp_xiPxC); // La distancia
																	// entre dos
																	// matrices.
		// Step 1. gMxC - xiMxC, gPxC - xiMxC
		RealMatrix hMxC = gMxC.subtract(xiMxC);
		RealMatrix hPxC = gPxC.subtract(xiPxC); // Step 2. Multiplicamos por
												// h*h_transpuesta
		hMxC = hMxC.multiply(hMxC.transpose());
		hPxC = hPxC.multiply(hPxC.transpose()); // Step 3. // Calculamos la
												// traza (Sumamos la diagonal)
		double trazaMxC = hMxC.getTrace();
		double trazaPxC = hPxC.getTrace(); // Determinamos la distancia -
											// Determinamos la raiz
		double distanceMxC = Math.sqrt(trazaMxC);
		double distancePxC = Math.sqrt(trazaPxC);

		return distanceMxC + distancePxC;
	}

	public Solution addition(Solution solution, double value) {
		//System.out.println("Adicion");
		// Realizar el cambio de maquina n veces, n = value, value = levy *
		// valor de la resta;
		int[][] machine_cell = new int[data.getM()][data.getC()];
		int[][] original_machine_cell = new int[data.getM()][data.getC()];
		int machines = solution.getMachine_cell().length;
		int cells = solution.getMachine_cell()[0].length;

		for (int i = 0; i < machines; i++) {
			for (int k = 0; k < cells; k++) {
				original_machine_cell[i][k] = solution.getMachine_cell()[i][k];
			}
		}
		// Copy original values
		for (int i = 0; i < data.getM(); i++) {
			System.arraycopy(original_machine_cell[i], 0, machine_cell[i], 0, data.getC());
		}
		int times = 0;
		int val = (int) value;
		if(val>500000){
			val=500000;
		}
		while (times < val) {// Random machine
			//System.out.println("times: "+times+" val: "+val+" value: "+value);
			Random rm = new Random();
			int randomMachine = rm.nextInt(machines - 0) + 0; // Clear row
			for (int k = 0; k < cells; k++) {
				machine_cell[randomMachine][k] = 0;
			} // Random cell
			Random rc = new Random();
			int randomCell = rc.nextInt(cells - 0) + 0;// Puts new value 1 in
			// new randomic cell
			machine_cell[randomMachine][randomCell] = 1;// aumentamos la
			// iteracion
			times++;
		}
		/** * hacer ahora partexcelda */ // Se crea la matriz pieza×celda para
		// el momento en que se necesita
		int[][] part_cell = new int[data.getP()][data.getC()];
		for (int j = 0; j < data.getP(); j++) { // Rellenar la matriz
			// pieza×celda de "0"
			for (int k = 0; k < data.getC(); k++) {
				part_cell[j][k] = 0;
			}
		}
		for (int j = 0; j < data.getP(); j++) {
			int[] tempPart = new int[data.getM()];
			int[] cellCount = new int[data.getC()];
			for (int k = 0; k < data.getC(); k++) {
				for (int i = 0; i < data.getM(); i++) {// Esto hace una
					// multiplicación para
					// revisar si: P(j) es
					// subconjunto de C(k)
					tempPart[i] = solution.getMachine_cell()[i][k] * data.getA()[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			} // Extraer el índice de la posición con el número más grande.
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++) {
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])) {
					maxIndex = i;
				}
			} // Puts un 1 in the new cell
			part_cell[j][maxIndex] = 1;
		}
		// toConsole("getRandomMove2", machine_cell, part_cell);
		return new Solution(machine_cell, part_cell);
	}

}
