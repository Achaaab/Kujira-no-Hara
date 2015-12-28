package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Attendre extends CommandeEvent {
	private int nombreDeFrames; //nombre de frames qu'il faut attendre
	private int ceQuiAEteFait; //nombre de frames qu'on a d�j� attendu
	
	/**
	 * Constructeur sp�cifique
	 */
	public Attendre(int nombreDeFrames){
		this.ceQuiAEteFait = 0;
		this.nombreDeFrames = nombreDeFrames;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Attendre(HashMap<String, Object> parametres){
		this( (Integer) parametres.get("nombreDeFrames") );
	}
	
	public void reinitialiser(){
		this.ceQuiAEteFait = 0;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(this.ceQuiAEteFait>=this.nombreDeFrames){
			reinitialiser();
			return curseurActuel+1;
		}else{
			this.ceQuiAEteFait++;
			return curseurActuel;
		}
	}

}
