package map;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.Graphismes;
import utilitaire.InterpreteurDeJson;

/**
 * Une Map est un d�cor rectangulaire constitu� de briques issues du Tileset.
 * Le Heros et les Events se d�placent dedans.
 */
public class Map {
	//constantes
	/** Chaque carreau du Tileset poss�de une altitude intrins�que */
	private static final int NOMBRE_ALTITUDES = 6;
	/** Certaines altitudes sont affich�es sous le H�ros */
	private static final int NOMBRE_ALTITUDES_SOUS_HEROS = 2;
	/** Certaines altitudes sont affich�es au dessus du H�ros */
	private static final int NOMBRE_ALTITUDES_SUR_HEROS = NOMBRE_ALTITUDES - NOMBRE_ALTITUDES_SOUS_HEROS;
	/** Le d�cor est constitu� de 3 couches, afin de pouvoir superposer plusieurs carreaux au m�me endroit de la Map */
	private static final int NOMBRE_LAYERS = 3;
	
	public final int numero;
	public LecteurMap lecteur;
	public String nom;
	public String nomBGM; //musique
	public String nomBGS; //fond sonore
	public String nomTileset;
	public Tileset tileset; //image contenant les decors
	public final int largeur;
	public final int hauteur;
	public final int[][] layer0; //couche du sol
	public final int[][] layer1; //couche de decor 1
	public final int[][] layer2; //couche de decor 2
	public final int[][][] layers;
	/** en cas d'Autotile anim� */
	private BufferedImage[] imagesCoucheSousHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME]; 
	/** en cas d'Autotile anim� */
	private BufferedImage[] imagesCoucheSurHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME]; 
	public boolean contientDesAutotilesAnimes;
	public Brouillard brouillard;
	/** liste des Events rang�s par coordonn�e y */
	public ArrayList<Event> events;
	/** hashmap des Events rang�s par id, 0 pour le H�ros */
	public HashMap<Integer, Event> eventsHash;
	public Heros heros;
	public int xDebutHeros;
	public int yDebutHeros;
	public int directionDebutHeros;
	public boolean[][] casePassable;
	public final boolean defilementCameraX;
	public final boolean defilementCameraY;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Map, c'est-�-dire num�ro du fichier map (au format JSON) � charger
	 * @param lecteur de la Map
	 * @param xDebutHerosArg position x du Heros � son arriv�e sur la Map
	 * @param yDebutHerosArg position y du Heros � son arriv�e sur la Map
	 * @param directionDebutHeros direction du Heros � son arriv�e sur la Map
	 * @throws FileNotFoundException 
	 */
	public Map(final int numero, final LecteurMap lecteur, final int xDebutHerosArg, final int yDebutHerosArg, final int directionDebutHeros) throws FileNotFoundException {
		this.numero = numero;
		this.lecteur = lecteur;
		lecteur.map = this; //on pr�vient le Lecteur qu'il a une Map
		
		//la map est un fichier JSON
		final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numero);
		
		this.nomBGM = jsonMap.getString("bgm");
		this.nomBGS = jsonMap.getString("bgs");
		this.nomTileset = jsonMap.getString("tileset");
		this.largeur = jsonMap.getInt("largeur");
		this.hauteur = jsonMap.getInt("hauteur");
		this.defilementCameraX = largeur>(Fenetre.LARGEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
		this.defilementCameraY = hauteur>(Fenetre.HAUTEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
		this.layer0 = recupererCouche(jsonMap, 0);
		this.layer1 = recupererCouche(jsonMap, 1);
		this.layer2 = recupererCouche(jsonMap, 2);
		this.layers = new int[][][] {this.layer0, this.layer1, this.layer2};
		this.xDebutHeros = xDebutHerosArg;
		this.yDebutHeros = yDebutHerosArg;
		this.directionDebutHeros = directionDebutHeros;
		
		//chargement du tileset
		try {
			//si jamais le Tileset est le m�me, pas la peine de le recr�er
			final Tileset tilesetActuel = ((LecteurMap) Fenetre.getFenetre().lecteur).tilesetActuel;
			if (this.nomTileset.equals(tilesetActuel.nom)) {
				this.tileset = tilesetActuel;
				System.out.println("Le Tileset n'a pas chang�, on garde le m�me.");
			} else {
				throw new Exception("Le Tileset a chang�.");
			}
		} catch (Exception e1) {
			//impossible de convertir le Lecteur en LecteurMap car c'est un LecteurMenu
			//ou bien
			//le Lecteur actuel est null
			//ou bien
			//le Tileset a chang�
			try {
				System.out.println("Le Tileset a chang�, il faut le recharger.");
				this.tileset = new Tileset(this.nomTileset);
			} catch (IOException e2) {
				System.err.println("Erreur lors de la cr�ation du Tileset :");
				e2.printStackTrace();
			}
		}
		
		//on dessine la couche de d�cor inf�rieure, qui sera sous le h�ros et les �v�nements
			creerImageDuDecorEnDessousDuHeros();
		
		//on dessine la couche de d�cor sup�rieure, qui sera au dessus du h�ros et des �v�nements
			creerImageDuDecorAuDessusDuHeros();
		
		//importer les events
			importerLesEvenements(jsonMap);
			
		//cr�ation de la liste des cases passables
			creerListeDesCasesPassables();
			
		//cr�ation du Brouillard
			this.brouillard = Brouillard.creerBrouillardAPartirDeJson(jsonMap);

	}

	/**
	 * L'affichage est un sandwich : une partie du d�cor est en fond, et les Events sont affich�s par dessus.
	 * On affiche en premier le d�cor arri�re.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS];
		final BufferedImage[][] couchesAutotile = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS][Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
			couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
		}
		
		int numeroCarreau;
		int altitudeCarreau;
		BufferedImage couche;
		BufferedImage[] coucheAnimee;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
						if (altitudeCarreau<NOMBRE_ALTITUDES_SOUS_HEROS) {
							if (numeroCarreau >= 0) { 
								//case normale
								couche = couches[altitudeCarreau];
								dessinerCarreau(couche, i, j, numeroCarreau, tileset);
							} else if (numeroCarreau < -1) { 
								//autotile
								couche = couches[altitudeCarreau];
								coucheAnimee = couchesAutotile[altitudeCarreau];
								dessinerAutotile(couche, coucheAnimee, i, j, numeroCarreau, tileset, layer);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
						//System.err.println(e);
					}
				}
			}
		}
		
		//assemblage des diff�rentes altitudes
		for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
			for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				//TODO ne pas superposer sur une image nulle, faire un clonage
				this.imagesCoucheSousHeros[j] = Graphismes.superposerImages(this.imagesCoucheSousHeros[j], couches[i], 0, 0);
				this.imagesCoucheSousHeros[j] = Graphismes.superposerImages(this.imagesCoucheSousHeros[j], couchesAutotile[i][j], 0, 0);
			}
		}
	}

	/**
	 * L'affichage est un sandwich : une partie du d�cor est affich�e par dessus les Events.
	 * On affiche en dernier le d�cor sup�rieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS];
		final BufferedImage[][] couchesAutotile = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS][Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		for (int i = 0; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
		}
		
		int numeroCarreau;
		int altitudeCarreau;
		BufferedImage couche;
		BufferedImage[] coucheAnimee;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
						if (altitudeCarreau >= NOMBRE_ALTITUDES_SOUS_HEROS) {
							if (numeroCarreau >= 0) { 
								//case normale
								couche = couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								dessinerCarreau(couche, i, j, numeroCarreau, tileset);
							} else if (numeroCarreau < -1) { 
								//autotile
								coucheAnimee = couchesAutotile[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								couche = couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								dessinerAutotile(couche, coucheAnimee, i, j, numeroCarreau, tileset, layer);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
						//System.err.println(e);
					}
				}
			}
		}
		
		//assemblage des diff�rentes altitudes
		for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
			for (int i = 0; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
				//TODO ne pas superposer sur une image nulle, faire un clonage
				this.imagesCoucheSurHeros[j] = Graphismes.superposerImages(this.imagesCoucheSurHeros[j], couches[i], 0, 0);
				this.imagesCoucheSurHeros[j] = Graphismes.superposerImages(this.imagesCoucheSurHeros[j], couchesAutotile[i][j], 0, 0);
			}
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
	 */
	public final void dessinerCarreau(final BufferedImage ecran, final int xEcran, final int yEcran, final int numeroCarreau, final Tileset tilesetUtilise) {
		final BufferedImage dessinCarreau = tilesetUtilise.carreaux[numeroCarreau];
		Graphismes.superposerImages(ecran, dessinCarreau, xEcran*Fenetre.TAILLE_D_UN_CARREAU, yEcran*Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Dessiner � l'�cran un carreau issu d'un autotile.
	 * @warning Ne pas oublier de r�cup�rer le r�sultat de cette m�thode.
	 * @param decor sup�rieur ou inf�rieur de la Map, sur lequel on doit dessiner un carreau
	 * @param decorAnime partie anim�e du d�cor (� peindre dans le cas d'un Autotile anim�)
	 * @param x coordonn�e x du carreau sur la Map
	 * @param y coordonn�e y du carreau sur la Map
	 * @param numeroCarreau num�ro de l'autotile (num�ro n�gatif)
	 * @param tilesetUtilise Tileset utilis� pour interpr�ter le d�cor de la Map
	 * @param layer couche de d�cor � laquelle appartient le carreau
	 */
	public final void dessinerAutotile(final BufferedImage decor, final BufferedImage[] decorAnime, final int x, final int y,
			final int numeroCarreau, final Tileset tilesetUtilise, final int[][] layer) {
		final Autotile autotile = tilesetUtilise.autotiles.get(numeroCarreau);
		
		//on pr�vient la Map qu'elle aura un d�cor anim�
		if (autotile.anime) {
			this.contientDesAutotilesAnimes = true;
		}
		
		final BufferedImage[] dessinCarreau = autotile.calculerAutotile(x, y, this.largeur, this.hauteur, numeroCarreau, layer);
		if (autotile.anime) {
			//d�cor anim� : on dessine les 4 �tapes de l'animation du d�cor
			for (int i = 0; i<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[i], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
			}
		} else {
			//d�cor fixe
			Graphismes.superposerImages(decor, dessinCarreau[0], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
		}
	}

	/**
	 * Constitue la liste des Events de la Map en allant lire dans le fichier JSON d�crivant la Map.
	 * @param jsonMap objet JSON d�crivant la Map
	 */
	private void importerLesEvenements(final JSONObject jsonMap) {
		try {
			this.events = new ArrayList<Event>();
			this.eventsHash = new HashMap<Integer, Event>();
			//d'abord le h�ros
			this.heros = new Heros(this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros);
			this.events.add(heros);
			//this.eventsHash.put(0, heros);
			//puis les autres
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			InterpreteurDeJson.recupererLesEvents(this.events, jsonEvents);
		} catch (Exception e3) {
			System.err.println("Erreur lors de la constitution de la liste des events :");
			e3.printStackTrace();
		}
		//num�rotation des Events
		final int nombreDEvents = this.events.size();
		Event event;
		for (int i = 0; i<nombreDEvents; i++) {
			event = this.events.get(i);
			event.map = this;
			event.numero = i;
			if (this.eventsHash.containsKey(event.id)) { //la num�rotation des Events comporte un doublon !
				System.err.println("Un autre event porte d�j� le num�ro : " + event.id);
			}
			this.eventsHash.put(event.id, event);
		}
	}

	/**
	 * Cr�ation d'un tableau pour connaitre les passabilit�s de la Map plus rapidement par la suite.
	 */
	private void creerListeDesCasesPassables() {
		this.casePassable = new boolean[this.largeur][this.hauteur];
		boolean passable;
		int numeroDeLaCaseDansLeTileset;
		for (int i = 0; i<this.largeur; i++) {
			for (int j = 0; j<this.hauteur; j++) {
				passable = true;
				this.casePassable[i][j] = true;
				for (int k = 0; (k<NOMBRE_LAYERS && passable); k++) { //si on en trouve une de non passable, on ne cherche pas les autres couches
					final int[][] layer = layers[k];
					numeroDeLaCaseDansLeTileset = layer[i][j];
					if (this.tileset.laCaseEstUnObstacle(numeroDeLaCaseDansLeTileset)) {
						this.casePassable[i][j] = false;
						passable = false;
					}
				}
			}
		}
	}

	/**
	 * Va chercher une couche de d�cor en particulier dans le fichier JSON qui repr�sente la Map.
	 * @param jsonMap objet JSON repr�sentant la map
	 * @param numeroCouche num�ro de la couche � r�cuperer
	 * @return un tableau bidimentionnel contenant le d�cor situ� sur cette couche
	 */
	private int[][] recupererCouche(final JSONObject jsonMap, final int numeroCouche) {
		final int[][] couche = new int[largeur][hauteur];
		final JSONArray array = jsonMap.getJSONArray("couche"+numeroCouche);
		JSONArray ligne;
		for (int j = 0; j<hauteur; j++) {
			ligne = (JSONArray) array.get(j);
			for (int i = 0; i<largeur; i++) {
				int numeroCarreau;
				try {
					numeroCarreau = (int) ligne.get(i);
				} catch (ClassCastException e) {
					numeroCarreau = Integer.parseInt((String) ligne.get(i));
				}
				couche[i][j] = numeroCarreau;
			}
		}
		return couche;
	}

	/**
	 * Inscrire l'Event dans la liste des Events en attente de suppression.
	 * L'Event sera supprim� � la fin de la boucle d'affichage.
	 * @param numeroEventASupprimer num�ro de l'Event qu'il faut inscrire � la suppression
	 * @return bool�en pour savoir si l'Event � supprimer a bien �t� trouv� dans la liste des �v�nements
	 */
	public final boolean supprimerEvenement(final int numeroEventASupprimer) {
		for (Event event : this.events) {
			if (event.numero == numeroEventASupprimer) {
				event.supprime = true;
				return true;
			}
		}
		System.out.println("L'�v�nement � supprimer num�ro "+numeroEventASupprimer+" n'a pas �t� trouv� dans la liste.");
		return false;
	}

	/**
	 * Obtenir le d�cor � afficher par dessus le H�ros.
	 * Ce d�cor est compos� du d�cor fixe et d'�ventuels autotiles anim�s.
	 * @param vignetteAutotileActuelle vignette de l'Autotile � afficher
	 * @return d�cor sup�rieur, avec l'autotile d�pendant de la frame
	 */
	public final BufferedImage getImageCoucheSurHeros(final int vignetteAutotileActuelle) {
		return this.imagesCoucheSurHeros[vignetteAutotileActuelle];
	}

	/**
	 * Obtenir le d�cor � afficher en dessous du H�ros.
	 * Ce d�cor est compos� du d�cor fixe et d'�ventuels autotiles anim�s.
	 * @param vignetteAutotileActuelle frame actuelle du Lecteur
	 * @return d�cor inf�rieur, avec l'autotile d�pendant de la frame
	 */
	public final BufferedImage getImageCoucheSousHeros(final int vignetteAutotileActuelle) {
		return this.imagesCoucheSousHeros[vignetteAutotileActuelle];
	}
	
}
