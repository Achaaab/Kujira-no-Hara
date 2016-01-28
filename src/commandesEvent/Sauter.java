package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.LecteurMap;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter extends CommandeEvent implements Mouvement {
	//constantes
	private static final int NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE = 8;
	
	private Integer idEventADeplacer; //Integer car cl� d'une HashMap
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	private int x;
	private int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	public int etapes;
	public int etapesFaites;
	private int direction;
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer identifiant de l'Event qui subira le Mouvement
	 * @param x nombre de cases de d�placement en horizontal
	 * @param y nombre de cases de d�placement en vertical
	 */
	public Sauter(final Integer idEventADeplacer, final int x, final int y) {
		this.idEventADeplacer = idEventADeplacer;
		this.x = x;
		this.y = y;
		if (x>y) {
			//haut droite
			if (y>=-x) {
				//bas droite
				this.direction = Event.Direction.DROITE;
			} else {
				//haut gauche
				this.direction = Event.Direction.HAUT;
			}
		} else {
			//bas gauche
			if (y>-x) {
				//bas droite
				this.direction = Event.Direction.BAS;
			} else {
				//haut gauche
				this.direction = Event.Direction.GAUCHE;
			}
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idEventADeplacer"),
			  (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 */
	public void reinitialiser() {
		//TODO
	}

	//TODO cette m�thode doit ajouter un Mouvement dans le D�placement forc�, rien d'autre
	//le LecteurMap prendra le relais
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Event event = this.page.event.map.heros;
		if (!event.saute) {
			//le mouvement n'a pas commenc�
			event.saute = true;
			this.xEventAvantSaut = event.x;
			this.yEventAvantSaut = event.y;
			this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
			this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
			this.etapes = NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE + ((Double) Math.sqrt(x*x+y+y)).intValue();
			this.etapesFaites = 0;
			this.etapes = 30; //TODO
		}
		
		if (etapesFaites>=etapes) {
			event.saute = false; //le mouvement est termin�
			return curseurActuel+1;
		}
		
		//le mouvement est en cours
		event.direction = this.direction;
		event.x = this.xEventApresSaut;
		event.y = this.yEventApresSaut;
		System.err.println("Sauter.executer() : deplacer()");
		event.deplacer();
		this.etapesFaites++;
		return curseurActuel;
	}
	
	/**
	 * D�place l'Event pour son d�placement naturel ou pour un d�placement forc�.
	 * Vu qu'on utilise "deplacementActuel", un d�placement forc� devra �tre ins�r� artificiellement dans la liste.
	 * @param deplacement deplacement dont est issu le mouvement (soit d�placement naturel, soit d�placement forc�)
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		final Event event = this.getEventADeplacer();
		if ( this.mouvementPossible() ) {
			event.saute = true;
			//d�placement :
			final double t = (double) etapesFaites /(double) etapes;
			final int x0 = xEventAvantSaut;
			final int y0 = yEventAvantSaut;
			final int xf = xEventApresSaut;
			final int yf = yEventApresSaut;
			final double xDroite = (1-t)*x0+t*xf;
			final double yDroite = (1-t)*y0+t*yf;
			final double yParabole = -(xDroite-x0)*(xDroite-xf) / (x0+xf); //on divise par (x0+xf) pour avoir une d�riv�e de 1 au d�part
			event.coordonneeApparenteXLorsDuSaut = (int) (xDroite * Fenetre.TAILLE_D_UN_CARREAU);
			event.coordonneeApparenteYLorsDuSaut = (int) ((yParabole+yDroite) * Fenetre.TAILLE_D_UN_CARREAU);
			System.out.println(event.coordonneeApparenteXLorsDuSaut);
			System.out.println(event.coordonneeApparenteYLorsDuSaut);
			
			//quelle sera la commande suivante ?
			if (etapesFaites>=etapes) {
				if (deplacement.repeterLeDeplacement) {
					//on le r�initialise et on le met en bout de file
					this.reinitialiser();
					deplacement.mouvements.add(this);
				}
				//on passe au mouvement suivant
				deplacement.mouvements.remove(0);
			}
		} else {
			event.saute = false;
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
	}
	
	/**
	 * Le Mouvement est-il possible pour cet Event ?
	 * @return true si le Mouvement est possible, false sinon
	 */
	public final boolean mouvementPossible() {
		//TODO � faire
		return true;
	}
	
	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * @return Event qui va �tre d�plac�
	 */
	public final Event getEventADeplacer() {
		return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
	}
	
}
