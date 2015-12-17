package comportementEvent;

import java.util.ArrayList;

import utilitaire.Parametre;

public class Attendre extends CommandeEvent {
	public int nombreDeFrames; //nombre de frames qu'il faut attendre
	public int ceQuiAEteFait; //nombre de frames qu'on a d�j� attendu
	
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
	public Attendre(ArrayList<Parametre> parametres){
		this( (Integer) trouverParametre("nombreDeFrames",parametres) );
	}
	
	public void reinitialiser(){
		ceQuiAEteFait = 0;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(ceQuiAEteFait>=nombreDeFrames){
			reinitialiser();
			return curseurActuel+1;
		}else{
			ceQuiAEteFait++;
			return curseurActuel;
		}
	}

}
