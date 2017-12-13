package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import commandes.Message;
import commandes.OuvrirMenu;
import jeu.Chronometre;
import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.meteo.Meteo;
import menu.Texte;
import mouvements.RegarderUnEvent;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;
import utilitaire.son.LecteurAudio;
import utilitaire.Maths;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il re�oit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	//jauges
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	private static final int X_AFFICHAGE_GADGET = 612;
	private static final int Y_AFFICHAGE_GADGET = 4;
	private static final int X_AFFICHAGE_ARGENT = 4;
	private static final int Y_AFFICHAGE_ARGENT = 450;
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
	
	/** Message � afficher dans la bo�te de dialogue */
	public Message messageActuel = null;
	
	/** Autoriser ou interdire l'acc�s au Menu depuis la Map ? */
	public boolean autoriserMenu = true;
	
	/** Position x de la cam�ra */
	private int xCamera;
	/** Position y de la cam�ra */
	private int yCamera;
	/** D�filement X de la cam�ra */
	public int defilementX;
	/** D�filement Y de la cam�ra */
	public int defilementY;
	/** D�calage (en pixels) de l'�cran � cause du tremblement de terre */
	public int tremblementDeTerre;
	/** Transition visuelle avec la Map pr�c�dente */
	public Transition transition = Transition.AUCUNE;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dont ce Lecteur assure l'affichage
	 * @param transition visuelle pour le passage d'une Map � l'autre
	 */
	public LecteurMap(final Fenetre fenetre, final Transition transition) {
		this.fenetre = fenetre;
		this.transition = transition;
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
		//final long t0 = System.nanoTime(); //mesure de performances
		
		//�ventuelle sortie vers la Map adjacente
		this.map.sortirVersLaMapAdjacente();
		
		//ouverture du tileset
		try {
			if (tilesetActuel == null) {
				tilesetActuel = this.map!=null && this.map.tileset!=null ? this.map.tileset : new Tileset(map.tileset.nom);
			}
		} catch (Exception e) {
			LOG.error("Erreur lors de l'ouverture du tileset :", e);
		}
		
		//calcul de la position de la cam�ra par rapport � la Map
		this.xCamera = calculerXCamera();
		this.yCamera = calculerYCamera();
		
		//panorama
		BufferedImage ecran = dessinerPanorama(xCamera, yCamera);
		
		//on dessine le d�cor inf�rieur
		animerLesAutotiles();
		ecran = dessinerDecorInferieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
		
		//lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		//d�placements des �v�nements
		deplacerLesEvents();
		
		//animation des �v�nements
		animerLesEvents(frame);

		//TODO DEBUG pour voir la hitbox de l'attaque du h�ros
		ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);
		
		//on dessine les �v�nements et la couche m�diane
		ecran = dessinerLesEvents(ecran, xCamera, yCamera, true, vignetteAutotileActuelle);
		
		//on dessine les animations
		ecran = Animation.dessinerLesAnimations(ecran);
		
		//ajouter imageCoucheSurHeros � l'�cran
		ecran = dessinerDecorSuperieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
		
		//m�t�o
		ecran = dessinerMeteo(ecran, frame);
		
		//brouillard
		if (map.brouillard != null) {
			ecran = map.brouillard.dessinerLeBrouillard(ecran, xCamera, yCamera, frame);
		}
		
		//ton
		if (this.map.tileset.ton != null) {
			ecran = Graphismes.superposerImages(ecran, ecran, 0, 0, Graphismes.OPACITE_MAXIMALE, ModeDeFusion.TON_DE_L_ECRAN);
		}
		
		//effet aquatique (lol)
		if (this.map.ondulation != null) {
			ecran = Ondulation.faireOndulerLEcran(ecran, frame);
		}
		
		// Transition visuelle avec la Map pr�c�dente
		if (!this.allume) {
			// Faire une capture d'�cran juste avant l'arr�t de l'ancienne Map
			final Lecteur futurLecteur0 = Fenetre.getFenetre().futurLecteur;
			if (futurLecteur0 instanceof LecteurMap) {
				final LecteurMap futurLecteur = (LecteurMap) futurLecteur0;
				if (!Transition.AUCUNE.equals(futurLecteur.transition)) {
					final boolean afficherLeHeros = Transition.ROND.equals(futurLecteur.transition);
					futurLecteur.transition.captureDeLaMapPrecedente = capturerLaMap(afficherLeHeros);
				}
			}
		} else {
			// R�utiliser cette capture d'�cran au d�but de la nouvelle Map
			ecran = this.transition.calculer(ecran, this.map, frame);
		}
		
		//afficher les images
		ecran = Picture.dessinerLesImages(ecran);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//chronometre
		final Chronometre chronometre = Fenetre.getPartieActuelle().chronometre;
		if (chronometre!=null) {
			ecran = chronometre.dessinerChronometre(ecran);
		}
		
		//on affiche le message
		if (messageActuel!=null) {
			ecran = Graphismes.superposerImages(
					ecran, 
					this.messageActuel.image,
					Message.positionBoiteMessage.xAffichage, 
					Message.positionBoiteMessage.yAffichage);
		}
		
		//supprimer events dont l'attribut "supprim�" est � true
		supprimerLesEventsASupprimer();
		
		ajouterLesEventsAAjouter();
		
		//this.fenetre.mesuresDePerformance.add(new Long(t1 - t0).toString());

		return ecran;
	}

	/**
	 * Dessiner l'image de fond (noir ou Panorama)
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y d ela cam�ra
	 * @return image de fond
	 */
	private BufferedImage dessinerPanorama(final int xCamera, final int yCamera) {
		if (this.map.panoramaActuel != null) {
			//parallaxe
			int xPanorama = this.map.parallaxeActuelle * xCamera / Maths.POURCENTS;
			int yPanorama = this.map.parallaxeActuelle * yCamera / Maths.POURCENTS;
			final int xMax = this.map.panoramaActuel.getWidth() - Fenetre.LARGEUR_ECRAN;
			final int yMax = this.map.panoramaActuel.getHeight() - Fenetre.HAUTEUR_ECRAN;
			if (xPanorama > xMax) {
				xPanorama = xMax;
			} else if (xPanorama < 0) {
				xPanorama = 0;
			}
			if (yPanorama > yMax) {
				yPanorama = yMax;
			} else if (yPanorama < 0) {
				yPanorama = 0;
			}
			return Graphismes.clonerUneImage(this.map.panoramaActuel.getSubimage(xPanorama, yPanorama, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN));
		} else {
			return Graphismes.ecranColore(Color.BLACK);
		}
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
			final ArrayList<String> contenuTexte = new ArrayList<String>();
			contenuTexte.add("" + argent);
			final Texte texte = new Texte(contenuTexte, Color.white, Color.black, Texte.Taille.MOYENNE);
			final BufferedImage texteImage = texte.getImage();
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
		} else {
			//lire tous les Events de la Map (sauf le H�ros)
			for (Event event : this.map.events) {
				if (!event.equals(this.map.heros)) { //le H�ros est calcul� en dernier
					activerUnePageEtLExecuter(event);
				}
			}
			//le H�ros est calcul� en dernier pour �viter les probl�mes d'�p�e
			activerUnePageEtLExecuter(this.map.heros);
			
			//lire les PagesCommunes
			lireLesPagesCommunes();
		}
	}
	
	/**
	 * Activer une Page (si aucune n'est activ�e) de l'Event (s'il n'est pas supprim� et l'ex�cuter.
	 * @param event dont il faut activer une Page et l'ex�cuter
	 */
	private void activerUnePageEtLExecuter(final Event event) {
		if (!event.supprime && !event.saute) {
			if (event.pageActive == null || event.pageActive.commandes==null) {
				event.activerUnePage();
			}

			if (event.pageActive != null) {
				event.pageActive.executer();
			}
		}
	}
	
	/**
	 * Lire les Pages de code commun.
	 */
	private void lireLesPagesCommunes() {
		if (this.pagesCommunes.size() > 0) {
			for (PageCommune pageCommune : this.pagesCommunes) {
				if (!pageCommune.active) {
					pageCommune.essayerDActiver();
				}
				if (pageCommune.active) {
					pageCommune.executer();
				}
			}
		}
	}

	/**
	 * Calculer le nouvel �cran, avec les Events dessin�s dessus.
	 * Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @param ecran sur lequel on dessine les Events
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @param dessinerLeHeros le H�ros doit-il �tre visible sur l'ancienne Map ?
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return �cran avec les Events dessin�s dessus
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, final int xCamera, final int yCamera, final boolean dessinerLeHeros, 
			final int vignetteAutotile) {
		try {
			Collections.sort(this.map.events, this.comparateur); //on trie les events du plus derri�re au plus devant
			int bandeletteActuelle = 0;
			int bandeletteEvent = 0;
			for (Event event : this.map.events) {
				if (!event.supprime) {
					//dessiner la bandelette de d�cor m�dian
					bandeletteEvent = event.y / Fenetre.TAILLE_D_UN_CARREAU;
					if (bandeletteEvent > bandeletteActuelle) {
						final BufferedImage imageBandelette = this.map.getImageCoucheAvecHeros(vignetteAutotile, bandeletteActuelle, bandeletteEvent);
						ecran = Graphismes.superposerImages(ecran, imageBandelette,
								-xCamera, bandeletteActuelle*Fenetre.TAILLE_D_UN_CARREAU-yCamera);
						
						bandeletteActuelle = bandeletteEvent;
					}
					
					//dessiner l'Event
					if (dessinerLeHeros || !event.equals(map.heros)) {
						ecran = dessinerEvent(ecran, event, xCamera, yCamera);
					}
				}
			}
			
			//derni�re bandelette
			final BufferedImage imageBandelette = this.map.getImageCoucheAvecHeros(vignetteAutotile, bandeletteEvent, this.map.hauteur+1);
			ecran = Graphismes.superposerImages(ecran, imageBandelette,
					-xCamera, bandeletteActuelle*Fenetre.TAILLE_D_UN_CARREAU-yCamera);
			
		} catch (Exception e) {
			LOG.error("Erreur lors du dessin des �v�nements :", e);
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
				final boolean passerALAnimationSuivante = (frame % event.frequenceActuelle == 0) //fr�quence d'animation
				|| (event.avance && !event.avancaitALaFramePrecedente); //la premi�re frame d'animation est un pas
				
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
			LOG.error("erreur lors de l'animation des �v�nements dans la boucle d'affichage de la map :", e);
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
			LOG.error("Erreur lors du d�placement des �v�nements :", e);
		}
	}

	/**
	 * Certains Events ont �t� marqu�s "� supprimer" durant la lecture des Commandes.
	 * On les �limine maintenant, une fois que la lecture des Commandes est termin�e.
	 * En effet on ne peut pas supprimer des Events lorsqu'on est encore dans la boucle qui parcourt la liste des Events.
	 */
	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		Event eventAsupprimer;
		for (int i = 0; i<nombreDEvents; i++) {
			eventAsupprimer = this.map.events.get(i);
			if (eventAsupprimer.supprime) {
				this.map.events.remove(i);
				LOG.info("Suppression de l'event "+eventAsupprimer.nom);
				LOG.debug("Nombre d'events sur la map : "+this.map.events.size());
				nombreDEvents--;
				i--;
			}
		}
	}
	
	/**
	 * Ajouter les nouveaux Events � ajouter � la Map pour le tour suivant.
	 */
	private void ajouterLesEventsAAjouter() {
		int nombreDEvents = this.map.eventsAAjouter.size();
		Event eventAajouter;
		for (int i = 0; i<nombreDEvents; i++) {
			eventAajouter = this.map.eventsAAjouter.get(i);
			
			//on l'ajoute au hash des Events avec un num�ro
			if (eventAajouter.id < 0) {
				eventAajouter.id = this.map.calculerNouvelIdPourEventsHash();
			}
			this.map.eventsHash.put(eventAajouter.id, eventAajouter);
			
			//on l'ajoute � la liste des Events
			this.map.events.add(eventAajouter);
			eventAajouter.id = this.map.events.size();
			
			LOG.info("Ajout de l'event "+eventAajouter.nom);
			LOG.debug("Nombre d'events sur la map : "+this.map.events.size());
			this.map.eventsAAjouter.remove(i);
			nombreDEvents--;
			i--;
		}
	}
	
	

	/**
	 * Faire une capture d'�cran des collisions
	 */
	public final void photographierCollision() {
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
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
			if (event.saute && !event.directionFixeActuelle) {
				//l'Event est en train de sauter
				animation = 0; //TODO attention : si la vignette normale de l'event n'est pas la vignette 0, l'event va changer d'apparence
				
			} else {
				//l'Event ne Saute pas
				animation = event.animation;
			}
			
			/*
			//DEBUG pour visualiser les collisions //TODO commenter
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			//voil�
			*/
			
			final BufferedImage apparence;
			try {
				apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			} catch (RasterFormatException rfe) {
				LOG.error("La vignette d'Event est mal d�coup�e "
						+ "(animation:"+animation+";direction:"+direction+";"
						+ "largeur:"+largeur+";hauteur:"+hauteur+")", rfe
				);
				return ecran;
			}
			return Graphismes.superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera, event.opaciteActuelle, event.modeDeFusionActuel);
		} else {
			//l'event n'a pas d'image
			return ecran;
		}
	}

	@Override
	public final void keyPressed(final ToucheRole touchePressee) {
		// action sp�cifique selon la touche
		switch (touchePressee) {
			case MENU : 
				this.ouvrirLeMenu(); 
				break;
			case HAUT : 
				this.haut(); 
				break;
			case GAUCHE : 
				this.gauche(); 
				break;
			case BAS : 
				this.bas(); 
				break;
			case DROITE : this.droite(); 
				break;
			case ARME_SUIVANTE : 
				this.equiperArmeSuivante(); 
				break;
			case ACTION : 
				this.action(); 
				break;
			case ARME_PRECEDENTE : 
				this.equiperArmePrecedente(); 
				break;
			case ACTION_SECONDAIRE : 
				//rien
				break;
			case CAPTURE_D_ECRAN : 
				this.faireUneCaptureDEcran(); 
				break;
			case BREAKPOINT :
				final Fenetre fenetre = Fenetre.getFenetre();
				break;
			default : 
				break; // touche inconnue
		}
	}
	
	/**
	 * Ouvre une autre Map (dans un nouveau LecteurMap).
	 * @warning cette m�thode ne doit �tre appel�e que par le nouveau Lecteur !
	 * @param nouvelleMap sur laquelle le H�ros voyage
	 */
	public final void devenirLeNouveauLecteurMap(final Map nouvelleMap) {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
		
		// On d�truit le Tileset actuel si le prochain n'est pas le m�me
		if (tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) {
			this.tilesetActuel = null;
		}
	}
	
	/**
	 * Prendre une capture d'�cran de la Map sans le H�ros ni les jauges.
	 * @param afficherLeHeros le H�ros doit-il �tre visible sur l'ancienne Map ?
	 * @return capture de la Map
	 */
	private BufferedImage capturerLaMap(final boolean afficherLeHeros) {
		BufferedImage capture = dessinerPanorama(this.xCamera, this.yCamera);
		capture = dessinerDecorInferieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerLesEvents(capture, this.xCamera, this.yCamera, afficherLeHeros, this.vignetteAutotileActuelle);
		capture = Animation.dessinerLesAnimations(capture);
		capture = dessinerDecorSuperieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerMeteo(capture, this.frameActuelle);
		//brouillard
		if (this.map.brouillard != null) {
			capture = map.brouillard.dessinerLeBrouillard(capture, this.xCamera, this.yCamera, this.frameActuelle);
		}
		//ton
		if (this.map.tileset.ton != null) {
			capture = Graphismes.superposerImages(capture, capture, 0, 0, Graphismes.OPACITE_MAXIMALE, ModeDeFusion.TON_DE_L_ECRAN);
		}
		return capture;
	}

	/**
	 * Ouvrir le Menu du jeu.
	 * On quitte la Map temporairement (elle est m�moris�e) pour parcourir le Menu.
	 */
	public final void ouvrirLeMenu() {
		if (!this.stopEvent && this.autoriserMenu) { //impossible d'ouvrir le Menu en cas de stopEvent ou de Menu interdit
			final Commande menuPause = new OuvrirMenu("Statut", 0);
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
			return this.tremblementDeTerre;
		} else {
			//grande map, d�filement possible
			int nouveauXCamera = map.heros.x - Fenetre.LARGEUR_ECRAN/2;
			
			if (nouveauXCamera<0) { //cam�ra ne d�borde pas de la map � gauche
				return (this.defilementX>0 ? this.defilementX : 0) 
						+ this.tremblementDeTerre;
			} else if (nouveauXCamera+Fenetre.LARGEUR_ECRAN > largeurMap*Fenetre.TAILLE_D_UN_CARREAU) { //cam�ra ne d�borde pas de la map � droite
				return largeurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.LARGEUR_ECRAN 
						+ (this.defilementX<0 ? this.defilementX : 0) 
						+ this.tremblementDeTerre;
			} else {
				return nouveauXCamera 
						+ this.defilementX 
						+ this.tremblementDeTerre;
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
			int nouveauYCamera = map.heros.y - Fenetre.HAUTEUR_ECRAN/2;
			
			if (nouveauYCamera<0) { //cam�ra ne d�borde pas de la map en haut
				return 0 + (this.defilementY>0 ? this.defilementY : 0);
			} else if (nouveauYCamera+Fenetre.HAUTEUR_ECRAN > hauteurMap*Fenetre.TAILLE_D_UN_CARREAU) { //cam�ra ne d�borde pas de la map en bas
				return hauteurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.HAUTEUR_ECRAN + (this.defilementY<0 ? this.defilementY : 0);
			} else {
				return nouveauYCamera + this.defilementY;
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
		if (!event.animeALArretActuel && !event.directionFixeActuelle) {
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
	 * Charge le petit carr� blanc qui entoure l'Arme dans le HUD � l'�cran.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudTouches() {
		try {
			return Graphismes.ouvrirImage("Pictures", "carre arme kujira.png");
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
			return Graphismes.ouvrirImage("Icons", "ecaille icon.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final String typeDeLecteur() {
		return "LecteurMap";
	}

	@Override
	public final void lireMusique() {
		final Map map = ((LecteurMap) this).map;
		if (map.volumeBGM == null) {
			LOG.warn("volumeBGM");
		}
		if (map.volumeBGS == null) {
			LOG.warn("volumeBGS");
		}
		if (map.nomBGM != null && !map.nomBGM.isEmpty()) {
			LecteurAudio.playBgm(map.nomBGM, map.volumeBGM, 0);
		}
		if (map.nomBGS != null && !map.nomBGS.isEmpty()) {
			LecteurAudio.playBgs(map.nomBGS, map.volumeBGS, 0);
		}
	}
	
	/**
	 * La Transition � partir de la Map pr�c�dente est-elle termin�e ?
	 * @return true si fini
	 */
	public final boolean laTransitionEstTerminee() {
		return Transition.AUCUNE.equals(this.transition) || this.frameActuelle >= Transition.DUREE_TRANSITION;
	}
	
}
