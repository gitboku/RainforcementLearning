package first;

import java.io.PrintWriter;
import java.util.Random;
import java.util.Arrays;
import org.apache.commons.math3.random.MersenneTwister;
import cc.mallet.util.Randoms;
import umontreal.iro.lecuyer.probdist.*;


public class Method {

	int bandNum, episode, repeatNum, trueBand, acc;
	double[] aveTrue, varTrue, bandAve, bandVar, valueQ, resultCsv, rewardAve, resultEpi;
	String mode;

	Method(int episode, int repeatNum, double[] aveTrue, double[] varTrue, String mode){

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
		this.valueQ = new double[bandNum];
		this.resultCsv = new double[repeatNum];
		this.rewardAve =new double[repeatNum];
		this.resultEpi = new double[episode];
	}

	
	double[] overTakeTuneEXP2(double alpha){
		
		System.out.println("\nEXP2 going.");

		//[start] variable
		int maxInd =0;
		int epiNow=0;
		int correctNum =0;
		double beta = 1-alpha;
		double[] band = new double[bandNum];//prepared result of each band in advance
		double[] varEachRep = new double[repeatNum];
		double[] aveEachRep = new double[repeatNum];//use in E[X]^2
		
		ChiSquareDist csd = new ChiSquareDist(2);
		double[] chiarr = new double[repeatNum];
		for(int i=1; i<repeatNum; i++){
			csd.setN(2*i);
			chiarr[i] = csd.inverseF(beta);
		}//[end]

		Operate_Files of = new Operate_Files();
		PrintWriter pw = of.Creating("var.csv", false);//output file of varEachRep
		
		for (epiNow = 0; epiNow < episode; epiNow++) {

			int repNow=0;
			int[] bandRepNum = new int[bandNum];
			Arrays.fill(valueQ, 0);
			Arrays.fill(bandAve, 0);
			Arrays.fill(bandVar, 0);
			double[] bandSqur = new double[repeatNum];//use in E[X^2]
			
			int[] init_key = {(int) System.currentTimeMillis(),
					(int) Runtime.getRuntime().freeMemory()};
			MersenneTwister mrs = new MersenneTwister(init_key);

			//[start]Initialize
			for(int tmp2=0; tmp2 < bandNum; tmp2++){
				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
				}
				bandRepNum[tmp2]++;
				
				int bunsi =2 *bandRepNum[tmp2];
				
				double chi = chiarr[bandRepNum[tmp2]];
				
				//caluclate variance each trials
				if(epiNow ==0){
					aveEachRep[repNow] = band[trueBand] - band[tmp2];
					bandSqur[repNow] += Math.pow(band[trueBand] - band[tmp2], 2);
				}else{
					aveEachRep[repNow] += ((band[trueBand] - band[tmp2]) - aveEachRep[repNow])/ (epiNow+1);
					bandSqur[repNow] += Math.pow(band[trueBand] - band[tmp2], 2);
					varEachRep[repNow] = (bandSqur[repNow] / (epiNow+1))
							- Math.pow(aveEachRep[repNow], 2);
				}
				
				this.averageCalc(band, bandRepNum, tmp2);
				valueQ[tmp2] = (bunsi *bandAve[tmp2]) /chi;

				repNow++;
			}//[end]

			//select a band according to valueQ
			while (repNow < repeatNum) {

				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
					}

				maxInd = this.searchMax(valueQ);
				bandRepNum[maxInd]++;
				
				int bunsi =2 *bandRepNum[maxInd];
				
				double chi = chiarr[bandRepNum[maxInd]];
				
				//caluclate variance each trials
				if(epiNow ==0){
					aveEachRep[repNow] = band[trueBand] - band[maxInd];
					bandSqur[repNow] += Math.pow(band[trueBand] - band[maxInd], 2);//ƒ°[X^2]
				}else{
					aveEachRep[repNow] += ((band[trueBand] - band[maxInd]) - aveEachRep[repNow])/ (epiNow+1);
					bandSqur[repNow] += Math.pow(band[trueBand] - band[maxInd], 2);//ƒ°[X^2]
					varEachRep[repNow] = (bandSqur[repNow] / (epiNow+1))
							- Math.pow(aveEachRep[repNow], 2);
				}
				
				this.averageCalc(band, bandRepNum, maxInd);
				valueQ[maxInd] = (bunsi *bandAve[maxInd]) /chi;

				this.writeToResultCsv(band, maxInd, epiNow, repNow);
				
				repNow++;
			}

