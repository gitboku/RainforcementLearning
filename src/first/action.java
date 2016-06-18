package first;

import java.io.PrintWriter;

import org.apache.commons.math3.random.MersenneTwister;

import umontreal.iro.lecuyer.probdist.*;

public class action {

	static int repeatNum = 20000;

	static String mode = "regret";
//		static String mode = "accRate";//You output "regret each agent" here

	public static void main(String[] args) {
		double aveTrue[][] = {{1.1, 1.2, 1.3, 1.4, 1.5},
								{0.2, 0.3},
								{0.3, 0.4},
								{0.4, 0.5}
								};
		double varTrue[] = { 1.0, 1.0};

		int episode = 10000;


		System.out.println("mode :" + mode);
		// -------------------------------------------------------------
		for(int i=0; i<1; i++){
			String ucbs ="UCB"+i+".csv";
			String ovtk ="ovTest.csv";
			
			Method mt = new Method(episode, repeatNum, aveTrue[i], varTrue, mode);
			UCB ucb = new UCB(episode, repeatNum, aveTrue[i], varTrue, mode);

			//When you output "regret each agent", change the CUB.java
//			printCsv(ucbs, ucb.ucb1());
			//		printCsv("overTake", mt.overTaking(0.99865));
			//		printCsv("T_acc.csv", mt.overTakeTuneT(0.99865));
//			printCsv(ovtk, mt.overTakeTuneEXP(0.99865));//write sum of regrets each agent.
			printCsv(ovtk, mt.overTakeTuneEXP2(0.99865));
		}
		// -------------------------------------------------------------
		
//		int[] init_key = {(int) System.currentTimeMillis(), (int) Runtime.getRuntime().freeMemory()};
//		MersenneTwister mrs = new MersenneTwister(init_key);
//		
//		double[] bandit = {1.3, 2.2, 1.5, 1.1};
//		double[] prob	= new double[4];
//		int counter[] = {0,0,0,0};
//		
//		for(int k=0; k<1000; k++){
//			for(int i=0; i<4; i++){
//				prob[i] = mrs.nextDouble()+ bandit[i];
//			}
//			int x = searchMax(prob);
//			counter[x]++;
//		}
//		System.out.printf("%d : %d : %d : %d", counter[0] , counter[1] , counter[2] , counter[3] );
	}

	// function of writing result to csvFile
	static void printCsv(String fileName, double[] resultCsv) {

		Operate_Files of = new Operate_Files();
		PrintWriter pw = of.Creating(fileName, false);

		int len = resultCsv.length;
		if(mode == "regret"){
			for(int i=1; i<len; i++){
				resultCsv[i] +=resultCsv[i-1];
			}
		}
		
		for (int i = 0; i < len;i++){
			pw.println(i + "," + resultCsv[i]);
		}

		System.out.println("last: " + resultCsv[resultCsv.length - 1]);
		pw.close();
	}

	static int searchMax(double[] arr){
		int maxInd =0;
		int len = arr.length;
		for(int tmp=0; tmp < len; tmp++){
			if(arr[maxInd] <= arr[tmp]){
				maxInd =tmp;
			}
		}
		return maxInd;
	}

}
