package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import comportementEvent.Avancer;
import comportementEvent.CommandeEvent;
import conditions.Condition;
import main.Fenetre;

/**
 * Un Event est un �l�ment actif du d�cor, voire interactif.
 * Ses �ventuels comportements sont repr�sent�s par une liste de Pages.
 */
public class Event implements Comparable<Event> {
	//constantes
	public static final int VITESSE_PAR_DEFAUT = 4;
	public static final int FREQUENCE_PAR_DEFAUT = 4;
	private static final int HAUTEUR_HITBOX_PAR_DEFAUT = 32;
	private static final int LARGEUR_HITBOX_PAR_DEFAUT = 32;
	
	/** Map � laquelle cet Event appartient */
	public Map map;
	/** nom de l'Event */
	public String nom;
	/** num�ro de l'Event sur la Map */
	public int numero;
	/** distance entre le bord gauche de la map et le bord gauche de la Hitbox de l'Event */
	public int x; //en pixels
	/** distance entre le bord haut de la map et le bord haut de la Hitbox de l'Event */
	public int y; //en pixels
	
	/** de combien de pixels avance l'Event � chaque pas ? */
	public int vitesseActuelle = VITESSE_PAR_DEFAUT; //1:tr�sLent 2:lent 4:normal 8:rapide 16:tr�srapide 32:tr�str�sRapide
	/** toutes les combien de frames l'Event change-t-il d'animation ? */
	public int frequenceActuelle = FREQUENCE_PAR_DEFAUT; //1:tr�sAgit� 2:agit� 4:normal 8:mou 16:tr�sMou
	
	public int direction = Event.Direction.BAS;
	public int animation = 0;
	public BufferedImage imageActuelle = null;
	/** l'Event est-il en train d'avancer en ce moment m�me ? (utile pour l'animation) */
	public boolean avance = false;
	/** L'Event avan�ait-il � la frame pr�c�dente ? (utile pour l'animation) */
	protected boolean avancaitALaFramePrecedente = false;
	
	/** 
	 * Ces param�tres sont remplis automatiquement au chargement de la page.
	 */
	public boolean animeALArretActuel = false;
	public boolean animeEnMouvementActuel = true;
	public ArrayList<CommandeEvent> deplacementActuel;
	public boolean repeterLeDeplacementActuel = true;
	public boolean ignorerLesMouvementsImpossiblesActuel = false;
	public boolean traversableActuel = false;
	public boolean auDessusDeToutActuel = false;
	public int largeurHitbox = LARGEUR_HITBOX_PAR_DEFAUT;
	public int hauteurHitbox = HAUTEUR_HITBOX_PAR_DEFAUT;
	/**
	 * D�cale l'affichage vers le bas.
	 * En effet, d�caler l'affichage dans les trois autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0; 
	public ArrayList<PageDeComportement> pages;
	public PageDeComportement pageActive = null;
	public boolean interrupteurLocalA = false;
	public boolean interrupteurLocalB = false;
	public boolean interrupteurLocalC = false;
	public boolean interrupteurLocalD = false;
	/**
	 * Lorsque ce marqueur est � true, on consid�re l'event comme supprim�.
	 * Ce n'est qu'un simple marqueur : l'event n'est r�ellement supprim� qu'apr�s la boucle for sur les events.
	 */
	public boolean supprime = false;
	
	/**
	 * Tout Event regarde dans une Direction.
	 */
	public static class Direction {
		public static final int BAS = 0;
		public static final int GAUCHE = 1;
		public static final int DROITE = 2;
		public static final int HAUT = 3;
		
		/**
		 * M�thode pratique pour r�cup�rer la direction d'un Event d�crit par un JSON.
		 * @param jsonEvent object JSON d�crivant l'�v�nement
		 * @return direction initiale de l'�v�nement
		 */
		public static int obtenirDirectionViaJson(final JSONObject jsonEvent) {
			int dir;
			try {
				dir = jsonEvent.getInt("direction");
			} catch (Exception e1) {
				dir = Event.Direction.BAS; //direction par d�faut
			}
			return dir;
		}
	}
	