			if(trueBand == this.searchMaxInt(bandRepNum)){correctNum++;}
			
		}
		System.out.println("correctNum is "+ correctNum);

		for (int i = 0; i < repeatNum;i++){
			pw.println(i + "," +varEachRep[i]);//output variance of each trials
		}
		pw.close();
		
		return resultCsv;

	}

	
	double[] overTakeTuneEXP(double alpha){
		
		System.out.println("\nEXP going.");

		//[start] variable
		int maxInd =0;
		int epiNow=0;
		int correctNum =0;
		double beta = 1-alpha;
		double[] band = new double[bandNum];//prepared result of each band in advance
		
		ChiSquareDist csd = new ChiSquareDist(2);
		double[] chiarr = new double[repeatNum];
		for(int i=1; i<repeatNum; i++){
			csd.setN(2*i);
			chiarr[i] = csd.inverseF(beta);
		}//[end]

		for (epiNow = 0; epiNow < episode; epiNow++) {

			int repNow=0;
			int[] bandRepNum = new int[bandNum];
			Arrays.fill(valueQ, 0);
			Arrays.fill(bandAve, 0);
			Arrays.fill(bandVar, 0);
			
			int[] init_key = {(int) System.currentTimeMillis(),
					(int) Runtime.getRuntime().freeMemory()};
			MersenneTwister mrs = new MersenneTwister(init_key);

			//[start]Initialize
			for(int tmp2=0; tmp2 < bandNum; tmp2++){
				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
				}
				bandRepNum[tmp2]++;
				
				int bunsi =2 *bandRepNum[tmp2];
				
				double chi = chiarr[bandRepNum[tmp2]];
				
				this.averageCalc(band, bandRepNum, tmp2);
				valueQ[tmp2] = (bunsi *bandAve[tmp2]) /chi;

				repNow++;
			}//[end]

			//select a band according to valueQ
			while (repNow < repeatNum) {

				for(int r=0; r<bandNum; r++){
					band[r] = -aveTrue[r] *Math.log(1.0 -mrs.nextDouble());
					}

				maxInd = this.searchMax(valueQ);
				bandRepNum[maxInd]++;
				
				int bunsi =2 *bandRepNum[maxInd];
				double chi = chiarr[bandRepNum[maxInd]];
				
				this.averageCalc(band, bandRepNum, maxInd);
				valueQ[maxInd] = (bunsi *bandAve[maxInd]) /chi;

//				this.writeToResultCsv(band, maxInd, epiNow, repNow);
				resultCsv[repNow] = band[trueBand] - band[maxInd];//use if resultEpi

				repNow++;
			}
			
			for(int i=1; i<repeatNum; i++){
				resultEpi[epiNow] += resultCsv[i];//Sum of 20000 regrets for an EpiNow
			}

			if(trueBand == this.searchMaxInt(bandRepNum)){
				correctNum++;//
			}else{
//				System.out.println(resultEpi[epiNow]);
			}
		}
		System.out.println("correctNum is "+ correctNum);
		
