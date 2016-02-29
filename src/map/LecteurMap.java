package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import bibliothequeMenu.MenuPause;
import commandes.Message;
import main.Fenetre;
import main.Lecteur;
import menu.LecteurMenu;
import menu.Menu;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il re�oit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	//constantes
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	private static final int X_AFFICHAGE_OBJET = 612;
	private static final int Y_AFFICHAGE_OBJET = 4;
	private static final int X_AFFICHAGE_MESSAGE = 76;
	private static final int Y_AFFICHAGE_MESSAGE = 320;
	
	public Map map;
	public Tileset tilesetActuel = null;
	
	/** permet de trier les events selon leur coordonn�e y pour l'affichage */
	public Comparator<Event> comparateur;
	
	/** si true, les �v�nements n'avancent plus naturellement (seuls mouvements forc�s autoris�s) */
	public boolean stopEvent = false; 
	
	/** message � afficher dans la bo�te de dialogue */
	public Message messageActuel = null;
	
	/**  */
	public static final BufferedImage HUD_TOUCHES = chargerImageHudTouches();
	
	/** frame o� le joueur a appuy� sur la touche action */
	public int frameDAppuiDeLaToucheAction;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dont ce Lecteur assure l'affichage
	 */
	public LecteurMap(final Fenetre fenetre) {
		this.fenetre = fenetre;
		this.comparateur = new Comparator<Event>() {
	        public int compare(final Event e1, final Event e2) {
	            return e1.compareTo(e2);
	        }
	    };
	}
	
	/**
	 * A chaque frame, calcule l'�cran � afficher, avec le d�cor et les Events dessus.
	 * @warning Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @return �cran repr�sentant la Map
	 */
	public final BufferedImage calculerAffichage() {
		BufferedImage ecran = ecranNoir();
		
		//ouverture du tileset
		try {
			if (tilesetActuel == null) {
				tilesetActuel = this.map!=null && this.map.tileset!=null ? this.map.tileset : new Tileset(map.tileset.nom);
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ouverture du tileset :");
			e.printStackTrace();
		}
		
		//on dessine la map
		final int xCamera = calculerXCamera();
		final int yCamera = calculerYCamera();
		ecran = superposerImages(ecran, map.imageCoucheSousHeros, -xCamera, -yCamera);

		//lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		//d�placements des �v�nements
		deplacerLesEvents();
		
		
		//animation des �v�nements
		animerLesEvents();

		//DEBUG pour voir la hitbox de l'attaque du h�ros
		//ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);
		
		//on dessine les �v�nements
		ecran = dessinerLesEvents(ecran, xCamera, yCamera);
		
		//ajouter imageCoucheSurHeros � l'�cran
		ecran = superposerImages(ecran, map.imageCoucheSurHeros, -xCamera, -yCamera);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//on affiche le message
		if (messageActuel!=null) {
			ecran = superposerImages(ecran, messageActuel.image, X_AFFICHAGE_MESSAGE, Y_AFFICHAGE_MESSAGE);
		}
		
		//supprimer events dont l'attribut "supprim�" est � true
		supprimerLesEventsASupprimer();
		
		return ecran;
	}

	/**
	 * Dessiner � l'�cran les jauges et informations extradi�g�tiques � destination du joueur
	 * @param ecran sur lequel on dessine les jauges
	 * @return �cran avec les jauges dessin�es
	 */
	private BufferedImage dessinerLesJauges(BufferedImage ecran) {
		//touches
		ecran = superposerImages(ecran, HUD_TOUCHES, 0, 0);
		
		//icone de l'arme equip�e
		try {
			ecran = superposerImages(ecran, Fenetre.getPartieActuelle().getArmeEquipee().icone, X_AFFICHAGE_ARME, Y_AFFICHAGE_ARME);
		} catch (NullPointerException e) {
			//pas d'arme �quip�e
		}
		
		//TODO icone de l'objet �quip�
		/*
		try
			X_AFFICHAGE_OBJET
			Y_AFFICHAGE_OBJETn, 
		*/
		return ecran;
	}

	/**
	 * Pour le debug, on peut souhaiter afficher la HitBox du H�ros � l'�cran.
	 * @param ecran sur lequel on dessine la Htibox du H�ros
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @return �cran avec la Hitbox du H�ros dessin�e dessus
	 */
	@SuppressWarnings("unused")
	private BufferedImage dessinerLaHitboxDuHeros(BufferedImage ecran, final int xCamera, final int yCamera) {
		try {
			final int[] coord = Hitbox.calculerCoordonneesAbsolues(this.map.heros);
			final int xminHitbox = coord[0];
			final int xmaxHitbox = coord[1];
			final int yminHitbox = coord[2];
			final int ymaxHitbox = coord[3];
			final Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.magenta);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, yminHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, ymaxHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xminHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xmaxHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Activer une page si l'event n'a aucune page activ�e.
	 * Sinon, continuer de lire la page de commandes active de l'event.
	 */
	private void continuerLaLectureDesPagesDeCommandesEvent() {
		for (Event event : this.map.events) {
			if (!event.equals(this.map.heros)) { //le H�ros est calcul� en dernier
				if (!event.supprime) {
					if (event.pageActive == null || event.pageActive.commandes==null) {
						event.activerUnePage();
					}
					if (event.pageActive != null) {
						event.pageActive.executer();
					}
				}
			}
		}
		//le H�ros est calcul� en dernier pour �viter les probl�mes d'�p�e
		final Event event = this.map.heros;
		if (!event.supprime) {
			if (event.pageActive == null || event.pageActive.commandes==null) {
				event.activerUnePage();
			}
			if (event.pageActive != null) {
				event.pageActive.executer();
			}
		}
	}

	/**
	 * Calculer le nouvel �cran, avec les Events dessin�s dessus.
	 * Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @param ecran sur lequel on dessine les Events
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @return �cran avec les Events dessin�s dessus
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, final int xCamera, final int yCamera) {
		try {
			Collections.sort(this.map.events, this.comparateur); //on trie les events du plus derri�re au plus devant
			for (Event event : this.map.events) {
				if (!event.supprime) {
					ecran = dessinerEvent(ecran, event, xCamera, yCamera);
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du dessin des �v�nements :");
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Donne la bonne valeur � l'attribut "animation" avant d'envoyer l'event � l'affichage.
	 */
	private void animerLesEvents() {
		if (this.stopEvent) {
			return;
		}
		try {
			//TODO if( event.saute )
			for (Event event : this.map.events) {
				final boolean passerALAnimationSuivante = (this.map.lecteur.frameActuelle%event.frequenceActuelle==0);
				//cas o� l'Event est anim� � l'arr�t
				if (!event.avance && event.animeALArretActuel && passerALAnimationSuivante) {
					event.animation = (event.animation+1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
				}
				//cas o� l'Event est vraiment en mouvement
				if ((event.avance||event.avancaitALaFramePrecedente) && event.animeEnMouvementActuel && passerALAnimationSuivante) {
					event.animation = (event.animation+1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
				}
				event.avancaitALaFramePrecedente = event.avance;
			}
		} catch (Exception e) {
			System.out.println("erreur lors de l'animation des �v�nements dans la boucle d'affichage de la map :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Donne la bonne valeur aux positions x et y avant d'envoyer l'Event � l'affichage.
	 */
	private void deplacerLesEvents() {
		try {
			//animer la marche du H�ros si touche press�e
			final ArrayList<Integer> touchesPressees = this.fenetre.touchesPressees;
			if ( touchesPressees.contains(ToucheRole.HAUT)
			|| touchesPressees.contains(ToucheRole.GAUCHE) 
			|| touchesPressees.contains(ToucheRole.BAS) 
			|| touchesPressees.contains(ToucheRole.DROITE) ) {
				map.heros.avance = true;
			}
			
			//d�placer chaque Event
			for (Event event : this.map.events) {
				if (!event.supprime) {
					event.deplacer(); //on effectue le d�placement si possible (pas d'obstacles rencontr�s)
				}
			}
		} catch (Exception e) {
			System.err.println("Erreur lors du d�placement des �v�nements :");
			e.printStackTrace();
		}
	}

	/**
	 * Certains Events ont �t� marqu�s "� supprimer" durant la lecture des Commandes.
	 * On les �limine maintenant, une fois que la lecture des Commandes est termin�e.
	 * En effet on ne peut pas supprimer des Events lorsqu'on est encore dans la boucle qui parcourt la liste des Events.
	 */
	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		for (int i = 0; i<nombreDEvents; i++) {
			if (this.map.events.get(i).supprime) {
				this.map.events.remove(i);
				nombreDEvents--;
				i--;
			}
		}
	}

	/**
	 * Faire une capture d'�cran des collisions
	 */
	public final void photographierCollision() {
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		
		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		sauvegarderImage(img);
	}

	/**
	 * Dessine l'Event sur l'�cran.
	 * @warning Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @param ecran sur lequel on dessine
	 * @param event � dessiner
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @return �cran sur lequel on a dessin� l'Event demand�
	 */
	private BufferedImage dessinerEvent(final BufferedImage ecran, final Event event, final int xCamera, final int yCamera) {
		final BufferedImage eventImage = event.imageActuelle;
		if (eventImage!=null) { 
			//l'apparence de l'event est une des 16 parties de l'image de l'event (suivant la direction et l'animation)
			final int largeur = eventImage.getWidth() / 4;
			final int hauteur = eventImage.getHeight() / 4;
			int direction;
			final int animation;
			int positionX;
			int positionY;
			if (event.saute) {
				//l'Event est en train de sauter
				direction = event.directionLorsDuSaut;
				animation = 0;
				positionX = event.coordonneeApparenteXLorsDuSaut;
				positionY = event.coordonneeApparenteYLorsDuSaut;
			} else {
				//l'Event ne Saute pas
				direction = event.direction;
				animation = event.animation;
				positionX = event.x;
				positionY = event.y;
			}
			//ajustement selon la taille de l'image
			positionX += event.largeurHitbox/2 - largeur/2;
			positionY += event.hauteurHitbox - hauteur + event.offsetY;
			
			final BufferedImage apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			return superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);
			/*
			//DEBUG pour visualiser les collisions
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			return ecran;
			*/
		} else {
			//l'event n'a pas d'image
			return ecran;
		}
	}
	
	/**
	 * Dessine � l'�cran un carreau du Tileset aux coordonn�es (xEcran;yEcran).
	 * @warning Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @param ecran sur lequel on doit dessiner un carreau
	 * @param xEcran position x o� dessiner le carreau � l'�cran
	 * @param yEcran position y o� dessiner le carreau � l'�cran
	 * @param numeroCarreau num�ro du carreau � dessiner
	 * @param tilesetUtilise Tileset utilis� pour interpr�ter le d�cor de la Map
	 * @return �cran sur lequel on a dessin� le carreau demand�
	 */
	public final BufferedImage dessinerCarreau(final BufferedImage ecran, final int xEcran, final int yEcran, final int numeroCarreau, final Tileset tilesetUtilise) {
		final BufferedImage dessinCarreau = tilesetUtilise.carreaux[numeroCarreau];
		return superposerImages(ecran, dessinCarreau, xEcran*Fenetre.TAILLE_D_UN_CARREAU, yEcran*Fenetre.TAILLE_D_UN_CARREAU);
	}

	@Override
	public final void keyPressed(final Integer keycode) {
		switch(keycode) {
			case GestionClavier.ToucheRole.MENU : this.ouvrirLeMenu(); break;
			case GestionClavier.ToucheRole.HAUT : this.haut(); break;
			case GestionClavier.ToucheRole.GAUCHE : this.gauche(); break;
			case GestionClavier.ToucheRole.BAS : this.bas(); break;
			case GestionClavier.ToucheRole.DROITE : this.droite(); break;
			case GestionClavier.ToucheRole.ARME_SUIVANTE : this.equiperArmeSuivante(); break;
			case GestionClavier.ToucheRole.ACTION : this.action(); break;
			case GestionClavier.ToucheRole.ARME_PRECEDENTE : this.equiperArmePrecedente(); break;
			case GestionClavier.ToucheRole.ACTION_SECONDAIRE : this.objet(); break;
			default : break;
		}
	}
	
	/**
	 * Ouvre une autre Map (dans un nouveau LecteurMap).
	 * @warning cette m�thode ne doit �tre appel�e que par le nouveau Lecteur !
	 * @param nouvelleMap sur laquelle le H�ros voyage
	 */
	public final void changerMap(final Map nouvelleMap) {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
		
		//on d�truit le Tileset actuel si le prochain n'est pas le m�me
		if (tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) {
			this.tilesetActuel = null;
		}
	}
	
	/**
	 * Ouvrir le Menu du jeu.
	 * On quitte la Map temporairement (elle est m�moris�e) pour parcourir le Menu.
	 */
	public final void ouvrirLeMenu() {
		final Menu menuPause = new MenuPause();
		final LecteurMenu lecteurMenu = new LecteurMenu(this.fenetre, menuPause, this);
		
		this.fenetre.futurLecteur = lecteurMenu;
		lecteurMenu.menu = menuPause;
		this.allume = false;
	}

	@Override
	public final void keyReleased(final Integer keycode) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arr�t�
		mettreHerosDansLaBonneDirection();
	}
	
	/**
	 * Lorsque le H�ros s'arr�te de marcher, on arr�te son animation.
	 */
	public final void remettreAZeroLAnimationDuHeros() {
		final ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
		final Event heros = map.heros;
		if (!touchesPressees.contains(GestionClavier.ToucheRole.BAS) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.HAUT) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.DROITE)) {
			heros.avance = false;
			heros.animation = 0;
		}
	}
	
	/**
	 * Tourner le H�ros dans la bonne Direction selon l'entr�e clavier.
	 */
	public final void mettreHerosDansLaBonneDirection() {
		final Heros heros = map.heros;
		if (!stopEvent && heros.animationAttaque<=0) {
			final ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
			if ( touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) ) {
				heros.direction = Event.Direction.GAUCHE;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.DROITE) ) {
				heros.direction = Event.Direction.DROITE;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.BAS) ) {
				heros.direction = Event.Direction.BAS;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.HAUT) ) {
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
	/**
	 * Calculer la position x de la cam�ra
	 * @return position x de la cam�ra
	 */
	private int calculerXCamera() {
		final int largeurMap = map.largeur;
		if ( !this.map.defilementCameraX ) {
			//map tr�s petite, d�filement inutile
			return 0;
		} else {
			//grande map, d�filement possible
			final int xCamera = map.heros.x - Fenetre.LARGEUR_ECRAN/2;
			if (xCamera<0) { //cam�ra ne d�borde pas de la map � gauche
				return 0;
			} else if (xCamera+Fenetre.LARGEUR_ECRAN > largeurMap*Fenetre.TAILLE_D_UN_CARREAU) { //cam�ra ne d�borde pas de la map � droite
				return largeurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.LARGEUR_ECRAN;
			} else {
				return xCamera;
			}
		}
	}
	
	/**
	 * Calculer la position y de la cam�ra
	 * @return position y de la cam�ra
	 */
	private int calculerYCamera() {
		final int hauteurMap = map.hauteur;
		if ( !this.map.defilementCameraY ) { 
			//map tr�s petite, d�filement inutile
			return 0;
		} else {
			//grande map, d�filement possible
			final int yCamera = map.heros.y - Fenetre.HAUTEUR_ECRAN/2;
			if (yCamera<0) { //cam�ra ne d�borde pas de la map en haut
				return 0;
			} else if (yCamera+Fenetre.HAUTEUR_ECRAN > hauteurMap*Fenetre.TAILLE_D_UN_CARREAU) { //cam�ra ne d�borde pas de la map en bas
				return hauteurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.HAUTEUR_ECRAN;
			} else {
				return yCamera;
			}
		}
	}
	
	/**
	 * Le H�ros arr�te son animation pour �couter un Message.
	 */
	public final void normaliserApparenceDuHerosAvantMessage() {
		this.map.heros.animation = 0;
		this.map.heros.animationAttaque = 0;
	}
	
	/**
	 * D�placer le H�ros vers le haut
	 */
	public final void haut() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * D�placer le H�ros vers la gauche
	 */
	public final void gauche() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * D�placer le H�ros vers le bas
	 */
	public final void bas() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * D�placer le H�ros vers la droite
	 */
	public final void droite() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}
	
	/**
	 * Attaquer ou parler (suivant si gentil ou m�chant)
	 */
	public final void action() {
		this.frameDAppuiDeLaToucheAction = this.frameActuelle;
	}

	/**
	 * Transmettre � la Partie le changement d'Arme ordonn� � la Fen�tre
	 */
	public final void equiperArmeSuivante() {
		if (!this.stopEvent) { //on ne change pas d'Arme lorsqu'on lit un Message
			Fenetre.getPartieActuelle().equiperArmeSuivante();
		}
	}
	
	/**
	 * Transmettre � la Partie le changement d'Arme ordonn� � la Fen�tre
	 */
	public final void equiperArmePrecedente() {
		if (!this.stopEvent) { //on ne change pas d'Arme lorsqu'on lit un Message
			Fenetre.getPartieActuelle().equiperArmePrecedente();
		}
	}

	/**
	 * Utiliser l'objet secondaire.
	 */
	public void objet() {
		//TODO
	}
	
	/**
	 * Charge le petit carr� blanc qui entoure l'Arme dans le HUD � l'�cran.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudTouches() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\carre arme kujira.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
