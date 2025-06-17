package site.wijerathne.harshana.fintech.listeners;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.Properties;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final AppProperties PROPS = new AppProperties();
        final BasicDataSource BDS = new BasicDataSource();

        BDS.setDriverClassName(PROPS.getProperty("app.datasource.driver"));
        BDS.setUsername(PROPS.getProperty("app.datasource.user"));
        BDS.setPassword(PROPS.getProperty("app.datasource.password"));
        BDS.setUrl(PROPS.getProperty("app.datasource.url"));
        BDS.setInitialSize(Integer.parseInt(PROPS.getProperty("app.datasource.initial-size", String.valueOf(5))));
        BDS.setMaxTotal(Integer.parseInt(PROPS.getProperty("app.datasource.max-total", String.valueOf(15))));
        BDS.setMaxIdle(Integer.parseInt(PROPS.getProperty("app.datasource.max-idle", String.valueOf(5))));
        BDS.setMinIdle(Integer.parseInt(PROPS.getProperty("app.datasource.min-idle", String.valueOf(2))));

        sce.getServletContext().setAttribute("DATA_SOURCE", BDS);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }

    private static class AppProperties extends Properties {
        public AppProperties() {
            try {
                load(getClass().getResourceAsStream("/db.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
