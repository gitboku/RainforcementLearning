package first;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.special.Erf;

import umontreal.iro.lecuyer.probdist.ChiSquareDist;
import umontreal.iro.lecuyer.probdist.StudentDist;
import cc.mallet.util.Randoms;

public class UCB {

	int bandNum, episode, repeatNum, trueBand, acc;
	double[] aveTrue, varTrue, bandAve, bandVar, valueQ, resultCsv, rewardAve, resultEpi;
	String mode;

	UCB(int episode, int repeatNum, double[] aveTrue, double[] varTrue, String mode){
		this.bandNum = aveTrue.length;
		this.episode = episode;
		this.repeatNum = repeatNum;
		this.aveTrue = aveTrue;
		this.varTrue = varTrue;
		this.trueBand = this.searchMax(aveTrue);
		this.mode = mode;
		this.acc=0;
		this.bandAve = new double[bandNum];
		this.bandVar = new double[bandNum];
		this.resultCsv = new double[repeatNum];
		this.resultEpi = new double[episode];
	}
	
	
	double[] ucb1(){
		
		System.out.println("\nUCB1 going.");

		int maxInd =0;
		int epiNow=0;
		int correctNum=0;
		Arrays.fill(resultCsv, 0);

		for (epiNow = 0; epiNow < episode; epiNow++) {
			int repNow=0;
			int[] bandRepNum =  new int[bandNum];
			double valueQ[] = new double[bandNum];
			Arrays.fill(bandAve, 0);
			Arrays.fill(bandVar, 0);
			
			int[] init_key = {(int) System.currentTimeMillis(), (int) Runtime.getRuntime().freeMemory()};
			MersenneTwister mrs = new MersenneTwister(init_key);//Seed of random variable

			//Initialize
			for(int tmp2=0; tmp2 < bandNum; tmp2++){
				double[] band = new double[bandNum];

				//prepare bandits
				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
					}

				bandRepNum[tmp2]++;
				
				bandAve[tmp2] = band[tmp2];
				for(int i=0; i<bandNum; i++){
					valueQ[i] = bandAve[i] + Math.sqrt(2 * Math.log(repNow+1) / bandRepNum[i]);
				}
				
				repNow++;
			}

			//select a band according to valueQ
			while (repNow < repeatNum) {				
				double[] band = new double[bandNum];//prepare bandits
				
				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
					}

				maxInd = this.searchMax(valueQ);//select a bandit
				bandRepNum[maxInd]++;
				this.averageCalc(band, bandRepNum, maxInd);
				
				for(int i=0; i<bandNum; i++){
					valueQ[i] = bandAve[i] + Math.sqrt(2 * Math.log(repNow+1) / bandRepNum[i]);
				}

				this.writeToResultCsv(band, maxInd, epiNow, repNow);
//				resultCsv[repNow] = band[trueBand] - band[maxInd];//use if resultEpi

				repNow++;
			}
			
//			for(int i=1; i<repeatNum; i++){
//				resultEpi[epiNow] += resultCsv[i];//Sum of 20000 regrets for an EpiNow
//			}

			if(trueBand == this.searchMaxInt(bandRepNum)){correctNum++;}
			
		}
		System.out.println("correctNum of ucb1 is "+ correctNum);
		
		return resultCsv;
//		return resultEpi;

	}
	
	
	void writeToResultCsv(double[] band, int index, int epiNow, int repNow){
		switch(mode){

		case "regret":
			//resultCsv = regret(true - select)
			resultCsv[repNow] += ((band[trueBand] - band[index]) - resultCsv[repNow]) / (epiNow+1);

			break;

		case"accRate":
			acc = index == trueBand ? 1:0;
			resultCsv[repNow] += (acc - resultCsv[repNow])/ (epiNow+1);

			break;
		}
	}
	
	void averageCalc(double[] band, int[] bandRepNum, int index){
		bandAve[index] += (band[index] - bandAve[index]) / bandRepNum[index];
	}

	int searchMax(double[] arr){
		int maxInd =0;
		int len = arr.length;
		for(int tmp=0; tmp < len; tmp++){
			if(arr[maxInd] <= arr[tmp]){
				maxInd =tmp;
			}
		}
		return maxInd;
	}

	int searchMaxInt(int[] arr){
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

