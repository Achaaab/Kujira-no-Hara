package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import jeu.Quete.AvancementQuete;
import main.Commande;

/**
 * Modifier l'�tat d'avancement de la Qu�te.
 */
public class ModifierAvancementQuete extends Commande implements CommandeEvent {

	private AvancementQuete avancement;
	private int numeroQuete;
	
	/**
	 * Constructeur explicite
	 * @param numeroQuete identifiant de la Qu�te � faire �voluer
	 * @param avancement nouvel �tat de la Qu�te
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
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = getPartieActuelle();
		partieActuelle.avancementDesQuetes[this.numeroQuete] = this.avancement;
		
		return curseurActuel+1;
	}

}
