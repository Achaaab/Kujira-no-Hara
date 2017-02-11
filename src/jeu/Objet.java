package jeu;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.ModifierTexte;
import commandes.CommandeMenu;
import conditions.Condition;
import conditions.ConditionObjetPossede;
import main.Commande;
import main.Fenetre;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le joueur collecte des Objets.
 */
public class Objet implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Objet.class);
	public static Objet[] objetsDuJeu = chargerLesObjetsDuJeu();
	
	public final Integer idObjet; //Integer car cl� d'une HashMap
	public final ArrayList<String> nom;
	private final String nomIcone;
	private BufferedImage icone;
	public final ArrayList<String> description;
	public final ArrayList<Commande> effet;
	
	/**
	 * Constructeur explicite
	 * @param numero dans le Menu
	 * @param nom de l'Objet
	 * @param nomIcone nom de l'ic�ne de l'Objet affich�e dans le Menu
	 * @param description de l'Objet
	 * @param effet de l'Objet lorsqu'on le consomme
	 */
	private Objet(final int idObjet, final ArrayList<String> nom, final String nomIcone, final ArrayList<String> description, final ArrayList<Commande> effet) {
		this.idObjet = idObjet;
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
		this( (int) parametres.get("idObjet"), 
			InterpreteurDeJson.construireTexteMultilingue(parametres.get("nom")),
			(String) parametres.get("nomIcone"),
			InterpreteurDeJson.construireTexteMultilingue(parametres.get("description")),
			(ArrayList<Commande>) parametres.get("effet") //TODO � revoir, je doute que �a marche
		);
	}

	/**
	 * Charger les Objets du jeu via JSON.
	 * @return tous les Objets du jeu
	 */
	public static Objet[] chargerLesObjetsDuJeu() {
		try {
			final JSONArray jsonObjets = InterpreteurDeJson.ouvrirJsonObjets();
			final ArrayList<Objet> objets = new ArrayList<Objet>();
			for (Object objectObjet : jsonObjets) {
				final JSONObject jsonObjet = (JSONObject) objectObjet;
				
				final HashMap<String, Object> parametresObjet = new HashMap<String, Object>();
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
				
				// On v�rifie que les identifiants soient bien uniques
				boolean identifiantUnique = true;
				for (Objet objet2 : objets) {
					if (objet2.idObjet.equals(objet.idObjet)) {
						LOG.error("Les deux objets ont le m�me identifiant : "+objet.nom+", "+objet2.nom);
						identifiantUnique = false;
					}
				}
				if (identifiantUnique) {
					objets.add(objet);
				}
			}
			
			Objet[] objetsDuJeu = new Objet[objets.size()];
			objets.toArray(objetsDuJeu);
			LOG.debug("Objets cr��s : " + objetsDuJeu.length);
			return objetsDuJeu;
			
		} catch (FileNotFoundException e) {
			//probl�me lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les objets du jeu.", e);
			return null;
		}
	}
	
	/**
	 * Obtenir l'ic�ne de cet Objet.
	 * @return ic�ne de l'Objet
	 */
	public final BufferedImage getIcone() {
		if (this.icone == null) {
			try {
				this.icone = Graphismes.ouvrirImage("Icons", nomIcone);
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
		conditions.add(new ConditionObjetPossede(this.idObjet));
		return conditions;
	}
	
	/**
	 * Liste de Commandes de Menu associ�e � l'Objet.
	 * Si l'Objet est s�lectionn� dans le Menu, la description de l'Objet est affich�e.
	 * @return liste de Commandes destin�e au Menu
	 */
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new ModifierTexte(this.description));
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

	/**
	 * Enumerer les Objets du jeu.
	 * @param possedes filtrer ou non sur les Objets poss�d�s
	 * @return association entre numero et Objet
	 */
	public static final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Objets poss�d�es
			final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
			for (int i = 0; i < objetsPossedes.length; i++) {
				if (objetsPossedes[i]>0) {
					listablesPossedes.put((Integer) i, objetsDuJeu[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Objet objet : objetsDuJeu) {
				listablesPossedes.put((Integer) objet.idObjet, objet);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe() {
		final ArrayList<String> contenuTexte = new ArrayList<String>();
		final int quantite = Fenetre.getPartieActuelle().objetsPossedes[this.idObjet];
		for (String nomLangue : this.nom) {
			contenuTexte.add(nomLangue + " : " + quantite);
		}
		final Texte texte = new Texte(contenuTexte);
		
		final BufferedImage imageTexte = texte.image;
		final int largeur = imageTexte.getWidth() + Texte.MARGE_A_DROITE + this.getIcone().getWidth();
		final int hauteur = Math.max(imageTexte.getHeight(), this.getIcone().getHeight());
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		image = Graphismes.superposerImages(image, this.getIcone(), 0, 0, false, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		image = Graphismes.superposerImages(image, imageTexte, this.getIcone().getWidth()+Texte.MARGE_A_DROITE, 0, 
				false, Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		return image;
	}

}
