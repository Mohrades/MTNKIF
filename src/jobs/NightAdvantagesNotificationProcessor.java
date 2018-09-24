package jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcPAMRunningReportingDao;
import dao.queries.JdbcSubscriberDao;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;
import util.BalanceAndDate;

public class NightAdvantagesNotificationProcessor implements ItemProcessor<PAMRunningReporting, Subscriber>, InitializingBean {

	private DAO dao;

	private ProductProperties productProperties;
	private Date night_advantages_expires_in = null;
	private boolean waitingForResponse;

	public NightAdvantagesNotificationProcessor() {

	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}

	@Override
	/**
	 * 
	Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.
	
	let’s look at the filtering rules for item processors:
		If  the  process  method  returns  null,  Spring  Batch  filters  out  the  item  and  it won’t go to the item writer.
		Filtering is different from skipping.
		An exception thrown by an item processor results in a skip (if you configured the skip strategy accordingly).
	
	The basic contract for filtering is clear, but we must point out the distinction between filtering and skipping:
		Filtering means that Spring Batch shouldn’t write a given record. For example, the item writer can’t handle a record.
		Skipping  means that a given record is invalid. For example, the format of a phone number is invalid.
	*/
	/* Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.*/
	public Subscriber process(PAMRunningReporting pamRunningReporting) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		// this time constraint is set to stop this process when it becomes unnecessary
		if((night_advantages_expires_in == null) || (new Date().before(night_advantages_expires_in))) {
			try {
				Subscriber subscriber = (new JdbcSubscriberDao(dao)).getOneSubscriber(pamRunningReporting.getSubscriber(), false);

				// check night advantages
				if(getNightAdvantages(productProperties, subscriber.getValue())) {
					// save notification
					if(!isWaitingForResponse()) pamRunningReporting.setFlag(true);
					(new JdbcPAMRunningReportingDao(dao)).notifyNightAdvantages(pamRunningReporting, subscriber.getId(), !isWaitingForResponse(), true);

					return subscriber;
				}
				else {
					if(!isWaitingForResponse()) pamRunningReporting.setFlag(false);
					(new JdbcPAMRunningReportingDao(dao)).notifyNightAdvantages(pamRunningReporting, subscriber.getId(), !isWaitingForResponse(), false);
				}

			} catch(AirAvailabilityException ex) {
				throw ex;

			} catch(Exception ex) {
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

				/**
				 *
				Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parameter. It returns true if so; otherwise it returns false.
				If this Class object represents a primitive type, this method returns true if the specified Class parameter is exactly this Class object; otherwise it returns false.
				Specifically, this method tests whether the type represented by the specified Class parameter can be converted to the type represented by this Class object via an identity conversion or via a widening reference conversion.

				 */
				/*if(ex instanceof AirAvailabilityException) throw (AirAvailabilityException)ex;*/
				try {
					AirAvailabilityException exceptionClass = (AirAvailabilityException)ex;
					throw exceptionClass;

				} catch(AirAvailabilityException exception) {
					throw exception;

				} catch(NullPointerException exception) {

				} catch(ClassCastException exception) {

				} catch(Exception exception) {

				} catch(Throwable th) {

				}

			} catch(Throwable th) {
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

				/**
				 *
				Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parameter. It returns true if so; otherwise it returns false.
				If this Class object represents a primitive type, this method returns true if the specified Class parameter is exactly this Class object; otherwise it returns false.
				Specifically, this method tests whether the type represented by the specified Class parameter can be converted to the type represented by this Class object via an identity conversion or via a widening reference conversion.

				 */
				/*if(th instanceof AirAvailabilityException) throw (AirAvailabilityException)th;*/
				try {
					AirAvailabilityException exceptionClass = (AirAvailabilityException)th;
					throw exceptionClass;

				} catch(AirAvailabilityException exception) {
					throw exception;

				} catch(NullPointerException exception) {

				} catch(ClassCastException exception) {

				} catch(Exception exception) {

				} catch(Throwable throwable) {

				}
			}

			return null;
		}
		else {
			return null;
		}
	}

	public boolean getNightAdvantages(ProductProperties productProperties, String msisdn) throws AirAvailabilityException {
		if(((productProperties.getNight_advantages_call_da() == 0) && (productProperties.getNight_advantages_data_da() == 0))) return false;

		try {
			// attempts
			int retry = 0;

			while(productProperties.getAir_preferred_host() == -1) {
				if(retry >= 3) throw new AirAvailabilityException();

				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
				retry++;
			}

			retry = 0;

			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<BalanceAndDate> balancesBonus = null;

			if((productProperties.getNight_advantages_call_da() > 0) && (productProperties.getNight_advantages_data_da() > 0)) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}, {productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});
			else if(productProperties.getNight_advantages_call_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}});
			else if(productProperties.getNight_advantages_data_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});

			if(((productProperties.getNight_advantages_call_da() > 0) || (productProperties.getNight_advantages_data_da() > 0)) && (!request.isSuccessfully())) {
				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host())) ;
				throw new AirAvailabilityException() ;
			}

			if((balancesBonus != null) && (!balancesBonus.isEmpty())) {
				BalanceAndDate balanceBonusCall = null;
				BalanceAndDate balanceBonusData = null;

				for(BalanceAndDate balanceAndDate : balancesBonus) {
					if(balanceAndDate.getExpiryDate() != null) {
						Date expiryDate = (Date)balanceAndDate.getExpiryDate();

						if((expiryDate == null) || ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(expiryDate).equals("9999-12-30 13:00:00")));
						else {
							if(((Date)balanceAndDate.getExpiryDate()).after(new Date())) {
								if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_call_da())) balanceBonusCall = balanceAndDate;
								else if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_data_da())) balanceBonusData = balanceAndDate;
							}
						}
					}
				}

				if((balanceBonusCall != null) || (balanceBonusData != null)) {
					return true;
				}
			}

		} catch(Throwable th) {
			
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
		try {
			Date today = new Date();
			night_advantages_expires_in = (productProperties.getNight_advantages_expires_in() == null) ? null : (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(productProperties.getNight_advantages_expires_in());
			night_advantages_expires_in.setYear(today.getYear()); night_advantages_expires_in.setMonth(today.getMonth()); night_advantages_expires_in.setDate(today.getDate());

		} catch (ParseException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO Auto-generated catch block

		} catch (Throwable th) {
			// TODO Auto-generated catch block

		}
	}
}
