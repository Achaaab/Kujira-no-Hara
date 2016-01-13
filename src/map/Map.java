package map;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Une Map est un d�cor rectangulaire constitu� de briques issues du Tileset.
 * Le Heros et les Events se d�placent dedans.
 */
public class Map {
	//constantes
	private static final int NOMBRE_ALTITUDES = 6;
	private static final int NOMBRE_ALTITUDES_SOUS_HEROS = 2;
	private static final int NOMBRE_ALTITUDES_SUR_HEROS = NOMBRE_ALTITUDES - NOMBRE_ALTITUDES_SOUS_HEROS;
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
	public BufferedImage imageCoucheSousHeros;
	public BufferedImage imageCoucheSurHeros;
	public ArrayList<Event> events;
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
	}

	/**
	 * L'affichage est un sandwich : une partie du d�cor est en fond, et les Events sont affich�s par dessus.
	 * On affiche en premier le d�cor arri�re.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
			final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS];
			for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
			}
			
			int numeroCarreau;
			int altitudeCarreau;
			for (int i = 0; i<largeur; i++) {
				for (int j = 0; j<hauteur; j++) {
					for (int k = 0; k<NOMBRE_LAYERS; k++) {
						final int[][] layer = layers[k];
						try {
							numeroCarreau = layer[i][j];
							altitudeCarreau = this.tileset.altitude[numeroCarreau];
							if (altitudeCarreau<NOMBRE_ALTITUDES_SOUS_HEROS) {
								couches[altitudeCarreau] = this.lecteur.dessinerCarreau(couches[altitudeCarreau], i, j, numeroCarreau, tileset);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							//case vide
						}
					}
				}
			}
			for (int i = 1; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				couches[0] = this.lecteur.superposerImages(couches[0], couches[i], 0, 0);
			}
			this.imageCoucheSousHeros = couches[0];
	}

	/**
	 * L'affichage est un sandwich : une partie du d�cor est affich�e par dessus les Events.
	 * On affiche en dernier le d�cor sup�rieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS];
		for (int i = 0; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
		}
		
		int numeroCarreau;
		int altitudeCarreau;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitude[numeroCarreau];
						if (altitudeCarreau>=NOMBRE_ALTITUDES_SOUS_HEROS) {
							couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS] = this.lecteur.dessinerCarreau(couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS], i, j, numeroCarreau, tileset);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
					}
				}
			}
		}
		for (int i = 1; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[0] = this.lecteur.superposerImages(couches[0], couches[i], 0, 0);
		}
		this.imageCoucheSurHeros = couches[0];
	}

	/**
	 * Constitue la liste des Events de la Map en allant lire dans le fichier JSON d�crivant la Map.
	 * @param jsonMap objet JSON d�crivant la Map
	 */
	private void importerLesEvenements(final JSONObject jsonMap) {
		try {
			this.events = new ArrayList<Event>();
			//d'abord le h�ros
			this.heros = new Heros(this, this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros);
			this.events.add(heros);
			//puis les autres
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			for (Object ev : jsonEvents) {
				final JSONObject jsonEvent = (JSONObject) ev;
				//r�cup�ration des donn�es dans le JSON
				final String nomEvent = jsonEvent.getString("nom");
				final int xEvent = jsonEvent.getInt("x");
				final int yEvent = jsonEvent.getInt("y");
				//instanciation de l'event
				Event event;
				try {
					//on essaye de le cr�er � partir de la biblioth�que JSON GenericEvents
					final JSONObject jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomEvent);
					int largeurHitbox;
					try {
						largeurHitbox = jsonEventGenerique.getInt("largeur");
					} catch (JSONException e2) {
						largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
					}
					int hauteurHitbox;
					try {
					hauteurHitbox = jsonEventGenerique.getInt("hauteur");
					} catch (JSONException e2) {
						hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
					}
					
					int direction;
					try {
						direction = jsonEvent.getInt("direction");
					} catch (Exception e1) {
						direction = Event.Direction.BAS; //direction par d�faut
					}
					
					final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
					event = new Event(this, xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
				} catch (Exception e3) {
					//l'event n'est pas g�n�rique, on le construit � partir de sa description dans la page JSON
					int largeurHitbox;
					try {
						largeurHitbox = jsonEvent.getInt("largeur");
					} catch (JSONException e2) {
						largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
					}
					int hauteurHitbox;
					try {
						hauteurHitbox = jsonEvent.getInt("hauteur");
					} catch (JSONException e2) {
						hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
					}
					int direction;
					try {
						direction = jsonEvent.getInt("direction");
					} catch (Exception e1) {
						direction = Event.Direction.BAS; //direction par d�faut
					}
					
					final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
					event = new Event(this, xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
				}
				this.events.add(event);
			}
			//events.add( new Panneau(this,2,7) );
			//events.add( new DarumaAleatoire(this,1,1) );
			//events.add( new Algue(this,2,8) );
			//events.add( new DarumaAleatoire(this,3,7) );
			//events.add( new DarumaAleatoire(this,3,8) );
		} catch (Exception e3) {
			System.err.println("Erreur lors de la constitution de la liste des events :");
			e3.printStackTrace();
		}
		//num�rotation des events
		for (int i = 0; i<this.events.size(); i++) {
			this.events.get(i).numero = i;
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
				for (int k = 0; k<NOMBRE_LAYERS&&passable; k++) { //si on en trouve une de non passable, on ne cherche pas les autres couches
					final int[][] layer = layers[k];
					numeroDeLaCaseDansLeTileset = layer[i][j];
					if (numeroDeLaCaseDansLeTileset!=-1 && !this.tileset.passabilite[numeroDeLaCaseDansLeTileset]) {
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
				try {
					couche[i][j] = Integer.parseInt((String) ligne.get(i));
				} catch (ClassCastException e) {
					couche[i][j] = (int) ligne.get(i);
				}
			}
		}
		return couche;
	}

	/**
	 * Inscrire l'Event dans la liste des Events en attente de suppression
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
	
}
