package commandes;

import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.Heros;
import map.Hitbox;
import map.Event.Direction;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
public class Avancer extends Mouvement {	
	protected int direction;
	public int ceQuiAEteFait; //avanc�e en pixel, doit atteindre nombreDeCarreaux*32
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer identifiant de l'Event qui subira le Mouvement
	 * @param direction dans laquelle l'Event doit avancer
	 * @param nombreDePixels distance parcourue
	 */
	public Avancer(final Integer idEventADeplacer, final int direction, final int nombreDePixels) {
		this.idEventADeplacer = idEventADeplacer;
		this.direction = direction;
		this.etapes = nombreDePixels;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("idEventADeplacer") ? (int) parametres.get("idEventADeplacer") : null,
			  (int) parametres.get("direction"), 
			  (int) parametres.get("nombreDeCarreaux")*Fenetre.TAILLE_D_UN_CARREAU );
	}
	
	/**
	 * Obtenir la Direction du mouvement
	 * @return la Direction du mouvement
	 */
	public final int getDirection() {
		return direction;
	}
	
	/**
	 * D�place l'Event pour son d�placement naturel ou pour un d�placement forc�.
	 * Vu qu'on utilise "deplacementActuel", un d�placement forc� devra �tre ins�r� artificiellement dans la liste.
	 * @param deplacement deplacement dont est issu le mouvement (soit d�placement naturel, soit d�placement forc�)
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		final int sens = this.getDirection();
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
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		final Event event = this.getEventADeplacer();
		final int sens = this.getDirection();
		
		//si c'est le H�ros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-m�me traversable, il peut faire son mouvement
		if (event.traversableActuel) {
			return true;
		}
		
		boolean reponse = true;
		int xAInspecter = event.x; //pour le d�cor
		int yAInspecter = event.y;
		int xAInspecter2 = event.x; //pour le d�cor, deuxi�me case � v�rifier si entre deux cases
		int yAInspecter2 = event.y;
		int xAInspecter3 = event.x; //pour les events
		int yAInspecter3 = event.y;
		switch(sens) {
		case Event.Direction.BAS : 
			yAInspecter += event.hauteurHitbox;   
			yAInspecter2 += event.hauteurHitbox;   
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= event.vitesseActuelle; 
			xAInspecter2 -= event.vitesseActuelle; 
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 -= event.vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += event.largeurHitbox;   
			xAInspecter2 += event.largeurHitbox;   
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= event.vitesseActuelle; 
			yAInspecter2 -= event.vitesseActuelle; 
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 -= event.vitesseActuelle; 
			break;
		default : 
			break;
		}
		try {
			//si rencontre avec un �l�ment de d�cor non passable -> false
			if (!event.map.casePassable[xAInspecter/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.BAS||sens==Direction.HAUT) && ((event.x+event.largeurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.x/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.GAUCHE||sens==Direction.DROITE) && ((event.y+event.hauteurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.y/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			//voil�
			
			//si rencontre avec un autre �v�nement non traversable -> false
			for (Event autreEvent : event.map.events) {
				if (event.numero != autreEvent.numero 
					&& !autreEvent.traversableActuel
					&& Hitbox.lesHitboxesSeChevauchent(xAInspecter3, yAInspecter3, event.largeurHitbox, event.hauteurHitbox, autreEvent.x, autreEvent.y, autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
				) {
					return false;
				}
			}
		} catch (Exception e) {
			//on sort de la map !
			e.printStackTrace();
			reponse = true;
		}
		return reponse;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		event.avance = false;
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		event.avance = false;
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}
	
}
