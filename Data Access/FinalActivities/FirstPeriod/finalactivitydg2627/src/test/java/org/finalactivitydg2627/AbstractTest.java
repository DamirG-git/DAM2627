package org.finalactivitydg2627;

import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractTest {

    @Mock
    protected ApplicationLogger logger;

}