	/**
	 * Constructeur explicite de l'Event
	 * @param map Map � laquelle l'Event appartient
	 * @param x numero du carreau o� se trouve l'Event, en abscisse, de gauche � droite
	 * @param y numero du carreau o� se trouve l'Event, en ordonn�e, de haut en bas
	 * @param direction de l'Event
	 * @param nom de l'Event
	 * @param pages ensemble de Pages d�crivant le comportement de l'Event
	 * @param largeurHitbox largeur de la bo�te de collision
	 * @param hauteurHitbox hauteur de la bo�te de collision
	 */
	protected Event(final Map map, final Integer x, final Integer y, final Integer direction, final String nom, final ArrayList<PageDeComportement> pages, final int largeurHitbox, final int hauteurHitbox) {
		this.map = map;
		this.x = x * Fenetre.TAILLE_D_UN_CARREAU;
		this.y = y * Fenetre.TAILLE_D_UN_CARREAU;
		this.direction = direction;
		this.nom = nom;
		this.pages = pages;
		this.largeurHitbox = largeurHitbox;
		this.hauteurHitbox = hauteurHitbox;
		initialiserLesPages();
		if (pages!=null && pages.size()>=1) {
			attribuerLesProprietesActuelles(pages.get(0)); //par d�faut, propri�t�s de la premi�re page
		}
	}
	
	/**
	 * Constructeur de l'Event utilisant un tableau de pages JSON
	 * @param map Map � laquelle l'Event appartient
	 * @param x numero du carreau o� se trouve l'Event, en abscisse, de gauche � droite
	 * @param y numero du carreau o� se trouve l'Event, en ordonn�e, de haut en bas
	 * @param direction de l'Event
	 * @param nom de l'Event
	 * @param tableauDesPages tableau JSON contenant les Pages de comportement
	 * @param largeurHitbox largeur de la bo�te de collision
	 * @param hauteurHitbox hauteur de la bo�te de collision
	 */
	protected Event(final Map map, final Integer x, final Integer y, final Integer direction, final String nom, final JSONArray tableauDesPages, final int largeurHitbox, final int hauteurHitbox) {
		this(map, x, y, direction, nom, creerListeDesPagesViaJson(tableauDesPages), largeurHitbox, hauteurHitbox);
	}

	/**
	 * Prend le tableau JSON des pages et cr�e la liste des Pages avec.
	 * @param tableauDesPages au format JSON
	 * @return liste des Pages de l'Event
	 */
	private static ArrayList<PageDeComportement> creerListeDesPagesViaJson(final JSONArray tableauDesPages) {
		final ArrayList<PageDeComportement> listeDesPages = new ArrayList<PageDeComportement>();
		for (Object pageJSON : tableauDesPages) {
			listeDesPages.add( new PageDeComportement((JSONObject) pageJSON) );
		}
		return listeDesPages;
	}

	/**
	 * On num�rote les Pages de l'Event.
	 * On relie les Conditions et les Commandes � leur Page.
	 */
	protected final void initialiserLesPages() {
		int numeroCondition = 0;
		int numeroPage = 0;
		try {
			for (PageDeComportement page : this.pages) {
				page.event = this;
				if (page!=null) {
					page.numero = numeroPage;
					//num�rotation des conditions et on apprend aux conditions qui est leur page
					try {
						for (Condition cond : page.conditions) {
							cond.numero = numeroCondition;
							cond.page = page;
							numeroCondition++;
						}
					} catch (NullPointerException e1) {
						//pas de conditions pour d�clencher cette page
					}
					//on apprend aux commandes qui est leur page
					try {
						for (CommandeEvent comm : page.commandes) {
							comm.page = page;
						}
					} catch (NullPointerException e2) {
						//pas de commandes dans cette page
					}
					numeroPage++;
				}
			}
		} catch (NullPointerException e3) {
			//pas de pages dans cet event
		}
	}

	/**
	 * Est-ce que cet Event pourra aller � cet endroit dans le futur, 
	 * sachant qu'un autre Event se trouve � tel endroit ?
	 * @param xFutur position x de l'Event dans le futur
	 * @param yFutur position y de l'Event dans le futur
	 * @param largHitbox largeur de la bo�te de collision de cet Event
	 * @param hautHitbox hauteur de la bo�te de collision de cet Event
	 * @param xAutre position x actuelle de l'autre Event
	 * @param yAutre position y actuelle de l'autre Event
	 * @param largHitboxAutre largeur de la bo�te de collision de l'autre Event
	 * @param hautHitboxAutre hauteur de la bo�te de collision de l'autre Event
	 * @return true si les deux Events vont se marcher dessus, false s'ils vont rester distants
	 */
	private boolean lesHitboxesSeChevauchent(final int xFutur, final int yFutur, final int largHitbox, final int hautHitbox, final int xAutre, final int yAutre, final int largHitboxAutre, final int hautHitboxAutre) {
		final int deltaX = (xFutur-xAutre);
		final int deltaY = (yFutur-yAutre);
		final int rayon = (Math.max(largHitbox, hautHitbox)+Math.max(largHitboxAutre, hautHitboxAutre));
		if (deltaX*deltaX+deltaY*deltaY > rayon*rayon/2) {
			return false; //si les deux events sont trop �loign�s l'un de l'autre, il n'y a pas de collision
		} else { 
			final int x1min = xFutur;
			final int x1max = xFutur+largHitbox;
			final int x2min = xAutre;
			final int x2max = xAutre+largHitboxAutre;
			final int y1min = yFutur;
			final int y1max = yFutur+hautHitbox;
			final int y2min = yAutre;
			final int y2max = yAutre+hautHitboxAutre;
			return Hitbox.lesDeuxRectanglesSeChevauchent(x1min, x1max, y1min, y1max, x2min, x2max, y2min, y2max, largHitbox, hautHitbox, largHitboxAutre, hautHitboxAutre);
		}
	}
	
