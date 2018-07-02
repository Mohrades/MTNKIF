package api;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import dao.DAO;
import dao.queries.JdbcServiceAccessDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.ServiceAccess;
import domain.models.USSDService;
import product.ProductProperties;

public class ExternalRequestInterceptor implements HandlerInterceptor {
	
	private ProductProperties productProperties;

	private DAO dao;

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object controller, Exception ex) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object controller, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller) throws Exception {
		// TODO Auto-generated method stub

		try {
			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());
			Date now = new Date();

			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				// Avec le protocole HTTP 1.1, le code HTTP de redirection est 303 alors qu'avec HTTP 1.0 c'est le code 302
				//  Comme beaucoup de clients HTTP 1.1 traitent le code 302 comme le code 303, il peut être intéressant d'envoyer le code 302 qui conviendra à la fois aux clients HTTP 1.0 et 1.1

				// this will redirect a request with a temporary 302 HTTP status code
				if(service.getStart_date() != null) {
					response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // response.setStatus(301);
				}
				// But is it possible to redirect it with a permanent 301 HTTP status code
				if(service.getStop_date() != null) {
					response.setStatus(403); // 404 Not found.

					// You need to set the response status and the Location header manually.
					// response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // response.setStatus(301);
					// response.setHeader("Location", null); // response.addHeader("Location", "/a01");
				}

				return false;
			}

			String originOperatorID = request.getParameter("originOperatorID");

			if((originOperatorID == null) || (originOperatorID.trim().length() == 0)) {
				response.setStatus(403); // 403 - Access Forbidden.

				return false;
			}
			else {
				originOperatorID = originOperatorID.trim();

				boolean logon = false;
				/*boolean logon = (productProperties.getOriginOperatorIDs_list() != null) && (productProperties.getOriginOperatorIDs_list().contains(originOperatorID));*/

				if(logon) ;
				else {
					ServiceAccess access = (new JdbcServiceAccessDao(dao)).getOneServiceAccess(productProperties.getSc(), originOperatorID);

					if(access == null) {
						response.setStatus(401); // 401 - Access denied.
					}
					else {
						logon = true;
					}
				}

				// on passe la main à l'intecepteur suivant
				return logon;
			}

		} catch(Throwable th) {

		}

		return false;
	}

}
