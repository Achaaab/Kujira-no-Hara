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

import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Le joueur doit r�ussir des Qu�tes durant le jeu.
 * Cette classe est une description inerte de la Qu�te, ind�pendante de l'action du joueur.
 */
public class Quete {
	//constantes
	public static final String NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT = "quete a faire icon.png";
	public static final String NOM_ICONE_QUETE_FAITE_PAR_DEFAUT = "quete faite icon.png";
	public static final HashMap<String, BufferedImage> ICONES_MEMORISEES = new HashMap<String, BufferedImage>();
	public static Quete[] quetesDuJeu;
	public static HashMap<String, Quete> quetesDuJeuHash = new HashMap<String, Quete>();
	
	public Integer numero; //Integer car cl� d'une HashMap
	public String nom;
	public String description;
	private final String nomIconeQuetePasFaite;
	private BufferedImage iconeQuetePasFaite;
	private final String nomIconeQueteFaite;
	private BufferedImage iconeQueteFaite;
	public int xCarte;
	public int yCarte;
	
	public static enum EtatQuete {
		INCONNUE("INCONNUE"), CONNUE("CONNUE"), FAITE("FAITE");
		
		private String nom;
		
		EtatQuete (String nom) {
			this.nom = nom;
		}
		
		public EtatQuete getEtat(String nom){
			for (EtatQuete etat : values()){
				if (etat.nom.equals(nom)) {
					return etat;
				}
			}
			return null;
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param numero de la Qu�te
	 * @param nom de la Qu�te
	 * @param description de la Qu�te
	 * @param nomIconeQuetePasFaite nom de l'ic�ne affich�e lorsque la Qu�te n'est pas encore faite
	 * @param nomIconeQueteFaite nom de l'ic�ne affich�e lorsque la Qu�te a �t�  faite
	 * @param xCarte position x sur la carte des Qu�tes
	 * @param yCarte position y sur la carte des Qu�tes
	 */
	private Quete(final int numero, final String nom, final String description, final String nomIconeQuetePasFaite, final String nomIconeQueteFaite, final int xCarte, final int yCarte) {
		this.numero = numero;
		this.nom = nom;
		this.description = description;
		this.nomIconeQuetePasFaite = nomIconeQuetePasFaite;
		this.nomIconeQueteFaite = nomIconeQueteFaite;
		this.xCarte = xCarte;
		this.yCarte = yCarte;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Quete(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("description"),
			(String) (parametres.containsKey("nomIconeQuetePasFaite") ? parametres.get("nomIconeQuetePasFaite") : NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT),
			(String) (parametres.containsKey("nomIconeQueteFaite") ? parametres.get("nomIconeQueteFaite") : NOM_ICONE_QUETE_FAITE_PAR_DEFAUT),
			(int) parametres.get("xCarte"),
			(int) parametres.get("yCarte")
		);
	}

	/**
	 * Charger les Qu�tes du jeu via JSON.
	 * @return nombre de Qu�tes dans le jeu
	 */
	public static int chargerLesQuetesDuJeu() {
		try {
			final JSONArray jsonQuetes = InterpreteurDeJson.ouvrirJsonQuetes();
			final ArrayList<Quete> quetes = new ArrayList<Quete>();
			int i = 0;
			for (Object objectQuete : jsonQuetes) {
				final JSONObject jsonQuete = (JSONObject) objectQuete;
				
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				parametres.put("numero", i);
				
				final Iterator<String> jsonParametres = jsonQuete.keys();
				while (jsonParametres.hasNext()) {
					final String parametre = jsonParametres.next();
					parametres.put(parametre, jsonQuete.get(parametre));
				}
				
				final Quete quete = new Quete(parametres);
				quetes.add(quete);
				quetesDuJeuHash.put(quete.nom, quete);
				i++;
			}
			
			quetesDuJeu = new Quete[quetes.size()];
			quetes.toArray(quetesDuJeu);
			return quetesDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//probl�me lors de l'ouverture du fichier JSON
			System.err.println("Impossible de charger les qu�tes du jeu.");
			e.printStackTrace();
			quetesDuJeu = null;
			return 0;
		}
	}
	
	/**
	 * Obtenir l'ic�ne de cette Qu�te lorsqu'elle n'a pas encore �t� faite par le joueur.
	 * @return ic�ne de la Qu�te non faite
	 */
	private BufferedImage getIconeQuetePasFaite() {
		if (this.iconeQuetePasFaite == null) {
			if (ICONES_MEMORISEES.containsKey(this.nomIconeQuetePasFaite)) {
				this.iconeQuetePasFaite = ICONES_MEMORISEES.get(this.nomIconeQuetePasFaite);
			} else {
				try {
					this.iconeQuetePasFaite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+this.nomIconeQuetePasFaite));
					ICONES_MEMORISEES.put(this.nomIconeQuetePasFaite, this.iconeQuetePasFaite);
				} catch (IOException e) {
					//l'image d'apparence n'existe pas
					this.iconeQuetePasFaite = null;
					ICONES_MEMORISEES.put(this.nomIconeQuetePasFaite, null);
					System.err.println("Impossible de trouver l'ic�ne de Quete : " + this.nomIconeQuetePasFaite);
				}
			}
		}
		return this.iconeQuetePasFaite;
	}
	
	/**
	 * Obtenir l'ic�ne de cette Qu�te lorsqu'elle a �t� faite par le joueur.
	 * @return ic�ne de la Qu�te faite
	 */
	private BufferedImage getIconeQueteFaite() {
		if (this.iconeQueteFaite == null) {
			if (ICONES_MEMORISEES.containsKey(this.nomIconeQueteFaite)) {
				this.iconeQueteFaite = ICONES_MEMORISEES.get(this.nomIconeQueteFaite);
			} else {
				try {
					this.iconeQueteFaite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+this.nomIconeQueteFaite));
					ICONES_MEMORISEES.put(nomIconeQueteFaite, this.iconeQueteFaite);
				} catch (IOException e) {
					//l'image d'apparence n'existe pas
					this.iconeQueteFaite = null;
					ICONES_MEMORISEES.put(this.nomIconeQueteFaite, null);
					System.err.println("Impossible de trouver l'ic�ne de Quete : " + this.nomIconeQueteFaite);
				}
			}
		}
		return this.iconeQueteFaite;
	}
	
	/**
	 * Obtenir l'ic�ne de la Qu�te.
	 * @return ic�ne de la Qu�te faite ou non faite, selon si la Qu�te est fait ou non.
	 */
	public final BufferedImage getIcone() {
		if (Quete.EtatQuete.FAITE.equals( Fenetre.getPartieActuelle().quetesEtat[this.numero]) ) {
			return this.getIconeQueteFaite();
		} else {
			return this.getIconeQuetePasFaite();
		}
	}
		
}
