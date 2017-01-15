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

import commandes.EquiperGadget;
import commandes.ModifierTexte;
import conditions.Condition;
import conditions.ConditionGadgetPossede;
import main.Commande;
import main.Fenetre;
import main.Lecteur;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le H�ros peut utiliser un certain nombre de Gadgets sur la Map. 
 */
public class Gadget implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Gadget.class);
	private static Gadget[] gadgetsDuJeu;
	public static HashMap<String, Gadget> gadgetsDuJeuHash = new HashMap<String, Gadget>();
	
	/**
	 * Chaque Gadget poss�de un id propre. 
	 * 0 pour les bottes, 1 pour le panier, etc.
	 */
	public final int id;
	public final String nom;
	private final String description;
	public BufferedImage icone;
	
	/**
	 * Constructeur explicite
	 * @param id chaque Gadget a un identifiant
	 * @param nom chaque Gadget a un nom
	 * @param description � afficher dans les Menus
	 * @param nomIcone nom de l'image d'icone
	 */
	private Gadget(final int id, final String nom, final String description, final String nomIcone) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		try {
			this.icone = Graphismes.ouvrirImage("Icons", nomIcone);
		} catch (IOException e) {
			//erreur lors du chargement de l'icone
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Gadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("description"),
			(String) parametres.get("nomIcone")
		);
	}
	
	/**
	 * @param idGadget identifiant du Gadget souhait�
	 * @return Gadget dont l'identifiant est idGadget
	 */
	public static Gadget getGadget(final int idGadget) {
		try {
			return gadgetsDuJeu[idGadget];
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Charger les Gadgets du jeu via JSON.
	 * @return nombre de Gadgets dans le jeu
	 */
	public static int chargerLesGadgetsDuJeu() {
		try {
			final JSONArray jsonGadgets = InterpreteurDeJson.ouvrirJsonGadgets();
			final ArrayList<Gadget> gadgets = new ArrayList<Gadget>();
			int i = 0;
			for (Object objectGadget : jsonGadgets) {
				final JSONObject jsonGadget = (JSONObject) objectGadget;
				
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				parametres.put("numero", i);
				
				final Iterator<String> jsonParametres = jsonGadget.keys();
				while (jsonParametres.hasNext()) {
					final String parametre = jsonParametres.next();
					parametres.put(parametre, jsonGadget.get(parametre));
				}
				
				final Gadget gadget = new Gadget(parametres);
				gadgets.add(gadget);
				gadgetsDuJeuHash.put(gadget.nom, gadget);
				i++;
			}
			
			gadgetsDuJeu = new Gadget[gadgets.size()];
			gadgets.toArray(gadgetsDuJeu);
			return gadgetsDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//probl�me lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les gadgets du jeu.", e);
			gadgetsDuJeu = null;
			return 0;
		}
	}

	/**
	 * Enumerer les Gadgets du jeu.
	 * @param possedes filtrer ou non sur les Gadgets poss�d�s
	 * @return association entre numero et Gadget
	 */
	public static final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Gadgets poss�d�es
			final boolean[] gadgetsPossedes = Fenetre.getPartieActuelle().gadgetsPossedes;
			for (int i = 0; i < gadgetsPossedes.length; i++) {
				if (gadgetsPossedes[i]) {
					listablesPossedes.put((Integer) i, gadgetsDuJeu[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Gadget gadget : gadgetsDuJeu) {
				listablesPossedes.put((Integer) gadget.id, gadget);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe() {
		final Texte texte = new Texte(this.nom);
		final BufferedImage imageTexte = texte.texteToImage();
		final int largeur = imageTexte.getWidth() + Texte.MARGE_A_DROITE + this.icone.getWidth();
		final int hauteur = Math.max(imageTexte.getHeight(), this.icone.getHeight());
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		image = Graphismes.superposerImages(image, this.icone, 0, 0, false, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		image = Graphismes.superposerImages(image, imageTexte, this.icone.getWidth()+Texte.MARGE_A_DROITE, 0, 
				false, Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		return image;
	}
	
	@Override
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new ConditionGadgetPossede(1, this.id));
		return conditions;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		final ArrayList<Commande> comportementConfirmation = new ArrayList<Commande>();
		comportementConfirmation.add(new ModifierTexte(this.description));
		return comportementConfirmation;
	}
	
	@Override
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new EquiperGadget(this.id));
		return comportementSelection;
	}

}
