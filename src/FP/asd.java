package FP;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import MCDP.model.Solution;

public class asd {

	public static void main(String[] args) {

	}
	public double subtraction(Solution solution1, Solution solution2) {     // Realizar la resta de g* - xi(t)     // g*    = MxC, PxC     // xi(t) = MxC, PxC     // Para ello definimos el valor de la resta generada como h. La resta es la distancia entre las matrices.     // g* - xi = h     // h(MxC) = g*(MxC) - xi(MxC)     // h(PxC) = g*(PxC) - xi(PxC)   
		double[][] temp_gMxC = solution1.getDoubleMachine_cell();
		double[][] temp_gPxC = solution1.getDoublePart_cell();
		double[][] temp_xiMxC = solution2.getDoubleMachine_cell();
		double[][] temp_xiPxC = solution2.getDoublePart_cell();
		RealMatrix gMxC = new Array2DRowRealMatrix(temp_gMxC);
		RealMatrix gPxC = new Array2DRowRealMatrix(temp_gPxC);
		RealMatrix xiMxC = new Array2DRowRealMatrix(temp_xiMxC);
		RealMatrix xiPxC = new Array2DRowRealMatrix(temp_xiPxC);      // La distancia entre dos matrices.
		// http://www.mty.itesm.mx/etie/deptos/m/ma00-130/lecturas/m130-08.pdf     
		// Step 1. gMxC - xiMxC, gPxC - xiMxC
		RealMatrix hMxC = gMxC.subtract(xiMxC);
		RealMatrix hPxC = gPxC.subtract(xiPxC);      // Step 2. Multiplicamos por h*h_transpuesta
		hMxC = hMxC.multiply(hMxC.transpose());
		hPxC = hPxC.multiply(hPxC.transpose());      // Step 3.     // Calculamos la traza (Sumamos la diagonal)
		double trazaMxC = hMxC.getTrace();
		double trazaPxC = hPxC.getTrace();      // Determinamos la distancia - Determinamos la raiz
		double distanceMxC = Math.sqrt(trazaMxC);
		double distancePxC = Math.sqrt(trazaPxC);

		return distanceMxC + distancePxC;
	}

	public Solution addition(Solution solution, double value){
		// Realizar el cambio de maquina n veces, n = value, value = levy * valor de la resta;
		int[][] machine_cell = new int[data.getM()][data.getC()];
		int[][] original_machine_cell = new int[data.getM()][data.getC()];
		int machines = solution.getMachine_cell().length;
		int cells = solution.getMachine_cell()[0].length;

		for (int i = 0; i < machines; i++){
			for (int k = 0; k < cells; k++){
				original_machine_cell[i][k] = solution.getMachine_cell()[i][k];
			}
		}
		// Copy original values
		for (int i = 0; i < data.getM(); i++){
			System.arraycopy(original_machine_cell[i], 0, machine_cell[i], 0, data.getC());
		}
		int times = 0;
		while (times < value){// Random machine
			Random rm = new Random();
			int randomMachine = rm.nextInt(machines - 0) + 0; // Clear row
			for (int k = 0; k < cells; k++){
				machine_cell[randomMachine][k] = 0;
			}       // Random cell
			Random rc = new Random();
			int randomCell = rc.nextInt(cells - 0) + 0;// Puts new value 1 in new randomic cell
			machine_cell[randomMachine][randomCell] = 1;// aumentamos la iteracion
			times++;}
		/**    * hacer ahora partexcelda      */  //Se crea la matriz pieza×celda para el momento en que se necesita
		int[][] part_cell = new int[data.getP()][data.getC()];
		for (int j = 0; j < data.getP(); j++){ //Rellenar la matriz pieza×celda de "0"
			for (int k = 0; k < data.getC(); k++){
				part_cell[j][k] = 0;
			}
		}
		for (int j = 0; j < data.getP(); j++){
			int[] tempPart = new int[data.getM()];
			int[] cellCount = new int[data.getC()];
			for (int k = 0; k < data.getC(); k++){
				for (int i = 0; i < data.getM(); i++){// Esto hace una multiplicación para revisar si: P(j) es subconjunto de C(k)
					tempPart[i] = solution.getMachine_cell()[i][k] * data.getA()[i][j];
				}
				cellCount[k] = IntStream.of(tempPart).sum();
			}// Extraer el índice de la posición con el número más grande.
			int maxIndex = 0;
			for (int i = 1; i < cellCount.length; i++){
				int newNumber = cellCount[i];
				if ((newNumber > cellCount[maxIndex])){
					maxIndex = i;
				}
			}// Puts un 1 in the new cell
			part_cell[j][maxIndex] = 1;
		}
		//toConsole("getRandomMove2", machine_cell, part_cell);
		return new Solution(machine_cell, part_cell);
	}
	
	public Solution addition(Solution solution, double value){

		int[][] machine_cell = new int[data.getM()][data.getC()];
		int[][] original_machine_cell = new int[data.getM()][data.getC()];
		int machines = solution.getMachine_cell().length;
		int cells = solution.getMachine_cell()[0].length;  for (int i = 0; i < machines; i++){
	        for (int k = 0; k < cells; k++){
	             original_machine_cell[i][k] = solution.getMachine_cell()[i][k];        
	         	} 
			}

		for (int i = 0; i < data.getM(); i++)   {
		        System.arraycopy(original_machine_cell[i], 0, machine_cell[i], 0, data.getC());
	 	}

	  int times = 0; 
		 while (times < value){     
		   // Random machine
		   Random rm = new Random();
		   int randomMachine = rm.nextInt(machines - 0) + 0;        // Clear row         for (int k = 0; k < cells; k++)         {          
		   machine_cell[randomMachine][k] = 0;       
		 }       // Random cell       
	 	 Random rc = new Random();       
		 int randomCell = rc.nextInt(cells - 0) + 0;
		  machine_cell[randomMachine][randomCell] = 1; 
	  	 times++;    }
	    int[][] part_cell = new int[data.getP()][data.getC()];
	    	for (int j = 0; j < data.getP(); j++){//Rellenar la matriz pieza×celda de "0"   
	  		    
	   		for (int k = 0; k < data.getC(); k++){     
	      part_cell[j][k] = 0;    
			}  
			  }
	   for (int j = 0; j < data.getP(); j++)   {      
	  int[] tempPart = new int[data.getM()];     
	   int[] cellCount = new int[data.getC()];  
	      for (int k = 0; k < data.getC(); k++)        {         
	   for (int i = 0; i < data.getM(); i++)            {      
	          // Esto hace una multiplicación para revisar si: P(j) es subconjunto de C(k)                 tempPart[i] = solution.getMachine_cell()[i][k] * data.getA()[i][j];   
	          }     
	       cellCount[k] = IntStream.of(tempPart).sum();        }

	        int maxIndex = 0;       
	 for (int i = 1; i < cellCount.length; i++)        {        
	   int newNumber = cellCount[i];           
	 if ((newNumber > cellCount[maxIndex]))            {            
	    maxIndex = i;          
	  }        }        // Puts un 1 in the new cell    
	     part_cell[j][maxIndex] = 1;    }    //toConsole("getRandomMove2", machine_cell, part_cell);      return new MCDPSolution(machine_cell, part_cell); }
	}
}
