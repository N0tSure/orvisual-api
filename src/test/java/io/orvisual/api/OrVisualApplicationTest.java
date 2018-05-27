package io.orvisual.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created on 09 May, 2018.
 *
 * <b>WARNING:</b> no file operation in this test, these operation can
 * change local directory content.
 * @author Artemis A. Sirosh
 */
@SpringBootTest(properties = "GALLERY_DIR=.")
@RunWith(SpringRunner.class)
public class OrVisualApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrVisualApplicationTest.class);

    @Test
    public void shouldLoadContextWithoutCrashing() {
        LOGGER.info("Looks like it is ok.");
    }
}
