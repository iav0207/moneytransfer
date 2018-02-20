package task.money.transfer.db.currency;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.io.RuntimeIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class CurrenciesInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CurrenciesInitializer.class);

    private static final String RESOURCE = "currencies.txt";
    private static final String DELIMITER = ",";

    private final CurrencyDao dao;

    public CurrenciesInitializer(CurrencyDao dao) {
        this.dao = dao;
    }

    /**
     * Inserts initial set of currencies into the table. The data set is taken from text resource.
     */
    public void populateCurrencies() {
        for (String line : readLinesResource()) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String[] parts = line.split(DELIMITER);
            int numCode = Integer.parseInt(parts[0]);
            String isoCode = parts[1];
            dao.put(numCode, isoCode);
        }
    }

    private List<String> readLinesResource() {
        try {
            return Files.readAllLines(new File(this.getClass().getResource(RESOURCE).toURI()).toPath());
        } catch (IOException | URISyntaxException e) {
            logger.error("Couldn't read currencies population data from resource.", e);
            throw new RuntimeIOException(e);
        }
    }
}
