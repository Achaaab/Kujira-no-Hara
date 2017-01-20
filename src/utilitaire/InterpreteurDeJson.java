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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import commandes.FermerMenu;
import conditions.Condition;
import jeu.Objet;
import main.Commande;
import main.Fenetre;
import map.Autotile;
import map.Event;
import map.Map;
import map.PageCommune;
import map.Tileset;
import menu.ElementDeMenu;
import menu.ImageMenu;
import menu.Listable;
import menu.Liste;
import menu.Menu;
import menu.Texte;
import menu.Texte.Taille;
import mouvements.Mouvement;
import utilitaire.graphismes.Graphismes;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	private static final Logger LOG = LogManager.getLogger(InterpreteurDeJson.class);
	
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
			LOG.error("Erreur de syntaxe dans le fichier JSON "+nomFichier, e);
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
				LOG.error("Erreur lors de l'instanciation de la Condition :", e1);
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
					LOG.error("La commande "+commande.getClass().getName()+" n'est pas une CommandeEvent !");
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
					LOG.error("La commande "+commande.getClass().getName()+" n'est pas une CommandeMenu !");
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
			LOG.error("Impossible de traduire l'objet JSON en CommandeEvent.", e1);
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
				LOG.error("Impossible de traduire l'objet JSON en CommandeMenu.", e1);
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
			LOG.trace("Impossible de trouver le fichier JSON pour contruire l'Event g�n�rique "+nomEvent, e1);
			return null;
		}
	}
	
	/**
	 * Traduit un objet JSON repr�sentant un Mouvement en un vrai objet Mouvement.
	 * @param mouvementJSON objet JSON repr�sentant un Mouvement
	 * @return un objet Mouvement
	 */
	private static Mouvement recupererUnMouvement(final JSONObject mouvementJSON) {
		final Class<?> classeMouvement;
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
			LOG.error("Impossible de traduire l'objet JSON en Mouvement "+nomClasseMouvement, e);
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
			
			// Comportement du Menu en cas d'annulation
			final ArrayList<Commande> comportementAnnulation = new ArrayList<Commande>();
			try {
				final JSONArray jsonComportementAnnulation = jsonObject.getJSONArray("comportementAnnulation");
				recupererLesCommandes(comportementAnnulation, jsonComportementAnnulation);
			} catch (JSONException e) {
				LOG.warn("Pas de comportement en cas d'annulation sp�cifi� pour le menu "+nom+".\n"
						+ "Comportement par d�faut : revenir au menu parent ou revenir � la map.");
				comportementAnnulation.add(new FermerMenu());
			}

			// El�ments du Menu
			final JSONArray jsonElements = jsonObject.getJSONArray("elements");
			final ArrayList<Texte> textes = new ArrayList<Texte>();
			final ArrayList<ImageMenu> images = new ArrayList<ImageMenu>();
			@SuppressWarnings("rawtypes")
			final ArrayList<Liste> listes = new ArrayList<Liste>();
			final ElementDeMenu selectionInitiale = recupererLesElementsDeMenu(idSelectionInitiale, jsonElements, images, textes, listes, nom);
			
			//instanciation
			final Menu menu = new Menu(fond, textes, images, listes, selectionInitiale, idDescription, menuParent, comportementAnnulation);	
			
			//associer un ElementDeMenu arbitraire aux Commandes en cas d'annulation pour qu'elles trouvent leur Menu
			for (Commande commande : menu.comportementAnnulation) {
				commande.element = menu.elements.get(0);
			}
			
			return menu;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * R�cuperer les Elements du Menu� partir d'un tableau d'objets JSON.
	 * @param idSelectionInitiale identifiant de l'ElementDeMenu s�lectionn� d'embl�e
	 * @param jsonElements tableau JSON des ElementsDeMenu
	 * @param images liste des ElementsDeMenu graphiques
	 * @param textes liste des ElementsDeMenu textuels
	 * @param listes tableaux bidimensionnels contenant des ElementsDeMenu
	 * @param nom du Menu
	 * @return ElementDeMenu s�lectionn� d'embl�e
	 */
	private static ElementDeMenu recupererLesElementsDeMenu(final int idSelectionInitiale, 
			final JSONArray jsonElements, final ArrayList<ImageMenu> images, final ArrayList<Texte> textes, 
			@SuppressWarnings("rawtypes") final ArrayList<Liste> listes, final String nom) {
		ElementDeMenu selectionInitiale = null;
		
		for (Object objetElement : jsonElements) {
			final JSONObject jsonElement = (JSONObject) objetElement;
			ElementDeMenu element = null;
			
			final String type = (String) jsonElement.get("type");
			
			final int x = (int) jsonElement.get("x");
			final int y = (int) jsonElement.get("y");
			
			if ("Liste".equals(type)) {
				// On a affaire � une Liste d'ElementsDeMenu
				@SuppressWarnings("rawtypes")
				final Liste liste = recupererElementDeMenuListe(jsonElement, x, y);
				listes.add(liste);
					
				// On s�lectionne le premier de la Liste
				if (liste.elements != null && liste.elements.size()>0) {
					selectionInitiale = (ElementDeMenu) liste.elements.get(0);
				}

			} else {
				// L'ElementDeMenu n'est pas une Liste..
				
				final int id = (int) jsonElement.get("id");
				final int largeur = jsonElement.has("largeur") ? (int) jsonElement.get("largeur") : -1;
				final int hauteur = jsonElement.has("hauteur") ? (int) jsonElement.get("hauteur") : -1;
				
				if ("Objet".equals(type)) {
					// L'ElementDeMenu est un ic�ne d'Objet
					final String nomObjet = (String) jsonElement.get("nom");
					
					final Objet objet = Objet.objetsDuJeuHash.get(nomObjet);
					final ImageMenu imageObjet = new ImageMenu(objet, x, y, largeur, hauteur, true, id);
					
					images.add(imageObjet);
					element = imageObjet;
				} else {
					//L'ElementDeMenu n'est pas un ic�ne d'Objet
					
					// L'ElementDeMenu est-il s�lectionnable ?
					final boolean selectionnable = (boolean) jsonElement.get("selectionnable");
					
					// CommandesMenu execut�es au survol de l'El�ment de Menu
					final ArrayList<Commande> commandesAuSurvol = new ArrayList<Commande>();
					try {
						final JSONArray jsonCommandesSurvol = jsonElement.getJSONArray("commandesSurvol");
						recupererLesCommandes(commandesAuSurvol, jsonCommandesSurvol);
					} catch (JSONException e) {
						LOG.warn("[Menu "+nom+"] Pas de commandes au survol pour l'�l�ment de menu : "+id);
					}
					
					// CommandesMenu execut�es � la confirmation de l'El�ment de Menu
					final ArrayList<Commande> commandesALaConfirmation = new ArrayList<Commande>();
					try {
						final JSONArray jsonCommandesConfirmation = jsonElement.getJSONArray("commandesConfirmation");
						recupererLesCommandes(commandesALaConfirmation, jsonCommandesConfirmation);
					} catch (JSONException e) {
						LOG.warn("[Menu "+nom+"] Pas de commandes � la confirmation pour l'�l�ment de menu : "+id);
					}
	
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
						
						final ImageMenu image;
						try {
							image = new ImageMenu(Graphismes.ouvrirImage(dossier, fichier), 
									x, y, largeur, hauteur, 
									conditions, selectionnable, commandesAuSurvol, commandesALaConfirmation, 
									id);
							images.add(image);
							element = image;
						} catch (IOException e) {
							LOG.error("Impossible d'ouvrir l'image : "+dossier+"/"+fichier, e);
						}
						
					} else  {
						LOG.error("Type inconnu pour un �lement de menu  : "+type);
					}
				}
				
				if (id == idSelectionInitiale) {
					selectionInitiale = element;
				}
			}
		}
		return selectionInitiale;
	}
	
	/**
	 * R�cup�rer une Liste d'ElementsDeMenu.
	 * @param jsonElement objet JSON repr�sentant la Liste
	 * @param x position x (en pixels) de la Liste dans le Menu
	 * @param y position y (en pixels) de la Liste dans le Menu
	 * @return Liste d'ElementsDeMenu.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Liste recupererElementDeMenuListe(final JSONObject jsonElement, final int x, final int y) {
		final String natureDuListable = Listable.PREFIXE_NOM_CLASSE + jsonElement.getString("provenance");
		try {
			final Class<? extends Listable> provenance = (Class<? extends Listable>) Class.forName(natureDuListable);
			
			final int nombreDeColonnes = jsonElement.getInt("nombreDeColonnes");
			final int nombreDeLignesVisibles = jsonElement.getInt("nombreDeLignesVisibles");
			
			final boolean possedes = jsonElement.getBoolean("possedes");
			
			final JSONArray jsonTousSauf; 
			ArrayList<Integer> tousSauf = new ArrayList<Integer>();
			try {
				jsonTousSauf = jsonElement.getJSONArray("tousSauf");
				for (Object o : jsonTousSauf) {
					tousSauf.add((Integer) o);
				}
			} catch (JSONException e) {
				tousSauf = null;
			}
			
			final JSONArray jsonAvec;
			ArrayList<Integer> avec = new ArrayList<Integer>();
			try {
				jsonAvec = jsonElement.getJSONArray("avec");
				for (Object o : jsonAvec) {
					avec.add((Integer) o);
				}
				
			} catch (JSONException e) {
				avec = null;
			}

			Liste liste = null;
			try {
				liste = new Liste(x, y, nombreDeColonnes, nombreDeLignesVisibles,
						provenance, possedes, avec, tousSauf);
			} catch (Exception e) {
				LOG.error("Impossible de cr�er la liste d'�l�ments pour le menu !", e);
			}
			return liste;
		} catch (ClassNotFoundException e) {
			LOG.error(natureDuListable+" n'est pas un Listable connu.", e);
			return null;
		}
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
				final int terrainAutotile = jsonAutotile.has("terrain") ? (int) jsonAutotile.get("terrain") : 0;
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
					LOG.warn("L'autotile "+nomImageAutotile+" n'a pas de cousins.");
				}
				
				final Autotile autotile;
				try {
					autotile = new Autotile(numeroAutotile, nomImageAutotile, passabiliteAutotile, altitudeAutotile, terrainAutotile, cousinsAutotile, tileset);
					autotiles.put(numeroAutotile, autotile);
				} catch (IOException e) {
					LOG.error("Impossible d'instancier l'autotile : "+nomImageAutotile, e);
				}
			} catch (JSONException e) {
				LOG.error("Impossible de lire le JSON de l'autotile", e);
			}
		}
		return autotiles;
	}

	/**
	 * R�cup�rer les Pages Communes d�crites dans un fichier JSON.
	 * @return Pages de code Event communes � toutes les Maps du jeu
	 */
	public static ArrayList<PageCommune> recupererLesPagesCommunes() {
		final ArrayList<PageCommune> pagesCommunes = new ArrayList<PageCommune>();
		
		try {
			final JSONObject jsonObjets = ouvrirJson("pagesCommunes", ".\\ressources\\Data\\");
			final JSONArray jsonPagesCommunes = jsonObjets.getJSONArray("pages");
		
			for (Object o : jsonPagesCommunes) {
				final JSONObject jsonPageCommune = (JSONObject) o;
				final PageCommune pageCommune = new PageCommune(jsonPageCommune);
				pagesCommunes.add(pageCommune);
			}

		} catch (FileNotFoundException e) {
			LOG.error("Impossible de trouver le fichier des pages communes !", e);
		} catch (JSONException e) {
			LOG.error("Impossible de lire le fichier JSON des pages communes !", e);
		}
		
		return pagesCommunes;
	}

	/**
	 * S�parer les langues dans un tableau.
	 * @param o soit le texte dans une langue unique au format String, soit le texte multilingue String[]
	 * @return tableau du texte dans chaque langue
	 */
	public static String[] construireTexteMultilangue(Object o) {
		String[] resultat;
		try {
			String texteUnique = (String) o;
			resultat = new String[1];
			resultat[0] = texteUnique;
		} catch(Exception e) {
			LOG.debug(e);
			JSONArray jsonTexteMulti = (JSONArray) o;
			int taille = jsonTexteMulti.length();
			resultat = new String[taille];
			for (int i = 0; i<taille; i++) {
				resultat[i] = (String) jsonTexteMulti.get(i);
			}
		}
		return resultat;
	}
	
}
