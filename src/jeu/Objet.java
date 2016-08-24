package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.AfficherDescription;
import commandes.CommandeMenu;
import conditions.Condition;
import conditions.ConditionObjetPossede;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Le joueur collecte des Objets.
 */
public class Objet {
	//constantes
	public static Objet[] objetsDuJeu;
	public static HashMap<String, Objet> objetsDuJeuHash = new HashMap<String, Objet>();;
	
	public final Integer numero; //Integer car cl� d'une HashMap
	public final String nom;
	private final String nomIcone;
	private BufferedImage icone;
	public final String description;
	public final ArrayList<Commande> effet;
	
	/**
	 * Constructeur explicite
	 * @param numero dans le Menu
	 * @param nom de l'Objet
	 * @param nomIcone nom de l'ic�ne de l'Objet affich�e dans le Menu
	 * @param description de l'Objet
	 * @param effet de l'Objet lorsqu'on le consomme
	 */
	private Objet(final int numero, final String nom, final String nomIcone, final String description, final ArrayList<Commande> effet) {
		this.numero = numero;
		this.nom = nom;
		this.nomIcone = nomIcone;
		this.description = description;
		this.effet = effet;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	@SuppressWarnings("unchecked")
	public Objet(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("nomIcone"),
			(String) parametres.get("description"),
			(ArrayList<Commande>) parametres.get("effet") //TODO � revoir, je doute que �a marche
		);
	}

	/**
	 * Charger les Objets du jeu via JSON.
	 * @return nombre d'Objets dans le jeu
	 */
	public static int chargerLesObjetsDuJeu() {
		try {
			final JSONArray jsonObjets = InterpreteurDeJson.ouvrirJsonObjets();
			final ArrayList<Objet> objets = new ArrayList<Objet>();
			int i = 0;
			for (Object objectObjet : jsonObjets) {
				final JSONObject jsonObjet = (JSONObject) objectObjet;
				
				final HashMap<String, Object> parametresObjet = new HashMap<String, Object>();
				parametresObjet.put("numero", i);
				
				final Iterator<String> jsonParametresObjet = jsonObjet.keys();
				while (jsonParametresObjet.hasNext()) {
					final String parametreObjet = jsonParametresObjet.next();
					
					if ("effet".equals(parametreObjet)) {
						//param�tre : effet
						final ArrayList<CommandeMenu> effet = new ArrayList<CommandeMenu>();
						final JSONArray jsonEffet = jsonObjet.getJSONArray("effet");
						InterpreteurDeJson.recupererLesCommandesMenu(effet, jsonEffet);
						parametresObjet.put("effet", effet);
					} else {
						//autres param�tres
						parametresObjet.put(parametreObjet, jsonObjet.get(parametreObjet));
					}
					
				}
				
				final Objet objet = new Objet(parametresObjet);
				objets.add(objet);
				objetsDuJeuHash.put(objet.nom, objet);
				i++;
			}
			
			objetsDuJeu = new Objet[objets.size()];
			objets.toArray(objetsDuJeu);
			//System.out.println("Objets cr��s : " + objetsDuJeu.length);
			return objetsDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//probl�me lors de l'ouverture du fichier JSON
			System.err.println("Impossible de charger les objets du jeu.");
			e.printStackTrace();
			objetsDuJeu = null;
			return 0;
		}
	}
	
	/**
	 * Obtenir l'ic�ne de cet Objet.
	 * @return ic�ne de l'Objet
	 */
	public final BufferedImage getIcone() {
		if (this.icone == null) {
			try {
				this.icone = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIcone));
			} catch (IOException e) {
				//l'image d'apparence n'existe pas
				System.out.println("Impossible de charger l'icone pour l'Objet : " + this.nomIcone);
				e.printStackTrace();
			}
		}
		return this.icone;
	}
	
	/**
	 * Fabriquer une liste de Conditions v�rifiant la possession de cet Objet.
	 * @return liste de Conditions destin�e au Menu
	 */
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new ConditionObjetPossede(this.numero));
		return conditions;
	}
	
	/**
	 * Liste de Commandes de Menu associ�e � l'Objet.
	 * Si l'Objet est s�lectionn� dans le Menu, la description de l'Objet est affich�e.
	 * @return liste de Commandes destin�e au Menu
	 */
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new AfficherDescription(this.description));
		return comportementSelection;
	}
	
	/**
	 * Liste de Commandes de Menu associ�e � l'Objet.
	 * Si l'Objet est valid� dans le Menu, il est consomm�.
	 * @return effet de l'Objet
	 */
	public final ArrayList<Commande> getComportementConfirmation() {
		return this.effet;
	}
}
