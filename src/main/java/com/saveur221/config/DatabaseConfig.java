package com.saveur221.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Properties PROPS = new Properties();

    static{
        try(InputStream in = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")){
                    if(in == null){
                        throw new IllegalStateException(
                            "application.properties introuvable dans src/main/ressources"
                        );
                    }
                    PROPS.load(in);
                }catch(IOException e){
                    throw new IllegalStateException("Erreur de la lecture de application.properties",e);
                }
    }

    private DatabaseConfig(){
        //c;asse Utilitaire, non instanciable
    }

    public static Connection getConnetion() throws SQLException{
        return DriverManager.getConnection(
            PROPS.getProperty("db.url"),            
            PROPS.getProperty("db.user"),
            PROPS.getProperty("db.password")

        );
    }
}
