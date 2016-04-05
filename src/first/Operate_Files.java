package first;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Operate_Files {

	PrintWriter Creating(String name, boolean flag){
		
		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new FileWriter(new File(name), flag));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintWriter pw = new PrintWriter(bw);

		return pw;
	}

	
	
	String[] CSVtoMatrix(String CSVname){

		ArrayList<String> inputList = new ArrayList<String>();

		try {
			File csv = new File(CSVname);
			BufferedReader br = new BufferedReader(new FileReader(csv));

			while (br.readLine() != null){
				inputList.add(br.readLine());
			}
			
			br.close();

		} catch (FileNotFoundException e) {
			System.out.println("Miss1 !");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Miss2 !");
			e.printStackTrace();
		}
		
		String[] stringData = new String[inputList.size()];
		
		for(int i=0; i<inputList.size(); i++){
			stringData[i] = inputList.get(i);
			}
		/*
		for(int k=0; k<stringData.length; k++)
			System.out.println(stringData[k]);
*/
		return stringData;
	}



	double[][] DivideMatrix(String[] stringData, String divideSymbol, int wide){

		String[][] dividedData = new String[stringData.length][wide];

		for(int i=0; i<stringData.length; i++){
			dividedData[i] = stringData[i].split(divideSymbol);			
		}
		
		double[][] doubleData = new double[stringData.length][wide];
		
		for (int i=0; i<doubleData.length; i++){
			for(int k=0; k<doubleData[i].length; k++){
				doubleData[i][k] = Double.parseDouble(dividedData[i][k]);
			}
		}
/*
		for(int i=0; i<dividedData.length; i++){
			for(int k=0; k<dividedData[i].length; k++){
				System.out.println(dividedData[i][k]);
			}
		}*/
		return doubleData;
	}
	
	
	
	double[][] CSVtoDividedMatrix(String CSVname, String divideSymbol, int matrixWide){	
		System.out.println(CSVname.length());
		return this.DivideMatrix(this.CSVtoMatrix(CSVname), divideSymbol, matrixWide);
	}
	
}