	/**
	 * Le d�placement dans cette direction est-il possible ?
	 * @param sens direction dans laquelle l'Event compte avancer
	 * @return si le d�placement est possible oui ou non
	 */
	protected boolean deplacementPossible(final int sens) {
		//si l'Event est lui-m�me traversable, il peut faire son mouvement
		if (this.traversableActuel) {
			return true;
		}
		
		boolean reponse = true;
		int xAInspecter = this.x; //pour le d�cor
		int yAInspecter = this.y;
		int xAInspecter2 = this.x; //pour le d�cor, deuxi�me case � v�rifier si entre deux cases
		int yAInspecter2 = this.y;
		int xAInspecter3 = this.x; //pour les events
		int yAInspecter3 = this.y;
		switch(sens) {
		case Event.Direction.BAS : 
			yAInspecter += hauteurHitbox;   
			yAInspecter2 += hauteurHitbox;   
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 += vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= vitesseActuelle; 
			xAInspecter2 -= vitesseActuelle; 
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 -= vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += largeurHitbox;   
			xAInspecter2 += largeurHitbox;   
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 += vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= vitesseActuelle; 
			yAInspecter2 -= vitesseActuelle; 
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 -= vitesseActuelle; 
			break;
		default : 
			break;
		}
		try {
			//si rencontre avec un �l�ment de d�cor non passable -> false
			if (!map.casePassable[xAInspecter/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.BAS||sens==Direction.HAUT) && ((x+largeurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(x/Fenetre.TAILLE_D_UN_CARREAU)) && !map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.GAUCHE||sens==Direction.DROITE) && ((y+hauteurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(y/Fenetre.TAILLE_D_UN_CARREAU)) && !map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			//voil�
			
			//si rencontre avec un autre �v�nement non traversable -> false
			for (Event autreEvent : this.map.events) {
				if (this.numero != autreEvent.numero 
					&& !autreEvent.traversableActuel
					&& lesHitboxesSeChevauchent(xAInspecter3, yAInspecter3, largeurHitbox, hauteurHitbox, autreEvent.x, autreEvent.y, autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
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

	/**
	 * D�placement naturel, inscrit dans la liste des d�placements de la page de l'event
	 */
	public void deplacer() {
		//si page active peut �tre nulle, le deplacementActuel est celui de la derni�re page qui a �t� activ�e
		if (pageActive!=null || (deplacementActuel!=null&&deplacementActuel.size()>0) ) {
			try {
				final CommandeEvent mouvementActuel = deplacementActuel.get(0);
				final String typeMouvement = mouvementActuel.getClass().getName();
				switch(typeMouvement) {
					case "comportementEvent.Avancer" : deplacer((Avancer) mouvementActuel); break;
					case "comportementEvent.AvancerAleatoirement" : deplacer((Avancer) mouvementActuel); break;
					//TODO dans le futur, ajouter les autres types de mouvements ici
					default : break;
				}
			} catch (NullPointerException e1) {
				//pas de d�placement pour cet event
			} catch (IndexOutOfBoundsException e2) {
				//le d�placement ne contient aucune commande
			}
		}
	}
	
	/**
	 * D�place l'Event pour son d�placement naturel ou pour un d�placement forc�.
	 * Vu qu'on utilise "deplacementActuel", un d�placement forc� devra �tre ins�r� artificiellement dans la liste.
	 * @param mouvementActuel mouvement � executer
	 */
	public void deplacer(final Avancer mouvementActuel) {
		try {
			final int sens = ((Avancer) mouvementActuel).getDirection();
			if ( deplacementPossible(sens) ) {
				this.avance = true;
				//d�placement :
				switch (sens) {
					case Direction.BAS : this.y += vitesseActuelle; break;
					case Direction.GAUCHE : this.x -= vitesseActuelle; break;
					case Direction.DROITE : this.x += vitesseActuelle; break;
					case Direction.HAUT : this.y -= vitesseActuelle; break;
				}
				((Avancer) mouvementActuel).ceQuiAEteFait += vitesseActuelle;
				//*ancien emplacement de l'animation*
				//quelle sera la commande suivante ?
				if ( ((Avancer) mouvementActuel).ceQuiAEteFait >= ((Avancer) mouvementActuel).nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU) {
					if (repeterLeDeplacementActuel) {
						//on le r�initialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacementActuel.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacementActuel.remove(0);
				}
			} else {
				this.avance = false;
				if (ignorerLesMouvementsImpossiblesActuel) {
					if (repeterLeDeplacementActuel) {
						//on le r�initialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacementActuel.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacementActuel.remove(0);
				}
			}
			
		} catch (NullPointerException e1) {
			//pas de mouvement pour cet �v�nement
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'�v�nement :");
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Permet de dire si un event est devant ou derri�re un autre en terme d'affichage.
	 */
	public final int compareTo(final Event e) {
		if (auDessusDeToutActuel) {
			if (e.auDessusDeToutActuel) {
				//les deux sont au dessus de tout, on applique la logique invers�e
				if (y > e.y) {
					return -1;
				}
				if (y < e.y) {
					return 1;
				}
			} else {
				//this est plus grand
				return 1;
			}
		} else {
			if (e.auDessusDeToutActuel) {
				//e est plus grand
				return -1;
			} else {
				//aucun n'est au dessus de tout, on applique la logique normale
				if (y > e.y) {
					return 1;
				}
				if (y < e.y) {
					return -1;
				}
			}
		}
		return 0;
	}

	/**
	 * Active la page de l'event qui v�rifie toutes les conditions de d�clenchement.
	 * S'il y a plusieurs pages valides, on prend la derni�re.
	 */
	public final void activerUnePage() {
		PageDeComportement pageQuOnChoisitEnRemplacement = null;
		try {
			boolean onATrouveLaPageActive = false;
			for (int i = pages.size()-1; i>=0 && !onATrouveLaPageActive; i--) {
				final PageDeComportement page = pages.get(i);
				boolean cettePageConvient = true;
				try {
					//si une condition est fausse, la page ne convient pas
					for (int j = 0; j<page.conditions.size() && cettePageConvient; j++) {
						final Condition cond = page.conditions.get(j);
						if (!cond.estVerifiee()) {
							cettePageConvient = false;
						}
					}
				} catch (NullPointerException e1) {
					//pas de conditions pour cette page
				}
				if (cettePageConvient) {
					onATrouveLaPageActive = true;
					pageQuOnChoisitEnRemplacement = page;
				}
			}
		} catch (NullPointerException e2) {
			//pas de pages pour cet event
		}
		//on ne change la page que si c'est une page diff�rente
		if ( this.pageActive==null || pageQuOnChoisitEnRemplacement==null || (this.pageActive.numero!=pageQuOnChoisitEnRemplacement.numero) ) {
			//changement de page
			this.pageActive = pageQuOnChoisitEnRemplacement;
			
			//on donne � l'�vent toutes les propri�t�s de la page
			if (this.pageActive != null) {
				attribuerLesProprietesActuelles(pageActive);
			}
		}
	}

	/**
	 * On assigne les propri�t�s actuelles en utilisant celles d'une Page donn�e.
	 * @param page dont on r�cup�re les propri�t�s pour les donner � l'Event
	 */
	private void attribuerLesProprietesActuelles(final PageDeComportement page) {
		//apparence
		this.imageActuelle = page.image;
		//propri�t�s
		this.vitesseActuelle = page.vitesse;
		this.frequenceActuelle = page.frequence;
		this.animeALArretActuel = page.animeALArret;
		this.auDessusDeToutActuel = page.auDessusDeTout;
		this.animeEnMouvementActuel = page.animeEnMouvement;
		this.traversableActuel = page.traversable;
		//d�placement
		this.deplacementActuel = page.deplacement;
		this.repeterLeDeplacementActuel = page.repeterLeDeplacement;
		this.ignorerLesMouvementsImpossiblesActuel = page.ignorerLesMouvementsImpossibles;
	}
	
}