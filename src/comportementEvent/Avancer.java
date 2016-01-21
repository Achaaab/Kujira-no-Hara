package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.Event.Direction;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
public class Avancer extends CommandeEvent {
	protected int direction;
	public int nombreDeCarreaux;
	public int ceQuiAEteFait = 0; //avanc�e en pixel, doit atteindre nombreDeCarreaux*32
	
	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit avancer
	 * @param nombreDeCarreaux distance parcourue
	 */
	public Avancer(final int direction, final int nombreDeCarreaux) {
		this.direction = direction;
		this.nombreDeCarreaux = nombreDeCarreaux;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("direction"), 
			  (int) parametres.get("nombreDeCarreaux") );
	}
	
	/**
	 * Obtenir la Direction du mouvement
	 * @return la Direction du mouvement
	 */
	public final int getDirection() {
		return direction;
	}
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public void reinitialiser() {
		ceQuiAEteFait = 0;
	}

	//TODO cette m�thode doit ajouter un Mouvement dans le D�placement forc�, rien d'autre
	//le LecteurMap prendra le relais
	@Override
	public int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Event event = this.page.event;
		if (ceQuiAEteFait >= nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU) {
			event.avance = false; //le mouvement est termin�
			return curseurActuel+1;
		}
		event.avance = true; //le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
	
	/**
	 * D�place l'Event pour son d�placement naturel ou pour un d�placement forc�.
	 * Vu qu'on utilise "deplacementActuel", un d�placement forc� devra �tre ins�r� artificiellement dans la liste.
	 * @param event qui doit avancer
	 * @param deplacement deplacement dont est issu le mouvement (soit d�placement naturel, soit d�placement forc�)
	 */
	public final void executerLeMouvement(final Event event, final Deplacement deplacement) {
		try {
			final int sens = this.getDirection();
			if ( event.mouvementPossible(sens) ) {
				event.avance = true;
				//d�placement :
				switch (sens) {
					case Direction.BAS : 
						event.y += event.vitesseActuelle; 
						break;
					case Direction.GAUCHE : 
						event.x -= event.vitesseActuelle; 
						break;
					case Direction.DROITE : 
						event.x += event.vitesseActuelle; 
						break;
					case Direction.HAUT : 
						event.y -= event.vitesseActuelle; 
						break;
				}
				this.ceQuiAEteFait += event.vitesseActuelle;
				//*ancien emplacement de l'animation*
				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU ) {
					if (deplacement.repeterLeDeplacement) {
						//on le r�initialise et on le met en bout de file
						this.reinitialiser();
						deplacement.mouvements.add(this);
					}
					//on passe au mouvement suivant
					deplacement.mouvements.remove(0);
				}
			} else {
				event.avance = false;
				if (deplacement.ignorerLesMouvementsImpossibles) {
					if (deplacement.repeterLeDeplacement) {
						//on le r�initialise et on le met en bout de file
						this.reinitialiser();
						deplacement.mouvements.add(this);
					}
					//on passe au mouvement suivant
					deplacement.mouvements.remove(0);
				}
			}
			
		} catch (NullPointerException e1) {
			//pas de mouvement pour cet �v�nement
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'�v�nement :");
			e.printStackTrace();
		}
	}
	
}
