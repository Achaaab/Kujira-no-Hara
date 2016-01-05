package comportementEvent;

import java.util.HashMap;

public class AvancerAleatoirement extends Avancer{	
	
	/**
	 * Constructeur sp�cifique
	 */
	public AvancerAleatoirement(){
		super(rand.nextInt(4), 1);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AvancerAleatoirement(HashMap<String,Object> parametres){
		this();
	}
	
	@Override
	public void reinitialiser(){
		super.reinitialiser();
		int nouvelleDirection = rand.nextInt(4);
		if( (direction==0 && nouvelleDirection!=3) 
				|| (direction==1 && nouvelleDirection!=2) 
				|| (direction==2 && nouvelleDirection!=1) 
				|| (direction==3 && nouvelleDirection!=0) 
		){
			direction = nouvelleDirection;
		}
		
	}
}
