package io.orvisual.api;

import io.orvisual.api.service.PictureStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created on 09 May, 2018.
 *
 * <b>WARNING:</b> do not use file operations of {@link io.orvisual.api.service.PictureStorageService} in this test,
 * these operation can change local directory content during test running.
 *
 * @author Artemis A. Sirosh
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrVisualApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrVisualApplicationTest.class);

    @MockBean
    private PictureStorageService storageService;

    @Test
    public void shouldLoadContextWithoutCrashing() {
        LOGGER.info("Looks like it is ok.");
    }
}