//		return resultCsv;
		return resultEpi;

	}

	
	double[] overTakeTuneT(double valueP){
		
		System.out.println("\nTuneT going.");

		int maxInd =0;
		int epiNow=0;
		int correctNum =0;
		double[] band = new double[bandNum];//prepared result of each band in advance
		double[] sumSquare = new double[bandNum];
		Arrays.fill(resultCsv, 0);

		Random rnd = new Random();
		Randoms rnda = new Randoms();

		for (epiNow = 0; epiNow < episode; epiNow++) {

			int repNow=0;
			int[] bandRepNum =  new int[bandNum];
			Arrays.fill(valueQ, 0);
			Arrays.fill(bandAve, 0);
			Arrays.fill(bandVar, 0);

			int[] init_key = {(int) System.currentTimeMillis(),
					(int) Runtime.getRuntime().freeMemory()};
			MersenneTwister mrs = new MersenneTwister(init_key);

			//create default of valueQ 
			for(int tmp=0; tmp<2; tmp++){

				for(int tmp2=0; tmp2 < bandNum; tmp2++){
					for(int r=0; r<bandNum; r++){
						band[r] = mrs.nextGaussian()*varTrue[r] +aveTrue[r];
//						band[r] = (rnd.nextGaussian() * Math.sqrt(varTrue[r]) + aveTrue[r]);
						sumSquare[r] = Math.pow(band[r], 2);
					}
					bandRepNum[tmp2]++;

					if(tmp==0){
						bandAve[tmp2] = band[tmp2];

					}else{

						StudentDist sDist = new StudentDist(bandRepNum[tmp2]-1);
						double tau = sDist.inverseF(valueP);

						bandVar[tmp2] = bandVar[tmp2] * (bandRepNum[tmp2]-1) / (bandRepNum[tmp2])
								+
								Math.pow(band[tmp2] - bandAve[tmp2], 2) / (bandRepNum[tmp2]+1);
						bandAve[tmp2] += (band[tmp2] - bandAve[tmp2]) / (bandRepNum[tmp2]+1);						
						valueQ[tmp2] = bandAve[tmp2] + 
								tau * (Math.sqrt(bandVar[tmp2] / (bandRepNum[tmp2]+1)));
					}

					this.writeToResultCsv(band, tmp2, epiNow, repNow);

					repNow++;
				}
			}

			//select band according to valueQ
			while (repNow < repeatNum) {

				for(int r=0; r<bandNum; r++){
					band[r] = mrs.nextGaussian()*varTrue[r] +aveTrue[r];
//					band[r] = (rnd.nextGaussian() * Math.sqrt(varTrue[r]) + aveTrue[r]);
					sumSquare[r] += Math.pow(band[r], 2);
					}


				maxInd = this.searchMax(valueQ);
				bandRepNum[maxInd]++;

				StudentDist sDist = new StudentDist(bandRepNum[maxInd]-1);
				double tau = sDist.inverseF(valueP);

				this.varianceCalc(sumSquare, band, bandRepNum, maxInd);
				this.averageCalc(band, bandRepNum, maxInd);
				valueQ[maxInd] = bandAve[maxInd] + 
						tau * (Math.sqrt(bandVar[maxInd] / bandRepNum[maxInd]));


				this.writeToResultCsv(band, maxInd, epiNow, repNow);

				repNow++;
			}

			correctNum =this.correctNumCounter(correctNum, bandRepNum);
		}
		System.out.println("correctNum is "+ correctNum);

		//		this.printCsv(fileName, resultCsv);//writing csv
		return resultCsv;

	}


	double[] overTaking(double valueP){
		
		System.out.println("\noverTaking going.");

		int maxInd =0;
		int epiNow=0;
		int correctNum =0;
		double err = 3;//Math.sqrt(2)*Erf.erf(2* valueP -1);
		Arrays.fill(resultCsv, 0);
		MyDist md = new MyDist();
		Operate_Files of = new Operate_Files();
		PrintWriter pw = of.Creating("katei.csv", false);

		for (epiNow = 0; epiNow < episode; epiNow++) {

			int repNow=0;
			int[] bandRepNum =  new int[bandNum];
			double sumSquare[] = new double[bandNum];
			Arrays.fill(bandAve, 0);
			Arrays.fill(bandVar, 0);
			Arrays.fill(valueQ, 0);

			//initialization
			for(int tmp=0; tmp<2; tmp++){	

				for(int tmp2=0; tmp2 < bandNum; tmp2++){
					
					double[] band = new double[bandNum];
					for(int r=0; r<bandNum; r++){
//						band[r] = rnda.nextGaussian(aveTrue[r], varTrue[r]);
//						band[r] = md.gaussian(aveTrue[r], varTrue[r]);
						band[r]= md.expRnd(aveTrue[r]);
						sumSquare[r] = Math.pow(band[r], 2);
						}

					bandRepNum[tmp2]++;

					this.averageCalc(band, bandRepNum, tmp2);
					this.varianceCalc(sumSquare, band, bandRepNum, tmp2);
					valueQ[tmp2] = bandAve[tmp2]
							+err * (Math.sqrt(bandVar[tmp2] / bandRepNum[tmp2]));

					resultCsv[repNow] =0;

					repNow++;
				}
			}
//			System.out.printf("%4f \t: %4f \n",valueQ[0], valueQ[1]);

			//select band according to valueQ
			while (repNow < repeatNum) {
				
				double[] band = new double[bandNum];
				for(int r=0; r<bandNum; r++){
//					band[r] = rnda.nextGaussian(aveTrue[r], varTrue[r]);
//					band[r] = md.gaussian(aveTrue[r], varTrue[r]);
					band[r]= md.expRnd(aveTrue[r]);
					sumSquare[r] += Math.pow(band[r], 2);
				}
				
				maxInd = this.searchMax(valueQ);
				bandRepNum[maxInd]++;
				
				pw.println(maxInd+ ","+ valueQ[0]+","+ valueQ[1]);
				
				this.averageCalc(band, bandRepNum, maxInd);
				this.varianceCalc(sumSquare, band, bandRepNum, maxInd);
				valueQ[maxInd] = bandAve[maxInd]
						+err * Math.sqrt(bandVar[maxInd] / bandRepNum[maxInd]);

				this.writeToResultCsv(band, maxInd, epiNow, repNow);
				
				repNow++;
			}

			correctNum =this.correctNumCounter(correctNum, bandRepNum);
		}
		System.out.println("correctNum is "+ correctNum);

		return resultCsv;
	}
	
	
	void writeToResultCsv(double[] band, int index, int epiNow, int repNow){
		switch(mode){

		case "regret":
			//resultCsv = average of regret(true - select)
			resultCsv[repNow] += ((band[trueBand] - band[index]) - resultCsv[repNow])
			/ (epiNow+1);
//			resultCsv[repNow] = band[trueBand] - band[index];

			break;

		case"accRate":
			acc = index == trueBand ? 1:0;
			resultCsv[repNow] += (acc - resultCsv[repNow])/ (epiNow+1);

			break;
			
		case"rewardVar":
			if(epiNow ==0){
				resultCsv[repNow]=0;
			}else{
				resultCsv[repNow]=(resultCsv[index]*(epiNow-1)/epiNow)
						+Math.pow(band[index] -rewardAve[index], 2)/(epiNow+1);
			}
			rewardAve[repNow]+=(band[index]-rewardAve[index]) /(epiNow+1);
			
			break;
			
		case"rewardAve":
			resultCsv[repNow]+=(band[index]-resultCsv[index]) /(epiNow+1);
			
			break;
		}
	}

	
	void averageCalc(double[] band, int[] bandRepNum, int index){
		bandAve[index] += (band[index] - bandAve[index]) / bandRepNum[index];
	}
	
	//use this before average
	void varianceCalc(double[] square, double[] band, int[] bandRepNum, int index){
		if(bandRepNum[index] <=1){
			bandVar[index] =0;
		}else{
//			bandVar[index] = bandVar[index]*(bandRepNum[index] -2)/(bandRepNum[index] -1)
//			+ Math.pow(band[index] -bandAve[index], 2) / bandRepNum[index];
			bandVar[index] = square[index] /(bandRepNum[index]-1) -Math.pow(bandAve[index], 2);
		
		}
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
	
	int correctNumCounter(int correctNum, int[] bandRepNum){
		if(trueBand == this.searchMaxInt(bandRepNum)){				
			correctNum++;//
		}
		
		return correctNum;
	}
}

