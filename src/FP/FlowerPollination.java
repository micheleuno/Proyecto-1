package FP;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import MCDP.benchmark.Statistics;
import MCDP.model.MCDPData;
import MCDP.model.MCDPModel;
import MCDP.model.MCDPRandomSolution;
import MCDP.model.Solution;

public class FlowerPollination {
	// Parametros de la metaheuristica
	private int numberPoblation;
	private int numberIteration;
	@SuppressWarnings("unused")
	private double delta;
	private double switchProbability;
	
	private int vector_fitness[];
	Grafico grafico = null;
	private String directoryName = null;
	private ArrayList<Solution> poblation; // Un arreglo de soluciones
	private Solution bestSolution, tempSolution; // Mejor Solucion
	int tempFitness = 0;
	
	int backFitness=0;
	int iterationAutonomousSearch=5;
	int poblationIncrease=5;

	private int matrizSimilitud[][];
	private int modoSwitch = 0; //variable Autonomous Search
	private int modoPoblation = 0;
	private int modoDelta = 1;
	// Dataset (benchmark)
	private MCDPData data;

	// Estadisticas
	// veces que un random genera una solucion que cumple las restricciones
	private long numAcceptedMoves;
	private long numRejectedMoves;

	Random rn;
	double d;
	Scanner sc = new Scanner(System.in);

	public FlowerPollination(int numberPoblation, int numberIteration, MCDPData data, double delta,
			double switchProbability, String directory) {
		this.numberPoblation = numberPoblation;
		this.numberIteration = numberIteration;
		this.data = data;
		this.poblation = new ArrayList<Solution>();
		this.bestSolution = new Solution();
		this.numAcceptedMoves = 0;
		this.numRejectedMoves = 0;
		this.delta = delta;
		this.switchProbability = switchProbability;
		this.vector_fitness = new int[numberIteration];
		this.directoryName = directory;
	}

