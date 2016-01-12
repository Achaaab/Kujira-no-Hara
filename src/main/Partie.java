package main;

import java.util.ArrayList;
import java.util.HashMap;

import map.Event;
import map.Map;
import utilitaire.Maths;

/**
 * Une Partie est l'ensemble des informations li�es � l'avanc�e du joueur dans le jeu.
 */
public final class Partie {
	public int numeroMap;
	public Map map;
	public int xHeros;
	public int yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	public final boolean[] interrupteurs;
	public final HashMap<String, Boolean> interrupteursLocaux;
	public final int[] variables;
	public boolean[] quetesFaites;
	private int idArmeEquipee = -1;
	public ArrayList<Integer> idArmesPossedees = new ArrayList<Integer>();
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 */
	private Partie() {
		//TODO valeurs � importer depuis un JSON
		this.numeroMap = 0;
		this.xHeros = 5;
		this.yHeros = 5;
		this.directionHeros = Event.Direction.BAS;
		this.vie = 6;
		this.vieMax = 6;
		this.interrupteurs = new boolean[100];
		this.interrupteursLocaux = new HashMap<String, Boolean>();
		this.variables = new int[100];
		this.quetesFaites = new boolean[100];
		Arme.initialiserLesArmesDuJeu();
	}
	
	/**
	 * Constructeur explicite
	 * @param numeroMap num�ro de la Map o� se trouve le H�ros en reprenant la Partie
	 * @param xHeros coordonn�e x du H�ros en reprenant la Partie
	 * @param yHeros coordonn�e y du H�ros en reprenant la Partie
	 * @param directionHeros direction dans laquelle se trouve le Heros en reprenant la Partie
	 * @param vie niveau d'�nergie vitale du H�ros en reprenant la Partie
	 * @param vieMax niveau maximal possible d'�nergie vitale du H�ros en reprenant la Partie
	 */
	private Partie(final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, final int vie, final int vieMax) {
		this();
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
	}
	
	/**
	 * G�n�re une nouvelle Partie vierge.
	 * @return une nouvelle partie
	 */
	public static Partie creerNouvellePartie() {
		return new Partie();
	}
	
	/**
	 * Charger une partie � l'aide d'un fichier de sauvegarde.
	 * @param numeroSauvegarde num�ro de la partie sauvegard�e
	 * @return une partie sauvegard�e
	 */
	public static Partie chargerPartie(final int numeroSauvegarde) {
		//TODO cr�er un objet Partie � partir d'un fichier de sauvegarde
		return null;
	}
	
	/**
	 * Connaitre l'Arme actuellement �quip�e
	 * @return Arme �quip�e
	 */
	public Arme getArmeEquipee() {
		return Arme.getArme(this.idArmeEquipee);
	}
	
	/**
	 * Equiper une Arme au Heros
	 * @param idArme identifiant de l'Arme � �quiper
	 */
	public void equiperArme(final int idArme) {
		if (this.idArmesPossedees.contains(idArme)) {
			this.idArmeEquipee = idArme;
		}
	}
	
	/**
	 * Equiper l'Arme suivante dans la liste des Armes poss�d�es par le H�ros
	 */
	public void equiperArmeSuivante() {
		final int nombreDArmesPossedees = this.idArmesPossedees.size();
		//pas d'armes poss�d�es
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'arme �quip�e, on �quipe la derni�re poss�d�e
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on �quipe l'arme suivante
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee + 1, this.idArmesPossedees.size());
		//affichage console
		if (this.getArmeEquipee()!=null) {
			System.out.println("arme suivante : "+this.getArmeEquipee().nom);
		}
	}
	
	/**
	 * Equiper l'Arme pr�c�dente dans la liste des Armes poss�d�es par le H�ros
	 */
	public void equiperArmePrecedente() {
		final int nombreDArmesPossedees = this.idArmesPossedees.size();
		//pas d'armes poss�d�es
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'arme �quip�e, on �quipe la derni�re poss�d�e
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on �quipe l'arme pr�c�dente
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee - 1, idArmesPossedees.size());
		//affichage console
		System.out.println("arme pr�c�dente : "+ this.getArmeEquipee().nom);
	}
	
}
