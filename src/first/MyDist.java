package first;

import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;

import umontreal.iro.lecuyer.probdist.GammaDist;

public class MyDist{


	int[] init_key = {(int) System.currentTimeMillis(), (int) Runtime.getRuntime().freeMemory()};
	MersenneTwister mrs = new MersenneTwister(init_key);
	Sfmt sfmt = new Sfmt(init_key);
	
	//gausian random number
	//box-mullar method
	double gaussian(double mean, double variable){
		double unit1, unit2;
		double coordinateX =Math.random();
		double coordinateY =Math.random();
		
		unit1 = Math.sqrt(-2 *Math.log(coordinateX));
		unit2 = Math.cos(2 *Math.PI *coordinateY);
		
		return mean +variable *unit1 *unit2;
	}
	
	
	//Quantile function
	//dof    <- degree of freedom
	//lambda <- mean
	double gammaRnd(double dof, double lambda){
		GammaDist gd = new GammaDist(dof, lambda);
		
		return gd.inverseF(sfmt.NextUnif());
//		return rnd.NextUnif();
	}
	
	
	double expRnd(double lambda){
		
//		Sfmt sf = new Sfmt((int)(Math.random() *10));
		Random rnd = new Random();
		
		//http://ebsa.ism.ac.jp/ebooks/sites/default/files/ebook/1223/pdf/ch04-01.pdf
		return -lambda *Math.log(1.0 -rnd.nextDouble());
	}
	
	
	double poisson(double mean){
		//http://ebsa.ism.ac.jp/ebooks/sites/default/files/ebook/1223/pdf/ch04-01.pdf
		double xp = Math.random();
		int k=0;
		
		
//		Math.exp(-mean)*(Math.pow(mean, k) / factorial(k));
		
		return k;
	}
	
	//http://www.rsch.tuis.ac.jp/~ohmi/software-intro/recursive.html
	static int factorial(int n){//
		if (n == 0)
			return 1;
		else // in case of n > 0
			return n * factorial(n - 1);
	}	
}


