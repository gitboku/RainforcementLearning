package first;

import java.io.PrintWriter;
import umontreal.iro.lecuyer.probdist.*;

public class action {

	static int repeatNum = 20000;

	static String mode = "regret";
//		static String mode = "accRate";
	//	static String mode="rewardVar";
	//	static String mode="rewardAve";

	public static void main(String[] args) {
		double aveTrue[][] = {{8.0, 5.0},
				{0.3, 0.4},
				{0.5, 0.6},
				{0.7, 0.8}};
		double varTrue[] = { 1.0, 1.0};

		int episode = 10000;

		System.out.println("mode :" + mode);
		// -------------------------------------------------------------
		for(int i=0; i<1; i++){
			Method mt = new Method(episode, repeatNum, aveTrue[i], varTrue, mode);
			UCB ucb = new UCB(episode, repeatNum, aveTrue[i], varTrue, mode);

			String ucbs ="ucbReg2.csv";
			String ovtk ="test.csv";

//			printCsv(ucbs, ucb.ucb1());
			//		printCsv("overTake", mt.overTaking(0.99865));
			//		printCsv("T_acc.csv", mt.overTakeTuneT(0.99865));
//			printCsv(ovtk, mt.overTakeTuneEXP(0.99865));
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
