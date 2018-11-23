package dao;

import java.io.Serializable;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DAO extends JdbcDaoSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504027749967734918L;

	public DAO() {
		// JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public DAO(ComboPooledDataSource dataSource) {
		setDataSource(dataSource);
		// super.setDataSource(dataSource);
		// JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*public void setDataSource(ComboPooledDataSource dataSource) {
		super.setDataSource(dataSource);
	}*/

	// DBA
	public void analyze() {
		// getJdbcTemplate().update("ANALYZE");
		getJdbcTemplate().update("VACUUM ANALYZE"); 
	}

}
