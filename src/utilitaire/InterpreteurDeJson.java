package utilitaire;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import conditions.Condition;
import jeu.Objet;
import main.Commande;
import main.Fenetre;
import map.Autotile;
import map.Event;
import map.Map;
import map.Tileset;
import menu.ElementDeMenu;
import menu.Image;
import menu.Menu;
import menu.Texte;
import menu.Texte.Taille;
import mouvements.Mouvement;
import utilitaire.graphismes.Graphismes;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	/**
	 * Charger un objet JSON quelconque
	 * @param nomFichier nom du fichier JSON � charger
	 * @param adresse du fichier JSON � charger
	 * @return objet JSON
	 * @throws FileNotFoundException fichier JSON introuvable
	 * @throws JSONException Erreur de syntaxe dans le fichier JSON
	 */
	private static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws FileNotFoundException, JSONException {
		final String nomFichierJson = adresse+nomFichier+".json";
		final Scanner scanner = new Scanner(new File(nomFichierJson));
		final String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		try {
			final JSONObject jsonObject = new JSONObject(contenuFichierJson);
			return jsonObject;
		} catch (JSONException e) {
			System.err.println("Erreur de syntaxe dans le fichier JSON "+nomFichier);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Charger le param�trage d'une nouvelle Partie au format JSON.
	 * @return objet JSON contenant le param�trage d'une nouvelle Partie
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonNouvellePartie() throws FileNotFoundException {
		return ouvrirJson("nouvellePartie", ".\\ressources\\Data\\");
	}
	
	/**
	 * Charger la liste des Qu�tes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Qu�tes du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonQuetes() throws FileNotFoundException {
		final JSONObject jsonQuetes = ouvrirJson("quetes", ".\\ressources\\Data\\");
		return jsonQuetes.getJSONArray("quetes");
	}
	
	/**
	 * Charger la liste des Objets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Objets du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonObjets() throws FileNotFoundException {
		final JSONObject jsonObjets = ouvrirJson("objets", ".\\ressources\\Data\\");
		return jsonObjets.getJSONArray("objets");
	}
	
	/**
	 * Charger la liste des Armes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Armes du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonArmes() throws FileNotFoundException {
		final JSONObject jsonArmes = ouvrirJson("armes", ".\\ressources\\Data\\");
		return jsonArmes.getJSONArray("armes");
	}
	
	/**
	 * Charger la liste des Gadgets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Gadgets du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonGadgets() throws FileNotFoundException {
		final JSONObject jsonGadgets = ouvrirJson("gadgets", ".\\ressources\\Data\\");
		return jsonGadgets.getJSONArray("gadgets");
	}
	
	/**
	 * Charger une Map au format JSON.
	 * @param numero de la Map � charger
	 * @return objet JSON contenant la description de la Map
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonMap(final int numero) throws FileNotFoundException {
		return ouvrirJson(""+numero, ".\\ressources\\Data\\Maps\\");
	}
	
	/**
	 * Charger un Tileset au format JSON.
	 * @param nom du Tileset � charger
	 * @return objet JSON contenant la description du Tileset
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonTileset(final String nom) throws FileNotFoundException {
		return ouvrirJson(""+nom, ".\\ressources\\Data\\Tilesets\\");
	}

	/**
	 * Charger un Event g�n�rique au format JSON.
	 * Les Events g�n�riques sont situ�s dans le dossier "ressources/Data/GenericEvents"
	 * @param nomEvent nom de l'Event g�n�rique
	 * @return objet JSON contenant la description de l'Event
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonEventGenerique(final String nomEvent) throws FileNotFoundException {
		return ouvrirJson(nomEvent, ".\\ressources\\Data\\GenericEvents\\");
	}
	
	/**
	 * Traduit les Conditions depuis le format JSON et les range dans la liste des Conditions de la Page.
	 * @param conditions liste des Conditions de la Page
	 * @param conditionsJSON tableau JSON contenant les Conditions au format JSON
	 */
	public static void recupererLesConditions(final ArrayList<Condition> conditions, final JSONArray conditionsJSON) {
		for (Object conditionJSON : conditionsJSON) {
			try {
				final Class<?> classeCondition = Class.forName("conditions.Condition"+((JSONObject) conditionJSON).get("nom"));
				try {
					//cas d'une Condition sans param�tres
					
					final Constructor<?> constructeurCondition = classeCondition.getConstructor();
					final Condition condition = (Condition) constructeurCondition.newInstance();
					conditions.add(condition);
					
				} catch (NoSuchMethodException e0) {
					//cas d'une Condition utilisant des param�tres
					
					final Iterator<String> parametresNoms = ((JSONObject) conditionJSON).keys();
					String parametreNom; //nom du param�tre pour instancier la Condition
					Object parametreValeur; //valeur du param�tre pour instancier la Condition
					final HashMap<String, Object> parametres = new HashMap<String, Object>();
					while (parametresNoms.hasNext()) {
						parametreNom = parametresNoms.next();
						if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
							parametreValeur = ((JSONObject) conditionJSON).get(parametreNom);
							parametres.put( parametreNom, parametreValeur );
						}
					}
					final Constructor<?> constructeurCondition = classeCondition.getConstructor(parametres.getClass());
					final Condition condition = (Condition) constructeurCondition.newInstance(parametres);
					conditions.add(condition);
				}
				
			} catch (Exception e1) {
				System.err.println("Erreur lors de l'instanciation de la Condition :");
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Traduit les Commandes depuis le format JSON et les range dans la liste des Commandes de la Page.
	 * @param commandes liste des Commandes de la Page.
	 * @param commandesJSON tableau JSON contenant les Commandes au format JSON
	 */
	public static void recupererLesCommandesEvent(final ArrayList<Commande> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			final Commande commande = recupererUneCommande( (JSONObject) commandeJSON );
			if (commande != null) {
				//on v�rifie que c'est bien une CommandeEvent
				if (commande instanceof CommandeEvent) {
					commandes.add(commande);
				} else {
					System.err.println("La commande "+commande.getClass().getName()+" n'est pas une CommandeEvent !");
				}
			}
		}
	}
	
	/**
	 * Traduit les Commandes depuis le format JSON et les range dans la liste des Commandes de l'Element de Menu.
	 * @param commandes liste des Commandes de la Page.
	 * @param commandesJSON tableau JSON contenant les Commandes au format JSON
	 */
	public static void recupererLesCommandesMenu(final ArrayList<CommandeMenu> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			final Commande commande = recupererUneCommande( (JSONObject) commandeJSON );
			if (commande != null) {
				//on v�rifie que c'est bien une CommandeMenu
				if (commande instanceof CommandeMenu) {
					commandes.add((CommandeMenu) commande);
				} else {
					System.err.println("La commande "+commande.getClass().getName()+" n'est pas une CommandeMenu !");
				}
			}
		}
	}
	
	/**
	 * Traduit un objet JSON repr�sentant une Commande en vrai objet Commande.
	 * @param commandeJson objet JSON repr�sentant une Commande
	 * @return objet Commande
	 */
	public static Commande recupererUneCommande(final JSONObject commandeJson) {
		try {
			Class<?> classeCommande;
			final String nomClasseCommande = commandeJson.getString("nom");
			try {
				//la Commande est juste une Commande du package "commandes"
				classeCommande = Class.forName("commandes." + nomClasseCommande);
			} catch (ClassNotFoundException e0) {
				//la Commande est une Condition du package "conditions"
				classeCommande = Class.forName("conditions." + nomClasseCommande);
			}
			final Iterator<String> parametresNoms = commandeJson.keys();
			String parametreNom; //nom du param�tre pour instancier la Commande Event
			Object parametreValeur; //valeur du param�tre pour instancier la Commande Event
			final HashMap<String, Object> parametres = new HashMap<String, Object>();
			while (parametresNoms.hasNext()) {
				parametreNom = parametresNoms.next();
				if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
					parametreValeur = commandeJson.get(parametreNom);
					parametres.put( parametreNom, parametreValeur );
				}
			}
			final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
			final Commande commande = (Commande) constructeurCommande.newInstance(parametres);
			return commande;
		} catch (Exception e1) {
			System.err.println("Impossible de traduire l'objet JSON en CommandeEvent.");
			e1.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Traduit les CommandesMenu depuis le format JSON et les range dans la liste des CommandesMenu de l'Element.
	 * @param commandes liste des Commandes de l'Element.
	 * @param commandesJSON tableau JSON contenant les CommandesMenu au format JSON
	 */
	public static void recupererLesCommandes(final ArrayList<Commande> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			try {
				final String nomClasseCommande = ((JSONObject) commandeJSON).getString("nom");
				final Class<?> classeCommande = Class.forName("commandes." + nomClasseCommande);
				final Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
				String parametreNom; //nom du param�tre pour instancier la Commande Event
				Object parametreValeur; //valeur du param�tre pour instancier la Commande Event
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				while (parametresNoms.hasNext()) {
					parametreNom = parametresNoms.next();
					if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
						parametreValeur = ((JSONObject) commandeJSON).get(parametreNom);
						parametres.put( parametreNom, parametreValeur );
					}
				}
				final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
				final Commande commande = (Commande) constructeurCommande.newInstance(parametres);
				commandes.add(commande);
			} catch (Exception e1) {
				System.err.println("Impossible de traduire l'objet JSON en CommandeMenu.");
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Traduit les Events depuis le format JSON et les range dans la liste des Events de la Map.
	 * @param events liste des Events de la Map
	 * @param eventsJSON tableau JSON contenant les Events au format JSON
	 * @param map des Events
	 */
	public static void recupererLesEvents(final ArrayList<Event> events, final JSONArray eventsJSON, final Map map) {
		for (Object ev : eventsJSON) {
			final JSONObject jsonEvent = (JSONObject) ev;
			//r�cup�ration des donn�es dans le JSON
			final String nomEvent = jsonEvent.getString("nom");
			final Integer id = jsonEvent.getInt("id");
			final int xEvent = jsonEvent.getInt("x") * Fenetre.TAILLE_D_UN_CARREAU;
			final int yEvent = jsonEvent.getInt("y") * Fenetre.TAILLE_D_UN_CARREAU;
			int offsetY;
			try {
				offsetY = jsonEvent.getInt("offsetY");
			} catch (JSONException e2) {
				offsetY = 0;
			}
			
			//instanciation de l'event
			Event event;

			//on essaye de le cr�er � partir de la biblioth�que JSON GenericEvents
			event = creerEventGenerique(id, nomEvent, xEvent, yEvent, offsetY, map);
			
			//si l'Event n'est pas g�n�rique, on le construit � partir de sa description dans la page JSON
			if (event == null) {
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

				final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
				event = new Event(xEvent, yEvent, offsetY, nomEvent, id, jsonPages, largeurHitbox, hauteurHitbox, map);
			}
			events.add(event);
		}
	}
	
	/**
	 * Cr�er un Event g�n�rique � partir de sa description JSON.
	 * @param id de l'Event � cr�er
	 * @param nomEvent nom de l'Event � cr�er
	 * @param xEvent (en pixels) position x de l'Event
	 * @param yEvent (en pixels) position y de l'Event
	 * @param offsetYEvent si on veut afficher l'Event plus bas que sa case r�elle
	 * @param map de l'Event
	 * @return un Event cr��
	 */
	public static Event creerEventGenerique(final int id, final String nomEvent, final int xEvent, final int yEvent, final int offsetYEvent, final Map map) {
		try {
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
	
			final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
			return new Event(xEvent, yEvent, offsetYEvent, nomEvent, id, jsonPages, largeurHitbox, hauteurHitbox, map);
		} catch (FileNotFoundException e1) {
			//System.err.println("Impossible de trouver le fichier JSON pour contruire l'Event g�n�rique "+nomEvent);
			//e1.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Traduit un objet JSON repr�sentant un Mouvement en un vrai objet Mouvement.
	 * @param mouvementJSON objet JSON repr�sentant un Mouvement
	 * @return un objet Mouvement
	 */
	private static Mouvement recupererUnMouvement(final JSONObject mouvementJSON) {
		Class<?> classeMouvement;
		final String nomClasseMouvement = ((JSONObject) mouvementJSON).getString("nom");
		Mouvement mouvement = null;
		try {
			classeMouvement = Class.forName("mouvements." + nomClasseMouvement);
			final Iterator<String> parametresNoms = ((JSONObject) mouvementJSON).keys();
			String parametreNom; //nom du param�tre pour instancier le mouvement
			Object parametreValeur; //valeur du param�tre pour instancier le mouvement
			final HashMap<String, Object> parametres = new HashMap<String, Object>();
			while (parametresNoms.hasNext()) {
				parametreNom = parametresNoms.next();
				if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
					parametreValeur = ((JSONObject) mouvementJSON).get(parametreNom);
					parametres.put( parametreNom, parametreValeur );
				}
			}
			final Constructor<?> constructeurMouvement = classeMouvement.getConstructor(parametres.getClass());
			mouvement = (Mouvement) constructeurMouvement.newInstance(parametres);
		} catch (Exception e) {
			System.err.println("Impossible de traduire l'objet JSON en Mouvement "+nomClasseMouvement);
			e.printStackTrace();
		}
		return mouvement;
	}
	
	/**
	 * Traduit un JSONArray repr�sentant les Mouvements en une liste de Mouvements.
	 * @param mouvementsJSON JSONArray repr�sentant les Mouvements
	 * @return liste des Mouvements
	 */
	public static ArrayList<Mouvement> recupererLesMouvements(final JSONArray mouvementsJSON) {
		final ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
		for (Object object : mouvementsJSON) {
			final JSONObject mouvementJson = (JSONObject) object;
			final Mouvement mouvement = recupererUnMouvement(mouvementJson);
			mouvements.add(mouvement);
		}
		return mouvements;
	}
	
	/**
	 * Traduit un JSONArray repr�sentant les alternatives d'un Choix en une liste de Strings.
	 * @param alternativesJSON JSONArray repr�sentant les alternatives
	 * @return liste des Strings
	 */
	public static ArrayList<String> recupererLesAlternativesDUnChoix(final JSONArray alternativesJSON) {
		final ArrayList<String> alternatives = new ArrayList<String>();
		for (Object object : alternativesJSON) {
			final String alternative = (String) object;
			alternatives.add(alternative);
		}
		return alternatives;
	}
	
	/**
	 * Cr�e un objet Menu � partir d'un fichier JSON.
	 * @param nom du fichier JSON d�crivant le Menu
	 * @param menuParent �ventuel Menu qui a appel� ce Menu
	 * @return Menu instanci�
	 */
	public static Menu creerMenuDepuisJson(final String nom, final Menu menuParent) {
		try {
			final JSONObject jsonObject = InterpreteurDeJson.ouvrirJson(nom, ".\\ressources\\Data\\Menus\\");
			
			// Identifiant de l'ElementDeMenu d�j� s�lectionn� par d�faut
			final int idSelectionInitiale = jsonObject.has("selectionInitiale") ? (int) jsonObject.get("selectionInitiale") : 0;
			
			// Identifiant de l'ElementDeMenu contenant les descriptions d'Objects
			final int idDescription = jsonObject.has("idDescription") ? (int) jsonObject.get("idDescription") : -1;
			
			// Image de fond du Menu
			BufferedImage fond = null;
			if (jsonObject.has("fond")) {
				final String nomFond = (String) jsonObject.get("fond");
				try {
					fond = Graphismes.ouvrirImage("Pictures", nomFond);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			ElementDeMenu selectionInitiale = null;
			final JSONArray jsonElements = jsonObject.getJSONArray("elements");
			final ArrayList<Texte> textes = new ArrayList<Texte>();
			final ArrayList<Image> images = new ArrayList<Image>();
			for (Object objetElement : jsonElements) {
				final JSONObject jsonElement = (JSONObject) objetElement;
				ElementDeMenu element = null;
				
				final int id = (int) jsonElement.get("id");
				final int x = (int) jsonElement.get("x");
				final int y = (int) jsonElement.get("y");
				
				final String type = (String) jsonElement.get("type");
				if ("Objet".equals(type)) {
					// L'ElementDeMenu est un ic�ne d'Objet
					final String nomObjet = (String) jsonElement.get("nom");
					
					final Objet objet = Objet.objetsDuJeuHash.get(nomObjet);
					final Image imageObjet = new Image(objet, x, y, true, id);
					
					images.add(imageObjet);
					element = imageObjet;
				} else {
					
					final boolean selectionnable = (boolean) jsonElement.get("selectionnable");
					
					final JSONArray jsonCommandesSurvol = jsonElement.getJSONArray("commandesSurvol");
					final ArrayList<Commande> commandesAuSurvol = new ArrayList<Commande>();
					recupererLesCommandes(commandesAuSurvol, jsonCommandesSurvol);
					
					final JSONArray jsonCommandesConfirmation = jsonElement.getJSONArray("commandesConfirmation");
					final ArrayList<Commande> commandesALaConfirmation = new ArrayList<Commande>();
					recupererLesCommandes(commandesALaConfirmation, jsonCommandesConfirmation);

					if ("Texte".equals(type)) {
						// L'ElementDeMenu est un Texte
						final String contenu = (String) jsonElement.get("contenu");
						final String taille = jsonElement.has("taille") ? (String) jsonElement.get("taille") : null;
						
						final Texte texte = new Texte(contenu, x, y, Taille.getTailleParNom(taille), selectionnable, commandesAuSurvol, commandesALaConfirmation, id);
						textes.add(texte);
						element = texte;
						
					} else if ("Image".equals(type)) {
						// L'ElementDeMenu est une Image
						final String dossier = (String) jsonElement.get("dossier");
						final String fichier = (String) jsonElement.get("fichier");
	
						final JSONArray jsonConditions = jsonElement.getJSONArray("conditions");
						final ArrayList<Condition> conditions = new ArrayList<Condition>();
						recupererLesConditions(conditions, jsonConditions);
						
						Image image;
						try {
							image = new Image(dossier, fichier, x, y, conditions, selectionnable, commandesAuSurvol, commandesALaConfirmation, id);
							images.add(image);
							element = image;
						} catch (IOException e) {
							System.err.println("Impossible d'ouvrir l'image : "+dossier+"/"+fichier);
							e.printStackTrace();
						}
						
					} else  {
						System.err.println("Type inconnu pour un �lement de menu  : "+type);
					}
				}
				
				if (id == idSelectionInitiale) {
					selectionInitiale = element;
				}
			}

			return new Menu(fond, textes, images, selectionInitiale, idDescription, menuParent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Charger les Autotiles d'un Tileset.
	 * @param jsonTileset objet JSON repr�sentant un Tileset
	 * @param tileset auquel appartiennent les Autotiles
	 * @return Autotiles de ce Tileset
	 */
	public static HashMap<Integer, Autotile> chargerAutotiles(final JSONObject jsonTileset, final Tileset tileset) {
		final HashMap<Integer, Autotile> autotiles = new HashMap<Integer, Autotile>();
		final JSONArray jsonAutotiles = jsonTileset.getJSONArray("autotiles");
		for (Object autotileObject : jsonAutotiles) {
			try {
				final JSONObject jsonAutotile = (JSONObject) autotileObject;
				final int numeroAutotile = (int) jsonAutotile.get("numero");
				final boolean passabiliteAutotile = ((int) jsonAutotile.get("passabilite") == 0);
				final int altitudeAutotile = (int) jsonAutotile.get("altitude");
				final String nomImageAutotile = (String) jsonAutotile.get("nomImage");
				
				//cousins
				final ArrayList<Integer> cousinsAutotile = new ArrayList<Integer>();
				try {
					final JSONArray jsonCousins = jsonAutotile.getJSONArray("cousins");
					if (jsonCousins != null) {
						for (Object cousinObject : jsonCousins) {
							cousinsAutotile.add((Integer) cousinObject);
						}
					}
				} catch (JSONException e) {
					System.err.println("L'autotile "+nomImageAutotile+" n'a pas de cousins.");
					//e.printStackTrace();
				}
				
				Autotile autotile;
				try {
					autotile = new Autotile(numeroAutotile, nomImageAutotile, passabiliteAutotile, altitudeAutotile, cousinsAutotile, tileset);
					autotiles.put(numeroAutotile, autotile);
				} catch (IOException e) {
					System.err.println("Impossible d'instancier l'autotile : "+nomImageAutotile);
					e.printStackTrace();
				}
			} catch (JSONException e) {
				System.err.println("Impossible de lire le JSON de l'autotile");
				e.printStackTrace();
			}
		}
		return autotiles;
	}
	
}