	public int run() {
		matrizSimilitud = new int[data.M][data.M];
		calcularSimilitudMaquinas();
		generateInitialPoblation();
		chooseBestSolutionInPoblation();
		// Metaheuristic cycle
		int iteration = 0;
		int iterationOpt = 0;
		int optimo = 9999999;
		int contAutonomousSearch = 0;
		int iterationEstancadaPoblation = 0;
		int iterationEstancadaSwitch = 0;
		int iterationEstancadaDelta = 0;
		rn = new Random();

		while (iteration < this.numberIteration) {
			// System.out.println("\n>> Iteración número (" + (iteration + 1) +
			// ")");
			// toConsolePoblation();
			// toConsoleBestSolution();
		
			numberPoblation=poblation.size();
			for (int i = 0; i < numberPoblation; i++) {
				// toConsoleSingleSolutio(i);
				d = rn.nextDouble(); // random value in range 0.0 - 1.0
				
				tempSolution = new Solution(poblation.get(i).getMachine_cell(), poblation.get(i).getPart_cell(),
						poblation.get(i).getFitness());

				if (d < switchProbability) {
					generarMovimiento(1, i);
				} else {
					generarMovimiento(2, i);
				}
				
				if (tempFitness < poblation.get(i).getFitness()) {
					poblation.get(i).setMachine_cell(tempSolution.getMachine_cell());
					poblation.get(i).setPart_cell(tempSolution.getPart_cell());
					poblation.get(i).setFitness(tempFitness);
				}
				
			}

			chooseBestSolutionInPoblation();
			vector_fitness[iteration] = bestSolution.getFitness();
			
			//Autonomous Search-----------------------------------//
			if(contAutonomousSearch==iterationAutonomousSearch){
				//System.out.println("ENTRAMOS");
				//System.out.println("Back Fitness: "+backFitness+" Best Fitness Actual: "+bestSolution.getFitness());
				
				if(backFitness==bestSolution.getFitness()&&bestSolution.getFitness()>data.getBestSGlobal()){
					//switchProbability = switchProbability+0.1f;
					/*for(int i=0;i<poblationIncrease;i++){
						addRandomSolutionToPoblation();
						
					}*/
				/*	System.out.println("==========================================================");
					System.out.println("[Aumento de la poblacion en"+poblation.size()+"]");
					System.out.println("==========================================================");*/
				}
				/*
				if(backFitness==bestSolution.getFitness()&&1f>switchProbability){
					switchProbability = switchProbability+0.1f;
					System.out.println("==========================================================");
					System.out.println("[Cambio de Probabilidad: "+switchProbability+"]");
					System.out.println("==========================================================");
				}
				*/
				contAutonomousSearch=0;
				backFitness = bestSolution.getFitness();
			}else{
				if(contAutonomousSearch==0){//Cuando inicia el algoritmo
					backFitness = bestSolution.getFitness();
				}
			}
			contAutonomousSearch++;
			//Autonomous Search-----------------------------------//
			
			iteration++;
			if(optimo> bestSolution.getFitness()){
				optimo=bestSolution.getFitness();
				iterationOpt=iteration;	
				iterationEstancadaSwitch = iterationEstancadaPoblation=iterationEstancadaDelta=0;
				
			}else{
				iterationEstancadaSwitch++;
				iterationEstancadaPoblation++;
				iterationEstancadaDelta++;
			}
			//iterationEstancadaSwitch=AutonomousSearchSwitch(iterationEstancadaSwitch);//llamada a autonomous search
			iterationEstancadaPoblation=AutonomousSearchPoblation(iterationEstancadaPoblation);
			//iterationEstancadaDelta=AutonomousSearchDelta(iterationEstancadaDelta);
		}

		// Crear excels con datos para grafico convergencia
		
		/*File excel = new File(directoryName);
		if(!excel.exists()){
			Statistics.createConvergenciGraph(data.getIdentificator(), vector_fitness, directoryName);
		}*/
		return iterationOpt;
		
		// Descomentar para crear grafico java
		/*
		  grafico = new Grafico(vector_fitness,
		  data,numberIteration,numberPoblation);
		  grafico.setVisible(true);
		  System.out.println("Presione una tecla para continuar")
		  sc.nextLine();
		  */
		 
	}
	public int AutonomousSearchSwitch(int iteraciones){
		int CantIntEstan = 2;
		int CantModoEstan = 2;
		float step=0.05f; //cantidad de población que se añade
		switch (modoSwitch){
		case 0: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan&&switchProbability+step<=1f){ //aumentar el switch de probabilidad
				switchProbability = switchProbability+step;	
				}
				if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
					modoSwitch=1;						 //Se cambia al modo de disminuir
					iteraciones=0;
				}
								break;
		case 1: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan&&switchProbability-step>=0.1f){ //disminuir el switch de probabilidad
			switchProbability = switchProbability-step;		
			}
			if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
				modoSwitch=0;						 //Se cambia al modo de aumentar
				iteraciones=0;
			}
			
			break;
		}
		//System.out.println("Iteracion estancada: "+iteraciones+" switch: "+switchProbability+" modo: "+modo);
		return iteraciones;
		
	}
	public int AutonomousSearchDelta(int iteraciones){
		int CantIntEstan = 2;
		int CantModoEstan = 5;
		float step=0.1f; //Aumento del delta
		switch (modoDelta){
		case 0: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan&&delta+step<=1.9f){ //aumentar delta de probabilidad
			delta = delta+step;	
				}
				if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
					modoDelta=1;						 //Se cambia al modo de disminuir
					iteraciones=0;
				}
								break;
		case 1: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan&&delta-step>=0.1f){ //disminuir delta de probabilidad
			delta = delta-step;		
			}
			if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
				modoDelta=0;						 //Se cambia al modo de aumentar
				iteraciones=0;
			}
			
			break;
		}
	//	System.out.println("Iteracion estancada: "+iteraciones+" Delta: "+delta+" modo: ");
		return iteraciones;
		
	}
	
	
	
	
	public int AutonomousSearchPoblation(int iteraciones){
		int CantIntEstan = 4;
		int CantModoEstan = 3;
		int step = 5;//cantidad de población que se añade
		switch (modoPoblation){
		case 0: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan){ //aumentar la poblacion
				//	System.out.println("Modo 0");
					for(int i=0;i<step;i++){
						addRandomSolutionToPoblation();	
					}
				}
				if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
					modoPoblation=1;						 //Se cambia al modo de disminuir
					iteraciones=0;
				}
								break;
		case 1: if(iteraciones%CantIntEstan==0&&iteraciones>=CantIntEstan){ //disminuir la poblacion
			//System.out.println("Modo 1");
				for(int i=0;i<step;i++){
					deleteRandomSolutionToPoblation();	
				}
			}
			if(iteraciones>CantIntEstan*CantModoEstan){ //Si van dos aumentos y aun no mejora
				modoPoblation=0;						 //Se cambia al modo de aumentar
				iteraciones=0;
			}
			
			break;
		}
		//System.out.println("Iteracion estancada: "+iteraciones+" switch: "+switchProbability+" modo: "+modo+" Poblacion: "+poblation.size()+" fitness: "+bestSolution.getFitness());
		return iteraciones;
		
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

			// Estoy en el ciclo hasta generar una solucion randomica que
			// satisfaga las restricciones
			while (constraintOK == false) {
				// Create random solution
				randomSolution.createRandomSolution();
				// Check constraint
				MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
						randomSolution.getMachine_cell(), randomSolution.getPart_cell());
				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					//System.out.println("Paso");
					randomSolutionFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					//System.out.println("Error");
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
	
	private void addRandomSolutionToPoblation(){
		boolean constraintOK = false;
		// crear una solucion segun los datos leidos
		MCDPRandomSolution randomSolution = new MCDPRandomSolution(data.A, data.M, data.P, data.C, data.mmax);
		int randomSolutionFitness = 0;

		// Estoy en el ciclo hasta generar una solucion randomica que
		// satisfaga las restricciones
		while (constraintOK == false) {
			// Create random solution
			randomSolution.createRandomSolution();
			// Check constraint
			MCDPModel boctorModel = new MCDPModel(data.A, data.M, data.P, data.C, data.mmax,
					randomSolution.getMachine_cell(), randomSolution.getPart_cell());
			constraintOK = boctorModel.checkConstraint();

			if (constraintOK == true) {
				//System.out.println("Paso");
				randomSolutionFitness = boctorModel.calculateFitness();
				this.numAcceptedMoves++;
				break;
			} else {
				//System.out.println("Error");
				this.numRejectedMoves++;
			}
		}

		// Create Solution
		Solution s = new Solution(randomSolution.getMachine_cell(), randomSolution.getPart_cell(),
				randomSolutionFitness);

		// Add Solution in poblation
		poblation.add(s);
	}
	
	private void deleteRandomSolutionToPoblation(){
		  Random randomGenerator = new Random();
		 int randomInt = randomGenerator.nextInt(poblation.size());
		
		 do{
			// System.out.println("deleting");
			 if(poblation.get(randomInt)!=bestSolution){
				// System.out.println("Best solution: "+bestSolution+ " solucion eliminada: "+poblation.get(randomInt).getFitness()+" Random: "+randomInt);
				 poblation.remove(randomInt);
				 
			 }
				 
			 randomInt = randomGenerator.nextInt(poblation.size());
		 }while(poblation.get(randomInt)==bestSolution);
	}
	

	@SuppressWarnings("unused")
	private void toConsolePoblation() {
		for (int i = 0; i < numberPoblation; i++) {
			System.out.println(">> Poblation > Solution [" + (i + 1) + "]");
			poblation.get(i).toConsoleMachineCell();
			poblation.get(i).toConsolePartCell();
			poblation.get(i).toConsoleFitness();
			System.out.println("");
		}
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
				constraintOK = boctorModel.checkConstraint();

				if (constraintOK == true) {
					tempFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					constraintOK = repararSolucion();
					if (constraintOK == false) {
						tempSolution = poblation.get(poblacion);
					} else {
						tempFitness = boctorModel.calculateFitness();
						break;
					}
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

				if (constraintOK == true) {
					tempFitness = boctorModel.calculateFitness();
					this.numAcceptedMoves++;
					break;
				} else {
					constraintOK = repararSolucion();
					if (constraintOK == false) {
						tempSolution = poblation.get(poblacion);
					} else {
						tempFitness = boctorModel.calculateFitness();
						break;
					}
					this.numRejectedMoves++;
				}
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
		for (int j = 0; j < data.P; j++) {
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

	@SuppressWarnings("static-access")
	private Solution generarPasoLevy(Solution tempSolution) {
		Vuelo_levy L = new Vuelo_levy();
		double step_levy;
		int[][] tempMachine_cell = new int[data.M][data.C];
		int[] vectorMaquinaBest = new int[data.M];
		int[] vectorMaquinaActual = new int[data.M];

		// Pasar a vector
		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				if (tempSolution.getMachine_cell()[i][j] == 1) {
					vectorMaquinaActual[i] = j + 1;
				}
				if (bestSolution.getMachine_cell()[i][j] == 1) {
					vectorMaquinaBest[i] = j + 1;
				}
			}
		}

		do {
			step_levy = L.levy_step(delta, 1);
		} while (Double.isNaN(step_levy));

		for (int i = 0; i < data.M; i++) {
			vectorMaquinaActual[i] = aproximar(
					vectorMaquinaActual[i] + step_levy * (vectorMaquinaBest[i] - vectorMaquinaActual[i]));
		}

		for (int i = 0; i < data.M; i++) {
			tempMachine_cell[i][vectorMaquinaActual[i] - 1] = 1;
		}

		tempSolution.setMachine_cell(tempMachine_cell);

		// Posteriormente generamos manualmente la matriz PxC
		for (int j = 0; j < data.P; j++) {
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

		return tempSolution;
	}

	private int aproximar(double movimiento) {
		int aproximado = 0;
		
		//aproximado = IntervalDiscretization.IntervalDoubleValue(SShaped.S4(movimiento), data.C, 0, 1)+1;
		//System.out.println("VALOR MOVIMIENTO: ("+movimiento+") APROXIMADO("+aproximado+")");
		aproximado = Math.round((float) movimiento);
		if (aproximado < 1) {
			aproximado = 1;
		} else if (aproximado > data.C) {
			aproximado = data.C;
		}
		 
		return aproximado;
	}

	private Solution generarPasoLocal(Solution tempSolution) {
		double epsilon;
		int randomPoblationK, randomPoblationJ;
		randomPoblationK = rn.nextInt((numberPoblation - 1) - 0 + 1);
		randomPoblationJ = rn.nextInt((numberPoblation - 1) - 0 + 1);
		int[] vectorMaquinaRandomK = new int[data.M];
		int[] vectorMaquinaRandomJ = new int[data.M];
		int[] vectorMaquinaActual = new int[data.M];
		int[][] tempMachine_cell = new int[data.M][data.C];
		epsilon = rn.nextDouble();

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				if (poblation.get(randomPoblationK).getMachine_cell()[i][j] == 1) {
					vectorMaquinaRandomK[i] = j + 1;
				}
				if (poblation.get(randomPoblationJ).getMachine_cell()[i][j] == 1) {
					vectorMaquinaRandomJ[i] = j + 1;
				}
				if (tempSolution.getMachine_cell()[i][j] == 1) {
					vectorMaquinaActual[i] = j + 1;
				}
			}
		}

		for (int i = 0; i < data.M; i++) {
			vectorMaquinaActual[i] = aproximar(
					vectorMaquinaActual[i] + epsilon * (vectorMaquinaRandomJ[i] - vectorMaquinaRandomK[i]));
		}

		for (int i = 0; i < data.M; i++) {
			tempMachine_cell[i][vectorMaquinaActual[i] - 1] = 1;
		}

		tempSolution.setMachine_cell(tempMachine_cell);

		// Posteriormente generamos manualmente la matriz PxC
		for (int j = 0; j < data.P; j++) {
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

		return tempSolution;
	}

	public int binarizacion(double numDiscreto) {
		/*
		 * double random = rn.nextDouble(); if (random <= numDiscreto) { return
		 * 1; } return 0;
		 */
		if (numDiscreto > 0.2) {
			return 1;
		}
		// return Math.round((float) numDiscreto);
		return 0;
	}

	private void chooseBestSolutionInPoblation() {
		// Escoger temporalmente el primer elemento como la mejor solucion.
		double[][] maquina_celda = new double[data.M][data.C];
		double[][] parte_celda = new double[data.P][data.C];

		bestSolution.setMachine_cell(poblation.get(0).getMachine_cell());
		bestSolution.setPart_cell(poblation.get(0).getPart_cell());
		bestSolution.setFitness(poblation.get(0).getFitness());

		for (int i = 0; i < data.M; i++) {
			for (int j = 0; j < data.C; j++) {
				maquina_celda[i][j] = poblation.get(0).getMachine_cell()[i][j];
			}
		}

		for (int i = 0; i < data.P; i++) {
			for (int j = 0; j < data.C; j++) {
				parte_celda[i][j] = poblation.get(0).getPart_cell()[i][j];
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
				for (int k = 0; k < data.M; k++) {
					for (int j = 0; j < data.C; j++) {
						maquina_celda[k][j] = poblation.get(i).getMachine_cell()[k][j];
					}
				}
				for (int k = 0; k < data.P; k++) {
					for (int j = 0; j < data.C; j++) {
						parte_celda[k][j] = poblation.get(i).getPart_cell()[k][j];
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
		System.out.println(">> Numero de movimientos aceptados: " + this.numAcceptedMoves);
		System.out.println(">> Numero de movimientos fallados : " + this.numRejectedMoves);
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	@SuppressWarnings("unused")
	private void toConsoleSingleSolutio(int i) {

		System.out.println(i + "Poblacion del vector soluciones");
		poblation.get(i).toConsoleMachineCell();
		poblation.get(i).toConsolePartCell();
		poblation.get(i).toConsoleFitness();
		System.out.println("");

	}
}
