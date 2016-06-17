package first;

import java.io.PrintWriter;
import umontreal.iro.lecuyer.probdist.*;

public class action {

	static int repeatNum = 20000;

	static String mode = "regret";
//		static String mode = "accRate";//You output "regret each agent" here

	public static void main(String[] args) {
		double aveTrue[][] = {{0.1, 0.2},
								{0.2, 0.3},
								{0.3, 0.4},
								{0.4, 0.5}
								};
		double varTrue[] = { 1.0, 1.0};

		int episode = 10000;


		System.out.println("mode :" + mode);
		// -------------------------------------------------------------
		for(int i=0; i<4; i++){
			String ucbs ="UCB"+i+".csv";
			String ovtk ="ovt"+i+".csv";
			
			Method mt = new Method(episode, repeatNum, aveTrue[i], varTrue, mode);
			UCB ucb = new UCB(episode, repeatNum, aveTrue[i], varTrue, mode);

			//When you output "regret each agent", change the CUB.java
			printCsv(ucbs, ucb.ucb1());
			//		printCsv("overTake", mt.overTaking(0.99865));
			//		printCsv("T_acc.csv", mt.overTakeTuneT(0.99865));
//			printCsv(ovtk, mt.overTakeTuneEXP(0.99865));//write sum of regrets each agent.
			printCsv(ovtk, mt.overTakeTuneEXP2(0.99865));

		}
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

}
