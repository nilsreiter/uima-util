package de.unistuttgart.ims.uimautil;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

public abstract class DatabaseAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_HOSTNAME = "Hostname";
	public static final String PARAM_DBNAME = "DBname";
	public static final String PARAM_DBUSER = "DBuser";
	public static final String PARAM_DBPASS = "DBpassword";

	@ConfigurationParameter(name = PARAM_DBUSER)
	String databaseUsername;

	@ConfigurationParameter(name = PARAM_DBPASS)
	String databasePassword;

	@ConfigurationParameter(name = PARAM_HOSTNAME)
	String databaseHostname;

	@ConfigurationParameter(name = PARAM_DBNAME)
	String databaseName;

	DataSource dataSource;

	Connection connection;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			Class.forName("com.mysql.jdbc.Driver");

			final BasicDataSource bds = new BasicDataSource();
			bds.setDriverClassName("com.mysql.jdbc.Driver");
			bds.setUsername(databaseUsername);
			bds.setPassword(databasePassword);
			bds.setUrl("jdbc:mysql://" + databaseHostname + "/" + databaseName);

			dataSource = bds;

			connection = dataSource.getConnection();

		} catch (final SQLException e) {
			throw new ResourceInitializationException(e);
		} catch (final ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		if (connection != null)
			try {
				connection.close();
			} catch (final SQLException e) {
				throw new AnalysisEngineProcessException(e);
			}
	}

}
