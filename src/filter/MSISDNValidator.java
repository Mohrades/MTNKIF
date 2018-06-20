package filter;

import java.util.List;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.MSISDNDAOJdbc;
import product.ProductProperties;
import util.AccountDetails;

public class MSISDNValidator {

	public MSISDNValidator() {

	}

	public boolean isFiltered(DAO dao, ProductProperties productProperties, String msisdn, String type) {
		if(type.equals("A")) {
			if(onNet(productProperties, msisdn)) {
				return validate(dao, productProperties.getAnumber_serviceClass_include_filter(), productProperties.getAnumber_db_include_filter(), productProperties.getAnumber_serviceClass_exclude_filter(), productProperties.getAnumber_db_exclude_filter(), msisdn, productProperties);
			}
		}
		else if(type.equals("B")) {
			if(onNet(productProperties, msisdn)) {
				return validate(dao, productProperties.getBnumber_serviceClass_include_filter(), productProperties.getBnumber_db_include_filter(), productProperties.getBnumber_serviceClass_exclude_filter(), productProperties.getBnumber_db_exclude_filter(), msisdn, productProperties);
			}
		}

		return false;
	}

	private boolean validate(DAO dao, List<String> number_serviceClass_include_filter, List<String> number_db_include_filter, List<String> number_serviceClass_exclude_filter, List<String> number_db_exclude_filter, String msisdn, ProductProperties productProperties) {
		// include
		boolean included = false;
		// exclude
		boolean excluded = true;

		try {
			// include
			if((number_serviceClass_include_filter == null) && (number_db_include_filter == null)) {
				included = true;
			}
			else {
				if(number_serviceClass_include_filter != null) {
					if(isServiceClassFiltered(number_serviceClass_include_filter, msisdn, productProperties)) {
						included = true;
					}
					else if(number_db_include_filter != null) {
						included = isDataTableFiltered(dao, number_db_include_filter, msisdn);
					}
				}
				else if(number_db_include_filter != null) {
					included = isDataTableFiltered(dao, number_db_include_filter, msisdn);
				}
			}

			// exclude
			if((number_serviceClass_exclude_filter == null) && (number_db_exclude_filter == null)) {
				excluded = false;
			}
			else {
				if(number_serviceClass_exclude_filter != null) {
					if(isServiceClassFiltered(number_serviceClass_exclude_filter, msisdn, productProperties)) {
						excluded = true;
					}
					else if(number_db_exclude_filter != null) {
						excluded = isDataTableFiltered(dao, number_db_exclude_filter, msisdn);
					}
				}
				else if(number_db_exclude_filter != null) {
					excluded = isDataTableFiltered(dao, number_db_exclude_filter, msisdn);
				}
			}

		} catch(Throwable th) {
			included = false;
			excluded = true;
		}

		return included && (!excluded);
	}

	private boolean isServiceClassFiltered(List<String> number_serviceClass_filter, String msisdn, ProductProperties productProperties) {
		try {
			AccountDetails accountDetails = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host()).getAccountDetails(msisdn);
			
			if(number_serviceClass_filter.contains(accountDetails.getServiceClassCurrent() + "")) {
				return true;
			}

		} catch(NullPointerException ex) {

		} catch(Exception ex) {

		} catch(Throwable th) {

		}

		return false;
	}

	private boolean isDataTableFiltered(DAO dao, List<String> number_db_filter, String msisdn) {
		try {
			for(String tableName : number_db_filter) {
				try {
					if(new MSISDNDAOJdbc(dao).getOneMSISDN(msisdn, tableName) != null) {
						return true;
					}

				} catch(NullPointerException ex) {

				} catch(Exception ex) {

				} catch(Throwable ex) {

				}
			}

		} catch(Throwable th) {

		}

		return false;
	}

	public boolean onNet(ProductProperties productProperties, String msisdn) {
		String country_code = productProperties.getMcc() + "";

		if((country_code.length() + productProperties.getMsisdn_length()) == (msisdn.length())) {
			if(productProperties.getMnc() == null) {
				return (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host()).getAccountDetails(msisdn)) != null;
			}
			else {
				for(String prefix : productProperties.getMnc()) {
					if(msisdn.startsWith(country_code+prefix)) {
						return (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host()).getAccountDetails(msisdn)) != null;
						// return true;
					}
				}
			}
		}

		return false;
	}

}
