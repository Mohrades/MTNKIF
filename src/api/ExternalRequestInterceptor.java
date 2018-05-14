package api;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import dao.DAO;
import dao.queries.USSDServiceDAOJdbc;
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
			USSDService service = new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc());
			Date now = new Date();

			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				return false;
			}

			String originOperatorID = request.getParameter("originOperatorID");

			if((originOperatorID == null) || (originOperatorID.trim().length() == 0)) {
				return false;
			}
			else {
				// on passe la main à l'intecepteur suivant
				originOperatorID = originOperatorID.trim();
				return (productProperties.getOriginOperatorIDs_list() == null) ? true : productProperties.getOriginOperatorIDs_list().contains(originOperatorID);				
			}

		} catch(Throwable th) {

		}

		return false;
	}

}
