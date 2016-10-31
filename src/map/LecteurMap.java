package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import commandes.Message;
import commandes.OuvrirMenu;
import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.meteo.Meteo;
import menu.Texte;
import mouvements.RegarderUnEvent;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.Graphismes;
import utilitaire.Maths;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il re�oit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	//constantes

	//jauges
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	private static final int X_AFFICHAGE_GADGET = 612;
	private static final int Y_AFFICHAGE_GADGET = 4;
	private static final int X_AFFICHAGE_ARGENT = 4;
	private static final int Y_AFFICHAGE_ARGENT = 450;
	private static final int X_AFFICHAGE_MESSAGE = 76;
	private static final int Y_AFFICHAGE_MESSAGE = 320;
	private static final int ESPACEMENT_ICONES = 4;
	//ic�nes de jauges
	public static final BufferedImage HUD_TOUCHES = chargerImageHudTouches();
	public static final BufferedImage HUD_ARGENT = chargerImageHudArgent();
	
	public Map map;
	public Tileset tilesetActuel = null;
	/** vignette actuelle pour l'animation des Autotiles anim�s de la Map */
	private int vignetteAutotileActuelle = 0;
	
	/** permet de trier les events selon leur coordonn�e y pour l'affichage */
	public Comparator<Event> comparateur;
	
	/** si true, les �v�nements n'avancent plus naturellement (seuls mouvements forc�s autoris�s) */
	public boolean stopEvent = false;
	public Event eventQuiALanceStopEvent;
	
	/** message � afficher dans la bo�te de dialogue */
	public Message messageActuel = null;
	
	/** Autoriser ou interdire l'acc�s au Menu depuis la Map ? */
	public boolean autoriserMenu = true;
	
	/** D�filement X de la cam�ra */
	public int defilementX;
	/** D�filement Y de la cam�ra */
	public int defilementY;
	
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
	 * @param frame dont l'�cran est calcul�
	 * @warning Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @return �cran repr�sentant la Map
	 */
	public final BufferedImage calculerAffichage(final int frame) {
		//final long t0 = System.currentTimeMillis(); //mesure de performances
		
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
		
		//calcul de la position de la cam�ra par rapport � la Map
		final int xCamera = calculerXCamera();
		final int yCamera = calculerYCamera();
		
		//on dessine le d�cor inf�rieur
		animerLesAutotiles();
		ecran = dessinerDecorInferieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
				
		//lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		//d�placements des �v�nements
		deplacerLesEvents();
		
		//animation des �v�nements
		animerLesEvents(frame);

		//DEBUG pour voir la hitbox de l'attaque du h�ros
		ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);
		
		//on dessine les �v�nements
		ecran = dessinerLesEvents(ecran, xCamera, yCamera);
		
		//ajouter imageCoucheSurHeros � l'�cran
		ecran = dessinerDecorSuperieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
		
		//m�t�o
		ecran = dessinerMeteo(ecran, frame);
		
		//brouillard
		ecran = dessinerLeBrouillard(ecran, map.brouillard, xCamera, yCamera, frame);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//on affiche le message
		if (messageActuel!=null) {
			ecran = Graphismes.superposerImages(ecran, messageActuel.image, X_AFFICHAGE_MESSAGE, Y_AFFICHAGE_MESSAGE);
		}
		
		//supprimer events dont l'attribut "supprim�" est � true
		supprimerLesEventsASupprimer();
		
		//final long t1 = System.currentTimeMillis(); //mesure de performances
		//this.fenetre.mesuresDePerformance.add(new Long(t1 - t0).toString());
		
		return ecran;
	}

	/**
	 * Les Autotiles anim�s ont plusieurs vignettes d'animation. De temps en temps, il faut changer de vignette.
	 */
	private void animerLesAutotiles() {
		if (this.map.contientDesAutotilesAnimes && (this.frameActuelle % Autotile.FREQUENCE_ANIMATION_AUTOTILE == 0)) {
			vignetteAutotileActuelle += 1;
			vignetteAutotileActuelle = Maths.modulo(vignetteAutotileActuelle, Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME);
		}
	}

	/**
	 * Dessiner � l'�cran le d�cor situ� au dessus du H�ros.
	 * @param ecran sur lequel dessiner le d�cor
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return �cran avec le d�cor sup�rieur peint
	 */
	private BufferedImage dessinerDecorSuperieur(BufferedImage ecran, final int xCamera, final int yCamera, final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheSurHeros(vignetteAutotile), -xCamera, -yCamera);
		return ecran;
	}

	/**
	 * Dessiner � l'�cran le d�cor situ� en dessous du H�ros.
	 * @param ecran sur lequel dessiner le d�cor
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return �cran avec le d�cor inf�rieur peint
	 */
	private BufferedImage dessinerDecorInferieur(BufferedImage ecran, final int xCamera, final int yCamera, final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheSousHeros(vignetteAutotile), -xCamera, -yCamera);
		return ecran;
	}

	/**
	 * Dessiner � l'�cran les effets m�t�orologiques.
	 * @param ecran sur lequel on dessine
	 * @param frame de l'effet m�t�orologique
	 * @return �cran avec la m�t�o
	 */
	private BufferedImage dessinerMeteo(BufferedImage ecran, final int frame) {
		final Meteo meteo = Fenetre.getPartieActuelle().meteo;
		if (meteo != null) {
			ecran = Graphismes.superposerImages(ecran, meteo.calculerImage(frame), 0, 0);
		}
		return ecran;
	}

	/**
	 * Dessiner � l'�cran les jauges et informations extradi�g�tiques � destination du joueur
	 * @param ecran sur lequel on dessine les jauges
	 * @return �cran avec les jauges dessin�es
	 */
	private BufferedImage dessinerLesJauges(BufferedImage ecran) {
		//touches
		ecran = Graphismes.superposerImages(ecran, HUD_TOUCHES, 0, 0);
		
		//icone de l'Arme equip�e
		try {
			ecran = Graphismes.superposerImages(ecran, Fenetre.getPartieActuelle().getArmeEquipee().icone, X_AFFICHAGE_ARME, Y_AFFICHAGE_ARME);
		} catch (NullPointerException e) {
			//pas d'Arme �quip�e
		}
		
		//icone du Gadget �quip�
		try {
			ecran = Graphismes.superposerImages(ecran, Fenetre.getPartieActuelle().getGadgetEquipe().icone, X_AFFICHAGE_GADGET, Y_AFFICHAGE_GADGET);
		} catch (NullPointerException e) {
			//pas de Gadget �quip�
		}
		
		//argent
		final int argent = Fenetre.getPartieActuelle().argent;
		if (argent > 0) {
			ecran = Graphismes.superposerImages(ecran, HUD_ARGENT, X_AFFICHAGE_ARGENT, Y_AFFICHAGE_ARGENT);
			final Texte texte = new Texte(new Integer(argent).toString(), Color.white);
			final BufferedImage texteImage = texte.image;
			ecran = Graphismes.superposerImages(ecran, texteImage, X_AFFICHAGE_ARGENT+HUD_ARGENT.getWidth()+ESPACEMENT_ICONES, Y_AFFICHAGE_ARGENT);
		}
		
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
			if (Fenetre.getPartieActuelle().getArmeEquipee() != null) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Lire la Page active de chaque Event de la Map.
	 */
	private void continuerLaLectureDesPagesDeCommandesEvent() {
		//en cas de stopEvent, seul l'Event qui a fig� tout le monde est lu (Commandes)
		if (stopEvent) {
			activerUnePageEtLExecuter(this.eventQuiALanceStopEvent);
			return;
		}
		
		//lire tous les Events de la Map (sauf le H�ros)
		for (Event event : this.map.events) {
			if (!event.equals(this.map.heros)) { //le H�ros est calcul� en dernier
				activerUnePageEtLExecuter(event);
			}
		}
		//le H�ros est calcul� en dernier pour �viter les probl�mes d'�p�e
		activerUnePageEtLExecuter(this.map.heros);
	}
	
	/**
	 * Activer une Page (si aucune n'est activ�e) de l'Event (s'il n'est pas supprim� et l'ex�cuter.
	 * @param event dont il faut activer une Page et l'ex�cuter
	 */
	private void activerUnePageEtLExecuter(final Event event) {
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
	 * @param frame d'animation des Events
	 */
	private void animerLesEvents(final int frame) {
		if (this.stopEvent) {
			return; //pas d'animation en cas de stopEvent
		}
		
		try {
			for (Event event : this.map.events) {
				final boolean passerALAnimationSuivante = (frame % event.frequenceActuelle == 0);
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
	 * En cas de stopEvent, seuls les Mouvements commandit�s par l'Event qui a fig� tout sont lus.
	 */
	private void deplacerLesEvents() {
		try {
			//animer la marche du H�ros si touche press�e
			if ( GestionClavier.ToucheRole.HAUT.touche.enfoncee
			  || GestionClavier.ToucheRole.GAUCHE.touche.enfoncee
			  || GestionClavier.ToucheRole.BAS.touche.enfoncee
			  || GestionClavier.ToucheRole.DROITE.touche.enfoncee ) {
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
	 * Dessiner le Brouillard au dessus de la Map et ses Events.
	 * @param ecran sur lequel on dessine
	 * @param brouillard informations sur le Brouillard
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @param frame d'animation du Brouillard
	 * @return �cran sur lequel on a dessin� le Brouillard
	 */
	private BufferedImage dessinerLeBrouillard(BufferedImage ecran, final Brouillard brouillard, final int xCamera, final int yCamera, final int frame) {
		if (brouillard == null || brouillard.image == null || brouillard.opacite <= 0) {
			//pas de Brouillard
			return ecran;
		}
		
		final int largeurEcran = ecran.getWidth();
		final int hauteurEcran = ecran.getWidth();
		final int decalageX = brouillard.defilementX * (frame % brouillard.largeur);
		final int decalageY = brouillard.defilementY * (frame % brouillard.hauteur); 
		int imin = (xCamera - decalageX) / brouillard.largeur;
		int imax = (xCamera + largeurEcran - decalageX) / brouillard.largeur;
		int jmin = (yCamera - decalageY) / brouillard.hauteur;
		int jmax = (yCamera + hauteurEcran - decalageY) / brouillard.hauteur;
		if (Brouillard.calculerAffichage(imin, brouillard.largeur, decalageX, xCamera) >= 0) {
			imin--;
		}
		if (Brouillard.calculerAffichage(imax, brouillard.largeur, decalageX, xCamera) <= largeurEcran) {
			imax++;
		}
		if (Brouillard.calculerAffichage(jmin, brouillard.hauteur, decalageY, yCamera) >= 0) {
			jmin--;
		}
		if (Brouillard.calculerAffichage(jmax, brouillard.largeur, decalageY, yCamera) <= hauteurEcran) {
			jmax++;
		}
		for (int i = imin; i<imax; i++) {
			for (int j = jmin; j<jmax; j++) {
				ecran = Graphismes.superposerImages(
					ecran, 
					brouillard.image, 
					Brouillard.calculerAffichage(i, brouillard.largeur, decalageX, xCamera), 
					Brouillard.calculerAffichage(j, brouillard.hauteur, decalageY, yCamera),
					brouillard.opacite
				);	
			}
		}
		return ecran;
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
		Graphismes.sauvegarderImage(img, "collision");
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
			final int positionX = event.xImage();
			final int positionY = event.yImage();
			
			final int direction = event.direction;
			final int animation;
			if (event.saute) {
				//l'Event est en train de sauter
				animation = 0; //TODO attention : si la vignette normale de l'event n'est pas la vignette 0, l'event va changer d'apparence
			} else {
				//l'Event ne Saute pas
				animation = event.animation;
			}			
			
			//DEBUG pour visualiser les collisions //TODO commenter
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			//voil�
			
			final BufferedImage apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			return Graphismes.superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);		
		} else {
			//l'event n'a pas d'image
			return ecran;
		}
	}

	@Override
	public final void keyPressed(ToucheRole touchePressee) {
		// action sp�cifique selon la touche
		switch (touchePressee) {
			case MENU : this.ouvrirLeMenu(); break;
			case HAUT : this.haut(); break;
			case GAUCHE : this.gauche(); break;
			case BAS : this.bas(); break;
			case DROITE : this.droite(); break;
			case ARME_SUIVANTE : this.equiperArmeSuivante(); break;
			case ACTION : this.action(); break;
			case ARME_PRECEDENTE : this.equiperArmePrecedente(); break;
			case ACTION_SECONDAIRE : this.accessoire(); break;
			default : break; // touche inconnue
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
		if (!this.stopEvent && this.autoriserMenu) { //impossible d'ouvrir le Menu en cas de stopEvent ou de Menu interdit
			final Commande menuPause = new OuvrirMenu("Pause");
			menuPause.executer(0, null);
		}
	}

	@Override
	public final void keyReleased(final ToucheRole toucheRelachee) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arr�t�
	}
	
	/**
	 * Lorsque le H�ros s'arr�te de marcher, on arr�te son animation.
	 */
	public final void remettreAZeroLAnimationDuHeros() {
		final Event heros = map.heros;
		if (!GestionClavier.ToucheRole.BAS.touche.enfoncee
		 && !GestionClavier.ToucheRole.HAUT.touche.enfoncee
		 && !GestionClavier.ToucheRole.GAUCHE.touche.enfoncee
		 && !GestionClavier.ToucheRole.DROITE.touche.enfoncee) {
			heros.avance = false;
			heros.animation = 0;
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
	 * @param event avec lequel le H�ros discute
	 */
	public final void normaliserApparenceDesInterlocuteursAvantMessage(final Event event) {
		// Normaliser le H�ros
		final Heros heros = this.map.heros;
		// Le H�ros arr�te son animation
		if (!heros.animeALArretActuel) {
			heros.animation = 0;
		}
		// Le H�ros arr�te son attaque
		heros.animationAttaque = 0;
				
		// Normaliser l'intelocuteur
		// L'interlocuteur arr�te son animation
		if (!event.animeALArretActuel) {
			event.animation = 0; //TODO attention : si la vignette par d�faut de l'event n'est pas la vignette 0, il va changer d'apparence
		}
		// L'interlocuteur se tourne vers le H�ros
		if (!event.directionFixeActuelle) {
			event.direction = RegarderUnEvent.calculerDirectionDeRegard(event, heros);
		}
	}
	
	/**
	 * D�placer le H�ros vers le haut
	 */
	public final void haut() {
		if (this.messageActuel!=null) {
			//les touches directionnelles servent au Message/Choix/EntrerUnNombre
			this.messageActuel.haut();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent � faire avancer le H�ros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * D�placer le H�ros vers la gauche
	 */
	public final void gauche() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.gauche();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent � faire avancer le H�ros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * D�placer le H�ros vers le bas
	 */
	public final void bas() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.bas();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent � faire avancer le H�ros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * D�placer le H�ros vers la droite
	 */
	public final void droite() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.droite();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent � faire avancer le H�ros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}
	
	/**
	 * Attaquer ou parler (suivant si gentil ou m�chant)
	 */
	public final void action() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.action();
		}
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
	public void accessoire() {
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
	
	/**
	 * Charge l'ic�ne de l'argent.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudArgent() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\ecaille icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final String typeDeLecteur() {
		return "LecteurMap";
	}
	
}
