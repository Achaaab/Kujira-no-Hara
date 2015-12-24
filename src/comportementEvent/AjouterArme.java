package comportementEvent;

import java.util.ArrayList;

import main.Partie;
import utilitaire.Parametre;

public class AjouterArme extends CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur sp�cifique
	 */
	public AjouterArme(int idArme){
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterArme(ArrayList<Parametre> parametres){
		this( (Integer) trouverParametre("idArme",parametres) );
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(!Partie.idArmesPossedees.contains(idArme)){
			Partie.idArmesPossedees.add(idArme);
		}
		return curseurActuel+1;
	}

}
