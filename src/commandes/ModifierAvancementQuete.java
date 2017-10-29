package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import jeu.Quete.AvancementQuete;
import main.Commande;
import main.Fenetre;

public class ModifierAvancementQuete extends Commande {

	private AvancementQuete avancement;
	private int numeroQuete;
	
	/**
	 * Constructeur explicite
	 * @param arme identifiant de l'Arme � ajouter : son num�ro ou son nom
	 */
	public ModifierAvancementQuete(final int numeroQuete, final AvancementQuete avancement) {
		this.numeroQuete = numeroQuete;
		this.avancement = avancement;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierAvancementQuete(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
			  AvancementQuete.getEtat((String) parametres.get("avancement")));
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		partieActuelle.avancementDesQuetes[this.numeroQuete] = this.avancement;
		
		return curseurActuel+1;
	}

}
