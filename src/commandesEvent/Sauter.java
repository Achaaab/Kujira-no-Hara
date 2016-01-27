package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter extends CommandeEvent implements Mouvement {
	//constantes
	private static final int NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE = 8;
	
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	private int x;
	private int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	public int etapes;
	public int etapesFaites;
	
	/**
	 * Constructeur explicite
	 * @param x nombre de cases de d�placement en horizontal
	 * @param y nombre de cases de d�placement en vertical
	 */
	public Sauter(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("x"), 
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
		final Event event = this.page.event;
		if (!event.saute) {
			//le mouvement n'a pas commenc�
			event.saute = true;
			this.xEventAvantSaut = event.x;
			this.yEventAvantSaut = event.y;
			this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
			this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
			this.etapes = NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE + ((Double) Math.sqrt(x*x+y+y)).intValue();
			this.etapesFaites = 0;
		}
		
		if (etapesFaites>=etapes) {
			event.saute = false; //le mouvement est termin�
			return curseurActuel+1;
		}
		
		//le mouvement est en cours
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
		/*
		try {
			if ( this.mouvementPossible(event) ) {
				event.saute = true;
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
		*/
	}
	
	public boolean mouvementPossible(Event e){
		//TODO � faire
		return true;
	}
	
}
