package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import menu.Menu;
import menu.Texte;
import son.LecteurAudio;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * La saisie de nombre donne la possibilit� au joueur d'�crire un nombre demand� par le jeu.
 * Le nombre entr� est m�moris� dans une Variable.
 * S'affiche comme un Message, mais avec un curseur � d�placer.
 */
public class EntrerUnNombre extends Message {
	/** base d�cimale */
	private static final int NOMBRE_DE_CHIFFRES = 10;
	
	/** Num�ro de la Variable qui m�morise le code */
	public int numeroDeVariable;
	/** Tableau des chiffres rentr�s par le joueur */
	private int[] chiffresRentres;
	private Texte[] chiffresRentresTexte;
	
	public int positionCurseur = 0;
	public boolean reactualiserLImage = true;
	
	private final BufferedImage surlignage;
	private BufferedImage imageDuMessage;
	private final int largeurChiffre;

	/**
	 * Constructeur explicite
	
	 * @param texte affich� dans la bo�te de dialogue
	 * @param numeroDeVariable num�ro de la Variable qui m�morise le nombre
	 * @param tailleDuNombre longueur (en chiffres) du nombre entr�
	 */
	public EntrerUnNombre(final String[] texte, final int numeroDeVariable, final int tailleDuNombre) {
		super(texte);
		this.numeroDeVariable = numeroDeVariable;
		this.chiffresRentres = new int[tailleDuNombre];
		this.chiffresRentresTexte = new Texte[tailleDuNombre];
		for (int i = 0; i<tailleDuNombre; i++) {
			chiffresRentresTexte[i] = new Texte("0");
		}
		this.surlignage = chiffresRentresTexte[0].creerImageDeSelection();
		this.largeurChiffre = chiffresRentresTexte[0].image.getWidth();
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EntrerUnNombre(final HashMap<String, Object> parametres) {
		this( 	InterpreteurDeJson.construireTexteMultilangue(parametres.get("texte")),
				(int) parametres.get("numeroDeVariable"),
				(int) parametres.get("tailleDuNombre")
		);
	}
	
	/**
	 * Fabrique l'image du Message � partir de l'image de la bo�te de dialogue et du texte.
	 * Une image est fabriqu�e pour chaque alternative � s�lectionner.
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		this.reactualiserLImage = false;
		
		// Texte de base
		final Texte texteDeBase = new Texte(this.texte[Fenetre.getPartieActuelle().langue]);
		final int hauteurTexte = calculerHauteurTexte();
		
		// Superposition
		imageDuMessage = Graphismes.clonerUneImage(Message.imageBoiteMessage);
		
		imageDuMessage = Graphismes.superposerImages(
				imageDuMessage, 
				surlignage, 
				MARGE_DU_TEXTE - Texte.CONTOUR + 2*positionCurseur*largeurChiffre, 
				MARGE_DU_TEXTE + hauteurTexte - Texte.CONTOUR
		);
		imageDuMessage = Graphismes.superposerImages(
				imageDuMessage, 
				texteDeBase.image, 
				MARGE_DU_TEXTE, 
				MARGE_DU_TEXTE
		);
		for (int i = 0; i<chiffresRentres.length; i++) {
			imageDuMessage = Graphismes.superposerImages(
					imageDuMessage, 
					chiffresRentresTexte[i].image, 
					MARGE_DU_TEXTE + 2*i*largeurChiffre, 
					MARGE_DU_TEXTE + hauteurTexte
			);
		}
		return imageDuMessage;
	}
	/**
	 * Le curseur du Choix a-t-il boug� ?
	 * Si oui il faut remplacer l'image de Message affich�e.
	 * @return 
	 */
	@Override
	protected final boolean siChoixLeCurseurATIlBouge() {
		return reactualiserLImage;
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void haut() {
		this.chiffresRentres[positionCurseur] = Maths.modulo(this.chiffresRentres[positionCurseur]+1, NOMBRE_DE_CHIFFRES);
		this.chiffresRentresTexte[positionCurseur] = new Texte( "" + chiffresRentres[positionCurseur] );
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void bas() {
		this.chiffresRentres[positionCurseur] = Maths.modulo(this.chiffresRentres[positionCurseur]-1, NOMBRE_DE_CHIFFRES);
		this.chiffresRentresTexte[positionCurseur] = new Texte( "" + chiffresRentres[positionCurseur] );
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void gauche() {
		this.positionCurseur = Maths.modulo(positionCurseur - 1, this.chiffresRentres.length);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void droite() {
		this.positionCurseur = Maths.modulo(positionCurseur + 1, this.chiffresRentres.length);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		int nombre = 0;
		for (int i = this.chiffresRentres.length-1; i >= 0; i--) {
			nombre *= NOMBRE_DE_CHIFFRES;
			nombre += this.chiffresRentres[i];
		}
		//on modifie la valeur de la variable
		Fenetre.getPartieActuelle().variables[this.numeroDeVariable] = nombre;
		return curseurActuel+1;
	}

}