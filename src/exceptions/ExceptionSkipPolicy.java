package exceptions;

import java.util.List;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;


/**
*
Implementing a skip policy with no skip limit
*/
public class ExceptionSkipPolicy implements SkipPolicy {

	/*private Class<? extends Exception> exceptionClassToSkip;*/
	private List<Class<? extends Exception>> exceptionsClassesToSkip;

	/*public ExceptionSkipPolicy(Class<? extends Exception> exceptionClassToSkip) {*/
	public ExceptionSkipPolicy(List<Class<? extends Exception>> exceptionsClassesToSkip) {
		super();

		/*this.exceptionClassToSkip = exceptionClassToSkip;*/
		this.exceptionsClassesToSkip = exceptionsClassesToSkip;
	}

	@Override
	/**
	 *
	Skips on Exception class and subclasses
	 */
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
		// TODO Auto-generated method stub

		for(Class<? extends Exception> exceptionClassToSkip : exceptionsClassesToSkip) {
			if(exceptionClassToSkip != null) {
				try {
					/**
					 *
					On peut s'assurer qu'un objet est bien une instance d'une classe donnée en recourant à l'opérateur instanceOf.
					Par exemple, l'expression p instanceOf Point vaudra true si p est (exactement) de type Point.
					Mais ce test ne reponds pas à nos problématiques ici. Et donc, nous n'utilisons pas cette méthode.
					
					Les conversions explicites de références:
					Nous avons largement insisté sur la compatibilité qui existe entre référence à un objet d’un type donné et référence à un objet d'un type ascendant.
					Comme on peut s'y attendre, la compatibilité n'a pas lieu dans le sens inverse. Considérons cet exemple, fondé sur nos classes Point et Pointcol habituelles :
					class Point { ..... }
					class Pointcol extends Point { ..... }
					   .....
					Pointcol pc ;
					pc = new Point (...) ;    // erreur de compilation
					Si l'affectation était légale, un simple appel tel que pc.colore(...) conduirait à attribuer une
					couleur à un objet de type Point, ce qui poserait quelques problèmes à l'exécution...
					Mais considérons cette situation :
					Point p ;
					Pointcol pc1 = new Pointcol(...), pc2 ;
					   .....
					p = pc1 ;   // p contient la référence à un objet de type Pointcol
					   .....
					pc2 = p ;   // refusé en compilation
					L'affectation pc2 = p est tout naturellement refusée. Cependant, nous sommes certains que p contient bien ici la référence à un objet de type Pointcol.
					En fait, nous pouvons forcer le compilateur à réaliser la conversion correspondante en utilisant l'opérateur de cast déjà rencontré pour les types primitifs. Ici, nous écrirons simplement :
					pc2 = (Pointcol) p ;   // accepté en compilation 
					Toutefois, lors de l'exécution, Java s'assurera que p contient bien une référence à un objet de type Pointcol (ou dérivé) afin de ne pas compromettre la bonne exécution du programme.
					Dans le cas contraire, on obtiendra une exception ClassCastException qui, si elle n'est pas traitée, conduira à un arrêt de l'exécution.

					!! CECI EST JUSTE UNE INFORMATION A SE RAPPELER

					 */
					if(exceptionClassToSkip.isAssignableFrom(t.getClass())) {
						return true;
					}

				} catch(NullPointerException ex) {

				} catch(Throwable th) {

				}
			}
		}

		return false;

		/*return exceptionClassToSkip.isAssignableFrom(t.getClass());*/
		// return false;
	}

}
