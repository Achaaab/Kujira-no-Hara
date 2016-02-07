package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.PageEvent;

/**
 * Equiper le Heros avec une Arme qu'il poss�de
 */
public class EquiperArme implements CommandeEvent {
	private PageEvent page;
	
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme � �quiper
	 */
	public EquiperArme(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EquiperArme(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		Fenetre.getPartieActuelle().equiperArme(this.idArme);
		return curseurActuel+1;
	}
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
