/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.jetrix;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import net.jetrix.config.DataSourceConfig;

/**
 * Manage the connection pools to the databases.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public final class DataSourceManager
{
    private Logger log = Logger.getLogger(getClass().getName());

    private static DataSourceManager instance = new DataSourceManager();

    private Map<String, DataSource> datasources = new HashMap<String, DataSource>();

    /** Key of the default datasource. */
    public static final String DEFAULT_DATASOURCE = "DEFAULT";

    /** Default number of the minimum idle connections */
    public static final int DEFAULT_MIN_IDLE = 1;

    /** Default number of maximum active connections */
    public static final int DEFAULT_MAX_ACTIVE = 50;

    private DataSourceManager()
    {
    }

    public static DataSourceManager getInstance()
    {
        return instance;
    }

    /** 
     * Returns the default datasource. 
     */
    public DataSource getDataSource()
    {
        return getDataSource(DEFAULT_DATASOURCE);
    }

    public DataSource getDataSource(String environnement)
    {
        return datasources.get(environnement);
    }

    /** 
     * Configure the default datasource. 
     */
    public void setDataSource(DataSourceConfig config)
    {
        setDataSource(config, DEFAULT_DATASOURCE);
    }

    /**
     * Configure a datasource.
     *
     * @param config      the configuration of the datasource
     * @param environment the environment of the datasource
     */
    public void setDataSource(DataSourceConfig config, String environment)
    {        
        try
        {
            Class.forName(config.getDriver());
        }
        catch (ClassNotFoundException e)
        {
            log.warning("Unable to find the database driver (" + config.getDriver() + "), put the related jar in the lib directory");
            return;
        }
        
        try
        {
            // close the previous datasource if necessary
            if (datasources.containsKey(environment))
            {
                BasicDataSource datasource = (BasicDataSource) datasources.get(environment);
                datasource.close();
            }            
            
            BasicDataSource datasource = new BasicDataSource();
            datasource.setDefaultAutoCommit(false);

            datasource.setDriverClassName(config.getDriver());
            datasource.setUrl(config.getUrl());
            datasource.setUsername(config.getUsername());
            datasource.setPassword(config.getPassword());
            datasource.setMinIdle(config.getMinIdle() != 0 ? config.getMinIdle() : DEFAULT_MIN_IDLE);
            datasource.setMaxActive(config.getMaxActive() != 0 ? config.getMaxActive() : DEFAULT_MAX_ACTIVE);
            
            // attempts to open the connection
            datasource.getConnection().close();

            datasources.put(environment, datasource);
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, "Unable to configure the datasource '" + environment + "'", e);
        }
    }
}
