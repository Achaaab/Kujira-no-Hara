package utilitaire;

/**
 * Un param�tre est envoy� � un constructeur appel� par l'introspection.
 * Cette pratique est employ�e lors de l'importation d'un �l�ment du jeu via un fichier JSON,
 * le nombre et la nature des param�tres pouvant varier selon le constructeur.
 * Un objet du jeu peut donc avoir un constructeur aux arguments explicitement sp�cifi�s, 
 * et � c�t� un constructeur qui prend en argument une liste de Param�tres.
 * TODO migrer vers une Map de param�tres, pour acc�der directement aux param�tres sans faire de rechercher sur la liste (co�teux).
 */
public class Parametre{
	public String nom;
	public Object valeur;
	public Class<?> type;
	
	public Parametre(String nom, Object valeur){
		this.nom = nom;
		this.valeur = valeur;
		this.type = devinerLeType(valeur);
	}
	
	@SuppressWarnings("unused")
	private Class<?> devinerLeType(Object val){
		try{
			Boolean bool = (Boolean) val;
			return Boolean.class;
		}catch(Exception e1){
			try{
				Integer integer = (Integer) val;
				return Integer.class;
			}catch(Exception e2){
				String string = (String) val;
				return String.class;
			}
		}
	}
}